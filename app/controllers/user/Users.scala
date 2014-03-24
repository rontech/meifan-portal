package controllers

import java.util.Date
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.commons.Imports._
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.Future

object Users extends Controller with LoginLogout with AuthElement with AuthConfigImpl {

  /*
   * 关注与收藏类别
   * collection： FollowType
   */

  //关注沙龙
  val FOLLOWSALON = "salon"
  //关注技师
  val FOLLOWSTYLIST = "stylist"
  //关注用户
  val FOLLOWUSER = "user"
  //收藏发型
  val FOLLOWSTYLE = "style"
  //收藏博客
  val FOLLOWBLOG = "blog"
  //收藏优惠券
  val FOLLOWCOUPON = "coupon"

  /*
   * 用户类别
   */

  //普通用户
  val NORMALUSER = "normalUser"
  //专业技师
  val STYLIST = "stylist"

  /*
    * 用户行为等级
    */

  //高
  val HIGH = "high"
  //中
  val MIDDLE = "middle"
  //底
  val LOW = "low"

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
          User(new ObjectId, userId, nickName, password._1, sex, birthDay, city, new ObjectId, tel, email, optContactMethod, socialStatus, "NormalUser", "userLevel.0", 0, new Date(), LoggedIn.toString, false)
      } {
        user => Some((user.id, user.userId, (user.password, ""), user.nickName, user.sex, user.birthDay, user.city, user.tel, user.email, user.optContactMethod, user.socialStatus))
      }.verifying(
        "This userId is not available", user => !User.findOneByUserId(user.userId).nonEmpty))

  val loginForm = Form(mapping(
    "userId" -> nonEmptyText,
    "password" -> nonEmptyText)(User.authenticate)(_.map(u => (u.userId, "")))
    .verifying("Invalid email or password", result => result.isDefined))

  def userForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6, 16),
      "password" -> text,
      "nickName" -> text,
      "sex" -> text,
      "birthDay" -> date,
      "city" -> text,
      "userPics" -> text,
      "tel" -> text,
      "email" -> email,
      "optContactMethod" -> seq(
        mapping(
          "contMethmodType" -> text,
          "account" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
      "socialStatus" -> text,
      "registerTime" -> date) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (id, userId, nickName, password, sex, birthDay, city, userPics, tel, email, optContactMethod, socialStatus, registerTime) => User(id, userId, password, nickName, sex, birthDay, city, new ObjectId(userPics), tel, email, optContactMethod, socialStatus, "NormalUser", "userLevel.0", 0, registerTime, "LoggedIn", false)
      } // Unbinding: Create the mapping values from an existing Hacker value
      {
        user => Some((user.id, user.userId, user.password, user.nickName, user.sex, user.birthDay, user.city, user.userPics.toString(), user.tel, user.email, user.optContactMethod, user.socialStatus, user.registerTime))
      }.verifying(
        "This userId is not available", user => User.findOneByNickNm(user.nickName).nonEmpty))

  /**
   * 用户申请技师用表单
   */

  val stylistApplyForm: Form[StylistApply] = Form(
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
  /**
   * 用户登录验证
   */
  def login = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => { Future.successful(BadRequest(views.html.user.login(formWithErrors))) },
      user => gotoLoginSucceeded(user.get.userId))
  }
  
  /**
   * 退出登录
   */
  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"
    ))
  }
  
  /**
   * 用户注册
   */
  def register = Action { implicit request =>
    Users.registerForm().bindFromRequest.fold(
      errors => BadRequest(views.html.user.register(errors)),
      {
        user =>
          User.save(user, WriteConcern.Safe)
          Ok(views.html.user.login(Users.loginForm))
      })
  }

  /**
   * 用户信息更新
   */
  def updateInfo(userId: String) = StackAction(AuthorityKey -> User.isOwner(userId) _) { implicit request =>
    val loginUser = loggedIn
    Users.userForm().bindFromRequest.fold(
      errors => BadRequest(views.html.user.Infomation(errors)),
      {
        user =>
          User.save(user.copy(id = loginUser.id), WriteConcern.Safe)
          Ok(views.html.user.myPageRes(user))
      })
  }

  /**
   * 登录用户基本信息
   */
  def myInfo() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val userForm = Users.userForm().fill(user)
    Ok(views.html.user.Infomation(userForm))
  }

  /**
   * 其他用户基本信息
   */
  def userInfo(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    User.findOneByUserId(userId).map{user =>
      val userForm = Users.userForm().fill(user)
      Ok(views.html.user.Infomation(userForm))
    }getOrElse{
      NotFound
    }
    
  }

  /**
   * 个人主页
   */
  def myPage() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    if ((user.userTyp).equals(NORMALUSER)) {
      Ok(views.html.user.myPageRes(user))
    } else if ((user.userTyp).equals(STYLIST)) {
      //TODO
      val stylist = Stylist.findOneById(user.id)
      Ok(views.html.stylist.management.stylistHomePage(user = user, stylist = stylist.get))
    } else {
      Ok(views.html.user.myPageRes(user))
    }
  }

  /**
   * 浏览他人主页
   */
  def userPage(userId : String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    User.findOneByUserId(userId).map{user =>
      Ok(views.html.user.otherPage(user))
    }getOrElse{
      NotFound
    }
  }

  /**
   * 我的预约
   */
  def myReservation() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    Ok(views.html.user.myPageRes(user))
  }

  /**
   * 我收藏的优惠劵
   */
  def myFollowedCoupon() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    Ok(views.html.user.mySaveCoupon(user))
  }

  /**
   * 我收藏的博客
   */
  def myFollowedBlog() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    Ok(views.html.user.mySaveBlog(user))
  }

  /**
   * 我收藏的风格
   */
  def myFollowedStyle() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    Ok(views.html.user.mySaveStyle(user))
  }

  /**
   * 我收藏的店铺动态
   */
  def myFollowedSalonActi() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    //TODO
    Ok(views.html.user.mySaveSalonActi(user))
  }

  /**
   * 他人收藏的博客
   */
  def userBlog(userId: String) = Action {
    User.findOneByUserId(userId).map{user =>
      Ok(views.html.user.otherSaveBlog(user))
    }getOrElse{
      NotFound
    }
  }

  /**
   * 他人收藏的风格
   */
  def userStyle(userId: String) = Action {
    User.findOneByUserId(userId).map{user =>
      Ok(views.html.user.otherSaveStyle(user))
    }getOrElse{
      NotFound
    }
  }

  /**
   * 列表显示关注的沙龙
   */
  def myFollowedSalon() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    //TODO 关注表 userId 希望改成 String
    val salonIdL: List[ObjectId] = MyFollows.getAllFollowObjId(FOLLOWSALON, user.id)
    val salonL = salonIdL.map(salonId =>
    	Salon.findById(salonId).get
    )
    //TODO view的显示
    Ok(views.html.user.showAllFollowSalon(salonL, user, Option(user.userId)))
  }

  /**
   * 列表显示关注的技师
   */
  def myFollowedStylist() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val stylistIdL: List[ObjectId] = MyFollows.getAllFollowObjId(FOLLOWSTYLIST, user.id)
    val stylistL = stylistIdL.map(stylistId =>
    	Stylist.findOneById(stylistId).get
    )
    Ok(views.html.user.showAllFollowStylist(stylistL, user, Option(user.userId)))
  }

  /**
   * 列表显示关注的其他用户
   */
  def myFollowedUser() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val userIdL: List[ObjectId] = MyFollows.getAllFollowObjId(FOLLOWUSER, user.id)
    val userL = userIdL.map(userId =>
    	User.findOneById(userId).get
    )
    Ok(views.html.user.showAllFollowUser(userL, user, Option(user.userId)))
  }

  /**
   * 列表显示我的粉丝
   */
  def myFollowers() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val myFollowersIdL: List[ObjectId] = MyFollows.getFollowers(user.id)
    val myFollowersL = myFollowersIdL.map(myFollowersId =>
    	User.findOneById(myFollowersId).get
    )
    Ok(views.html.user.showMyFollowers(myFollowersL.toList, user, Option(user.userId)))
  }
