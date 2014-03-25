package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import java.util.Date

case class SalonAndStylist(
		id: ObjectId = new ObjectId,
		salonId: ObjectId,
		stylistId: ObjectId,
		position: List[IndustryAndPosition],
		entryDate: Date,
		leaveDate: Option[Date],
		isValid: Boolean
)

object SalonAndStylist extends SalonAndStylistDAO

trait SalonAndStylistDAO extends ModelCompanion[SalonAndStylist, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("SalonAndStylist")
   
  val dao = new SalatDAO[SalonAndStylist, ObjectId](collection){}
  
  def findBySalonId(salonId: ObjectId): Seq[SalonAndStylist] = {
     dao.find(MongoDBObject("salonId" -> salonId, "isValid" -> true)).toList
  }
   
  def findByStylistId(stylistId: ObjectId): Option[SalonAndStylist] = {
	 dao.findOne(MongoDBObject("stylistId" -> stylistId, "isValid" -> true))
  }
  
  /**
   *  与店铺签约
   */
  def entrySalon(salonId: ObjectId,stylistId: ObjectId) = {
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
  def leaveSalon(salonId: ObjectId,stylistId: ObjectId) = {
    val salonAndStylist = dao.findOne(MongoDBObject("salonId" -> salonId, "stylistId" -> stylistId, "isValid" -> true))
    salonAndStylist match {
      case Some(relation) => dao.update(MongoDBObject("_id" -> relation.id), MongoDBObject("$set" -> (
    		  MongoDBObject("leaveDate" -> new Date)++
    		  MongoDBObject("isValid" -> false)
      )))
      // TODO
      case None => None 
    }
 }
   
} 

case class IndustryAndPosition(
		id: ObjectId,
		positionName: String,
		industryName: String
)

object IndustryAndPosition extends IndustryAndPositionDAO

trait IndustryAndPositionDAO extends ModelCompanion[IndustryAndPosition, ObjectId] {
      def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("IndustryAndPosition")
  
  val dao = new SalatDAO[IndustryAndPosition, ObjectId](collection){}
      
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
          "Could not find mongodb.default.db in settings"))
  )("Position")
  
  val dao = new SalatDAO[Position, ObjectId](collection){}
      
}
