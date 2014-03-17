package models

import play.api.Play.current
import play.api.PlayException

import com.novus.salat._
import com.novus.salat.dao._

import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection

import com.novus.salat.Context

import mongoContext._

/**
 * The table of styleLength
 */
case class StyleLength(
    id: ObjectId = new ObjectId,
    styleLength: String
)


object StyleLengthDAO extends SalatDAO[StyleLength, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleLength"))


object StyleLength {

    def findAll(): List[StyleLength] = {
        StyleLengthDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[StyleLength] = {
        StyleLengthDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(styleLength: StyleLength): Option[ObjectId] = {
        StyleLengthDAO.insert(
            StyleLength(
			    styleLength = styleLength.styleLength
            )
        )
    }

    def save(styleLength: StyleLength) = {
        StyleLengthDAO.save(
            StyleLength(
		id = styleLength.id,
                styleLength = styleLength.styleLength
            )
        )
    }

   def delete(id: String) {
        StyleLengthDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of styleColor
 */
case class StyleColor(
    id: ObjectId = new ObjectId,
    styleColor: String
)


object StyleColorDAO extends SalatDAO[StyleColor, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleColor"))


object StyleColor {

    def findAll(): List[StyleColor] = {
        StyleColorDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[StyleColor] = {
        StyleColorDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(styleColor: StyleColor): Option[ObjectId] = {
        StyleColorDAO.insert(
            StyleColor(
			    styleColor = styleColor.styleColor
            )
        )
    }

    def save(styleColor: StyleColor) = {
        StyleColorDAO.save(
            StyleColor(
		id = styleColor.id,
                styleColor = styleColor.styleColor
            )
        )
    }

   def delete(id: String) {
        StyleColorDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of impression
 */
case class Impression(
    id: ObjectId = new ObjectId,
    impression: String
)


object ImpressionDAO extends SalatDAO[Impression, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Impression"))


object Impression {

    def findAll(): List[Impression] = {
        ImpressionDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[Impression] = {
        ImpressionDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(impression: Impression): Option[ObjectId] = {
        ImpressionDAO.insert(
            Impression(
			    impression = impression.impression
            )
        )
    }

    def save(impression: Impression) = {
        ImpressionDAO.save(
            Impression(
		id = impression.id,
                impression = impression.impression
            )
        )
    }

   def delete(id: String) {
        ImpressionDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of styleAmount
 */
case class StyleAmount(
    id: ObjectId = new ObjectId,
    styleAmount: String
)


object StyleAmountDAO extends SalatDAO[StyleAmount, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleAmount"))


object StyleAmount {

    def findAll(): List[StyleAmount] = {
        StyleAmountDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[StyleAmount] = {
        StyleAmountDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(styleAmount: StyleAmount): Option[ObjectId] = {
        StyleAmountDAO.insert(
            StyleAmount(
			    styleAmount = styleAmount.styleAmount
            )
        )
    }

    def save(styleAmount: StyleAmount) = {
        StyleAmountDAO.save(
            StyleAmount(
		id = styleAmount.id,
                styleAmount = styleAmount.styleAmount
            )
        )
    }

   def delete(id: String) {
        StyleAmountDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of styleQuality
 */
case class StyleQuality(
    id: ObjectId = new ObjectId,
    styleQuality: String
)


object StyleQualityDAO extends SalatDAO[StyleQuality, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleQuality"))


object StyleQuality {

    def findAll(): List[StyleQuality] = {
        StyleQualityDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[StyleQuality] = {
        StyleQualityDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(styleQuality: StyleQuality): Option[ObjectId] = {
        StyleQualityDAO.insert(
            StyleQuality(
			    styleQuality = styleQuality.styleQuality
            )
        )
    }

    def save(styleQuality: StyleQuality) = {
        StyleQualityDAO.save(
            StyleQuality(
		id = styleQuality.id,
                styleQuality = styleQuality.styleQuality
            )
        )
    }

   def delete(id: String) {
        StyleQualityDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of styleDiameter
 */
case class StyleDiameter(
    id: ObjectId = new ObjectId,
    styleDiameter: String
)


object StyleDiameterDAO extends SalatDAO[StyleDiameter, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleDiameter"))


object StyleDiameter {

    def findAll(): List[StyleDiameter] = {
        StyleDiameterDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[StyleDiameter] = {
        StyleDiameterDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(styleDiameter: StyleDiameter): Option[ObjectId] = {
        StyleDiameterDAO.insert(
            StyleDiameter(
			    styleDiameter = styleDiameter.styleDiameter
            )
        )
    }

    def save(styleDiameter: StyleDiameter) = {
        StyleDiameterDAO.save(
            StyleDiameter(
		id = styleDiameter.id,
                styleDiameter = styleDiameter.styleDiameter
            )
        )
    }

   def delete(id: String) {
        StyleDiameterDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of faceType
 */
case class FaceType(
    id: ObjectId = new ObjectId,
    faceType: String
)


object FaceTypeDAO extends SalatDAO[FaceType, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("FaceType"))


object FaceType {

    def findAll(): List[FaceType] = {
        FaceTypeDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[FaceType] = {
        FaceTypeDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(faceType: FaceType): Option[ObjectId] = {
        FaceTypeDAO.insert(
            FaceType(
			    faceType = faceType.faceType
            )
        )
    }

    def save(faceType: FaceType) = {
        FaceTypeDAO.save(
            FaceType(
		id = faceType.id,
                faceType = faceType.faceType
            )
        )
    }

   def delete(id: String) {
        FaceTypeDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}