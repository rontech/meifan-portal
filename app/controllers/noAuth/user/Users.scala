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
import scala.Some
import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import models.portal.common.{DefaultLog, OptContactMethod}
import models.portal.user.{LoggedIn, Permission, User}
import models.portal.salon.Salon

object Users extends MeifanNetCustomerOptionalApplication {

  /**
   * create a user's register form
   * @param id new ObjectId for user
   * @return register form
   */
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
          User(new ObjectId, userId, nickName, BCrypt.hashpw(password._1, BCrypt.gensalt()), "M", None, None,
            DefaultLog.getImgId, None, email, optContactMethods, None, User.NORMAL_USER, User.HIGH, 20, 0,
            (new Date()).getTime, Permission.valueOf(LoggedIn), true)
      } {
        user => Some((user.id, user.userId, (user.password, ""), user.nickName, user.email, user.optContactMethods, false))
      })

  /**
   * Handler guest user's register request
   * */
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
   * Redirect to other user's home page
   * @param userId user's userId
   * @return
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
   * Checks for email,nickName,accountId,phone
   * @param value need to checks value
   * @param key the type of checks ;e.g. ITEM_TYPE_EMAIL
   * @return
   */
  def checkIsExist(value: String, key: String) = StackAction { implicit request =>
    val loggedUser = loggedIn
    key match {
      case ITEM_TYPE_ID =>
        Ok((User.isExist(value, User.findOneByUserId) || Salon.isExist(value, Salon.findOneByAccountId)).toString)
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
