package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._

/**
 * The table of styleLength
 */
case class StyleLength(
    id: ObjectId = new ObjectId,
    styleLength: String,
    description: String
)

object StyleLength extends StyleLengthDAO

trait StyleLengthDAO extends ModelCompanion[StyleLength, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleLength")
  
  val dao = new SalatDAO[StyleLength, ObjectId](collection){}
}


/**
 * The table of styleColor
 */
case class StyleColor(
    id: ObjectId = new ObjectId,
    styleColor: String,
    description: String
)

object StyleColor extends StyleColorDAO

trait StyleColorDAO extends ModelCompanion[StyleColor, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleColor")
  
  val dao = new SalatDAO[StyleColor, ObjectId](collection){}
}


/**
 * The table of StyleStyleImpression
 */
case class StyleImpression(
    id: ObjectId = new ObjectId,
    styleImpression: String,
    description: String
)

object StyleImpression extends StyleImpressionDAO

trait StyleImpressionDAO extends ModelCompanion[StyleImpression, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleImpression")
  
  val dao = new SalatDAO[StyleImpression, ObjectId](collection){}
}


/**
 * The table of styleAmount
 */
case class StyleAmount(
    id: ObjectId = new ObjectId,
    styleAmount: String,
    description: String
)

object StyleAmount extends StyleAmountDAO

trait StyleAmountDAO extends ModelCompanion[StyleAmount, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleAmount")
  
  val dao = new SalatDAO[StyleAmount, ObjectId](collection){}
}


/**
 * The table of styleQuality
 */
case class StyleQuality(
    id: ObjectId = new ObjectId,
    styleQuality: String,
    description: String
)

object StyleQuality extends StyleQualityDAO

trait StyleQualityDAO extends ModelCompanion[StyleQuality, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleQuality")
  
  val dao = new SalatDAO[StyleQuality, ObjectId](collection){}
}


/**
 * The table of styleDiameter
 */
case class StyleDiameter(
    id: ObjectId = new ObjectId,
    styleDiameter: String,
    description: String
)

object StyleDiameter extends StyleDiameterDAO

trait StyleDiameterDAO extends ModelCompanion[StyleDiameter, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("StyleDiameter")
  
  val dao = new SalatDAO[StyleDiameter, ObjectId](collection){}
}


/**
 * The table of faceShape
 */
case class FaceShape(
    id: ObjectId = new ObjectId,
    faceShape: String,
    description: String
)

object FaceShape extends FaceShapeDAO

trait FaceShapeDAO extends ModelCompanion[FaceShape, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("FaceShape")
  
  val dao = new SalatDAO[FaceShape, ObjectId](collection){}
}

/**
 * The table of socialStatus
 */
case class SocialStatus(
    id: ObjectId = new ObjectId,
    socialStatus: String,
    description: String
)

object SocialStatus extends SocialStatusDAO

trait SocialStatusDAO extends ModelCompanion[SocialStatus, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("SocialStatus")
  
  val dao = new SalatDAO[SocialStatus, ObjectId](collection){}
}

/**
 * The table of ageGroup
 */
case class AgeGroup(
    id: ObjectId = new ObjectId,
    ageGroup: String,
    description: String
)

object AgeGroup extends AgeGroupDAO

trait AgeGroupDAO extends ModelCompanion[AgeGroup, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("AgeGroup")
  
  val dao = new SalatDAO[AgeGroup, ObjectId](collection){}
}

/**
 * The table of sex
 */
case class Sex(
    id: ObjectId = new ObjectId,
    sex: String
)

object Sex extends SexDAO

trait SexDAO extends ModelCompanion[Sex, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Sex")
  
  val dao = new SalatDAO[Sex, ObjectId](collection){}
}
