package models

import play.api.Play.current
import play.api.PlayException
import java.util.Date
import com.novus.salat.dao._
import mongoContext._

import com.mongodb.casbah.query.Imports._

case class Coupon (
        id: ObjectId = new ObjectId,
        couponId: String,
        couponName: String,
        salonId: ObjectId,
        serviceItems: Seq[Service],
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


object CouponDAO extends SalatDAO[Coupon, ObjectId](
  collection = com.mongodb.casbah.MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Coupon"))

object Coupon {
  
  def findAll(): List[Coupon] = {
        CouponDAO.find(com.mongodb.casbah.commons.MongoDBObject.empty).toList
    }
  
  def findBySalon(salonId: ObjectId): List[Coupon] = {
    CouponDAO.find(com.mongodb.casbah.commons.Imports.DBObject("salonId" -> salonId)).toList
  }
  
  def save(coupon: Coupon) = {
        CouponDAO.save(
            Coupon(
                id = coupon.id,
                couponId = coupon.couponId,
                couponName = coupon.couponName,
                salonId = coupon.salonId,
                serviceItems = coupon.serviceItems,
                originalPrice = coupon.originalPrice,
                perferentialPrice = coupon.perferentialPrice,
                serviceDuration = coupon.serviceDuration,
                startDate = coupon.startDate,
                endDate = coupon.endDate,
                useConditions = coupon.useConditions,
                presentTime = coupon.presentTime,
                description = coupon.description,
                isValid = coupon.isValid
            )
        )
    }
  
  def findContainCondtions(serviceTypes: Seq[String]): List[Coupon] = {
    CouponDAO.find("serviceItems.serviceType" $all serviceTypes).toList
  }
  
  def checkCoupon(CouponName:String): Boolean = CouponDAO.find(com.mongodb.casbah.commons.Imports.DBObject("couponName" -> CouponName)).hasNext
}
