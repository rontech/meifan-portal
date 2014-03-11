package models

import play.api.Play.current
import play.api.PlayException

import com.novus.salat._
import com.novus.salat.dao._
import com.novus.salat.Context

import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection


import mongoContext._



case class Salon(
    id: ObjectId = new ObjectId,
    label: String
)


object SalonDAO extends SalatDAO[Salon, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Salon"))


object Salon {

    def findAll(): List[Salon] = {
        SalonDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[Salon] = {
        SalonDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(salon: Salon): Option[ObjectId] = {
        SalonDAO.insert(
            Salon(
                label = salon.label
            )
        )
    }

    def save(salon: Salon) = {
        SalonDAO.save(
            Salon(
		id = salon.id,
                label = salon.label
            )
        )

    }

    def delete(id: String) {
        SalonDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

} 

