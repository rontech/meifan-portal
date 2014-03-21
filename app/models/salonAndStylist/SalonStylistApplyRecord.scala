package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.Context
import mongoContext._
import java.util.Date

case class SalonStylistApplyRecord(
		id: ObjectId = new ObjectId,
		salonId: ObjectId,
		stylistId: ObjectId,
		applyType: Int,
		applyDate: Date,
		verifiedResult: Int,
		verifiedDate: Option[Date]
)

object SalonStylistApplyRecord extends SalonStylistApplyRecordDAO

trait SalonStylistApplyRecordDAO extends ModelCompanion[SalonStylistApplyRecord, ObjectId] {  
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("SalonStylistApplyRecord")
  
  val dao = new SalatDAO[SalonStylistApplyRecord, ObjectId](collection){}
  
  /**
   *  根据店铺id查找申请中的技师
   */
  def findApplingStylist(salonId: ObjectId): Seq[SalonStylistApplyRecord] = {
    dao.find(MongoDBObject("salonId" -> salonId, "applyType" -> 1, "verifiedResult" -> 0)).toList
  }
  
  /**
   *  根据店铺id查找申请中的技师个数
   */
  def applingStylistCount(salonId: ObjectId): Long = {
    dao.count(MongoDBObject("salonId" -> salonId, "applyType" -> 1, "verifiedResult" -> 0))
  }
  
  /**
   *  根据技师id查找申请中的店铺
   */
  def findApplingSalon(stylistId: ObjectId): Seq[SalonStylistApplyRecord] = {
    dao.find(MongoDBObject("salonId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0)).toList
  }
  
  /**
   *  根据技师id查找申请中的店铺个数
   */
  def applingSalonCount(stylistId: ObjectId): Long = {
    dao.count(MongoDBObject("salonId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0))
  }
  
}