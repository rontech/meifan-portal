package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date

case class SalonAndStylist(
  id: ObjectId = new ObjectId,
  salonId: ObjectId,
  stylistId: ObjectId,
  position: List[IndustryAndPosition],
  entryDate: Date,
  leaveDate: Option[Date],
  isValid: Boolean)

object SalonAndStylist extends SalonAndStylistDAO

trait SalonAndStylistDAO extends ModelCompanion[SalonAndStylist, ObjectId] {
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("SalonAndStylist")

  val dao = new SalatDAO[SalonAndStylist, ObjectId](collection) {}
  
  
  /**
   * 查找已绑定店铺的所有技师记录
   */
  def findBySalonId(salonId: ObjectId): List[SalonAndStylist] = {
    dao.find(MongoDBObject("salonId" -> salonId, "isValid" -> true)).toList
  }
  
  /**
   * 查找已绑定店铺的技师关系记录
   */
  def findByStylistId(stylistId: ObjectId): Option[SalonAndStylist] = {
    dao.findOne(MongoDBObject("stylistId" -> stylistId, "isValid" -> true))
  }
  
  /**
   * 检查技师与店铺关系是否有效
   */
  def checkSalonAndStylistValid(salonId: ObjectId, stylistId: ObjectId): Boolean = {
    val isValid = dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" ->stylistId, "isValid" -> true))
    isValid match {
      case Some(is) => true
      case None => false
    }
  }

  /**
   * To Check that if a stylist is active in a salon. 
   */
  def getSalonStylistsInfo(salonId: ObjectId): List[StylistDetailInfo] = {
    var stlDtls: List[StylistDetailInfo] = Nil

    // TODO check if the salon is active.
    // get all the stylists of the specified salon.
    val stlsIn = findBySalonId(salonId)
    stlsIn.map { work =>

      // TODO begin  should be modified later!!!
      // because now the SalonAndStylist table is wrong!
      val stlTmp = Stylist.findOneById(work.stylistId)
      stlTmp match {
        case Some(tmp) => {
          val dtlinfo = Stylist.findStylistByPubId(tmp.publicId)    //work.stylistId
          dtlinfo match {
            case Some(dtl) => stlDtls = dtl :: stlDtls
            case None => stlDtls
          }
        }
        case None => stlDtls

      } 
      // TODO end should be modified later!!!
/*
      val dtlinfo = Stylist.findStylistByPubId(work.stylistId)
      dtlinfo match {
        case Some(dtl) => stlDtls = dtl :: stlDtls
        case None => stlDtls
      }
*/
    } 

    // return
    //println("All stylist detail info in a salon: " + stlDtls) 
    stlDtls
  } 

  /**
   * To Check that if a stylist is active in a salon. 
   */
  def isStylistActive(salonId: ObjectId, stylistId: ObjectId): Boolean = {
    val stls: Option[SalonAndStylist] = dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "isValid" -> true))
    stls match {
      case Some(s) => true
      case None => false 
    }
  }



  /**
   *  与店铺签约
   */
  def entrySalon(salonId: ObjectId, stylistId: ObjectId) = {
    val stylist = Stylist.findOneById(stylistId)
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
   *  技师与店铺解约
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

}

case class IndustryAndPosition(
  id: ObjectId,
  positionName: String,
  industryName: String)

object IndustryAndPosition extends IndustryAndPositionDAO

trait IndustryAndPositionDAO extends ModelCompanion[IndustryAndPosition, ObjectId] {
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("IndustryAndPosition")

  val dao = new SalatDAO[IndustryAndPosition, ObjectId](collection) {}

  collection.ensureIndex(DBObject("_id" -> 1), "id", unique = true)

}

case class Position(
  id: ObjectId,
  positionName: String
 )

object Position extends PositionDAO

trait PositionDAO extends ModelCompanion[Position, ObjectId] {
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("Position")

  val dao = new SalatDAO[Position, ObjectId](collection) {}

}
