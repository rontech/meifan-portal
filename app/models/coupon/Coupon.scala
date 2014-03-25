package models

import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.query.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.Imports.MongoConnection
import play.api.Play._
import play.api.PlayException


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

case class CouponServiceType (
		serviceTypes: List[ServiceType],
		subMenuFlg: Option[String]
)

object Coupon extends ModelCompanion[Coupon, ObjectId]{

  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
      "Configuration error",
      "Could not find mongodb.default.db in settings")))("Coupon")

  val dao = new SalatDAO[Coupon, ObjectId](collection){}
    
  def findBySalon(salonId: ObjectId): List[Coupon] = {
    dao.find(DBObject("salonId" -> salonId)).toList
  }
  
  def findContainCondtions(serviceTypes: Seq[String]): List[Coupon] = {
    dao.find("serviceItems.serviceType" $all serviceTypes).toList
  }

  def checkCoupon(CouponName:String): Boolean = dao.find(DBObject("couponName" -> CouponName)).hasNext

}
