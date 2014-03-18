package models

import play.api.Play.current
import play.api.PlayException

import com.novus.salat._
import com.novus.salat.dao._

//import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection

import com.novus.salat.Context

import mongoContext._

import models.Style._

import com.mongodb.casbah.query.Imports._

case class Style(
    id: ObjectId = new ObjectId,
    styleName: String,
    stylistId: ObjectId,
    stylePic: List[String],
    styleImpression: List[String],
    serviceType: List[String],
    styleLength: String,
    styleColor: List[String],
    styleAmount: List[String],
    styleQuality: List[String],
    styleDiameter: List[String],
    faceType: List[String],
    description: String
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

    def findByStylistId(stylistId: ObjectId): List[Style] = {
      StyleDAO.find(DBObject("stylistId" -> stylistId)).toList
    }
    
    def create(style: Style): Option[ObjectId] = {
        StyleDAO.insert(
            Style(
                styleName = style.styleName,
                stylistId = style.stylistId,
                stylePic = style.stylePic,
                styleImpression = style.styleImpression,
			    serviceType = style.serviceType,
			    styleLength = style.styleLength,
			    styleColor = style.styleColor,
			    styleAmount = style.styleAmount,
			    styleQuality = style.styleQuality,
			    styleDiameter = style.styleDiameter,
			    faceType = style.faceType,
			    description = style.description
            )
        )
    }

    def save(style: Style) = {
        StyleDAO.save(
            Style(
		id = style.id,
                styleName = style.styleName,
                stylistId = style.stylistId,
                stylePic = style.stylePic,
                styleImpression = style.styleImpression,
			    serviceType = style.serviceType,
			    styleLength = style.styleLength,
			    styleColor = style.styleColor,
			    styleAmount = style.styleAmount,
			    styleQuality = style.styleQuality,
			    styleDiameter = style.styleDiameter,
			    faceType = style.faceType,
			    description = style.description
            )
        )
    }

   def delete(id: String) {	
        StyleDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }
   
   def findByPara(style: models.Style) : List[Style] = {
        StyleDAO.find($and("styleDiameter" $in style.styleDiameter ,"styleImpression" $in style.styleImpression)).toList	
   }
}
