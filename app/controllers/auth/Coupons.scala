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

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date
import java.util.Calendar
import models._
import views._
import play.api.i18n.Messages
import play.api.templates.Html
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import controllers._
import com.meifannet.portal.MeifanNetSalonApplication
import models.portal.service.{ServiceByType, ServiceType, Service}
import models.portal.coupon.{CouponServiceType, Coupon}

object Coupons extends MeifanNetSalonApplication {

  /**
   * 优惠劵Form
   * 用于优惠劵创建
   */
  def couponForm: Form[Coupon] = Form {
    mapping(
      "couponName" -> text,
      "salonId" -> text,
      "serviceItems" -> list(
        mapping(
          "id" -> text) { (id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true) } { service => Some((service.id.toString())) }).verifying(Messages("coupon.couponServiceRequired"), serviceItems => !serviceItems.isEmpty),
      "perferentialPrice" -> bigDecimal,
      "startDate" -> date,
      "endDate" -> date,
      "useConditions" -> text,
      "presentTime" -> text,
      "description" -> text) {
        (couponName, salonId, serviceItems, perferentialPrice, startDate, endDate, useConditions, presentTime, description) =>
          Coupon(new ObjectId, "", couponName,
            new ObjectId(salonId), serviceItems, BigDecimal(0), perferentialPrice, 0, startDate, endDate, useConditions, presentTime, description, true)
      } {
        coupon =>
          Some((coupon.couponName, coupon.salonId.toString(), coupon.serviceItems, coupon.perferentialPrice, coupon.startDate,
            coupon.endDate, coupon.useConditions, coupon.presentTime, coupon.description))
      }.verifying(
        Messages("coupon.couponNameRepeat"),
        coupon => !Coupon.checkCouponIsExist(coupon.couponName, coupon.salonId)).verifying(Messages("coupon.endDateGtstartMsg"),
          coupon => coupon.startDate.before(coupon.endDate))
  }

  /**
   * 优惠劵更新Form
   * 用于优惠劵更新
   */
  def couponUpdateForm: Form[Coupon] = Form {
    mapping(
      "couponName" -> text,
      "salonId" -> text,
      "serviceItems" -> list(
        mapping(
          "id" -> text) { (id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true) } { service => Some((service.id.toString())) }).verifying(Messages("coupon.couponServiceRequired"), serviceItems => !serviceItems.isEmpty),
      "perferentialPrice" -> bigDecimal,
      "startDate" -> date,
      "endDate" -> date,
      "useConditions" -> text,
      "presentTime" -> text,
      "description" -> text) {
        (couponName, salonId, serviceItems, perferentialPrice, startDate, endDate, useConditions, presentTime, description) =>
          Coupon(new ObjectId, "", couponName,
            new ObjectId(salonId), serviceItems, BigDecimal(0), perferentialPrice, 0, startDate, endDate, useConditions, presentTime, description, true)
      } {
        coupon =>
          Some((coupon.couponName, coupon.salonId.toString(), coupon.serviceItems, coupon.perferentialPrice, coupon.startDate,
            coupon.endDate, coupon.useConditions, coupon.presentTime, coupon.description))
      }.verifying(Messages("coupon.endDateGtstartMsg"),
        coupon => coupon.startDate.before(coupon.endDate))
  }

  /**
   * 用于优惠劵，菜单等查找
   */
  def conditionForm: Form[CouponServiceType] = Form {
    mapping(
      "serviceTypes" -> list(
        mapping(
          "serviceTypeName" -> text) { (serviceTypeName) => ServiceType(new ObjectId(), "Hairdressing", serviceTypeName, "") } { serviceType => Some((serviceType.serviceTypeName)) }),
      "subMenuFlg" -> optional(text))(CouponServiceType.apply)(CouponServiceType.unapply)
  }

