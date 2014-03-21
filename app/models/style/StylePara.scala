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
    styleLength: String,
    description: String
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
			    styleLength = styleLength.styleLength,
			    description = styleLength.description
            )
        )
    }

    def save(styleLength: StyleLength) = {
        StyleLengthDAO.save(
            StyleLength(
		id = styleLength.id,
                styleLength = styleLength.styleLength,
                description = styleLength.description
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
    styleColor: String,
    description: String
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
			    styleColor = styleColor.styleColor,
			    description = styleColor.description
            )
        )
    }

    def save(styleColor: StyleColor) = {
        StyleColorDAO.save(
            StyleColor(
		id = styleColor.id,
                styleColor = styleColor.styleColor,
                description = styleColor.description
            )
        )
    }

   def delete(id: String) {
        StyleColorDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of StyleStyleImpression
 */
case class StyleImpression(
    id: ObjectId = new ObjectId,
    styleImpression: String,
    description: String
)


object StyleImpressionDAO extends SalatDAO[StyleImpression, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleImpression"))


object StyleImpression {

    def findAll(): List[StyleImpression] = {
        StyleImpressionDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[StyleImpression] = {
        StyleImpressionDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(styleImpression: StyleImpression): Option[ObjectId] = {
        StyleImpressionDAO.insert(
            StyleImpression(
			    styleImpression = styleImpression.styleImpression,
			    description = styleImpression.description
            )
        )
    }

    def save(styleImpression: StyleImpression) = {
        StyleImpressionDAO.save(
            StyleImpression(
		id = styleImpression.id,
			styleImpression = styleImpression.styleImpression,
			description = styleImpression.description
            )
        )
    }

   def delete(id: String) {
        StyleImpressionDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of styleAmount
 */
case class StyleAmount(
    id: ObjectId = new ObjectId,
    styleAmount: String,
    description: String
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
			    styleAmount = styleAmount.styleAmount,
			    description = styleAmount.description
            )
        )
    }

    def save(styleAmount: StyleAmount) = {
        StyleAmountDAO.save(
            StyleAmount(
		id = styleAmount.id,
                styleAmount = styleAmount.styleAmount,
                description = styleAmount.description
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
    styleQuality: String,
    description: String
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
			    styleQuality = styleQuality.styleQuality,
			    description = styleQuality.description
            )
        )
    }

    def save(styleQuality: StyleQuality) = {
        StyleQualityDAO.save(
            StyleQuality(
		id = styleQuality.id,
                styleQuality = styleQuality.styleQuality,
                description = styleQuality.description
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
    styleDiameter: String,
    description: String
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
			    styleDiameter = styleDiameter.styleDiameter,
			    description = styleDiameter.description
            )
        )
    }

    def save(styleDiameter: StyleDiameter) = {
        StyleDiameterDAO.save(
            StyleDiameter(
		id = styleDiameter.id,
                styleDiameter = styleDiameter.styleDiameter,
                description = styleDiameter.description
            )
        )
    }

   def delete(id: String) {
        StyleDiameterDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}


/**
 * The table of faceShape
 */
case class FaceShape(
    id: ObjectId = new ObjectId,
    faceShape: String,
    description: String
)


object FaceShapeDAO extends SalatDAO[FaceShape, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("FaceShape"))


object FaceShape {

    def findAll(): List[FaceShape] = {
        FaceShapeDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[FaceShape] = {
        FaceShapeDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(faceShape: FaceShape): Option[ObjectId] = {
        FaceShapeDAO.insert(
            FaceShape(
			    faceShape = faceShape.faceShape,
			    description = faceShape.description
            )
        )
    }

    def save(faceShape: FaceShape) = {
        FaceShapeDAO.save(
            FaceShape(
		id = faceShape.id,
                faceShape = faceShape.faceShape,
                description = faceShape.description
            )
        )
    }

   def delete(id: String) {
        FaceShapeDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}

/**
 * The table of socialStatus
 */
case class SocialStatus(
    id: ObjectId = new ObjectId,
    socialStatus: String,
    description: String
)


object SocialStatusDAO extends SalatDAO[SocialStatus, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("SocialStatus"))


object SocialStatus {

    def findAll(): List[SocialStatus] = {
        SocialStatusDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[SocialStatus] = {
        SocialStatusDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(socialStatus: SocialStatus): Option[ObjectId] = {
        SocialStatusDAO.insert(
            SocialStatus(
			    socialStatus = socialStatus.socialStatus,
			    description = socialStatus.description
            )
        )
    }

    def save(socialStatus: SocialStatus) = {
        SocialStatusDAO.save(
            SocialStatus(
		id = socialStatus.id,
                socialStatus = socialStatus.socialStatus,
                description = socialStatus.description
            )
        )
    }

   def delete(id: String) {
        SocialStatusDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}

/**
 * The table of ageGroup
 */
case class AgeGroup(
    id: ObjectId = new ObjectId,
    ageGroup: String,
    description: String
)


object AgeGroupDAO extends SalatDAO[AgeGroup, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("AgeGroup"))


object AgeGroup {

    def findAll(): List[AgeGroup] = {
        AgeGroupDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[AgeGroup] = {
        AgeGroupDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(ageGroup: AgeGroup): Option[ObjectId] = {
        AgeGroupDAO.insert(
            AgeGroup(
			    ageGroup = ageGroup.ageGroup,
			    description = ageGroup.description
            )
        )
    }

    def save(ageGroup: AgeGroup) = {
        AgeGroupDAO.save(
            AgeGroup(
		id = ageGroup.id,
                ageGroup = ageGroup.ageGroup,
                description = ageGroup.description
            )
        )
    }

   def delete(id: String) {
        AgeGroupDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}

/**
 * The table of sex
 */
case class Sex(
    id: ObjectId = new ObjectId,
    sex: String
)


object SexDAO extends SalatDAO[Sex, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Sex"))


object Sex {

    def findAll(): List[Sex] = {
        SexDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[Sex] = {
        SexDAO.findOne(MongoDBObject("_id" -> id))
    }

    def create(sex: Sex): Option[ObjectId] = {
        SexDAO.insert(
            Sex(
			    sex = sex.sex
            )
        )
    }

    def save(sex: Sex) = {
        SexDAO.save(
            Sex(
		id = sex.id,
                sex = sex.sex
            )
        )
    }

   def delete(id: String) {
        SexDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

}