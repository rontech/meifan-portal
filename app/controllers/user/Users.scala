package controllers

import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent._
import play.api.templates.Html
import play.api.i18n.Messages

object Users extends Controller with LoginLogout with AuthElement with AuthConfigImpl {

  def registerForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6, 16),
      "password" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[a-zA-Z]\w{5,17}$""")),
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
            Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2),
      "nickName" -> nonEmptyText,
      "sex" -> text,
      "birthDay" -> date,
      "address" ->  mapping(
         "province" -> text,
         "city" -> optional(text),
         "region" -> optional(text)){
          (province,city,region) => Address(province,city,region,None,"NO NEED",None,None)
      }{
          address => Some(address.province,address.city,address.region)
      },
      "tel" -> text.verifying(Messages("user.telError"), tel => tel.matches("""^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$""")),
      "email" -> email,
      "optContactMethods" -> seq(
        mapping(
          "contMethodType" -> text,
          "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
      "socialStatus" -> text){
        (id, userId, password, nickName, sex, birthDay, address, tel, email, optContactMethods, socialStatus) =>
          User(new ObjectId, userId, nickName, password._1, sex, birthDay, address, new ObjectId, tel, email, optContactMethods, socialStatus, User.NORMAL_USER,  User.HIGH, 0, new Date(), Permission.valueOf(LoggedIn), false)
      } {
        user => Some((user.id, user.userId, (user.password, ""), user.nickName, user.sex, user.birthDay, user.address, user.tel, user.email, user.optContactMethods, user.socialStatus))
      }.verifying(
        Messages("user.userIdNotAvailable"), user => !User.findOneByUserId(user.userId).nonEmpty))

  val loginForm = Form(mapping(
    "userId" -> nonEmptyText,
    "password" -> nonEmptyText)(User.authenticate)(_.map(u => (u.userId, "")))
    .verifying(Messages("user.loginErr"), result => result.isDefined))

  val changePassForm = Form(
    mapping(
      "user" ->mapping(
        "userId" -> text,
        "oldPassword" -> nonEmptyText)(User.authenticate)(_.map(u => (u.userId, ""))).verifying("Invalid OldPassword", result => result.isDefined),
      "newPassword" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[a-zA-Z]\w{5,17}$""")),
        "confirm" -> text).verifying(
        // Add an additional constraint: both passwords must match
        Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)
    ){(user, newPassword) => (user.get, newPassword._1)}{user => Some((Option(user._1),("","")))}
  )


  def userForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6, 16),
      "nickName" -> nonEmptyText,
      "password" -> text,
      "sex" -> nonEmptyText,
      "birthDay" -> date,
      "address" ->  mapping(
         "province" -> text,
         "city" -> optional(text),
         "region" -> optional(text)){
          (province,city,region) => Address(province,city,region,None,"NO NEED",None,None)
      }{
          address => Some((address.province,address.city,address.region))
      },
      "userPics" -> text,
      "tel" -> nonEmptyText.verifying(Messages("user.telError"), tel => tel.matches("""^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$""")),
      "email" -> email,
      "optContactMethods" -> seq(
        mapping(
          "contMethodType" -> text,
          "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
      "socialStatus" -> text,
      "registerTime" -> date,
      "userTyp" -> text,
      "userBehaviorLevel" ->text,
      "point" ->number,
      "permission" -> text,
      "isValid" -> boolean
    ) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (id, userId, nickName, password, sex, birthDay, address, userPics, tel, email, optContactMethods, socialStatus, registerTime, userTyp, userBehaviorLevel, point, permission, isValid)
        => User(id, userId, nickName, password,  sex, birthDay, address, new ObjectId(userPics), tel, email, optContactMethods, socialStatus, userTyp, userBehaviorLevel, point, registerTime, permission, isValid)
      } // Unbinding: Create the mapping values from an existing Hacker value
      {
        user => Some((user.id, user.userId, user.nickName, user.password, user.sex, user.birthDay, user.address, user.userPics.toString, user.tel, user.email, user.optContactMethods, user.socialStatus, user.registerTime,
        user.userTyp, user.userBehaviorLevel, user.point, user.permission, user.isValid))
      })

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
			    		industryAndPosition => Some(industryAndPosition.positionName, industryAndPosition.industryName)
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
		      => Stylist(new ObjectId, new ObjectId(), workYears, position, goodAtImage, goodAtStatus,
		    	   goodAtService, goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR, 
		           List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), false, false)
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
   * 跳转至密码修改页面
   */
  def password = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.changePassword(Users.changePassForm.fill((user,"")), user, followInfo))
  }

  /**
   * 密码修改
   */
  def changePassword(userId :String) = StackAction(AuthorityKey -> User.isOwner(userId) _) { implicit request =>
    val loginUser = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
    Users.changePassForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.changePassword(errors, loginUser, followInfo)),
      {
        case (user, main) =>
          User.save(user.copy(password = main), WriteConcern.Safe)
          Redirect(routes.Users.logout)
    })
  }


  /**
   * 用户信息更新
   */
  def updateInfo(userId: String) = StackAction(AuthorityKey -> User.isOwner(userId) _) { implicit request =>
    val loginUser = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
    Users.userForm().bindFromRequest.fold(
      errors => BadRequest(views.html.user.Infomation(errors,followInfo)),
      {
        user =>
          User.save(user.copy(id = loginUser.id), WriteConcern.Safe)
          val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
          Ok(views.html.user.myPageRes(user,followInfo))
      })
  }

  /**
   * 登录用户基本信息
   */
  def myInfo() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val userForm = Users.userForm().fill(user)
    Ok(views.html.user.Infomation(userForm,followInfo))
  }

  /**
   * 其他用户基本信息
   */
  def userInfo(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
    User.findOneByUserId(userId).map{user =>
      val userForm = Users.userForm().fill(user)
      Ok(views.html.user.Infomation(userForm, followInfo))
    }getOrElse{
      NotFound
    }
    
  }

  /**
   * 个人主页
   */
  def myPage() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    if (user.userTyp.equals(User.NORMAL_USER)) {
      Ok(views.html.user.myPageRes(user,followInfo))
    } else if ((user.userTyp).equals(User.STYLIST)) {
      //TODO
      val stylist = Stylist.findOneByStylistId(user.id)
      Ok(views.html.stylist.management.stylistHomePage(user = user, stylist = stylist.get, followInfo = followInfo))
    } else {
      Ok(views.html.user.myPageRes(user,followInfo))
    }
  }

  /**
   * 保存图片
   */
  def saveImg(id :ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _){implicit request =>
    val user = loggedIn
    User.save(user.copy(userPics = id), WriteConcern.Safe)
    Redirect(routes.Users.myPage())
  }
  
  /**
   * 更新图片
   */
  def changeImage = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.changeImg(user,followInfo))
  }
  /**
   * 浏览他人主页
   */
  def userPage(userId : String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    User.findOneByUserId(userId).map{user =>
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      Ok(views.html.user.otherPage(user, followInfo,loginUser.id))
    }getOrElse{
      NotFound
    }
  }

  /**
   * 我的预约
   */
  def myReservation() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.myPageRes(user,followInfo))
  }

  /**
   * 他人收藏的博客
   */
  def userBlog(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    User.findOneByUserId(userId).map{user =>
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      Ok(views.html.user.otherFollowBlog(user, followInfo, loginUser.id))
    }getOrElse{
      NotFound
    }
  }

  /**
   * 他人收藏的风格
   */
  def userStyle(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    User.findOneByUserId(userId).map{user =>
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      Ok(views.html.user.otherFollowStyle(user, followInfo, loginUser.id))
    }getOrElse{
      NotFound
    }
  }

  /**
   * 申请成为技师
   */

  def applyStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val goodAtStylePara = Stylist.findGoodAtStyle
    Ok(views.html.user.applyStylist(stylistApplyForm, user, goodAtStylePara,followInfo))

  }

  /**
   * 店长或店铺管理者确认后才录入数据库
   */

  def commitStylistApply = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
     val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val goodAtStylePara = Stylist.findGoodAtStyle
    stylistApplyForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.applyStylist(errors, user, goodAtStylePara, followInfo)),
      {
        case(stylistApply) => {
          Stylist.save(stylistApply.stylist.copy(stylistId = user.id))
          val applyRecord = new SalonStylistApplyRecord(new ObjectId, stylistApply.salonId, stylistApply.stylist.stylistId, 1, new Date, 0, None)
    	  SalonStylistApplyRecord.save(applyRecord)
          Ok(views.html.user.applyStylist(stylistApplyForm.fill(stylistApply), user, goodAtStylePara, followInfo))
        }
      })
    	 
  }

}
