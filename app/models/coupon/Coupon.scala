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
package models

import java.util.Date
import com.mongodb.casbah.query.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import com.meifannet.framework.db._

/**
 *
 * @param id
 * @param couponId
 * @param couponName
 * @param salonId
 * @param serviceItems
 * @param originalPrice
 * @param perferentialPrice
 * @param serviceDuration
 * @param startDate
 * @param endDate
 * @param useConditions
 * @param presentTime
 * @param description
 * @param isValid
 */
case class Coupon(
  id: ObjectId = new ObjectId,
  couponId: String,
  couponName: String,
  salonId: ObjectId,
  serviceItems: List[Service],
  originalPrice: BigDecimal,
  perferentialPrice: BigDecimal,
  serviceDuration: Int, // Unit: Minutes.
  startDate: Date,
  endDate: Date,
  useConditions: String,
  presentTime: String,
  description: String,
  isValid: Boolean)

/**
 *
 * @param serviceTypes
 * @param subMenuFlg
 */
case class CouponServiceType(
  serviceTypes: List[ServiceType],
  subMenuFlg: Option[String])

/**
 *
 * @param couponItem
 * @param salon
 * @param services
 */
case class CreateCoupon(
  couponItem: Coupon,
  salon: Salon,
  services: List[Service])

object Coupon extends MeifanNetModelCompanion[Coupon] {

  val dao = new MeifanNetDAO[Coupon](collection = loadCollection()) {}

  // Indexes
  //collection.ensureIndex(DBObject("couponName" -> 1), "couponName", unique = true)

  /**
   *  查找出该沙龙所用优惠劵
   *
   * @param salonId
   * @return
   */
  def findBySalon(salonId: ObjectId): List[Coupon] = {
    dao.find(DBObject("salonId" -> salonId)).toList
  }

  /**
   *  查找出该沙龙所有有效且没有过期的优惠劵
   *
   * @param salonId
   * @return
   */
  def findValidCouponBySalon(salonId: ObjectId): List[Coupon] = {
    dao.find($and(DBObject("salonId" -> salonId, "isValid" -> true), "endDate" $gt new Date(), "startDate" $lt new Date())).toList
  }

  /**
   *  查找出该沙龙所有符合条件的有效且没有过期的优惠劵
   *
   * @param serviceTypes
   * @param salonId
   * @return
   */
  def findContainConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Coupon] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId))).toList
  }

  /**
   *  查找出该沙龙所用有效且没有过期的优惠劵
   *
   * @param serviceTypes
   * @param salonId
   * @return
   */
  def findValidCouponByConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Coupon] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId, "isValid" -> true), "endDate" $gt new Date(), "startDate" $lt new Date())).toList
  }

  /**
   *
   * @param CouponName
   * @param salonId
   * @return
   */
  def checkCouponIsExist(CouponName: String, salonId: ObjectId): Boolean = dao.find(DBObject("couponName" -> CouponName, "salonId" -> salonId)).hasNext

  /**
   *
   * @param couponId
   * @param salon
   * @return
   */
  def isOwner(couponId: ObjectId)(salon: Salon): Future[Boolean] = Future { Coupon.findOneById(couponId).map(coupon => coupon.salonId.equals(salon.id)).getOrElse(false) }
}
