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
package models

import com.mongodb.casbah.commons.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date
import com.meifannet.framework.db._

case class SalonStylistApplyRecord(
  id: ObjectId = new ObjectId,
  salonId: ObjectId,
  stylistId: ObjectId,
  applyType: Int,
  applyDate: Date,
  verifiedResult: Int,
  verifiedDate: Option[Date])

object SalonStylistApplyRecord extends MeifanNetModelCompanion[SalonStylistApplyRecord] {
  val dao = new MeifanNetDAO[SalonStylistApplyRecord](collection = loadCollection()) {}

  /**
   *  根据店铺id查找申请中的技师
   */
  def findApplyingStylist(salonId: ObjectId): List[SalonStylistApplyRecord] = {
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
   *  查看店铺是否已经申请过此技师
   */
  def checkSalonApplyStylist(salonId: ObjectId, stylistId: ObjectId): Boolean = {
    val record = dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "verifiedResult" -> 0))
    record match {
      case Some(re) => true
      case None => false
    }
  }

  /**
   *  查看技师当前有无申请
   */
  def checkStylistApply(stylistId: ObjectId): Boolean = {
    val record = dao.findOne(MongoDBObject("stylistId" -> stylistId, "applyType" -> 1, "verifiedResult" -> 0))
    record match {
      case Some(re) => true
      case None => false
    }
  }

  /**
   *  根据技师id查找申请中的店铺
   */
  def findApplingSalon(stylistId: ObjectId): List[Salon] = {
    var salons: List[Salon] = Nil
    val records = dao.find(MongoDBObject("stylistId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0)).toList
    records.map { re =>
      val salon = Salon.findOneById(re.salonId)
      salon match {
        case Some(s) => salons :::= List(s)
        case None => None
      }
    }
    salons
  }

  /**
   *  根据技师id查找申请中的店铺个数
   */
  def applingSalonCount(stylistId: ObjectId): Long = {
    dao.count(MongoDBObject("stylistId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0))
  }

  /**
   *  同意技师或者店铺申请
   */
  def agreeStylistApply(record: SalonStylistApplyRecord) = {
    dao.update(MongoDBObject("_id" -> record.id), MongoDBObject("$set" -> (MongoDBObject("verifiedResult" -> 1) ++
      MongoDBObject("verifiedDate" -> Option(new Date)))))
  }

  /**
   *  拒绝技师或者店铺申请
   */
  def rejectStylistApply(record: SalonStylistApplyRecord) = {
    dao.update(MongoDBObject("_id" -> record.id), MongoDBObject("$set" -> (MongoDBObject("verifiedResult" -> 2) ++
      MongoDBObject("verifiedDate" -> Option(new Date)))))
  }

}
