package controllers

import play.api.mvc._
import java.util.Date
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import models._
import scala.collection.mutable.ListBuffer
import com.mongodb.casbah.commons.Imports._


object Users extends Controller {
  def registerForm(id :ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6,16),
      // Create a tuple mapping for the password/confirm
      "password" -> tuple(
        "main" -> text,
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          "Passwords don't match", passwords => passwords._1 == passwords._2),
      "nickNm" ->text,
      "birthDay" ->date,
      "sex" ->text, 
      "city" ->text,
      "job" ->text,
      "email" -> email,
      "tel" -> text,
      "qq" -> text,
      "msn" -> text,
      "weChat" -> text,
      "accept" -> checked("")) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (id, userId, password, nickNm, birthDay, sex, city, job, email, tel, qq, msn, weChat, _)
        => User(id, userId, password._1, nickNm, birthDay, sex, city, job, email, tel, qq, msn, weChat, "userTyp.0", "userLevel.0", 20, new Date(), "status.0")
      } // Unbinding: Create the mapping values from an existing Hacker value
      {
        user => Some((user.id, user.userId, (user.password, ""), user.nickNm, user.birthDay, user.sex, user.city, user.job, user.email, user.tel, user.qq, user.msn, user.weChat, false))
      }.verifying(
        "This userId is not available",user => !User.findOneByUserId(user.userId).nonEmpty)
  )

  val loginForm = Form(tuple(
    "userId" -> nonEmptyText,
    "password" -> nonEmptyText).verifying(user => User.authenticate(user._1, user._2).nonEmpty)
    
  )

  def userForm(id :ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6,16),
      // Create a tuple mapping for the password/confirm
      "password" -> text,
      "nickNm" ->text,
      "birthDay" ->date,
      "sex" ->text,
      "city" ->text,
      "job" ->text,
      "email" -> email,
      "tel" -> text,
      "qq" -> text,
      "msn" -> text,
      "weChat" -> text,
      "userTyp" ->text,
      "userBehaviorLevel" ->text,
      "point" ->number,
      "added" ->date,
      "status" ->text
      ) {
      // Binding: Create a User from the mapping result (ignore the second password and the accept field)
      (id, userId, password, nickNm, birthDay, sex, city, job, email, tel, qq, msn, weChat, userTyp, userBehaviorLevel, point, added, status)
      => User(id, userId, password, nickNm, birthDay, sex, city, job, email, tel, qq, msn, weChat, userTyp, userBehaviorLevel, point, added, status)
    } // Unbinding: Create the mapping values from an existing Hacker value
    {
      user => Some((user.id, user.userId, user.password, user.nickNm, user.birthDay, user.sex, user.city, user.job, user.email, user.tel, user.qq, user.msn, user.weChat, user.userTyp, user.userBehaviorLevel, user.point, user.added, user.status))
    }.verifying(
        "This userId is not available",user => User.findByNickNm(user.nickNm).nonEmpty)
  )
  
  /**
   * 定义用户申请技师的表单
   */
  def stylistApplyForm: Form[StylistApply] = Form(
        mapping("stylist" -> 
		    mapping(
		    	"workYears" -> number,
			    "position" -> seq(
			    	mapping(
			    		"positionName" -> text,
			    		"industryName" -> text
			    	){
			    		(positionName, industryName) => IndustryAndPosition(new ObjectId, positionName, industryName)
			    	}{
			    		industryAndPosition => Some(industryAndPosition.positionName, industryAndPosition.indestryName)
			    	}	
			    ),
			    "goodAtImage" -> seq(text),
			    "goodAtStatus" -> seq(text),
			    "goodAtService" -> seq(text),
			    "goodAtUser" -> seq(text),
			    "goodAtAgeGroup" -> seq(text),
			    "myWords" -> text,
			    "mySpecial" -> text,
			    "myBoom" -> text,
			    "myPR" -> text
			){
		      (workYears, position, goodAtImage, goodAtStatus, goodAtService,
		          goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR)
		      => Stylist(new ObjectId, new ObjectId(), 0, position, goodAtImage, goodAtStatus,
		    	   goodAtService, goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR, 
		           Option(Seq(new OnUsePicture(new ObjectId, new PictureUse(new ObjectId, "" , 1), 1, ""))), false, false)
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

  def login = Action {
    Ok(views.html.user.login(Users.loginForm))
  }

  def register = Action {
    Ok(views.html.user.register(Users.registerForm()))
  }
  
  /**
   * 登录触发动作
   */
  def doLogin = Action { implicit request =>
    Users.loginForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.login(errors)),
      {
        user =>
          Redirect(routes.Users.myPage(user._1)).withSession(request.session + ("userId" -> user._1))
      })
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
      errors => BadRequest(views.html.user.Infomation(errors,User.findOneByID(id).get)),
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
      Ok(views.html.user.Infomation(userForm,user))
    } getOrElse {
      NotFound
    }
  }

  /**
   * 点击主页“我的主页”触发的动作，判断是否已登录
   */
  def index = Action{ implicit request =>
    if(!request.session.get("userId").nonEmpty){
      Redirect(routes.Users.login)
    }else
	  Redirect(routes.Users.myPage(request.session.get("userId").get))
  }
  
  /**
   * 个人主页
   */
  def myPage(userId :String) = Action{
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
  def otherIndex(id: ObjectId) = Action{
    val other = User.findById(id).get
    Ok(views.html.user.otherPage(other))
  }
  
  /**
   * 我的预约
   */
  def myReservation(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.myPageRes(user = user.get))
  }
  
  /**
   * 我收藏的优惠劵
   */
  def mySaveCoupon(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.mySaveCoupon(user = user.get))
  }
  
  /**
   * 我收藏的博客
   */
  def mySaveBlog(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.mySaveBlog(user = user.get))
  }
  
  /**
   * 我收藏的风格
   */
  def mySaveStyle(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.mySaveStyle(user = user.get))
  }
  
  /**
   *我收藏的店铺动态 
   */
  def mySaveSalonActi(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.mySaveSalonActi(user = user.get))
  }

  /**
   *他人收藏的博客 
   */
  def SaveBlog(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.otherSaveBlog(user = user.get))
  }

  /**
   *他人收藏的风格 
   */
  def SaveStyle(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.otherSaveStyle(user = user.get))
  }

  /**
   *列表显示关注的沙龙 
   */
  def showAllFollowSalon(userId: ObjectId) = Action{implicit request =>
    val sessionUserId = request.session.get("userId")
    val user: Option[User] = User.findById(userId)
    val salonIdList: List[ObjectId] = FollowCollect.getAllFollowCollectAtId(1,userId)
    val salonList = ListBuffer[Salon]()
    for (i <-0 to salonIdList.length-1){
      val salon =Salon.findById(salonIdList(i)).get
      salonList += salon
    }
    Ok(views.html.user.showAllFollowSalon(salonList.toList,user.get,sessionUserId))
  }
  
  /**
   *列表显示关注的技师 
   */
  def showAllFollowStylist(userId: ObjectId) = Action{implicit request =>
    val sessionUserId = request.session.get("userId")
    val user: Option[User] = User.findById(userId)
    val stylistIdList: List[ObjectId] = FollowCollect.getAllFollowCollectAtId(2,userId)
    val stylistList = ListBuffer[Stylist]()
    for (i <-0 to stylistIdList.length-1){
      val stylist =Stylist.findOneById(stylistIdList(i)).get
      stylistList += stylist
    }
    Ok(views.html.user.showAllFollowStylist(stylistList.toList,user.get,sessionUserId))
  }

  /**
   *列表显示关注的其他用户 
   */
  def showAllFollowUser(userId: ObjectId) = Action{implicit request =>
    val sessionUserId = request.session.get("userId")
    val user: Option[User] = User.findById(userId)
    val userIdList: List[ObjectId] = FollowCollect.getAllFollowCollectAtId(6,userId)
    val userList = ListBuffer[User]()
    for (i <-0 to userIdList.length-1){
      val followUser =User.findById(userIdList(i)).get
      userList += followUser
    }
    Ok(views.html.user.showAllFollowUser(userList.toList,user.get,sessionUserId))
  }
  
  /**
   *列表显示我的粉丝 
   */
   def showMyFollowers(userId: ObjectId) = Action{implicit request =>
    val sessionUserId = request.session.get("userId")
    val user: Option[User] = User.findById(userId)
    val myFollowersIdList: List[ObjectId] = FollowCollect.getFollowers(userId)
    val myFollowersList = ListBuffer[User]()
    for (i <-0 to myFollowersIdList.length-1){
      val myFollowers =User.findById(myFollowersIdList(i)).get
      myFollowersList += myFollowers
    }
    Ok(views.html.user.showMyFollowers(myFollowersList.toList,user.get,sessionUserId))
  }
   
   /**
    * 退出登录
    */
   def loginout = Action{
     Redirect(routes.Application.index).withNewSession
   }
  
   /**
    * 取消关注
    */
   def cancelFollow(userName:String,salonId:ObjectId,relationTypeId:Int) =Action{
    val userId = User.findOneByUserId(userName).get.id
   	FollowCollect.delete(userId, salonId, relationTypeId)
   	Redirect(routes.Users.showAllFollowSalon(userId))
   }
   
   /**
    * 添加关注或收藏
    */
   def addFollow(followId:ObjectId,relationTypeId:Int) = Action{implicit request =>
     val userId = request.session.get("userId")
     if(userId.nonEmpty){
       val user = User.findOneByUserId(userId.get).get
       if(!FollowCollect.checkIfFollowOff(user.id, followId) && !FollowCollect.checkIfFollowOn(user.id, followId)){
         FollowCollect.create(user.id, followId, relationTypeId)
       }else if(FollowCollect.checkIfFollowOff(user.id, followId)){
         FollowCollect.createAgain(user.id, followId, relationTypeId)
       }
       Redirect(routes.Users.myPage(userId.get))
     }else{
       Redirect(routes.Users.myPage(userId.get))//TODO
     }
   }
   
  /**
   * 申请成为技师
   */
  def applyStylist = Action { implicit request=>
    val user = User.findById(new ObjectId("53202c29d4d5e3cd47efffd4"))
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
    val userId = new ObjectId("53202c29d4d5e3cd47efffd4")
    stylistApplyForm.bindFromRequest.fold(
      errors => BadRequest(views.html.fortest(errors)),
      {
        case(stylistApply) => {
        	Stylist.save(stylistApply.stylist)
        	val applyRecord = new SalonStylistApplyRecord(new ObjectId, stylistApply.salonId, stylistApply.stylist.id, 1, new Date, 0, None)
    	    SalonStylistApplyRecord.save(applyRecord)
    	    Ok(views.html.fortest(stylistApplyForm.fill(stylistApply)))
        }
      })
    	 
  }
  
}
