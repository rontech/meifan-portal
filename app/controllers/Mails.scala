/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
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

/**
 * this object is to reset password of user and salon by sending mail
 *
 * person will get a mail included a link when he passes the verification
 * each link just can reset password one time successfully and works in 30 minutes
 */
object Mails extends Controller {

  /**
   * 用户重置密码form
   */
  val resetPassForm = Form(
    mapping(
      "user" -> mapping(
        "userId" -> text)(User.findOneByUserId)(_.map(u => (u.userId))),
      "newPassword" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]{6,16}+$""")),
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)) { (user, newPassword) => (user.get, BCrypt.hashpw(newPassword._1, BCrypt.gensalt())) } { user => Some((Option(user._1), ("", ""))) })

  /**
   * salon重置密码form
   */
  val resetPassOfSalonForm = Form(
    mapping(
      "salonChange" -> mapping(
        "accountId" -> text)(Salon.findByAccountId)(_.map(s => (s.salonAccount.accountId))),
      "newPassword" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]{6,16}+$""")),
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)) { (salonChange, newPassword) => (salonChange.get, BCrypt.hashpw(newPassword._1, BCrypt.gensalt())) } { salonChange => Some((Option(salonChange._1), ("", ""))) })

  /**
   * form to input userId and email when register for user
   */
  val mailAndUserIdForm = Form(mapping(
    "userId" -> nonEmptyText,
    "email" -> text //TODO 正则表达式
    )(User.findOneByUserIdAndEmail)(_.map(u => (u.userId, u.email)))
    .verifying(Messages("user.resetErr"), result => result.isDefined))

  //店铺登录Form
  val mailAndAccountIdForm = Form(mapping(
    "accountId" -> nonEmptyText,
    "email" -> text //TODO 正则表达式
    )(Salon.findOneByAccountIdAndEmail)(_.map(salon => (salon.salonAccount.accountId, salon.contactMethod.email))).verifying(Messages("user.resetErr"), result => result.isDefined)) // TODO error

  /**
   * 跳转至密码重置的页面，输入用户的邮箱和用户名
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
          val root: Configuration = Configuration.root()
          val mail = use[MailerPlugin].email
          mail.setSubject(Messages("mail.subjectOfResetPwd"))
          mail.setRecipient("<" + user.get.email + ">")
          val mailFrom: String = root.getString("mail.from")
          mail.setFrom(mailFrom)
          val uuid: String = UUID.randomUUID().toString()
          val mailUser = Mail.findOneByObjId(user.get.id)
          val endTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000); //30分钟后过期
          mailUser match {
            case Some(m) => Mail.save(m.copy(uuid = uuid, endTime = endTime), WriteConcern.Safe)
            case None => Mail.save(uuid, user.get.id, 1, endTime) // 1代表用户找回密码
          }
          //          val url : String = "http://" + root.getString("server.hostname") +routes.Mails.password(uuid)  //OK
          // 会不会到时url是以Https开头的呢？
          val url: String = "http://" + request.host + routes.Mails.password(uuid)
          mail.send("A text only message", "<html><body><p>" + Messages("user.resetInfo") + "<br><a href = " + url + ">" + url + "</a>" + "<br>" + Messages("mail.limitInfo") + "</p></body></html>")
          Ok(views.html.user.checkMail())
        //Redirect(auth.routes.Users.logout)

      })
  }

  /**
   * 跳转至密码重置的页面，输入店铺的邮箱和用户名
   */
  def forgotPasswordOfSalon = Action { implicit request =>
    Ok(views.html.salon.sendMailForResetPwdOfSalon(mailAndAccountIdForm))
  }

  /**
   * 发送邮件给salon
   */
  // 代码整合, 方法重构 // TODO
  def sendMailForResetPwdOfSalon = Action { implicit request =>

    Mails.mailAndAccountIdForm.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.sendMailForResetPwdOfSalon(errors)),
      {
        salon =>
          val root: Configuration = Configuration.root()
          val mail = use[MailerPlugin].email
          mail.setSubject(Messages("mail.subjectOfResetPwd"))
          mail.setRecipient("<" + salon.get.contactMethod.email + ">")
          val mailFrom: String = root.getString("mail.from")
          mail.setFrom(mailFrom)
          val uuid: String = UUID.randomUUID().toString()
          val mailSalon = Mail.findOneByObjId(salon.get.id)
          val endTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000); //30分钟后过期
          mailSalon match {
            case Some(m) => Mail.save(m.copy(uuid = uuid, endTime = endTime), WriteConcern.Safe)
            case None => Mail.save(uuid, salon.get.id, 2, endTime) // 2代表salon找回密码
          }
          //          val url : String = "http://" + root.getString("server.hostname") +routes.Mails.password(uuid)  //OK
          // 会不会到时url是以Https开头的呢？
          val url: String = "http://" + request.host + routes.Mails.passwordOfSalon(uuid)
          //          mail.send("A text only message", "<html><body><p>" + Messages("user.resetInfo")+ "<br><a href = " + url + ">" + url+ "</a></p></body></html>" )
          mail.send("A text only message", "<html><body><p>" + Messages("user.resetInfo") + "<br><a href = " + url + ">" + url + "</a>" + "<br>" + Messages("mail.limitInfo") + "</p></body></html>")
          //          Redirect(auth.routes.Salons.salonLogout)
          Ok(views.html.user.checkMail())

      })
  }

  /**
   * 跳转至密码重置的页面，新密码和确认密码
   *
   * @param uuid
   * @return
   */
  def password(uuid: String) = Action { implicit request =>
    // 这边需要判断一下，是用户还是店铺，暂时是用户
    val mail = Mail.findOneByUuid(uuid)
    mail match {
      case Some(m) => {
        if (m.endTime.before(new Date())) {
          NotFound
        } else {
          val user = User.findOneById(m.objId).get
          Ok(views.html.user.resetPassword(resetPassForm.fill((user, "")), user.userId, m.uuid))
        }
      }
      case None => NotFound
    }

  }

  /**
   * 密码修改
   *
   * @param uuid
   * @return
   */
  def resetPassword(uuid: String) = Action { implicit request =>
    val newUuid: String = UUID.randomUUID().toString()
    val mail = Mail.findOneByUuid(uuid).get
    val user = User.findOneById(mail.objId).get
    Mails.resetPassForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.resetPassword(errors, user.userId, uuid)),
      //          errors => BadRequest(views.html.user.resetFwdResult(false, errors.toString())),
      {
        case (user, main) =>
          // 一个url只能修改一次密码
          val mail = Mail.findOneByUuid(uuid).get
          // 当密码修改成功后，原来mail中的那条数据的uuid会更新一下，确保一条链接只能成功修改一次密码
          Mail.save(mail.copy(uuid = newUuid), WriteConcern.Safe)

          User.save(user.copy(password = main), WriteConcern.Safe)
          Ok(views.html.user.resetPwdResult(true, ""))
        //Redirect(auth.routes.Users.logout)
      })
  }

  /**
   * 跳转至密码重置的页面，新密码和确认密码
   *
   * @param uuid
   * @return
   */
  def passwordOfSalon(uuid: String) = Action { implicit request =>
    // 这边需要判断一下，是用户还是店铺，暂时是用户
    val mail = Mail.findOneByUuid(uuid)
    mail match {
      case Some(m) => {
        if (m.endTime.before(new Date())) {
          NotFound
        } else {
          val salon = Salon.findOneById(m.objId).get
          Ok(views.html.salon.resetPasswordOfSalon(resetPassOfSalonForm.fill((salon, "")), salon.salonAccount.accountId, m.uuid))
        }
      }
      case None => NotFound
    }

  }

  /**
   * 沙龙密码修改
   *
   * @param uuid
   * @return
   */
  def resetPasswordOfSalon(uuid: String) = Action { implicit request =>
    val newUuid: String = UUID.randomUUID().toString()
    val mail = Mail.findOneByUuid(uuid).get
    val salon = Salon.findOneById(mail.objId).get
    Mails.resetPassOfSalonForm.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.resetPasswordOfSalon(errors, salon.salonAccount.accountId, uuid)),
      {
        case (salon, main) =>
          // 一个url只能修改一次密码
          val mail = Mail.findOneByUuid(uuid).get
          // 当密码修改成功后，原来mail中的那条数据的uuid会更新一下，确保一条链接只能成功修改一次密码
          Mail.save(mail.copy(uuid = newUuid), WriteConcern.Safe)

          Salon.save(salon.copy(salonAccount = new SalonAccount(salon.salonAccount.accountId, main)), WriteConcern.Safe)
          //          Redirect(routes.Application.salonLogin)
          Ok(views.html.salon.resetPwdResultOfSalon(true, ""))
      })
  }
}