  /**
   * 进入创建优惠劵画面
   */
  def couponMain = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Ok(html.salon.admin.createSalonCoupon(salon, couponForm, Service.findBySalonId(salon.id)))
  }

  /**
   * 创建优惠劵
   */
  def createCoupon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    couponForm.bindFromRequest.fold(
      errors => BadRequest(html.salon.admin.createSalonCoupon(salon, errors, Service.findBySalonId(salon.id))),
      {
        coupon =>
          var services: List[Service] = Nil
          var originalPrice: BigDecimal = 0
          var serviceDuration: Int = 0

          for (serviceItem <- coupon.serviceItems) {
            val service: Option[Service] = Service.findOneById(serviceItem.id)
            service match {
              case Some(s) =>
                services = s :: services
                originalPrice = s.price + originalPrice
                serviceDuration = s.duration + serviceDuration
              case None => NotFound
            }
          }

          // 处理截至时间，将截止时间的时分秒改为23:59:59
          var endDate: Date = coupon.endDate
          var endTime: Calendar = Calendar.getInstance()
          endTime.setTime(endDate)
          endTime.set(Calendar.HOUR_OF_DAY, 23)
          endTime.set(Calendar.MINUTE, 59)
          endTime.set(Calendar.SECOND, 59)

          val couponTemp = coupon.copy(couponId = coupon.id.toString(), salonId = salon.id, serviceItems = services,
            originalPrice = originalPrice, serviceDuration = serviceDuration, endDate = endTime.getTime())
          Coupon.save(couponTemp)

          val coupons: List[Coupon] = Coupon.findBySalon(salon.id)
          val serviceTypes: List[ServiceType] = ServiceType.findAllServiceTypes(salon.salonIndustry)
          val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
          Ok(html.salon.admin.mySalonCouponAll(salon, conditionForm.fill(couponServiceType), serviceTypes, coupons))
      })
  }

  /**
   * 进入修改优惠劵画面
   * @param couponId 优惠劵id
   * @return
   */
  def editCouponInfo(couponId: ObjectId) = StackAction(AuthorityKey -> Coupon.isOwner(couponId) _) { implicit request =>
    val salon = loggedIn
    val coupon = Coupon.findOneById(couponId)

    coupon match {
      case Some(cp) =>
        Ok(html.salon.admin.editSalonCoupon(salon, couponForm.fill(cp), Service.findBySalonId(salon.id), cp))
      case None => NotFound
    }
  }

  /**
   * 更新优惠劵信息
   * * @param couponId 优惠劵id
   */
  def updateCoupon(couponId: ObjectId) = StackAction(AuthorityKey -> Coupon.isOwner(couponId) _) { implicit request =>
    val salon = loggedIn
    couponUpdateForm.bindFromRequest.fold(
      errors => BadRequest(html.salon.admin.editSalonCoupon(salon, errors, Service.findBySalonId(salon.id), Coupon.findOneById(couponId).get)),
      {
        coupon =>
          val oldCoupon: Option[Coupon] = Coupon.findOneById(couponId)
          var services: List[Service] = Nil
          var originalPrice: BigDecimal = 0
          var serviceDuration: Int = 0

          for (serviceItem <- coupon.serviceItems) {
            val service: Option[Service] = Service.findOneById(serviceItem.id)
            service match {
              case Some(s) =>
                services = s :: services
                originalPrice = s.price + originalPrice
                serviceDuration = s.duration + serviceDuration
              case None => NotFound
            }
          }

          // 处理截至时间，将截止时间的时分秒改为23:59:59
          var endDate: Date = coupon.endDate
          var endTime: Calendar = Calendar.getInstance()
          endTime.setTime(endDate)
          endTime.set(Calendar.HOUR_OF_DAY, 23)
          endTime.set(Calendar.MINUTE, 59)
          endTime.set(Calendar.SECOND, 59)

          oldCoupon match {
            case Some(c) => {
              val newCoupon = c.copy(serviceItems = services, originalPrice = originalPrice, perferentialPrice = coupon.perferentialPrice,
                serviceDuration = serviceDuration, startDate = coupon.startDate, endDate = endTime.getTime(), useConditions = coupon.useConditions,
                presentTime = coupon.presentTime, description = coupon.description)
              Coupon.save(newCoupon)
            }
            case None => NotFound
          }

          val coupons: List[Coupon] = Coupon.findBySalon(salon.id)
          val serviceTypes: List[ServiceType] = ServiceType.findAllServiceTypes(salon.salonIndustry)
          val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
          Ok(html.salon.admin.mySalonCouponAll(salon, conditionForm.fill(couponServiceType), serviceTypes, coupons))
      })

  }

  /**
   * 无效优惠劵
   * * @param couponId 优惠劵id
   */
  def invalidCoupon(couponId: ObjectId) = StackAction(AuthorityKey -> Coupon.isOwner(couponId) _) { implicit request =>
    val salon = loggedIn
    val coupon: Option[Coupon] = Coupon.findOneById(couponId)

    coupon match {
      case Some(s) =>
        val couponTemp = s.copy(isValid = false)
        Coupon.save(couponTemp)
        val coupons: List[Coupon] = Coupon.findBySalon(salon.id)
        val serviceTypes: List[ServiceType] = ServiceType.findAllServiceTypes(salon.salonIndustry)
        val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
        Ok(html.salon.admin.mySalonCouponAll(salon, conditionForm.fill(couponServiceType), serviceTypes, coupons))
      case None => NotFound
    }
  }

  /**
   * 根据查找条件检索出符合的优惠劵
   * 用于后台优惠劵的查找
   */
  def findCoupons = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Coupons.conditionForm.bindFromRequest.fold(
      errors => BadRequest(Html(errors.toString)),
      {
        serviceType =>
          var coupons: List[Coupon] = Nil
          var conditions: List[String] = Nil
          var servicesByTypes: List[ServiceByType] = Nil
          var typeBySearches: List[ServiceType] = Nil
          var couponServiceType: CouponServiceType = CouponServiceType(Nil, serviceType.subMenuFlg)

          for (serviceTypeOne <- serviceType.serviceTypes) {
            conditions = serviceTypeOne.serviceTypeName :: conditions
            val serviceType: Option[ServiceType] = ServiceType.findOneByTypeName(serviceTypeOne.serviceTypeName)
            serviceType match {
              case Some(s) => typeBySearches = s :: typeBySearches
              case None => NotFound
            }
          }
          couponServiceType = couponServiceType.copy(serviceTypes = typeBySearches)

          val serviceTypes: List[ServiceType] = ServiceType.findAllServiceTypes(salon.salonIndustry)
          if (serviceType.subMenuFlg == None) {
            //coupons = Coupon.findContainConditions(serviceTypes)
          } else {
            if (serviceType.serviceTypes.isEmpty) {
              coupons = Coupon.findBySalon(salon.id)
            } else {
              coupons = Coupon.findContainConditions(conditions, salon.id)
            }
          }
          Ok(html.salon.admin.mySalonCouponAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, coupons))
      })
  }
}
