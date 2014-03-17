package models

import play.api.Play.current
import play.api.PlayException
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.libs.json._
import play.api.libs.functional.syntax._

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
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Coupon"))

object Coupon {
  
  def findAll(): List[Coupon] = {
        CouponDAO.find(MongoDBObject.empty).toList
    }
  
  def findBySalon(salonId: ObjectId): List[Coupon] = {
    CouponDAO.find(DBObject("salonId" -> salonId)).toList
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
  
  def findContainCondtions(serviceTypes: ObjectId): List[Coupon] = {
    // in mongodb, we can select like this below:
    // db.Coupon.find({"serviceCategories._id": {$all: [ObjectId("5316798cd4d5cb7e816db34b"), ObjectId("53167ae7d4d5cb7e816db355")]}})
   
    // faild1 
    //val lst = List(new ObjectId("5316798cd4d5cb7e816db34b"), new ObjectId("53167ae7d4d5cb7e816db355"))
    //CouponDAO.find(DBObject("serviceCategories._id" -> {"$all" -> lst} )).toList
    
    // List(ServiceType(5316798cd4d5cb7e816db34b,剪), ServiceType(53167ae7d4d5cb7e816db355,烫))
    //val lst1 = List(ServiceType(new ObjectId("5316798cd4d5cb7e816db34b"),"剪"), ServiceType(new ObjectId("53167ae7d4d5cb7e816db355"),"烫"))
    //CouponDAO.find(DBObject("serviceCategories" -> {"$all" -> lst1} )).toList

    // failed2
    //CouponDAO.find(DBObject("originalPrice" $lt 200 $gt 50 )).toList
    // failed 3
    //CouponDAO.find(DBObject("originalPrice" -> { "$lt" -> 200} )).toList

    // success1
    //val itm = new ObjectId("5316798cd4d5cb7e816db34b")
    //CouponDAO.find(DBObject("serviceCategories._id" -> itm )).toList

    // success2
    //val lst1 = List(ServiceType(new ObjectId("5316798cd4d5cb7e816db34b"),"剪"))
    //CouponDAO.find(DBObject("serviceCategories._id" -> lst1(0).id )).toList

    // success2
    val lst1 = ServiceType(new ObjectId("5316798cd4d5cb7e816db34b"),"剪")
    val lst2 = ServiceType(new ObjectId("53167ae7d4d5cb7e816db355"),"烫")
    println(lst1::lst2::Nil)
    CouponDAO.find(DBObject("serviceCategories" -> {"$all" -> lst1::lst2::Nil})).toList

    //CouponDAO.find(DBObject("serviceCategories._id" -> {"$all" -> serviceTypes} )).toList
//   CouponDAO.find(DBObject("serviceCategories" -> Seq[ServiceType(ObjectId())] )).toList
  }
}