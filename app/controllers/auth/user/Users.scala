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
import org.mindrot.jbcrypt.BCrypt
import controllers._
import play.api.data.validation.Constraints._
import utils.Const._
import play.api.templates.Html
import models.portal.user.{LoggedIn, MyFollow, User}
import models.portal.common.{OnUsePicture, OptContactMethod, Address}
import models.portal.stylist.{Stylist, StylistApply, GoodAtStyle}
import models.portal.industry.IndustryAndPosition
import models.portal.salon.Salon
import models.portal.relation.SalonStylistApplyRecord
import com.meifannet.portal.MeifanNetCustomerApplication
import models.portal.reservation.Reservation

object Users extends MeifanNetCustomerApplication {

  //login form for user
  val loginForm = Form(mapping(
    "userId" -> text,
    "password" -> text)(User.authenticate)(_.map(u => (u.userId, "")))
    .verifying(Messages("user.loginErr"), result => result.isDefined))

  //change password form for user
  val changePassForm = Form(
    mapping(
      "user" -> mapping(
        "userId" -> text,
        "oldPassword" -> text)(User.authenticate)(_.map(u => (u.userId, ""))).verifying("Invalid OldPassword", result => result.isDefined),
      "newPassword" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]{6,16}+$""")),
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)) { (user, newPassword) => (user.get, BCrypt.hashpw(newPassword._1, BCrypt.gensalt())) } { user => Some((Option(user._1), ("", ""))) })

  /**
   * create a message form for user by ObjectId
   * @param id the ObjectId of user's record
   * @return message form for user
   */
  def userForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> text,
      "nickName" -> text,
      "password" -> text,
      "sex" -> text,
      "birthDay" -> optional(date),
      "address" -> optional(mapping(
        "province" -> text,
        "city" -> optional(text),
        "region" -> optional(text)) {
          (province, city, region) => Address(province, city, region, None, "NO NEED", None, None, "No NEED")
        } {
          address => Some((address.province, address.city, address.region))
        }),
      "userPics" -> text,
      "tel" -> optional(text),
      "email" -> text,
      "optContactMethods" -> seq(
        mapping(
          "contMethodType" -> text,
          "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
      "socialScene" -> optional(text),
      "registerTime" -> longNumber,
      "userTyp" -> text,
      "userBehaviorLevel" -> text,
      "point" -> number,
      "activity" -> number,
      "permission" -> text) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (id, userId, nickName, password, sex, birthDay, address, userPics, tel, email, optContactMethods, socialScene, registerTime, userTyp, userBehaviorLevel, point, activity, permission) => User(id, userId, nickName, password, sex, birthDay, address, new ObjectId(userPics), tel, email, optContactMethods, socialScene, userTyp, userBehaviorLevel, point, activity, registerTime, permission, true)
      } // Unbinding: Create the mapping values from an existing Hacker value
      {
        user =>
          Some((user.id, user.userId, user.nickName, user.password, user.sex, user.birthDay, user.address, user.userPics.toString, user.tel, user.email, user.optContactMethods, user.socialScene, user.registerTime,
            user.userTyp, user.userBehaviorLevel, user.point, user.activity, user.permission))
      })

  /**
   * 用户申请技师第一步表单（申请某沙龙技师）
   * @return
   */
  def applyForSalonForm = Form(
    "salonAccountId" -> text
  )

  /** 用户申请技师用表单
   *
   * @return
   */
  def stylistApplyForm: Form[StylistApply] = Form(
    mapping("stylist" ->
      mapping(
        "workYears" -> number,
        "position" -> list(
          mapping(
            "positionName" -> text,
            "industryName" -> text) {
              (positionName, industryName) => IndustryAndPosition(new ObjectId, positionName, industryName)
            } {
              industryAndPosition => Some(industryAndPosition.positionName, industryAndPosition.industryName)
            }),
        "goodAtImage" -> list(text),
        "goodAtStatus" -> list(text),
        "goodAtService" -> list(text),
        "goodAtUser" -> list(text),
        "goodAtAgeGroup" -> list(text),
        "myWords" -> text,
        "mySpecial" -> text,
        "myBoom" -> text,
        "myPR" -> text) {
          (workYears, position, goodAtImage, goodAtStatus, goodAtService,
          goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR) =>
            Stylist(new ObjectId, new ObjectId(), workYears, position, goodAtImage, goodAtStatus,
              goodAtService, goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR,
              List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), false, false)
        } {
          stylist =>
            Some(stylist.workYears, stylist.position,
              stylist.goodAtImage, stylist.goodAtStatus, stylist.goodAtService, stylist.goodAtUser,
              stylist.goodAtAgeGroup, stylist.myWords, stylist.mySpecial, stylist.myBoom, stylist.myPR)
        },
      "salonAccountId" -> text) {
        (stylist, salonAccountId) => StylistApply(stylist, salonAccountId)
      } {
        stylistApply => Some((stylistApply.stylist, stylistApply.salonAccountId))
      })

  /**
   * login for user
   * @return add user to session
   */
  def login = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => { Future.successful(BadRequest(views.html.user.login(formWithErrors))) },
      user => gotoLoginSucceeded(user.get.userId))
  }

  /**
   * Logout for user
   * @return remove logged user from session
   */

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"))
  }

  /**
   * Redirect to change password's view with logged user's followed info
   * @return
   */
  def password = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.changePassword(Users.changePassForm.fill((user, "")), user, followInfo))
  }

  /**
   * Handler the request of user's change password
   * @param userId logged user's userId
   * @return Redirect to user's logout
   */
  def changePassword(userId: String) = StackAction(AuthorityKey -> User.isOwner(userId) _) { implicit request =>
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
   * Handler the request of user's update information
   * @param userId logged user's userId
   * @return
   */
  def updateInfo(userId: String) = StackAction(AuthorityKey -> User.isOwner(userId) _) { implicit request =>
    val loginUser = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
    Users.userForm().bindFromRequest.fold(
      //errors => BadRequest(views.html.user.Infomation(errors, loginUser, followInfo)),
      errors => BadRequest(Html(errors.toString)),
      {
        user =>
          User.save(user.copy(id = loginUser.id), WriteConcern.Safe)
          val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
          Ok(views.html.user.myPageRes(user, followInfo))
      })
  }

  /**
   * Show my information
   * @return
   */
  def myInfo() = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val userForm = Users.userForm().fill(user)
    Ok(views.html.user.Infomation(userForm, user, followInfo))
  }

  /**
   * Show other user's information
   * @param userId user's userId
   * @return
   */
  //TODO
  def userInfo(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val loginUser = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(loginUser.id)
    User.findOneByUserId(userId).map { user =>
      val userForm = Users.userForm().fill(user)
      Ok(views.html.user.Infomation(userForm, user, followInfo))
    } getOrElse {
      NotFound
    }

  }

  /**
   * Redirect to logged user's home page
   * @return
   */
  def myPage() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val reservingList = Reservation.findResving(user.userId)
    User.findOneByUserId(user.userId).map { user =>
      if ((user.userTyp.toUpperCase()).equals("NORMALUSER")) {
//        Ok(views.html.user.myPageRes(user, followInfo))
        Ok(views.html.user.myReserving(user, followInfo, reservingList))
      } else {
        Redirect(controllers.auth.routes.Stylists.myHomePage)
      }
    } getOrElse {
      NotFound
    }

  }

  /**
   * Save the ObjectId of user's logo in user's Information
   * @param id the ObjectId of user's logo in mongodb
   * @return
   */
  def saveImg(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    User.save(user.copy(userPics = id), WriteConcern.Safe)
    Redirect(routes.Users.myPage())
  }

  /**
   * Redirect to the view of change logo
   * @return
   */
  def changeImage = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.changeImg(user, followInfo))
  }

  /**
   * Show my reservation
   * @return
   */
  def myReservation() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val reservingList = Reservation.findResving(user.userId)
    Ok(views.html.user.myReserving(user, followInfo, reservingList))
//    Ok(views.html.user.myPageRes(user, followInfo))
  }

  /**
   *
   * @return
   */
  def selectSalonIdForApply = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.selectSalonForApply(user, followInfo))
  }

  /**
   *
   * @return
   */
  def getSalonIdForApply = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    applyForSalonForm.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        applyForSalon =>
          Redirect(controllers.auth.routes.Users.applyStylist).withSession("salonAccountId" -> applyForSalon)
      }
    )
  }

  /**
   * Redirect to applying for stylist page
   * @return
   */
  def applyStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    var goodAtStylePara = GoodAtStyle(Nil, Nil, Nil, Nil, Nil, Nil, Nil)
    session.get("salonAccountId") match {
      case Some(salonAccountId) => {
        Salon.findOneByAccountId(salonAccountId).map { salon =>
          goodAtStylePara = Stylist.findGoodAtStyle(salon.salonIndustry.head)
          // 跳转画面且清除session中salonAccountId
          Ok(views.html.user.applyStylist(stylistApplyForm, user, goodAtStylePara, followInfo, salonAccountId)).withSession(session - "salonAccountId")
        }
      } getOrElse {
        Ok(views.html.user.selectSalonForApply(user, followInfo))
      }
      case None => Ok(views.html.user.selectSalonForApply(user, followInfo))
    }
  }

  /**
   * Handler the request of user's applying stylist
   * @return
   */
  def commitStylistApply = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val goodAtStylePara = Stylist.findGoodAtStyle("Hairdressing")
    stylistApplyForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.applyStylist(errors, user, goodAtStylePara, followInfo, "")),
      {
        case (stylistApply) => {
          Stylist.save(stylistApply.stylist.copy(stylistId = user.id))
          Stylist.updateImages(stylistApply.stylist, user.userPics)
          Salon.findOneByAccountId(stylistApply.salonAccountId).map { salon =>
            val applyRecord = new SalonStylistApplyRecord(new ObjectId, salon.id, user.id, 1, new Date, 0, None)
            SalonStylistApplyRecord.save(applyRecord)
            Redirect(controllers.auth.routes.Stylists.updateStylistImage("user"))
          } getOrElse {
            NotFound
          }
        }
      }
    )
  }

  /**
   * Cancel the apply for stylist of logged user
   * @return
   */
  def cancelApplyStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    SalonStylistApplyRecord.findOneStylistApRd(user.id).map { record =>
      SalonStylistApplyRecord.save(record.copy(verifiedResult = 2, verifiedDate = Some(new Date)))
      Redirect(routes.Users.myPage())
    } getOrElse {
      NotFound
    }
  }

}
