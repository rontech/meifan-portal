package models

import play.api.Play.current
import play.api.PlayException
import com.mongodb.casbah.commons.Imports._
import mongoContext._
import com.meifannet.framework.db._

/**
 * The table of styleLength
 */
case class StyleLength(
    id: ObjectId = new ObjectId,
    styleLength: String,
    description: String)

object StyleLength extends MeifanNetModelCompanion[StyleLength] {
    val dao = new MeifanNetDAO[StyleLength](collection = loadCollection()) {}
}

/**
 * The table of styleColor
 */
case class StyleColor(
    id: ObjectId = new ObjectId,
    styleColor: String,
    description: String)

object StyleColor extends MeifanNetModelCompanion[StyleColor] {
    val dao = new MeifanNetDAO[StyleColor](collection = loadCollection()) {}
}

/**
 * The table of StyleStyleImpression
 */
case class StyleImpression(
    id: ObjectId = new ObjectId,
    styleImpression: String,
    description: String)

object StyleImpression extends MeifanNetModelCompanion[StyleImpression] {
    val dao = new MeifanNetDAO[StyleImpression](collection = loadCollection()) {}
}

/**
 * The table of styleAmount
 */
case class StyleAmount(
    id: ObjectId = new ObjectId,
    styleAmount: String,
    description: String)

object StyleAmount extends MeifanNetModelCompanion[StyleAmount] {
    val dao = new MeifanNetDAO[StyleAmount](collection = loadCollection()) {}
}

/**
 * The table of styleQuality
 */
case class StyleQuality(
    id: ObjectId = new ObjectId,
    styleQuality: String,
    description: String)

object StyleQuality extends MeifanNetModelCompanion[StyleQuality] {
    val dao = new MeifanNetDAO[StyleQuality](collection = loadCollection()) {}
}

/**
 * The table of styleDiameter
 */
case class StyleDiameter(
    id: ObjectId = new ObjectId,
    styleDiameter: String,
    description: String)

object StyleDiameter extends MeifanNetModelCompanion[StyleDiameter] {
    val dao = new MeifanNetDAO[StyleDiameter](collection = loadCollection()) {}
}

/**
 * The table of faceShape
 */
case class FaceShape(
    id: ObjectId = new ObjectId,
    faceShape: String,
    description: String)

object FaceShape extends MeifanNetModelCompanion[FaceShape] {
    val dao = new MeifanNetDAO[FaceShape](collection = loadCollection()) {}
}

/**
 * The table of socialStatus
 */
case class SocialStatus(
    id: ObjectId = new ObjectId,
    socialStatus: String,
    description: String)

object SocialStatus extends MeifanNetModelCompanion[SocialStatus] {
    val dao = new MeifanNetDAO[SocialStatus](collection = loadCollection()) {}
}

/**
 * The table of ageGroup
 */
case class AgeGroup(
    id: ObjectId = new ObjectId,
    ageGroup: String,
    description: String)

object AgeGroup extends MeifanNetModelCompanion[AgeGroup] {
    val dao = new MeifanNetDAO[AgeGroup](collection = loadCollection()) {}
}

/**
 * The table of sex
 */
case class Sex(
    id: ObjectId = new ObjectId,
    sex: String)

object Sex extends MeifanNetModelCompanion[Sex] {
    val dao = new MeifanNetDAO[Sex](collection = loadCollection()) {}
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

object SearchByLengthForF extends MeifanNetModelCompanion[SearchByLengthForF] {
    val dao = new MeifanNetDAO[SearchByLengthForF](collection = loadCollection()) {}
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

object SearchByLengthForM extends MeifanNetModelCompanion[SearchByLengthForM] {
    val dao = new MeifanNetDAO[SearchByLengthForM](collection = loadCollection()) {}
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

object SearchByImpression extends MeifanNetModelCompanion[SearchByImpression] {
    val dao = new MeifanNetDAO[SearchByImpression](collection = loadCollection()) {}
    //保存图片
    def saveSearchByImpressionImage(searchByImpression: SearchByImpression, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> searchByImpression.id), 
            MongoDBObject("$set" -> ( MongoDBObject("stylePic" ->  imgId))),false,true)
    }
}
