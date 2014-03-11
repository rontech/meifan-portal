package models

import play.api.Play.current
import play.api.PlayException

import com.novus.salat._
import com.novus.salat.dao._

import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection

import com.novus.salat.Context

import mongoContext._

import models.Style._


case class Style(
    id: ObjectId = new ObjectId,
    label: String,
    salonId: ObjectId,
    stylistId: ObjectId
)


object StyleDAO extends SalatDAO[Style, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Style"))


object Style {

    def findAll(): List[Style] = {
        StyleDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[Style] = {
        StyleDAO.findOne(MongoDBObject("_id" -> id))
    }

    def findBySalon(salonId: ObjectId): List[Style] = {
        StyleDAO.find(DBObject("salonId" -> salonId)).toList
    }

    def findBySalon(salonId: ObjectId, styleId: ObjectId): Option[Style] = {
        StyleDAO.findOne(DBObject("salonId" -> salonId, "_id" -> styleId))
    }
    
    def findBySalonId(salonId: ObjectId): List[Style] = {
         StyleDAO.find(DBObject("salonId" -> salonId)).toList
    }
    
    def findByStylistId(salonId: ObjectId, stylistId: ObjectId): List[Style] = {
      StyleDAO.findOne(DBObject("salonId" -> salonId, "stylistId" -> stylistId)).toList
    }
    
    def create(style: Style): Option[ObjectId] = {
        StyleDAO.insert(
            Style(
                label = style.label,
                salonId = style.salonId,
                stylistId = style.stylistId
            )
        )
    }

    def save(style: Style) = {
        StyleDAO.save(
            Style(
		id = style.id,
                label = style.label,
                salonId = style.salonId,
                stylistId = style.stylistId
            )
        )
    }

   def delete(id: String) {
        StyleDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}
