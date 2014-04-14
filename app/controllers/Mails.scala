package controllers

import play.api.mvc._
import com.typesafe.plugin._
import play.api.Play.current
import models._
import play.api.data.Form
import play.api.data.Forms._
import org.mindrot.jbcrypt.BCrypt
import controllers._
import play.Configuration
import play.api.i18n.Messages
import com.mongodb.casbah.WriteConcern
import play.api.templates.Html
import java.util.UUID

object Mails extends Controller {
  
//  val resetPassForm = Form(
//    mapping(
//      "newPassword" -> tuple(
//        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]+$""")),
//        "confirm" -> text).verifying(
//        // Add an additional constraint: both passwords must match
//        Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)
//    ){(newPassword) => (BCrypt.hashpw(newPassword._1, BCrypt.gensalt()))}{user => Some((Option(user._1),("","")))}
//  )
  
  val resetPassForm = Form(
    mapping(
      "user" ->mapping(
        "userId" -> text)(User.findOneByUserId)(_.map(u => (u.userId))),
      "newPassword" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]+$""")),
        "confirm" -> text).verifying(
        // Add an additional constraint: both passwords must match
        Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)
    ){(user, newPassword) => (user.get, BCrypt.hashpw(newPassword._1, BCrypt.gensalt()))}{user => Some((Option(user._1),("","")))}
  )
  
//  val mailAndUserIdForm = Form(tuple(
//	"userId" -> text,
//    "email" -> text
//  ))
  
  val mailAndUserIdForm = Form(mapping(
	"userId" -> text,
    "email" -> text
  )(User.findOneByUserIdAndEmail)(_.map(u => (u.userId, u.email)))
    .verifying(Messages("user.resetErr"), result => result.isDefined))
  
  
  def resetPassword = Action { implicit request =>
	  
	  Mails.mailAndUserIdForm.bindFromRequest.fold(
	    errors => BadRequest(views.html.user.resetPassword(errors)),
//	      errors => BadRequest(Html(errors.toString)),
      {
//        case (userId, email) =>
	      user =>
         	
          val root : Configuration = Configuration.root()
          val mail = use[MailerPlugin].email
		  mail.setSubject("reset password")
		  mail.setRecipient("<"+ user.get.email + ">")
		  val mailFrom : String = root.getString("mail.from")
		  mail.setFrom(mailFrom)
		  val uuid : String = UUID.randomUUID().toString()
//		  val url : String = "http://" + root.getString("server.hostname") +routes.Mails.resetForNew(user.get.userId)
		  val url : String = "http://" + root.getString("server.hostname") +routes.Mails.resetForNew(user.get.userId, uuid)
		  mail.send("A text only message", "<html><body><p><a href = " + url + ">" + uuid+ "</a></p></body></html>" )
          Redirect(auth.routes.Users.logout)
		  
    })
	}
  
  /**
   * 跳转至密码重置的页面，输入邮箱和用户名
   */
  def reset = Action { implicit request =>
    Ok(views.html.user.resetPassword(mailAndUserIdForm))
  }
  
  /**
   * 跳转至密码重置的页面，新密码和确认密码
   */
  def resetForNew(userId : String, uuid: String) = Action { implicit request =>
    val user = User.findOneByUserId(userId).get
    Ok(views.html.user.resetPasswordForNew(resetPassForm.fill((user,"")),userId))
  }
  
    /**
   * 密码修改
   */
  def resetPasswordForNew(userId :String) = Action { implicit request =>
    Mails.resetPassForm.bindFromRequest.fold(
//      errors => BadRequest(views.html.user.resetPasswordForNew(errors,userId)),
        errors => BadRequest(Html(errors.toString)),
      {
        case (user, main) =>
          User.save(user.copy(password = main), WriteConcern.Safe)
          Redirect(auth.routes.Users.logout)
    })
  }
}