package models

import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.query.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.Imports.MongoConnection
import play.api.Play._
import play.api.PlayException

case class StylePara(
    styleImpression: List[String],
    serviceType: List[String],
    styleLength: List[String],
    styleColor: List[String],
    styleAmount: List[String],
    styleQuality: List[String],
    styleDiameter: List[String],
    faceShape: List[String],
    consumerAgeGroup: List[String],
    consumerSex: List[String],
    consumerSocialStatus: List[String])

case class Style(
    id: ObjectId = new ObjectId,
    styleName: String,
    stylistId: ObjectId,
    stylePic: List[String],
    styleImpression: List[String],
    serviceType: List[String],
    styleLength: List[String],
    styleColor: List[String],
    styleAmount: List[String],
    styleQuality: List[String],
    styleDiameter: List[String],
    faceShape: List[String],
    description: String,
    consumerAgeGroup: List[String],
    consumerSex: List[String],
    consumerSocialStatus: List[String],
    createDate: Date,
    isValid: Boolean)

object Style extends StyleDAO

trait StyleDAO extends ModelCompanion[Style, ObjectId] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("Style")

    val dao = new SalatDAO[Style, ObjectId](collection) {}

    def findByStylistId(stylistId: ObjectId): List[Style] = {
        dao.find(DBObject("stylistId" -> stylistId, "isValid" -> true)).toList
    }

    def delete(id: ObjectId) {
        dao.remove(MongoDBObject("_id" -> id))
    }

    def findByPara(style: models.Style): List[Style] = {
        
        val styleLength = if(style.styleLength.isEmpty) {"styleLength" $in Style.findParaAll.styleLength}else {"styleLength" $in style.styleLength }
        val styleColor = if(style.styleColor.isEmpty) {"styleColor" $in Style.findParaAll.styleColor}else {"styleColor" $in style.styleColor }
        val styleImpression = if(style.styleImpression.isEmpty) {"styleImpression" $in Style.findParaAll.styleImpression}else {"styleImpression" $in style.styleImpression }
        val serviceType = if(style.serviceType.isEmpty) {"serviceType" $in Style.findParaAll.serviceType}else {"serviceType" $in style.serviceType }
        val styleAmount = if(style.styleAmount.isEmpty) {"styleAmount" $in Style.findParaAll.styleAmount}else {"styleAmount" $in style.styleAmount }
        val styleQuality = if(style.styleQuality.isEmpty) {"styleQuality" $in Style.findParaAll.styleQuality}else {"styleQuality" $in style.styleQuality }
        val styleDiameter = if(style.styleDiameter.isEmpty) {"styleDiameter" $in Style.findParaAll.styleDiameter}else {"styleDiameter" $in style.styleDiameter }
        val faceShape = if(style.faceShape.isEmpty) {"faceShape" $in Style.findParaAll.faceShape}else {"faceShape" $in style.faceShape }
        val consumerSocialStatus = if(style.consumerSocialStatus.isEmpty) {"consumerSocialStatus" $in Style.findParaAll.consumerSocialStatus}else {"consumerSocialStatus" $in style.consumerSocialStatus }
        val consumerSex = if(style.consumerSex.isEmpty) {"consumerSex" $in Style.findParaAll.consumerSex}else {"consumerSex" $in style.consumerSex }
        val consumerAgeGroup = if(style.consumerAgeGroup.isEmpty) {"consumerAgeGroup" $in Style.findParaAll.consumerAgeGroup}else {"consumerAgeGroup" $in style.consumerAgeGroup }
        
        dao.find($and(
            styleLength,
            styleColor,
            styleImpression,
            serviceType,
            styleAmount,
            styleQuality,
            styleDiameter,
            faceShape,
            consumerSocialStatus,
            consumerSex,
            consumerAgeGroup,
            MongoDBObject("isValid" -> true))).toList
    }

    def updateStyle(style: models.Style) = {
        dao.update(MongoDBObject("_id" -> style.id), MongoDBObject("$set" -> (
            MongoDBObject("styleName" -> style.styleName) ++
            MongoDBObject("stylePic" -> style.stylePic) ++
            MongoDBObject("styleLength" -> style.styleLength) ++
            MongoDBObject("styleColor" -> style.styleColor) ++
            MongoDBObject("styleImpression" -> style.styleImpression) ++
            MongoDBObject("serviceType" -> style.serviceType) ++
            MongoDBObject("styleAmount" -> style.styleAmount) ++
            MongoDBObject("styleQuality" -> style.styleQuality) ++
            MongoDBObject("styleDiameter" -> style.styleDiameter) ++
            MongoDBObject("faceShape" -> style.faceShape) ++
            MongoDBObject("consumerSocialStatus" -> style.consumerSocialStatus) ++
            MongoDBObject("consumerSex" -> style.consumerSex) ++
            MongoDBObject("consumerAgeGroup" -> style.consumerAgeGroup) ++
            MongoDBObject("description" -> style.description))))
    }

    /**
     * 获取发型检索字段的主表信息
     */
    def findParaAll = {
        val paraStyleImpression = StyleImpression.findAll().toList
        var paraStyleImpressions: List[String] = Nil
        paraStyleImpression.map { para =>
            paraStyleImpressions :::= List(para.styleImpression)
        }
        val paraServiceType = ServiceType.findAll().toList
        var paraServiceTypes: List[String] = Nil
        paraServiceType.map { para =>
            paraServiceTypes :::= List(para.serviceTypeName)
        }
        val paraStyleLength = StyleLength.findAll().toList
        var paraStyleLengths: List[String] = Nil
        paraStyleLength.map { para =>
            paraStyleLengths :::= List(para.styleLength)
        }
        val paraStyleColor = StyleColor.findAll().toList
        var paraStyleColors: List[String] = Nil
        paraStyleColor.map { para =>
            paraStyleColors :::= List(para.styleColor)
        }
        val paraStyleAmount = StyleAmount.findAll().toList
        var paraStyleAmounts: List[String] = Nil
        paraStyleAmount.map { para =>
            paraStyleAmounts :::= List(para.styleAmount)
        }
        val paraStyleQuality = StyleQuality.findAll().toList
        var paraStyleQualitys: List[String] = Nil
        paraStyleQuality.map { para =>
            paraStyleQualitys :::= List(para.styleQuality)
        }
        val paraStyleDiameter = StyleDiameter.findAll().toList
        var paraStyleDiameters: List[String] = Nil
        paraStyleDiameter.map { para =>
            paraStyleDiameters :::= List(para.styleDiameter)
        }
        val paraFaceShape = FaceShape.findAll().toList
        var paraFaceShapes: List[String] = Nil
        paraFaceShape.map { para =>
            paraFaceShapes :::= List(para.faceShape)
        }
        val paraConsumerAgeGroup = AgeGroup.findAll().toList
        var paraConsumerAgeGroups: List[String] = Nil
        paraConsumerAgeGroup.map { para =>
            paraConsumerAgeGroups :::= List(para.ageGroup)
        }
        val paraConsumerSex = Sex.findAll().toList
        var paraConsumerSexs: List[String] = Nil
        paraConsumerSex.map { para =>
            paraConsumerSexs :::= List(para.sex)
        }
        val paraConsumerSocialStatus = SocialStatus.findAll().toList
        var paraConsumerSocialStatuss: List[String] = Nil
        paraConsumerSocialStatus.map { para =>
            paraConsumerSocialStatuss :::= List(para.socialStatus)
        }
        val styleList = new StylePara(paraStyleImpressions, paraServiceTypes, paraStyleLengths, paraStyleColors, paraStyleAmounts, paraStyleQualitys, paraStyleDiameters, paraFaceShapes, paraConsumerAgeGroups, paraConsumerSexs, paraConsumerSocialStatuss)
        styleList
    }

    def styleToInvalid(id: ObjectId, isValid: Boolean) = {
        dao.update(MongoDBObject("_id" -> id), MongoDBObject("$set" -> (
            MongoDBObject("isValid" -> false))))
    }
}

