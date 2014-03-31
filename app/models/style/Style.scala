package models

import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.query.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.Imports.MongoConnection
import play.api.Play._
import play.api.PlayException
import models._

case class Style(
    id: ObjectId = new ObjectId,
    styleName: String,
    stylistId: ObjectId,
    stylePic: List[OnUsePicture],
    styleImpression: String,
    serviceType: List[String],
    styleLength: String,
    styleColor: List[String],
    styleAmount: List[String],
    styleQuality: List[String],
    styleDiameter: List[String],
    faceShape: List[String],
    description: String,
    consumerAgeGroup: List[String],
    consumerSex: String,
    consumerSocialStatus: List[String],
    createDate: Date,
    isValid: Boolean)

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

case class StyleAndSalon(
    style: Style,
    salon: Salon)

object Style extends StyleDAO

trait StyleDAO extends ModelCompanion[Style, ObjectId] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("Style")

    val dao = new SalatDAO[Style, ObjectId](collection) {}

    /**
     * 通过发型师ID检索该发型师所有发型
     */
    def findByStylistId(stylistId: ObjectId): List[Style] = {
        dao.find(DBObject("stylistId" -> stylistId, "isValid" -> true)).toList
    }

    /**
     * 通过店铺ID检索该店铺所有签约的发型师
     */
    def findStylistBySalonId(salonId: ObjectId): List[models.Stylist] = {
        val salonAndStylists = SalonAndStylist.findBySalonId(salonId)
        var stylists: List[Stylist] = Nil
        salonAndStylists.map { salonAndStylist =>
            val stylist = Stylist.findOneByStylistId(salonAndStylist.stylistId)
            stylist match {
                case Some(stylist) => {
                    stylists :: List(stylist)
                }
                case None => None
            }
        }
        stylists
    }

    /**
     * 前台检索逻辑
     */
    def findByLength(styleLength: String, consumerSex: String): List[Style] = {
        dao.find(MongoDBObject("styleLength" -> styleLength, "consumerSex" -> consumerSex, "isValid" -> true)).toList
    }

    def findByImpression(styleImpression: String): List[Style] = {
        dao.find(MongoDBObject("styleImpression" -> styleImpression, "isValid" -> true)).toList
    }

    def findByPara(style: models.Style): List[Style] = {
        val styleLength = if (style.styleLength.equals("all")) { "styleLength" $in Style.findParaAll.styleLength } else { MongoDBObject("styleLength" -> style.styleLength) }
        val styleImpression = if (style.styleImpression.equals("all")) { "styleImpression" $in Style.findParaAll.styleImpression } else { MongoDBObject("styleImpression" -> style.styleImpression) }
        val styleColor = if (style.styleColor.isEmpty) { "styleColor" $in Style.findParaAll.styleColor } else { "styleColor" $in style.styleColor }
        val serviceType = if (style.serviceType.isEmpty) { "serviceType" $in Style.findParaAll.serviceType } else { "serviceType" $in style.serviceType }
        val styleAmount = if (style.styleAmount.isEmpty) { "styleAmount" $in Style.findParaAll.styleAmount } else { "styleAmount" $in style.styleAmount }
        val styleQuality = if (style.styleQuality.isEmpty) { "styleQuality" $in Style.findParaAll.styleQuality } else { "styleQuality" $in style.styleQuality }
        val styleDiameter = if (style.styleDiameter.isEmpty) { "styleDiameter" $in Style.findParaAll.styleDiameter } else { "styleDiameter" $in style.styleDiameter }
        val faceShape = if (style.faceShape.isEmpty) { "faceShape" $in Style.findParaAll.faceShape } else { "faceShape" $in style.faceShape }
        val consumerSocialStatus = if (style.consumerSocialStatus.isEmpty) { "consumerSocialStatus" $in Style.findParaAll.consumerSocialStatus } else { "consumerSocialStatus" $in style.consumerSocialStatus }
        val consumerSex = if (style.consumerSex.equals("all")) { "consumerSex" $in Style.findParaAll.consumerSex } else { MongoDBObject("consumerSex" -> style.consumerSex) }
        val consumerAgeGroup = if (style.consumerAgeGroup.isEmpty) { "consumerAgeGroup" $in Style.findParaAll.consumerAgeGroup } else { "consumerAgeGroup" $in style.consumerAgeGroup }

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

    def findSalonByStyle(stylistId: ObjectId): Option[models.Salon] = {
        val salonAndStylist = SalonAndStylist.findByStylistId(stylistId)
        var salonOne: Option[models.Salon] = None
        salonAndStylist match {
            case Some(salonAndStylist) => {
                salonOne = Salon.findById(salonAndStylist.salonId)
            }
            case None => None
        }
        salonOne
    }

    /**
     * 后台检索逻辑
     */
    def findStylesByStylistBack(style: models.Style, stylistId: ObjectId): List[Style] = {
        val styleLength = if (style.styleLength.equals("all")) { "styleLength" $in Style.findParaAll.styleLength } else { MongoDBObject("styleLength" -> style.styleLength) }
        val styleImpression = if (style.styleImpression.equals("all")) { "styleImpression" $in Style.findParaAll.styleImpression } else { MongoDBObject("styleImpression" -> style.styleImpression) }
        val styleColor = if (style.styleColor.isEmpty) { "styleColor" $in Style.findParaAll.styleColor } else { "styleColor" $in style.styleColor }
        val serviceType = if (style.serviceType.isEmpty) { "serviceType" $in Style.findParaAll.serviceType } else { "serviceType" $in style.serviceType }
        val styleAmount = if (style.styleAmount.isEmpty) { "styleAmount" $in Style.findParaAll.styleAmount } else { "styleAmount" $in style.styleAmount }
        val styleQuality = if (style.styleQuality.isEmpty) { "styleQuality" $in Style.findParaAll.styleQuality } else { "styleQuality" $in style.styleQuality }
        val styleDiameter = if (style.styleDiameter.isEmpty) { "styleDiameter" $in Style.findParaAll.styleDiameter } else { "styleDiameter" $in style.styleDiameter }
        val faceShape = if (style.faceShape.isEmpty) { "faceShape" $in Style.findParaAll.faceShape } else { "faceShape" $in style.faceShape }
        val consumerSocialStatus = if (style.consumerSocialStatus.isEmpty) { "consumerSocialStatus" $in Style.findParaAll.consumerSocialStatus } else { "consumerSocialStatus" $in style.consumerSocialStatus }
        val consumerSex = if (style.consumerSex.equals("all")) { "consumerSex" $in Style.findParaAll.consumerSex } else { MongoDBObject("consumerSex" -> style.consumerSex) }
        val consumerAgeGroup = if (style.consumerAgeGroup.isEmpty) { "consumerAgeGroup" $in Style.findParaAll.consumerAgeGroup } else { "consumerAgeGroup" $in style.consumerAgeGroup }
        
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
            MongoDBObject("isValid" -> true, "stylistId" -> stylistId))).toList
    }

    def findStylesBySalonBack(style: models.Style, salonId: ObjectId): List[Style] = {
        val styleLength = if (style.styleLength.equals("all")) { "styleLength" $in Style.findParaAll.styleLength } else { MongoDBObject("styleLength" -> style.styleLength) }
        val styleImpression = if (style.styleImpression.equals("all")) { "styleImpression" $in Style.findParaAll.styleImpression } else { MongoDBObject("styleImpression" -> style.styleImpression) }
        val styleColor = if (style.styleColor.isEmpty) { "styleColor" $in Style.findParaAll.styleColor } else { "styleColor" $in style.styleColor }
        val serviceType = if (style.serviceType.isEmpty) { "serviceType" $in Style.findParaAll.serviceType } else { "serviceType" $in style.serviceType }
        val styleAmount = if (style.styleAmount.isEmpty) { "styleAmount" $in Style.findParaAll.styleAmount } else { "styleAmount" $in style.styleAmount }
        val styleQuality = if (style.styleQuality.isEmpty) { "styleQuality" $in Style.findParaAll.styleQuality } else { "styleQuality" $in style.styleQuality }
        val styleDiameter = if (style.styleDiameter.isEmpty) { "styleDiameter" $in Style.findParaAll.styleDiameter } else { "styleDiameter" $in style.styleDiameter }
        val faceShape = if (style.faceShape.isEmpty) { "faceShape" $in Style.findParaAll.faceShape } else { "faceShape" $in style.faceShape }
        val consumerSocialStatus = if (style.consumerSocialStatus.isEmpty) { "consumerSocialStatus" $in Style.findParaAll.consumerSocialStatus } else { "consumerSocialStatus" $in style.consumerSocialStatus }
        val consumerSex = if (style.consumerSex.equals("all")) { "consumerSex" $in Style.findParaAll.consumerSex } else { MongoDBObject("consumerSex" -> style.consumerSex) }
        val consumerAgeGroup = if (style.consumerAgeGroup.isEmpty) { "consumerAgeGroup" $in Style.findParaAll.consumerAgeGroup } else { "consumerAgeGroup" $in style.consumerAgeGroup }
        val stylists = SalonAndStylist.findBySalonId(salonId)
        var stylistIds: List[ObjectId]= Nil
        stylists.map { stylist =>
            stylistIds :::= List(stylist.stylistId)
        }
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
            "stylistId" $in stylistIds,
            MongoDBObject("isValid" -> true))).toList
    }
    
    /**
     * 后台发型更新
     */
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

    /**
     * 后台发型删除
     */
    def styleToInvalid(id: ObjectId) = {
        dao.update(MongoDBObject("_id" -> id), MongoDBObject("$set" -> (
            MongoDBObject("isValid" -> false))))
    }
    
    def updateStyleImage(style: Style, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> style.id, "stylePic.picUse" -> "logo"), 
            MongoDBObject("$set" -> ( MongoDBObject("stylePic.$.fileObjId" ->  imgId))),false,true)
    }
    
    def saveStyleImage(style: Style, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> style.id, "stylePic.showPriority" -> style.stylePic.last.showPriority.get), 
            MongoDBObject("$set" -> ( MongoDBObject("stylePic.$.fileObjId" ->  imgId))),false,true)
    }
}

