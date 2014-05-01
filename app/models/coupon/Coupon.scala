package models

import java.util.Date
import com.mongodb.casbah.query.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import com.meifannet.framework.db._


case class Coupon (
        id: ObjectId = new ObjectId,
        couponId: String,
        couponName: String,
        salonId: ObjectId,
        serviceItems: List[Service],
        originalPrice: BigDecimal,
        perferentialPrice: BigDecimal,
        serviceDuration: Int,            // Unit: Minutes.
        startDate: Date,
        endDate: Date,
        useConditions: String,
        presentTime: String,
        description: String,
        isValid: Boolean
)

case class CouponServiceType (
		serviceTypes: List[ServiceType],
		subMenuFlg: Option[String]
)

case class CreateCoupon (
		couponItem: Coupon,
		salon: Salon,
		services: List[Service]
)

object Coupon extends MeifanNetModelCompanion[Coupon]{

    val dao = new MeifanNetDAO[Coupon](collection = loadCollection()){}
  
  // Indexes
  //collection.ensureIndex(DBObject("couponName" -> 1), "couponName", unique = true)
  
  // 查找出该沙龙所用优惠劵
  def findBySalon(salonId: ObjectId): List[Coupon] = {
    dao.find(DBObject("salonId" -> salonId)).toList
  }
  
  // 查找出该沙龙所有有效且没有过期的优惠劵
  def findValidCouponBySalon(salonId: ObjectId): List[Coupon] = {
    dao.find($and(DBObject("salonId" -> salonId, "isValid" -> true), "endDate" $gt new Date(), "startDate" $lt new Date())).toList
  }
  
  // 查找出该沙龙所有符合条件的有效且没有过期的优惠劵
  def findContainConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Coupon] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId))).toList
  }
  
  // 查找出该沙龙所用有效且没有过期的优惠劵
  def findValidCouponByConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Coupon] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId, "isValid" -> true), "endDate" $gt new Date(),  "startDate" $lt new Date())).toList
  }

  def checkCouponIsExist(CouponName: String, salonId: ObjectId): Boolean = dao.find(DBObject("couponName" -> CouponName, "salonId" -> salonId)).hasNext

  def isOwner(couponId: ObjectId)(salon: Salon): Future[Boolean] = Future {Coupon.findOneById(couponId).map(coupon => coupon.salonId.equals(salon.id)).getOrElse(false)}
}