////////////////////////////////////////////////////////////////
  
  /**
   * 取消关注
   */
  def cancelFollow(userName: String, salonId: ObjectId) = Action {
    val userId = User.findOneByUserId(userName).get.id
    MyFollows.delete(userId, salonId)
    Redirect(routes.Users.myFollowedSalon())
  }

  /**
   * 添加关注或收藏
   */
  def addFollow(followId: ObjectId, followObjType: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    if (!MyFollows.checkIfFollow(user.id, followId)) {
      MyFollows.create(user.id, followId, followObjType)
    }
    if (followObjType == FOLLOWSALON || followObjType == FOLLOWSTYLIST || followObjType == FOLLOWUSER)
      UserMessage.sendFollowMsg(user, followId, followObjType)
    Redirect(routes.Users.myPage())
  }

  /**
   * 申请成为技师
   */

  def applyStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val industry = Industry.findAll.toList
    val position = Position.findAll.toList
    val goodAtImage = StyleImpression.findAll.toList
    val goodAtStatus = SocialStatus.findAll.toList
    val goodAtService = Service.findAll.toList
    val goodAtUser = Sex.findAll.toList
    val goodAtAgeGroup = AgeGroup.findAll.toList
    Ok(views.html.user.applyStylist(stylistApplyForm, user, position, industry, goodAtImage, goodAtStatus, goodAtService, goodAtUser, goodAtAgeGroup))
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
