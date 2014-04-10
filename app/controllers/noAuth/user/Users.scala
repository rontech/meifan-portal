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
import org.mindrot.jbcrypt.BCrypt
import controllers._

object Users extends Controller with OptionalAuthElement with UserAuthConfigImpl{

  def registerForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6, 16).verifying(Messages("user.userIdErr"), userId => userId.matches("""^\w+$""")),
      "password" -> tuple(
        "main" -> text(6, 18).verifying(Messages("user.passwordError"), main => main.matches("""^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]+$""")),
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
            Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2),
      "nickName" -> nonEmptyText,
      "email" -> email,
      "optContactMethods" -> seq(
        mapping(
          "contMethodType" -> text,
          "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply))){
          (id, userId, password, nickName, email, optContactMethods) =>
          User(new ObjectId, userId, nickName, BCrypt.hashpw(password._1, BCrypt.gensalt()), "M", None, None, new ObjectId,None, email, optContactMethods, None, User.NORMAL_USER,  User.HIGH, 20, 0, new Date(), Permission.valueOf(LoggedIn), true)
      } {
        user => Some((user.id, user.userId, (user.password, ""), user.nickName, user.email, user.optContactMethods))
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
   * 浏览他人主页
   */
  def userPage(userId : String) = StackAction{ implicit request =>
      User.findOneByUserId(userId).map{ user =>
        if((user.userTyp.toUpperCase()).equals("NORMALUSER")) {
          Redirect(controllers.noAuth.routes.Blogs.getAllBlogsOfUser(userId))
        } else {
          Redirect(controllers.noAuth.routes.Stylists.otherHomePage(user.id))
        }
      }getOrElse{
         NotFound
      }
      
      
  }
}
