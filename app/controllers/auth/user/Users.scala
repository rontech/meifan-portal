package controllers.auth

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
import play.api.i18n.Messages
import controllers.AuthConfigImpl
import org.mindrot.jbcrypt.BCrypt

object Users extends Controller with LoginLogout with AuthElement with AuthConfigImpl {

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
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[A-Za-z0-9]+$""")),
        "confirm" -> text).verifying(
        // Add an additional constraint: both passwords must match
        Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)
    ){(user, newPassword) => (user.get, BCrypt.hashpw(newPassword._1, BCrypt.gensalt()))}{user => Some((Option(user._1),("","")))}
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
		    "salonAccountId" -> text
		    ){
		      (stylist, salonAccountId) => StylistApply(stylist, salonAccountId)
		    }{
		      stylistApply => Some((stylistApply.stylist, stylistApply.salonAccountId))
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
      errors => BadRequest(views.html.user.Infomation(errors, loginUser, followInfo)),
      {
        user =>
          User.save(user.copy(id = loginUser.id), WriteConcern.Safe)
          val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
          Ok(views.html.user.myPageRes(user, followInfo))
      })
  }

  /**
   * 登录用户基本信息
   */
  def myInfo() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val userForm = Users.userForm().fill(user)
    Ok(views.html.user.Infomation(userForm, user, followInfo))
  }

  /**
   * 其他用户基本信息
   */
  def userInfo(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
    User.findOneByUserId(userId).map{user =>
      val userForm = Users.userForm().fill(user)
      Ok(views.html.user.Infomation(userForm, user, followInfo))
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
    User.findOneByUserId(user.userId).map{ user =>
        if((user.userTyp.toUpperCase()).equals("NORMALUSER")) {
          Ok(views.html.user.myPageRes(user,followInfo))
        } else {
          Redirect(controllers.auth.routes.Stylists.myHomePage)
        }
      }getOrElse{
         NotFound
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
   * 我的预约
   */
  def myReservation() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.myPageRes(user,followInfo))
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
          Salon.findByAccountId(stylistApply.salonAccountId).map{salon=>
            val applyRecord = new SalonStylistApplyRecord(new ObjectId, salon.id, user.id, 1, new Date, 0, None)
            SalonStylistApplyRecord.save(applyRecord)
            Ok(views.html.user.applyStylist(stylistApplyForm.fill(stylistApply), user, goodAtStylePara, followInfo))
          }getOrElse{
        	  NotFound
          }
        }
      })
    	 
  }

}
