package models

import play.api.Play.current
import play.api.PlayException
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
//import com.mongodb.casbah.MongoConnection
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.mongodb.casbah.query.Imports._

case class Coupon (
        id: ObjectId = new ObjectId,
        couponId: String,
        couponName: String,
        salonId: ObjectId,
        stylistId: ObjectId,
        serviceItems: Seq[Service],
        serviceCategories: Seq[ServiceType],
        originalPrice: BigDecimal,
        perferentialPrice: BigDecimal,
        serviceDuration: Int,            // Unit: Minutes.
        startDate: Date,
        endDate: Date,
        useConditions: String,
        presentTime: String,
        description: String,
        status: String
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
                stylistId = coupon.stylistId,
                serviceItems = coupon.serviceItems,
                serviceCategories = coupon.serviceCategories,
                originalPrice = coupon.originalPrice,
                perferentialPrice = coupon.perferentialPrice,
                serviceDuration = coupon.serviceDuration,
                startDate = coupon.startDate,
                endDate = coupon.endDate,
                useConditions = coupon.useConditions,
                presentTime = coupon.presentTime,
                description = coupon.description,
                status = coupon.status
            )
        )
    }
  
  def findContainCondtions(serviceTypes: Seq[ObjectId]): List[Coupon] = {
    println("serviceTypes = " + serviceTypes)
    CouponDAO.find("serviceCategories._id" $all serviceTypes).toList
  }
}
