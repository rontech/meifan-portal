package controllers

import java.util.Date
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.commons.Imports.MongoDBObject
import com.mongodb.casbah.commons.Imports.ObjectId
import jp.t2v.lab.play2.auth._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.Future

object Users extends Controller with LoginLogout with AuthElement with AuthConfigImpl{
  def registerForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6, 16),
      "password" -> tuple(
        "main" -> text,
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          "Passwords don't match", passwords => passwords._1 == passwords._2),
      "nickName" -> text,
      "sex" -> text,
      "birthDay" -> date,
      "city" -> text,
      "tel" -> text,
      "email" -> email,
      "optContactMethod" -> seq(
        mapping(
          "contMethmodType" -> text,
          "account" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
      "socialStatus" -> text) {
        (id, userId, password, nickName, sex, birthDay, city, tel, email, optContactMethod, socialStatus) => 
          User(new ObjectId, userId, password._1, nickName, sex, birthDay, city, new ObjectId, tel, email, optContactMethod, socialStatus, "NormalUser", "userLevel.0", 0, new Date(), "LoggedIn", false)
      } {
        user => Some((user.id, user.userId, (user.password, ""), user.nickName, user.sex, user.birthDay, user.city, user.tel, user.email, user.optContactMethod, user.socialStatus))
      }.verifying(
        "This userId is not available", user => !User.findOneByUserId(user.userId).nonEmpty))

  val loginForm = Form(mapping(
    "userId" -> nonEmptyText,
    "password" -> nonEmptyText)(User.authenticate)(_.map(u => (u.userId, "")))
      .verifying("Invalid email or password", result => result.isDefined) 
  )

  def userForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6,16),
      "password" -> text,
      "nickName" ->text,
      "sex" ->text,
      "birthDay" ->date,
      "city" ->text,
      "userPics" ->text,
      "tel" -> text,
      "email" -> email,
      "optContactMethod" -> seq(
          mapping(
            "contMethmodType" ->text,
            "account" -> list(text)
          )(OptContactMethod.apply)(OptContactMethod.unapply)   
       ),
      "socialStatus" ->text,
      "registerTime" -> date
      ) {
      // Binding: Create a User from the mapping result (ignore the second password and the accept field)
      (id, userId, password, nickName, sex, birthDay, city, userPics,tel, email, optContactMethod,socialStatus,registerTime)
      => User(id, userId,password, nickName, sex, birthDay, city, new ObjectId(userPics), tel, email, optContactMethod, socialStatus, "NormalUser", "userLevel.0",0, registerTime, "LoggedIn", false) 
    } // Unbinding: Create the mapping values from an existing Hacker value
    {
      user => Some((user.id, user.userId, user.password, user.nickName, user.sex, user.birthDay, user.city, user.userPics.toString(), user.tel, user.email, user.optContactMethod, user.socialStatus, user.registerTime))
    }.verifying(
        "This userId is not available",user => User.findOneByNickNm(user.nickName).nonEmpty)
  )
  /**
   * 定义用户申请技师的表单
   */

  def stylistApplyForm: Form[StylistApply] = Form(
        mapping("stylist" -> 
		    mapping(
		    	"workYears" -> number,
			    "position" -> list(
			    	mapping(
			    		"positionName" -> text,
			    		"industryName" -> text
			    	){
			    		(positionName, industryName) => IndustryAndPosition(new ObjectId, positionName, industryName)
			    	}{
			    		industryAndPosition => Some(industryAndPosition.positionName, industryAndPosition.indestryName)
			    	}	
			    ),
			    "goodAtImage" -> list(text),
			    "goodAtStatus" -> list(text),
			    "goodAtService" -> list(text),
			    "goodAtUser" -> list(text),
			    "goodAtAgeGroup" -> list(text),
			    "myWords" -> text,
			    "mySpecial" -> text,
			    "myBoom" -> text,
			    "myPR" -> text
			){
		      (workYears, position, goodAtImage, goodAtStatus, goodAtService,
		          goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR)
		      => Stylist(new ObjectId, new ObjectId(), 0, position, goodAtImage, goodAtStatus,
		    	   goodAtService, goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR, 
		           Option(List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), false, false)
		    }{
		      stylist => Some(stylist.workYears, stylist.position, 
		          stylist.goodAtImage, stylist.goodAtStatus, stylist.goodAtService, stylist.goodAtUser,
		          stylist.goodAtAgeGroup, stylist.myWords, stylist.mySpecial, stylist.myBoom, stylist.myPR)
		    },
		    "salonId" -> text
		    ){
		      (stylist, salonId) => StylistApply(stylist, new ObjectId(salonId))
		    }{
		      stylistApply => Some((stylistApply.stylist, stylistApply.salonId.toString))
		    }
		)

  

  //  val userForm: Form[User] = Form(
  //    mapping(
  //      "username" -> text,
  //      "password" -> text,
  //      "sex" -> text,
  //      "age" -> number,
  //      "tel" -> text,
  //      "email" -> text,
  //      "education" -> text,
  //      "introduce" -> text,
  //      "added" -> date,
  //      "updated" -> date) {
  //        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
  //        (username, password, sex, age, tel, email, education, introduce, added, _) => User(new ObjectId, username, password, sex, age, tel, email, education, introduce, added, new Date())
  //      } // Unbinding: Create the mapping values from an existing Hacker value
  //      {
  //        user => Some((user.username, user.password, user.sex, user.age, user.tel, user.email, user.education, user.introduce, user.added, user.updated))
  //      }
  //  )
  
  /**
   * 登录触发动作
   */
  def doLogin = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.user.login(formWithErrors)))},
      user           => gotoLoginSucceeded(user.get.userId)
    )
  }

  /**
   * 注册触发动作
   */
  def doRegister = Action { implicit request =>
    Users.registerForm().bindFromRequest.fold(
      errors => BadRequest(views.html.user.register(errors)),
      {
        user =>
          User.save(user, WriteConcern.Safe)
          Ok(views.html.user.myPageRes(user))
      })
  }

  /**
   * 更新触发的动作
   */
  def update(id: ObjectId) = Action { implicit request =>
    Users.userForm(id).bindFromRequest.fold(
      errors => BadRequest(views.html.user.Infomation(errors, User.findOneById(id).get)),
      {
        user =>
          User.save(user, WriteConcern.Safe)
          Ok(views.html.user.myPageRes(user))
      })
  }

  /**
   * 显示用户基本信息
   */
  def show(userId: String) = Action {
    User.findOneByUserId(userId).map { user =>
      val userForm = Users.userForm().fill(user)
      Ok(views.html.user.Infomation(userForm, user))
    } getOrElse {
      NotFound
    }
  }

  /**
   * 点击主页“我的主页”触发的动作，判断是否已登录
   */
  def index = StackAction(AuthorityKey -> authorization(LoggedIn) _) {implicit request =>
    val user = loggedIn
      Redirect(routes.Users.myPage(user.userId))
  }

  /**
   * 个人主页
   */
  def myPage(userId: String) = Action {
    val user = User.findOneByUserId(userId).get
    if((user.userTyp).equals("userTyp.0")) {
    	Ok(views.html.user.myPageRes(user))
    } else if((user.userTyp).equals("userTyp.1")) {
    	val stylist = Stylist.findOne(MongoDBObject("userId" -> new ObjectId(user.userId)))
    	Ok(views.html.stylist.management.stylistHomePage(user = user, stylist = stylist.get))
    }else {
    	Ok(views.html.user.myPageRes(user))
    }
  }

  /**
   * 浏览他人主页
   */
  def otherIndex(id: ObjectId) = Action {
    val other = User.findOneById(id).get
    Ok(views.html.user.otherPage(other))
  }

  /**
   * 我的预约
   */
  def myReservation(userId: ObjectId) = Action {
    val user: Option[User] = User.findOneById(userId)
    Ok(views.html.user.myPageRes(user = user.get))
  }

  /**
   * 我收藏的优惠劵
   */
  def mySaveCoupon(userId: ObjectId) = Action {
    val user: Option[User] = User.findOneById(userId)
    Ok(views.html.user.mySaveCoupon(user = user.get))
  }

  /**
   * 我收藏的博客
   */
  def mySaveBlog(userId: ObjectId) = Action {
    val user: Option[User] = User.findOneById(userId)
    Ok(views.html.user.mySaveBlog(user = user.get))
  }

  /**
   * 我收藏的风格
   */
  def mySaveStyle(userId: ObjectId) = Action {
    val user: Option[User] = User.findOneById(userId)
    Ok(views.html.user.mySaveStyle(user = user.get))
  }

  /**
   * 我收藏的店铺动态
   */
  def mySaveSalonActi(userId: ObjectId) = Action {
    val user: Option[User] = User.findOneById(userId)
    Ok(views.html.user.mySaveSalonActi(user = user.get))
  }

  /**
   * 他人收藏的博客
   */
  def SaveBlog(userId: ObjectId) = Action {
    val user: Option[User] = User.findOneById(userId)
    Ok(views.html.user.otherSaveBlog(user = user.get))
  }

  /**
   * 他人收藏的风格
   */
  def SaveStyle(userId: ObjectId) = Action {
    val user: Option[User] = User.findOneById(userId)
    Ok(views.html.user.otherSaveStyle(user = user.get))
  }

  /**
   * 列表显示关注的沙龙
   */
  def showAllFollowSalon(userId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user =loggedIn
    val salonIdList: List[ObjectId] = FollowCollect.getAllFollowCollectAtId(1, userId)
    val salonList = ListBuffer[Salon]()
    for (i <- 0 to salonIdList.length - 1) {
      val salon = Salon.findById(salonIdList(i)).get
      salonList += salon
    }
    Ok(views.html.user.showAllFollowSalon(salonList.toList, user, Option(user.userId)))
  }

  /**
   * 列表显示关注的技师
   */
  def showAllFollowStylist(userId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user =loggedIn
    val stylistIdList: List[ObjectId] = FollowCollect.getAllFollowCollectAtId(2, userId)
    val stylistList = ListBuffer[Stylist]()

    for (i <- 0 to stylistIdList.length - 1) {
      val stylist = Stylist.findOneById(stylistIdList(i)).get
      stylistList += stylist
    }
    Ok(views.html.user.showAllFollowStylist(stylistList.toList, user, Option(user.userId)))
  }

  /**
   * 列表显示关注的其他用户
   */
  def showAllFollowUser(userId: ObjectId)= StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user =loggedIn
    val userIdList: List[ObjectId] = FollowCollect.getAllFollowCollectAtId(6, userId)
    val userList = ListBuffer[User]()
    for (i <- 0 to userIdList.length - 1) {
      val followUser = User.findOneById(userIdList(i)).get
      userList += followUser
    }
    Ok(views.html.user.showAllFollowUser(userList.toList, user, Option(user.userId)))
  }

  /**
   * 列表显示我的粉丝
   */
  def showMyFollowers(userId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user =loggedIn
    val myFollowersIdList: List[ObjectId] = FollowCollect.getFollowers(userId)
    val myFollowersList = ListBuffer[User]()
    for (i <- 0 to myFollowersIdList.length - 1) {
      val myFollowers = User.findOneById(myFollowersIdList(i)).get
      myFollowersList += myFollowers
    }
    Ok(views.html.user.showMyFollowers(myFollowersList.toList, user, Option(user.userId)))
  }

  /**
   * 退出登录
   */
  def loginout = Action {
    Redirect(routes.Application.index).withNewSession
  }

  /**
   * 取消关注
   */
  def cancelFollow(userName: String, salonId: ObjectId, relationTypeId: Int) = Action {
    val userId = User.findOneByUserId(userName).get.id
    FollowCollect.delete(userId, salonId, relationTypeId)
    Redirect(routes.Users.showAllFollowSalon(userId))
  }

  /**
   * 添加关注或收藏
   */
  def addFollow(followId: ObjectId, relationTypeId: Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
      val user = loggedIn
      if (!FollowCollect.checkIfFollowOff(user.id, followId) && !FollowCollect.checkIfFollowOn(user.id, followId)) {
        FollowCollect.create(user.id, followId, relationTypeId)
      } else if (FollowCollect.checkIfFollowOff(user.id, followId)) {
        FollowCollect.createAgain(user.id, followId, relationTypeId)
      }
      if (relationTypeId == 1 || relationTypeId == 2 || relationTypeId== 6)
         UserMessage.sendFollowMsg(user,followId,relationTypeId)
      Redirect(routes.Users.myPage(user.userId))
  }

  /**
   * 申请成为技师
   */

  def applyStylist = Action { implicit request=>
    val user = User.findOneById(new ObjectId("53202c29d4d5e3cd47efffd4"))
    val industry = Industry.findAll.toList
    val position = Position.findAll.toList
    val goodAtImage = StyleImpression.findAll.toList
    val goodAtStatus = SocialStatus.findAll.toList
    val goodAtService = Service.findAll.toList
    val goodAtUser = Sex.findAll.toList
    val goodAtAgeGroup = AgeGroup.findAll.toList
    Ok(views.html.user.applyStylist(stylistApplyForm, user.get, position, industry, goodAtImage, goodAtStatus, goodAtService, goodAtUser, goodAtAgeGroup))

  }

  /**
   * 店长或店铺管理者确认后才录入数据库
   */

  def commitStylistApply() = Action {implicit request=>
    val user = User.findOneById(new ObjectId("53202c29d4d5e3cd47efffd4"))
    val industry = Industry.findAll.toList
    val position = Position.findAll.toList
    val goodAtImage = StyleImpression.findAll.toList
    val goodAtStatus = SocialStatus.findAll.toList
    val goodAtService = Service.findAll.toList
    val goodAtUser = Sex.findAll.toList
    val goodAtAgeGroup = AgeGroup.findAll.toList
    stylistApplyForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("")),
      {
        case(stylistApply) => {
        	Stylist.save(stylistApply.stylist)
        	val applyRecord = new SalonStylistApplyRecord(new ObjectId, stylistApply.salonId, stylistApply.stylist.id, 1, new Date, 0, None)
    	    SalonStylistApplyRecord.save(applyRecord)
    	    Ok(views.html.user.applyStylist(stylistApplyForm.fill(stylistApply), user.get, position, industry, goodAtImage, goodAtStatus, goodAtService, goodAtUser, goodAtAgeGroup))
        }
      })
    	 
  }

}
