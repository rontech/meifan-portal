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

case class SalonAndStylist(
		id: ObjectId = new ObjectId,
		salonId: ObjectId,
		stylistId: ObjectId,
		position: IndustryAndPosition,
		entryTime: Date,
		leaveTime: Option[Date],
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
   
} 

case class IndustryAndPosition(
		id: ObjectId,
		positionName: String,
		indestryName: String
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