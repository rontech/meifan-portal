package models

import play.api.Play.current
import play.api.PlayException
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import com.meifannet.framework.db._

/**
 * The table of styleLength
 */
case class StyleLength(
    id: ObjectId = new ObjectId,
    styleLength: String,
    description: String)

object StyleLength extends StyleLengthDAO

trait StyleLengthDAO extends MeifanNetModelCompanion[StyleLength] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("StyleLength")

    val dao = new MeifanNetDAO[StyleLength](collection) {}
}

/**
 * The table of styleColor
 */
case class StyleColor(
    id: ObjectId = new ObjectId,
    styleColor: String,
    description: String)

object StyleColor extends StyleColorDAO

trait StyleColorDAO extends MeifanNetModelCompanion[StyleColor] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("StyleColor")

    val dao = new MeifanNetDAO[StyleColor](collection) {}
}

/**
 * The table of StyleStyleImpression
 */
case class StyleImpression(
    id: ObjectId = new ObjectId,
    styleImpression: String,
    description: String)

object StyleImpression extends StyleImpressionDAO

trait StyleImpressionDAO extends MeifanNetModelCompanion[StyleImpression] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("StyleImpression")

    val dao = new MeifanNetDAO[StyleImpression](collection) {}
}

/**
 * The table of styleAmount
 */
case class StyleAmount(
    id: ObjectId = new ObjectId,
    styleAmount: String,
    description: String)

object StyleAmount extends StyleAmountDAO

trait StyleAmountDAO extends MeifanNetModelCompanion[StyleAmount] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("StyleAmount")

    val dao = new MeifanNetDAO[StyleAmount](collection) {}
}

/**
 * The table of styleQuality
 */
case class StyleQuality(
    id: ObjectId = new ObjectId,
    styleQuality: String,
    description: String)

object StyleQuality extends StyleQualityDAO

trait StyleQualityDAO extends MeifanNetModelCompanion[StyleQuality] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("StyleQuality")

    val dao = new MeifanNetDAO[StyleQuality](collection) {}
}

/**
 * The table of styleDiameter
 */
case class StyleDiameter(
    id: ObjectId = new ObjectId,
    styleDiameter: String,
    description: String)

object StyleDiameter extends StyleDiameterDAO

trait StyleDiameterDAO extends MeifanNetModelCompanion[StyleDiameter] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("StyleDiameter")

    val dao = new MeifanNetDAO[StyleDiameter](collection) {}
}

/**
 * The table of faceShape
 */
case class FaceShape(
    id: ObjectId = new ObjectId,
    faceShape: String,
    description: String)

object FaceShape extends FaceShapeDAO

trait FaceShapeDAO extends MeifanNetModelCompanion[FaceShape] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("FaceShape")

    val dao = new MeifanNetDAO[FaceShape](collection) {}
}

/**
 * The table of socialStatus
 */
case class SocialStatus(
    id: ObjectId = new ObjectId,
    socialStatus: String,
    description: String)

object SocialStatus extends SocialStatusDAO

trait SocialStatusDAO extends MeifanNetModelCompanion[SocialStatus] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("SocialStatus")
    val dao = new MeifanNetDAO[SocialStatus](collection) {}
}

/**
 * The table of ageGroup
 */
case class AgeGroup(
    id: ObjectId = new ObjectId,
    ageGroup: String,
    description: String)

object AgeGroup extends AgeGroupDAO

trait AgeGroupDAO extends MeifanNetModelCompanion[AgeGroup] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("AgeGroup")

    val dao = new MeifanNetDAO[AgeGroup](collection) {}
}

/**
 * The table of sex
 */
case class Sex(
    id: ObjectId = new ObjectId,
    sex: String)

object Sex extends SexDAO

trait SexDAO extends MeifanNetModelCompanion[Sex] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("Sex")

    val dao = new MeifanNetDAO[Sex](collection) {}
}

/**
 * The table of searchByLengthForF
 */
case class SearchByLengthForF(
    id: ObjectId = new ObjectId,
    sex: String,
    styleLength: String,
    stylePic: ObjectId,
    description: String)

object SearchByLengthForF extends SearchByLengthForFDAO

trait SearchByLengthForFDAO extends MeifanNetModelCompanion[SearchByLengthForF] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("SearchByLengthForF")

    val dao = new MeifanNetDAO[SearchByLengthForF](collection) {}
    //保存图片
    def saveSearchByLengthForFImage(searchByLengthForF: SearchByLengthForF, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> searchByLengthForF.id), 
            MongoDBObject("$set" -> ( MongoDBObject("stylePic" ->  imgId))),false,true)
    }
}

/**
 * The table of searchByLengthForM
 */
case class SearchByLengthForM(
    id: ObjectId = new ObjectId,
    sex: String,
    styleLength: String,
    stylePic: ObjectId,
    description: String)

object SearchByLengthForM extends SearchByLengthForMDAO

trait SearchByLengthForMDAO extends MeifanNetModelCompanion[SearchByLengthForM] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("SearchByLengthForM")

    val dao = new MeifanNetDAO[SearchByLengthForM](collection) {}
    //保存图片
    def saveSearchByLengthForMImage(searchByLengthForM: SearchByLengthForM, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> searchByLengthForM.id), 
            MongoDBObject("$set" -> ( MongoDBObject("stylePic" ->  imgId))),false,true)
    }
}

/**
 * The table of searchByImpression
 */
case class SearchByImpression(
    id: ObjectId = new ObjectId,
    sex: String,
    styleImpression: String,
    stylePic: ObjectId,
    description: String)

object SearchByImpression extends SearchByImpressionDAO

trait SearchByImpressionDAO extends MeifanNetModelCompanion[SearchByImpression] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("SearchByImpression")

    val dao = new MeifanNetDAO[SearchByImpression](collection) {}
    //保存图片
    def saveSearchByImpressionImage(searchByImpression: SearchByImpression, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> searchByImpression.id), 
            MongoDBObject("$set" -> ( MongoDBObject("stylePic" ->  imgId))),false,true)
    }
}
