package models

import play.api.Play.current
import play.api.PlayException
import com.meifannet.framework.db._
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

object SalonAndStylist extends MeifanNetModelCompanion[SalonAndStylist]{
  
  val dao = new MeifanNetDAO[SalonAndStylist](collection = loadCollection()){}
  
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
  
  def countStylistBySalon(salonId: ObjectId): Long = {
    dao.count(MongoDBObject("salonId" -> salonId, "isValid" -> true))
  }
  
  def getStylistsBySalon(salonId: ObjectId): List[Stylist] = {
    var stylists: List[Stylist] = Nil
    val record = SalonAndStylist.findBySalonId(salonId)
    record.map{re=>
      val stylist = Stylist.findOneByStylistId(re.stylistId)
      stylist match {
        case Some(sty) => stylists:::=List(sty)
        case None => None
      }
    }
    stylists
  }
}

