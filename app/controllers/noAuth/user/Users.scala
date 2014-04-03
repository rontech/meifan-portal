package controllers.noAuth

import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent._
import play.api.i18n.Messages
import jp.t2v.lab.play2.auth._
import controllers.AuthConfigImpl

object Users extends Controller with OptionalAuthElement with AuthConfigImpl{

  def registerForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6, 16).verifying(Messages("user.userIdErr"), userId => userId.matches("""^\w+$""")),
      "password" -> tuple(
        "main" -> text(6, 18).verifying(Messages("user.passwordError"), main => main.matches("""^[A-Za-z0-9]+$""")),
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
   * 其他用户会员信息
   */
  //TODO
  /*def userInfo(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
    User.findOneByUserId(userId).map{user =>
      val userForm = Users.userForm().fill(user)
      Ok(views.html.user.Infomation(userForm, followInfo))
    }getOrElse{
      NotFound
    }
  }*/

  /**
   * 浏览他人主页
   */
  def userPage(userId : String) = StackAction{ implicit request =>
   /* User.findOneByUserId(userId).map{user =>
      val userFollowInfo = MyFollow.getAllFollowInfo(user.id)
      loggedIn.map{loggedUser =>
         Ok(views.html.user.otherPage(user, userFollowInfo, loggedUser.id, true))
      }getOrElse{
    	 Ok(views.html.user.otherPage(user, userFollowInfo))
      }
    }getOrElse{
      NotFound
    }*/
      Redirect(controllers.noAuth.routes.Blogs.showBlog(userId))
  }
}
