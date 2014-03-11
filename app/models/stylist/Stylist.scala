package models

import play.api.Play.current
import play.api.PlayException

import com.novus.salat._
import com.novus.salat.dao._

import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection

import com.novus.salat.Context

import mongoContext._


case class Stylist(
    id: ObjectId = new ObjectId,
    label: String,
    salonId: ObjectId,
    userId: ObjectId,
    workYears: String,
    stylistStyle: List[String],
    imageId: ObjectId,
    consumerId: ObjectId,
    description: String,
    pictureName: String
)


object StylistDAO extends SalatDAO[Stylist, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Stylist"))


object Stylist {

    def findAll(): List[Stylist] = {
        StylistDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[Stylist] = {
        StylistDAO.findOne(MongoDBObject("_id" -> id))
    }

    def findBySalon(salonId: ObjectId): List[Stylist] = {
        StylistDAO.find(DBObject("salonId" -> salonId)).toList
    }

    def create(stylist: Stylist): Option[ObjectId] = {
        StylistDAO.insert(
            Stylist(
                label = stylist.label,
                salonId = stylist.salonId,
                userId = stylist.userId,
                workYears = stylist.workYears,
                stylistStyle = stylist.stylistStyle,
                imageId = stylist.imageId,
                consumerId = stylist.consumerId,
                description = stylist.description,
                pictureName = stylist.pictureName
            )
        )
    }

    def save(stylist: Stylist) = {
        StylistDAO.save(
            Stylist(
            	id = stylist.id,
                label = stylist.label,
                salonId = stylist.salonId,
                userId = stylist.userId,
                workYears = stylist.workYears,
                stylistStyle = stylist.stylistStyle,
                imageId = stylist.imageId,
                consumerId = stylist.consumerId,
                description = stylist.description,
                pictureName = stylist.pictureName
                
                
            )
        )
    }

   def delete(id: String) {
        StylistDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}
