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
package models.portal.relation

import play.api.Play.current
import play.api.PlayException
import com.meifannet.framework.db._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date
import models.portal.industry.IndustryAndPosition
import models.portal.stylist.Stylist

/**
 * the class for salon and stylist relationship
 * @param id
 * @param salonId   salon primary key id
 * @param stylistId stylist primary key stylistId
 * @param position
 * @param entryDate
 * @param leaveDate
 * @param isValid
 */
case class SalonAndStylist(
  id: ObjectId = new ObjectId,
  salonId: ObjectId,
  stylistId: ObjectId,
  position: List[IndustryAndPosition],
  entryDate: Date,
  leaveDate: Option[Date],
  isValid: Boolean)

object SalonAndStylist extends MeifanNetModelCompanion[SalonAndStylist] {

  val dao = new MeifanNetDAO[SalonAndStylist](collection = loadCollection()) {}

  /**
   * check all records which stylist is binding salon
   * @param salonId
   * @return
   */
  def findBySalonId(salonId: ObjectId): List[SalonAndStylist] = {
    dao.find(MongoDBObject("salonId" -> salonId, "isValid" -> true)).toList
  }

  /**
   * find stylist relationship record that has binding salon
   * @param stylistId
   * @return
   */
  def findByStylistId(stylistId: ObjectId): Option[SalonAndStylist] = {
    dao.findOne(MongoDBObject("stylistId" -> stylistId, "isValid" -> true))
  }

  /**
   * check stylist and salon relationship is valid
   * @param salonId
   * @param stylistId
   * @return
   */
  def checkSalonAndStylistValid(salonId: ObjectId, stylistId: ObjectId): Boolean = {
    val isValid = dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "isValid" -> true))
    isValid match {
      case Some(is) => true
      case None => false
    }
  }

  /**
   * To Check that if a stylist is active in a salon.
   * @param salonId
   */
  def getSalonStylistsInfo(salonId: ObjectId): List[models.portal.stylist.StylistDetailInfo] = {
    var stlDtls: List[models.portal.stylist.StylistDetailInfo] = Nil
    // TODO check if the salon is active.

    // get all the stylists of the specified salon.
    val employ = SalonAndStylist.findBySalonId(salonId)
    employ.map { emp =>
      val dtlinfo = Stylist.findStylistDtlByUserObjId(emp.stylistId)
      dtlinfo match {
        case None => stlDtls
        case Some(dtl) => stlDtls = dtl :: stlDtls
      }
    }
    stlDtls
  }

  /**
   * To check stylist is binding salon
   * @param salonId
   * @param stylistId
   */
  def isStylistActive(salonId: ObjectId, stylistId: ObjectId): Boolean = {
    val stls: Option[SalonAndStylist] = dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "isValid" -> true))
    stls match {
      case Some(s) => true
      case None => false
    }
  }

  /**
   * binging salon
   * @param salonId
   * @param stylistId
   * @return
   */
  def entrySalon(salonId: ObjectId, stylistId: ObjectId) = {
    val stylist = Stylist.findOneByStylistId(stylistId)
    stylist match {
      case Some(sty) => {
        dao.save(new SalonAndStylist(new ObjectId, salonId,
          stylistId, sty.position, new Date, None,
          true))
      }
      case None => None
    }
  }

  /**
   * stylist dissolution of contract with salon
   * @param salonId
   * @param stylistId
   * @return
   */
  def leaveSalon(salonId: ObjectId, stylistId: ObjectId) = {
    val salonAndStylist = dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "isValid" -> true))
    salonAndStylist match {
      case Some(relation) => dao.update(MongoDBObject("_id" -> relation.id), MongoDBObject("$set" -> (
        MongoDBObject("leaveDate" -> new Date) ++
        MongoDBObject("isValid" -> false))))
      // TODO
      case None => None
    }
  }

  /**
   * cost current stylist in salon numbers
   * @param salonId
   * @return
   */
  def countStylistBySalon(salonId: ObjectId): Long = {
    dao.count(MongoDBObject("salonId" -> salonId, "isValid" -> true))
  }

  /**
   * get all stylists to a list of salon
   * @param salonId
   * @return
   */
  def getStylistsBySalon(salonId: ObjectId): List[Stylist] = {
    var stylists: List[Stylist] = Nil
    val record = SalonAndStylist.findBySalonId(salonId)
    record.map { re =>
      val stylist = Stylist.findOneByStylistId(re.stylistId)
      stylist match {
        case Some(sty) => stylists :::= List(sty)
        case None => None
      }
    }
    stylists
  }
}

