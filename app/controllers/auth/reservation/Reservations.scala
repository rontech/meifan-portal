/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 * [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 * All Rights Reserved.
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

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import java.util.Calendar
import java.util.Date
import com.mongodb.casbah.commons.Imports._
import jp.t2v.lab.play2.auth._
import scala.concurrent.ExecutionContext.Implicits.global
import controllers._

import models._
import java.text.SimpleDateFormat
import play.cache.Cache
import models.portal.reservation._
import com.meifannet.portal.MeifanNetCustomerApplication
import models.portal.salon.Salon
import models.portal.relation.SalonAndStylist
import models.portal.service.ServiceType
import com.mongodb.casbah.commons.ValidBSONType.ObjectId
import com.mongodb.casbah.commons.TypeImports.ObjectId
import models.portal.user.{MyFollow, User}

object Reservations extends MeifanNetCustomerApplication {

  /**
   * 预约信息确认form
   */
  def reservationForm: Form[Reservation] = Form {
    mapping(
      "userPhone" -> text,
      "userLeaveMsg" -> text,
      "confirmFlg" -> optional(text)) {
      (userPhone, userLeaveMsg, _) => Reservation(new ObjectId(), "", new ObjectId(), "",0, new Date(), 0, None, Nil, None, userPhone, userLeaveMsg,
        BigDecimal(0), 0, BigDecimal(0), new Date(), new Date())
    } {
      reservation => Some((reservation.userPhone, reservation.userLeaveMsg, None))
    }
  }

  /**
   *
   * @return
   */
  def HandleResvFrom: Form[HandleReservation] = Form {
    mapping(
      "handleType" -> text,
      "reservs" -> list(
        mapping(
          "resvId" -> text
        ){(resvId) => Reservation(new ObjectId(resvId), "", new ObjectId, "", 0, new Date, 0, None, Nil, None, "", "", BigDecimal(0), 0, BigDecimal(0), new Date, new Date)}
        {reservation => Some((reservation.id.toString))}
      )
    )(HandleReservation.apply)(HandleReservation.unapply)
  }


  /**
   * 预约最后一步，跳出完成预约的画面
   */
  def reservFinish = StackAction(AuthorityKey -> isLoggedIn _) {
    implicit request =>
      val user = loggedIn
      var reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)
      reservation = reservation.copy(createDate = new Date, processDate = new Date)
      Cache.set("reservation", reservation)
      val salon: Option[Salon] = Salon.findOneById(reservation.salonId)

      // 检查该用户是否已经在该时间预约
      var userHasResvFlg: Boolean = Reservation.findReservByDateAndUserId(reservation.expectedDate, reservation.userId)
      if (userHasResvFlg) {
        salon match {
          case Some(s) => Ok(views.html.reservation.reservFail(s, reservation, "userHasResv", Some(user)))
          case None => NotFound
        }
      } else {
        // 检查店铺或技师在该时间是否已被预约
        reservation.stylistId match {
          case Some(sty) => {
            var stylistHasResvedFlg: Boolean = Reservation.findReservByDateAndStylist(reservation.expectedDate, sty)
            if (stylistHasResvedFlg) {
              salon match {
                case Some(s) => Ok(views.html.reservation.reservFail(s, reservation, "stylistHasResved", Some(user)))
                case None => NotFound
              }
            } else {
              Redirect(controllers.auth.routes.Reservations.reservSuccess)
            }
          }
          case None => {
            // 查看该店铺已被预约的个数
            var salonHasResvedCount: Long = Reservation.findResvByDateAndSalon(reservation.salonId, reservation.expectedDate)
            // 查看该店铺中技师的个数
            val stylistNum: Long = SalonAndStylist.countStylistBySalon(reservation.salonId)
            if (salonHasResvedCount >= stylistNum) {
              salon match {
                case Some(s) => Ok(views.html.reservation.reservFail(s, reservation, "salonHasResved", Some(user)))
                case None => NotFound
              }
            } else {
              Redirect(controllers.auth.routes.Reservations.reservSuccess)
            }
          }
        }
      }
  }

  /**
   * 如果符合条件那么将该信息存储到预约表中并跳转到预约成功画面
   */
  def reservSuccess = StackAction(AuthorityKey -> isLoggedIn _) {
    implicit request =>
      val user = loggedIn

      var reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)
      // 将预约存入数据库
      Reservation.save(reservation)
      // 根据预约的所填写的手机号码，查看是否需要更新会员中的手机号码
      User.updateUserPhone(reservation)

      val salon: Option[Salon] = Salon.findOneById(reservation.salonId)
      salon match {
        case Some(s) => Ok(views.html.reservation.reservFinish(s, Some(user)))
        case None => NotFound
      }
  }

  /**
   * 将指名的发型加入到预约表中
   * @param styleId 发型id
   */
  def selectResvStyle(styleId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) {
    implicit request =>
      val user = loggedIn
      var reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)

      reservation = reservation.copy(styleId = Some(styleId))
      Cache.set("reservation", reservation)

      // 跳转到编辑预约信息方法中
      Redirect(controllers.auth.routes.Reservations.editReservInfo)
  }

  /**
   * 将预约时间和技师以及发型存入cache中，并且跳转到信息输入画面
   * 用于从预约发型路径进入的情况
   * @param resvDate 预约时间
   * @param stylistId 预约的技师
   * @param styleId 预约的发型
   * @return
   */
  def editResvInfoFromStyle(resvDate: String, stylistId: ObjectId, styleId: String) = StackAction(AuthorityKey -> isLoggedIn _) {
    implicit request =>
      val user = loggedIn
      var reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)
      reservation = reservation.copy(stylistId = Some(stylistId))
      if(styleId != "") {
        reservation = reservation.copy(styleId = Some(new ObjectId(styleId)))
      } else {
        reservation = reservation.copy(styleId = None)
      }
      if(resvDate != "") {
        // 将String类型的格式转化为Date
        var expectedDate: Date = new SimpleDateFormat("yyyyMMddHHmm").parse(resvDate)
        reservation = reservation.copy(expectedDate = expectedDate)
      }
      Cache.set("reservation", reservation)

      // 跳转到编辑预约信息方法中
      Redirect(controllers.auth.routes.Reservations.editReservInfo)

  }

  /**
   * 预约时填写预约相关信息（联系电话，注意事项等）
   */
  def editReservInfo = StackAction(AuthorityKey -> isLoggedIn _) {
    implicit request =>
      val user = loggedIn
      var reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)
      var userInfo: Option[User] = User.findOneById(user.id)
      userInfo match {
        case Some(u) => {
          reservation = reservation.copy(userId = u.userId)
          u.tel.map {
            t =>
              reservation = reservation.copy(userPhone = t)
          } getOrElse {
            NotFound
          }
          Cache.set("reservation", reservation)
          val salon: Option[Salon] = Salon.findOneById(reservation.salonId)
          salon match {
            case Some(s) => Ok(views.html.reservation.editReservInfo(s, reservation, reservationForm.fill(reservation), u))
            case None => NotFound
          }
        }
        case None => NotFound
      }
  }

  /**
   * 预约时确认之前预约的信息(用户电话，预约内容...)
   */
  def reservConfirmInfo = StackAction(AuthorityKey -> isLoggedIn _) {
    implicit request =>
      val user = loggedIn

      reservationForm.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)), {
        resv =>
          var reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)
          reservation = reservation.copy(userPhone = resv.userPhone, userLeaveMsg = resv.userLeaveMsg)
          Cache.set("reservation", reservation)

          val salon: Option[Salon] = Salon.findOneById(reservation.salonId)
          salon match {
            case Some(s) => Ok(views.html.reservation.reservConfirmInfo(s, reservation))
            case None => NotFound
          }
      })
  }

  // TODO the method below -> isOwner
  /**
   * 查看本人处理中的预约
   * @return
   */
  def getReserving = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
//    val user = User.findOneByUserId(userId).get
    val user = loggedIn
    val reservingList = Reservation.findResving(user.userId)
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.myReserving(user, followInfo, reservingList))
  }

  def showReservationDetailById(reservationId : ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val reservation = Reservation.findOneById(reservationId).get
//    val salon: Option[Salon] = Salon.findOneById(reservation.salonId)
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.reservDetailNew(reservation, user, followInfo))
  }

  def deletingReserv(reservationId : ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val reservation = Reservation.findOneById(reservationId).get
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.deletingReserv(reservation, user, followInfo))
  }

  def deletedReserv(reservationId : ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    Reservation.delete(reservationId)
    val reservation = Reservation.findOneById(reservationId).get
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.deletedReserv(reservation, user, followInfo))
  }

  /**
   * 查看本人预约履历
   * @return
   */
  def getReservationHistory = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val user = loggedIn
//    val user = User.findOneByUserId(user.userId).get
    val reservedList = Reservation.findReservationHistory(user.userId)
    val reservingList = Reservation.findResving(user.userId)
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.myReservationHistory(user, followInfo, reservedList, reservingList))
  }
}
