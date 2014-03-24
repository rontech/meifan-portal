package models

import play.api.Play.current
import play.api.PlayException
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah._ 
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

object Coupon extends ModelCompanion[Coupon, ObjectId]{

  val dao = new SalatDAO[Coupon, ObjectId](collection = mongoCollection("Coupon")){}
    
  def findBySalon(salonId: ObjectId): List[Coupon] = {
    dao.find(com.mongodb.casbah.commons.Imports.DBObject("salonId" -> salonId)).toList
  }
  
  def findContainCondtions(serviceTypes: Seq[String]): List[Coupon] = {
    dao.find("serviceItems.serviceType" $all serviceTypes).toList
  }
  
  def checkCoupon(CouponName:String): Boolean = dao.find(com.mongodb.casbah.commons.Imports.DBObject("couponName" -> CouponName)).hasNext

}
