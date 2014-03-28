package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import java.util.Date

case class ApplyRecord (
		id: ObjectId = new ObjectId,
		stylistId: ObjectId,
		salonId: ObjectId,
		applyType: Int,
		createTime: Date,
		acceptTime: Option[Date],
		rejectTime: Option[Date],
		relieveTime: Option[Date],
		status: Int
)

object ApplyRecordDAO extends SalatDAO[ApplyRecord, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("ApplyRecord"))
  
object ApplyRecord{
  /**
   * 计算在salon中来自技师申请的个数
   */
  def countStylistApply(salonId: ObjectId): Long = {
    ApplyRecordDAO.count(MongoDBObject("salonId" -> salonId, "acceptTime" -> None, "applyType" -> 1, "status" -> 0))
  }
  
  /**
   * 计算在技师中来自salon的邀请的个数
   */
  def countSalonApply(stylistId: ObjectId): Long = {
    ApplyRecordDAO.count(MongoDBObject("stylistId" -> stylistId, "acceptTime" -> None, "applyType" -> -1, "status" -> -1))
  }
  
  /**
   * 技师查找到哪些salon邀请自己
   */
  def findSalonApply(stylistId: ObjectId): List[Salon] = {
     var salons: List[Salon] = Nil 
     val records = ApplyRecordDAO.find(MongoDBObject("stylistId" -> stylistId, "acceptTime" -> None, "applyType" -> -1,
         "status" -> -1)).toList
     records.map {re =>
       val salo = Salon.findById(re.salonId)
       salo match {
         case Some(s) => salons:::=List(s)
         case None => None
       }
     }    
     salons
  }
  
  /**
   *店铺查找有哪些技师申请 
   */
  def findStylistApply(salonId: ObjectId): List[Stylist] = {
    var stylists: List[Stylist] = Nil
    val records = ApplyRecordDAO.find(MongoDBObject("salonId" -> salonId, "acceptTime" -> None, "applyType" -> 1, "status" -> 0)).toList
    records.map {re =>
       val stylist = Stylist.findOneByStylistId(re.stylistId)
       stylist match {
         case Some(s) => stylists:::=List(s)
         case None => None
       }
     } 
    stylists
  }
  
  /**
   * 查找在店铺中单个技师申请的记录
   */
  def findStylistAyRd(salonId: ObjectId, stylistId: ObjectId): Option[ApplyRecord] = {
    ApplyRecordDAO.findOne(MongoDBObject("salonId" -> salonId, "stylistId" ->stylistId, "acceptTime" -> None,
        "applyType" -> 1, "status" -> 0))
  }
  
  /**
   * 查找在技师中单个salon的记录
   */
  def findSalonAyRd(salonId: ObjectId, stylistId: ObjectId): Option[ApplyRecord] = {
     ApplyRecordDAO.findOne(MongoDBObject("salonId" -> salonId, "stylistId" ->stylistId, "acceptTime" -> None,
        "applyType" -> -1, "status" -> {"$ne" -> 1}))
  }
  
  def create(apply: ApplyRecord): Option[ObjectId] = {
       ApplyRecordDAO.insert(
            ApplyRecord(
                stylistId = apply.stylistId,
                salonId = apply.salonId,
                applyType = apply.applyType,
                createTime = apply.createTime,
                acceptTime = apply.acceptTime,
                rejectTime = apply.rejectTime,
                relieveTime = apply.relieveTime,
                status = apply.status
            )
        )
    }

    def save(apply: ApplyRecord) = {
        ApplyRecordDAO.save(
            ApplyRecord(
            	id = apply.id,
                stylistId = apply.stylistId,
                salonId = apply.salonId,
                applyType = apply.applyType,
                createTime = apply.createTime,
                acceptTime = apply.acceptTime,
                rejectTime = apply.rejectTime,
                relieveTime = apply.relieveTime,
                status = apply.status
            )
        )
    }
}