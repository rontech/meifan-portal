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
import utils.Const._
import models.OptContactMethod
import scala.Some
import models.OptContactMethod
import scala.Some

object Users extends Controller with OptionalAuthElement with UserAuthConfigImpl {

  def registerForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> text,
      "password" -> tuple(
        "main" -> text,
        "confirm" -> text),
      "nickName" -> text,
      "email" -> text,
      "optContactMethods" -> seq(
        mapping(
          "contMethodType" -> text,
          "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
      "accept" -> checked("You must accept the conditions")) {
        (id, userId, password, nickName, email, optContactMethods, _) =>
          User(new ObjectId, userId, nickName, BCrypt.hashpw(password._1, BCrypt.gensalt()), "M", None, None, DefaultLog.getImgId, None, email, optContactMethods, None, User.NORMAL_USER, User.HIGH, 20, 0, (new Date()).getTime, Permission.valueOf(LoggedIn), true)
      } {
        user => Some((user.id, user.userId, (user.password, ""), user.nickName, user.email, user.optContactMethods, false))
      })

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
          Redirect(auth.routes.Users.login)
      })
  }

  /**
   * 浏览他人主页
   */
  def userPage(userId: String) = StackAction { implicit request =>
    User.findOneByUserId(userId).map { user =>
      if ((user.userTyp.toUpperCase()).equals("NORMALUSER")) {
        Redirect(controllers.noAuth.routes.Blogs.getAllBlogsOfUser(userId))
      } else {
        Redirect(controllers.noAuth.routes.Stylists.otherHomePage(user.id))
      }
    } getOrElse {
      NotFound
    }
  }

  /**
   * checks for email,nickName,accountId,phone
   */
  def checkIsExist(value: String, key: String) = StackAction { implicit request =>
    val loggedUser = loggedIn
    key match {
      case ITEM_TYPE_ID =>
        Ok((User.isExist(value, User.findOneByUserId) || Salon.isExist(value, Salon.findByAccountId)).toString)
      case ITEM_TYPE_NAME =>
        if (User.isValid(value, loggedUser, User.findOneByNickNm)) {
          Ok((Salon.isExist(value, Salon.findOneBySalonName) || Salon.isExist(value, Salon.findOneBySalonNameAbbr)).toString)
        } else {
          Ok("true")
        }
      case ITEM_TYPE_EMAIL =>
        Ok((!User.isValid(value, loggedUser, User.findOneByEmail)).toString)
      case ITEM_TYPE_TEL =>
        Ok((!User.isValid(value, loggedUser, User.findOneByTel)).toString)
    }
  }
}
