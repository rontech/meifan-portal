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
  def findApplingStylist(salonId: ObjectId): List[SalonStylistApplyRecord] = {
    dao.find(MongoDBObject("salonId" -> salonId, "applyType" -> 1, "verifiedResult" -> 0)).toList
  }
  
  /**
   *  根据技师id查找该申请中的记录
   */
  def findOneStylistApRd(stylistId: ObjectId): Option[SalonStylistApplyRecord] = {
    dao.findOne(MongoDBObject("stylistId" -> stylistId, "applyType" -> 1, "verifiedResult" -> 0))
  } 
  
  /**
   *  根据salonId,stylistId查找某个技师被某店铺邀请的记录
   */
  def findOneSalonApRd(salonId: ObjectId, stylistId: ObjectId): Option[SalonStylistApplyRecord] = {
    dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0))
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
  def findApplingSalon(stylistId: ObjectId): List[SalonStylistApplyRecord] = {
    dao.find(MongoDBObject("stylistId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0)).toList
  }
  
  /**
   *  根据技师id查找申请中的店铺个数
   */
  def applingSalonCount(stylistId: ObjectId): Long = {
    dao.count(MongoDBObject("salonId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0))
  }
  
  /**
   *  同意技师或者店铺申请
   */
  def agreeStylistApply(record: SalonStylistApplyRecord) = {
    dao.update(MongoDBObject("_id" -> record.id), MongoDBObject("$set" -> (MongoDBObject("applyDate" -> new Date) ++
                MongoDBObject("verifiedResult" -> 1))))
  }
  
  /**
   *  拒绝技师或者店铺申请
   */
  def rejectStylistApply(record: SalonStylistApplyRecord) = {
    dao.update(MongoDBObject("_id" -> record.id), MongoDBObject("$set" -> (MongoDBObject("verifiedResult" -> 2) ++
                MongoDBObject("verifiedTime" -> new Date))))
  }
}