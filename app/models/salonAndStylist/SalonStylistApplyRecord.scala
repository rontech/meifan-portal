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
   * 根据店铺id查找申请中的技师
   * @param salonId
   * @return List of apply record
   */
  def findApplyingStylist(salonId: ObjectId): List[SalonStylistApplyRecord] = {
    dao.find(MongoDBObject("salonId" -> salonId, "applyType" -> 1, "verifiedResult" -> 0)).toList
  }

  /**
   * 根据技师id查找该申请中的记录
   * @param stylistId
   * @return a apply record
   */
  def findOneStylistApRd(stylistId: ObjectId): Option[SalonStylistApplyRecord] = {
    dao.findOne(MongoDBObject("stylistId" -> stylistId, "applyType" -> 1, "verifiedResult" -> 0))
  }

  /**
   * 根据salonId,stylistId查找某个技师被某店铺邀请的记录
   * @param salonId
   * @param stylistId
   * @return a option record
   */
  def findOneSalonApRd(salonId: ObjectId, stylistId: ObjectId): Option[SalonStylistApplyRecord] = {
    dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0))
  }

  /**
   * cost current apply record from stylist in salon
   * @param salonId
   * @return
   */
  def applingStylistCount(salonId: ObjectId): Long = {
    dao.count(MongoDBObject("salonId" -> salonId, "applyType" -> 1, "verifiedResult" -> 0))
  }

  /**
   * check salon had apply stylist this current
   * @param salonId
   * @param stylistId
   * @return true or false
   */
  def checkSalonApplyStylist(salonId: ObjectId, stylistId: ObjectId): Boolean = {
    val record = dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "verifiedResult" -> 0))
    record match {
      case Some(re) => true
      case None => false
    }
  }

  /**
   * check whether stylist had apply, because we limit his apply number
   * @param stylistId
   * @return
   */
  def checkStylistApply(stylistId: ObjectId): Boolean = {
    val record = dao.findOne(MongoDBObject("stylistId" -> stylistId, "applyType" -> 1, "verifiedResult" -> 0))
    record match {
      case Some(re) => true
      case None => false
    }
  }

  /**
   * stylist find a apply from salon by stylistId
   * @param stylistId
   * @return List of tuple contains salon and apply record id
   */
  def findApplingSalon(stylistId: ObjectId): List[(Salon, ObjectId)] = {
    var salons: List[(Salon, ObjectId)] = Nil
    val records = dao.find(MongoDBObject("stylistId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0)).toList
    records.map { re =>
      val salon = Salon.findOneById(re.salonId)
      salon match {
        case Some(s) => salons :::= List((s,re.id))
        case None => None
      }
    }
    salons
  }

  /**
   * cost my applying numbers by stylistId
   * @param stylistId
   * @return
   */
  def applingSalonCount(stylistId: ObjectId): Long = {
    dao.count(MongoDBObject("stylistId" -> stylistId, "applyType" -> 2, "verifiedResult" -> 0))
  }

  /**
   * 将申请记录的验证结果改为‘1’，为批准状态
   * 验证日期为当前日期
   */
  def agreeApply(record: SalonStylistApplyRecord) = {
    dao.update(MongoDBObject("_id" -> record.id), MongoDBObject("$set" -> (MongoDBObject("verifiedResult" -> 1) ++
      MongoDBObject("verifiedDate" -> Option(new Date)))))
  }

  /**
   * 将申请记录的验证结果改为‘2’，为拒绝状态
   * 验证日期改为当前的日期
   * @param record - 申请记录
   */
  def rejectApply(record: SalonStylistApplyRecord) = {
    dao.update(MongoDBObject("_id" -> record.id), MongoDBObject("$set" -> (MongoDBObject("verifiedResult" -> 2) ++
      MongoDBObject("verifiedDate" -> Option(new Date)))))
  }

  /**
   * 计算店铺邀请技师的个数
   * @param salonId - 店铺ID
   * @return 返回总计个数，类型为Long
   */
  def costSalonInvited(salonId: ObjectId): Long = {
    dao.count(MongoDBObject("salonId" -> salonId, "applyType" -> 2, "verifiedResult" -> 0))
  }

  /**
   * 店铺根据自己的id查找正在邀请中的记录
   * @param salonId - 店铺ID
   * @return 返回申请履历的集合
   */
  def findSalonInvited(salonId: ObjectId): List[SalonStylistApplyRecord] = {
    dao.find(MongoDBObject("salonId" -> salonId, "applyType" -> 2, "verifiedResult" -> 0)).toList
  }

  /**
   * 店铺根据申请记录的id取消对某个技师的邀请
   * verifiedResult -> 3 为取消状态
   * @param applyRecordId - 申请记录ID
   * @return Unit
   */
  def cancelSalonInvited(applyRecordId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> applyRecordId), MongoDBObject("$set" -> (MongoDBObject("verifiedResult" -> 3) ++
      MongoDBObject("verifiedDate" -> Option(new Date)))))
  }
}
