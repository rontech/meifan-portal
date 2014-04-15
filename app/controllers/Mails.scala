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
import java.util.Date

object Mails extends Controller {
    
  val resetPassForm = Form(
    mapping(
      "user" ->mapping(
        "userId" -> text)(User.findOneByUserId)(_.map(u => (u.userId))),
      "newPassword" -> tuple(
        "main" -> text(6,18).verifying(Messages("user.passwordError"), main => main.matches("""^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]+$""")),
        "confirm" -> text).verifying(
        // Add an additional constraint: both passwords must match
        Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)
    ){(user, newPassword) => (user.get, BCrypt.hashpw(newPassword._1, BCrypt.gensalt()))}{user => Some((Option(user._1),("","")))}
  )
  
  val mailAndUserIdForm = Form(mapping(
	"userId" -> nonEmptyText,
    "email" -> nonEmptyText
  )(User.findOneByUserIdAndEmail)(_.map(u => (u.userId, u.email)))
    .verifying(Messages("user.resetErr"), result => result.isDefined))
  
  /**
   * 跳转至密码重置的页面，输入邮箱和用户名
   */
  def forgotPassword = Action { implicit request =>
    Ok(views.html.user.sendMailForResetPwd(mailAndUserIdForm))
  }
  
  /**
   * 发送邮件
   */
  def sendMailForResetPwd = Action { implicit request =>
	  
	  Mails.mailAndUserIdForm.bindFromRequest.fold(
	    errors => BadRequest(views.html.user.sendMailForResetPwd(errors)),
      {
	      user =>        	
          val root : Configuration = Configuration.root()
          val mail = use[MailerPlugin].email
		  mail.setSubject("reset password")
		  mail.setRecipient("<"+ user.get.email + ">")
		  val mailFrom : String = root.getString("mail.from")
		  mail.setFrom(mailFrom)
		  val uuid : String = UUID.randomUUID().toString()
		  // 1 代表用户重置密码
		  val mailUser = Mail.findOneByObjId(user.get.id)
		  val endTime = new Date(System.currentTimeMillis()+30*60*1000);//30分钟后过期
		  mailUser match {
            case Some(m) => Mail.save(m.copy(uuid = uuid, endTime = endTime), WriteConcern.Safe)
            case None => Mail.save(uuid, user.get.id, 1, endTime)
          }
		  val url : String = "http://" + root.getString("server.hostname") +routes.Mails.password(uuid)
		  mail.send("A text only message", "<html><body><p>" + Messages("user.resetInfo")+ "<br><a href = " + url + ">" + uuid+ "</a></p></body></html>" )
          Redirect(auth.routes.Users.logout)
		  
    })
	}
  

  
  /**
   * 跳转至密码重置的页面，新密码和确认密码
   */
  def password(uuid: String) = Action { implicit request =>
    // 这边需要判断一下，是用户还是店铺，暂时是用户
    val mail = Mail.findOneByUuid(uuid)
    mail match {
      case Some(m) =>{
        if(m.endTime.before(new Date())){
          NotFound
        }else{
	        val user = User.findOneById(m.objId).get
	        Ok(views.html.user.resetPassword(resetPassForm.fill((user,"")),user.userId,m.uuid))
        }
      }
      case None => NotFound
    }
    
  }
  
  /**
   * 密码修改
   */
  def resetPassword(uuid : String) = Action { implicit request =>
    val newUuid : String = UUID.randomUUID().toString()
    val mail = Mail.findOneByUuid(uuid).get
    val user = User.findOneById(mail.objId).get
    Mails.resetPassForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.resetPassword(errors,user.userId, uuid)),
      {
        case (user, main) =>
          // 一个url只能修改一次密码
          val mail = Mail.findOneByUuid(uuid).get
          Mail.save(mail.copy(uuid = newUuid), WriteConcern.Safe)
          
          User.save(user.copy(password = main), WriteConcern.Safe)
          Redirect(auth.routes.Users.logout)
    })
  }
}