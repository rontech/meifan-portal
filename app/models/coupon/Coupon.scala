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
package models.portal.coupon

import java.util.Date
import com.mongodb.casbah.query.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import com.meifannet.framework.db._
import models.portal.service.{ServiceType, Service}
import models.portal.salon.Salon

/**
 * 优惠劵表
 * 
 * @param id
 * @param couponId 优惠劵id
 * @param couponName 优惠劵名称
 * @param salonId 沙龙id
 * @param serviceItems 优惠劵中所包含的服务列表
 * @param originalPrice 原价
 * @param perferentialPrice 优惠劵
 * @param serviceDuration 服务总时长
 * @param startDate 优惠劵开始时间
 * @param endDate 优惠劵截至时间
 * @param useConditions 使用条件
 * @param presentTime 出示时间，如“在消费前使用”
 * @param description 描述
 * @param isValid 该优惠劵是否有效
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
 * 用于优惠劵，菜单和服务的检索
 * 
 * @param serviceTypes 服务列表
 * @param subMenuFlg 是否只包含以上条件
 */
case class CouponServiceType(
  serviceTypes: List[ServiceType],
  subMenuFlg: Option[String])


object Coupon extends MeifanNetModelCompanion[Coupon] {

  val dao = new MeifanNetDAO[Coupon](collection = loadCollection()) {}

  // Indexes
  //collection.ensureIndex(DBObject("couponName" -> 1), "couponName", unique = true)

  /**
   * 查找出该沙龙所用优惠劵
   *
   * @param salonId
   * @return
   */
  def findBySalon(salonId: ObjectId): List[Coupon] = {
    dao.find(DBObject("salonId" -> salonId)).toList
  }

  /**
   * 查找出该沙龙所有有效且没有过期的优惠劵
   *
   * @param salonId
   * @return
   */
  def findValidCouponBySalon(salonId: ObjectId): List[Coupon] = {
    dao.find($and(DBObject("salonId" -> salonId, "isValid" -> true), "endDate" $gt new Date(), "startDate" $lt new Date())).toList
  }

  /**
   * 查找出该沙龙所有符合条件的有效且没有过期的优惠劵
   * 用于前台优惠劵等查找功能
   * @param serviceTypes 服务列表
   * @param salonId
   * @return
   */
  def findContainConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Coupon] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId))).toList
  }

  /**
   * 查找出该沙龙所用有效且没有过期的优惠劵
   * 用于后台优惠劵等查找功能
   * @param serviceTypes 服务列表
   * @param salonId
   * @return
   */
  def findValidCouponByConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Coupon] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId, "isValid" -> true), "endDate" $gt new Date(), "startDate" $lt new Date())).toList
  }

  /**
   * 检查该优惠劵是否存在
   * 用于创建优惠劵时根据优惠劵名判断优惠劵是否已存在
   * @param CouponName 优惠劵名
   * @param salonId
   * @return
   */
  def checkCouponIsExist(CouponName: String, salonId: ObjectId): Boolean = dao.find(DBObject("couponName" -> CouponName, "salonId" -> salonId)).hasNext

  /**
   * 判断优惠劵是否属于当前沙龙的
   * 
   * @param couponId
   * @param salon
   * @return
   */
  def isOwner(couponId: ObjectId)(salon: Salon): Future[Boolean] = Future { Coupon.findOneById(couponId).map(coupon => coupon.salonId.equals(salon.id)).getOrElse(false) }
}
