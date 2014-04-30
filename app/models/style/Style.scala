package models

import java.util.Date
import com.mongodb.casbah.query.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import play.api.Play._
import play.api.PlayException
import models._
import com.meifannet.framework.db._

trait StyleIdUsed {
    val styleId: Option[ObjectId]
}

/*------------------------
 * Assistant Class: For Style
 *------------------------*/
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

/*------------------------
 * Main Class: Style
 *------------------------*/
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

object Style extends MeifanNetModelCompanion[Style] {

    val dao = new MeifanNetDAO[Style](collection = loadCollection()) {}

    /**
     * 通过发型师ID检索该发型师所有发型
     */
    def findByStylistId(stylistId: ObjectId): List[Style] = {
        dao.find(DBObject("stylistId" -> stylistId, "isValid" -> true)).toList.sortBy(_.createDate).reverse
    }

    /**
     * 通过styleId判断该发型是否有效
     */
    def findByStyleId(id: ObjectId) = {
        dao.findOne(DBObject("_id" -> id, "isValid" -> true))
    }

    /**
     * 通过发型师ID和发型适合性别检索该发型师所有发型
     */
    def findByStylistIdAndSex(stylistId: ObjectId, sex: String): List[Style] = {
        if (sex.equals("all")) {
            dao.find(DBObject("stylistId" -> stylistId, "isValid" -> true)).toList.sortBy(_.createDate).reverse
        } else {
            dao.find(DBObject("stylistId" -> stylistId, "consumerSex" -> sex, "isValid" -> true)).toList.sortBy(_.createDate).reverse
        }
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
                    stylists :::= List(stylist)
                }
                case None => None
            }
        }
        stylists
    }

    /**
     * 前台检索逻辑
     * 导航栏，男女式发型长度快捷
     */
    def findByLength(styleLength: String, consumerSex: String): List[Style] = {
        dao.find(MongoDBObject("styleLength" -> styleLength, "consumerSex" -> consumerSex, "isValid" -> true)).toList
    }

    /**
     * 前台检索逻辑
     * 导航栏，女式发型风格快捷
     */
    def findByImpression(styleImpression: String, consumerSex: String): List[Style] = {
        dao.find(MongoDBObject("styleImpression" -> styleImpression, "consumerSex" -> consumerSex, "isValid" -> true)).toList
    }

    /**
     * 前台检索逻辑
     * 前台综合排名检索-热度
     */
    def findByRanking: List[models.Style] = {
        val reservationAll = Reservation.findByStatusAndStyleId
        var reservations: List[models.Reservation] = Nil
        reservationAll.map { reservation =>
            if (SalonAndStylist.findByStylistId(reservation.stylistId.get) != None) {
                if (findByStyleId(reservation.styleId.get).nonEmpty) {
                    reservations = reservation :: reservations
                }
            }
        }
        val styles: List[models.Style] = sortForRanking(reservations)
        styles
    }

    /**
     * 前台检索逻辑
     * 前台热度加女士长度排名检索
     */
    def findByRankingAndLengthForF(styleLength: String, consumerSex: String): List[models.Style] = {
        val reservationAll = Reservation.findByStatusAndStyleId
        var reservations: List[models.Reservation] = Nil
        reservationAll.map { reservation =>
            if (SalonAndStylist.findByStylistId(reservation.stylistId.get) != None) {
                findByStyleId(reservation.styleId.get) match {
                    case Some(style) => {
                        if (style.styleLength.equals(styleLength) && style.consumerSex.equals(consumerSex)) {
                            reservations :::= List(reservation)
                        }
                    }
                    case None => None
                }
            }
        }
        val styles: List[models.Style] = sortForRanking(reservations)
        styles
    }

    /**
     * 前台检索逻辑
     * 前台热度加性别排名检索
     */
    def findByRankingAndSex(consumerSex: String): List[models.Style] = {
        val reservationAll = Reservation.findByStatusAndStyleId
        var reservations: List[models.Reservation] = Nil
        reservationAll.map { reservation =>
            if (SalonAndStylist.findByStylistId(reservation.stylistId.get) != None) {
                findByStyleId(reservation.styleId.get) match {
                    case Some(style) => {
                        if (style.consumerSex.equals(consumerSex)) {
                            reservations :::= List(reservation)
                        }
                    }
                    case None => None
                }
            }
        }
        val styles: List[models.Style] = sortForRanking(reservations)
        styles
    }

    /**
     * 前台检索逻辑
     * ranking分组排序
     */
    def sortForRanking(reservationAll: List[models.Reservation]): List[models.Style] = {
        var styles: List[models.Style] = Nil
        if (reservationAll.nonEmpty) {
            val reservations = reservationAll.sortBy(_.styleId)
            var lists: List[(ObjectId, Int)] = Nil
            var count = 1
            var styleId = reservations(0).styleId.get
            for (i <- 0 to reservations.length - 1) {
                if (i > 0) {
                    if (reservations(i).styleId.equals(reservations(i - 1).styleId)) {
                        count = count + 1
                    } else {
                        styleId = reservations(i - 1).styleId.get
                        lists :::= List((styleId, count))
                        styleId = reservations(i).styleId.get
                        count = 1
                    }
                }
            }
            lists :::= List((styleId, count))
            val listAll = lists.sortWith((f, s) => f._2 < s._2) //递增排序
            //val listAll =   lists.sortBy(_._2).reverse  此方法同样可以实现
            listAll.map { list =>
                val style = Style.findOneById(list._1).get
                styles = style :: styles
            }
        }
        styles
    }

    /**
     * 取得指定店铺的最热发型前N名
     * data: 统计依据数据，比如预约的数据，或者消费的数据
     * N = 0, 默认值，为取得所有
     */
    def findTopStylesInSalon[T <: StyleIdUsed](data: List[T], topN: Int = 0): List[Style] = {
        // sort by reserved counts.
        val styleWithCnt = data.groupBy(x => x.styleId).map(y => (y._1, y._2.length)).toList.filter(_._1 != None).sortWith(_._2 > _._2)
        // get all stylesId or only top N stylesId of a salon according the topN parameters.  
        val hot = if (topN == 0) styleWithCnt else styleWithCnt.slice(0, topN)
        var hotStyles: List[Style] = Nil
        // get all styles of a salon.  
        hot.map { itm =>
            val stl = Style.findOneById(itm._1.getOrElse(new ObjectId))
            stl match {
                case Some(style) => hotStyles :::= List(style)
                case None => hotStyles
            }
        }
        // return
        hotStyles
    }

    /**
     * 取得指定店铺的最热发型前N名
     * N = 0, 默认值，为取得所有
     */
    def getBestRsvedStylesInSalon(sid: ObjectId, topN: Int = 0): List[Style] = {
        // get the reservation with which we can get the styles be reserved.
        val rsvs = Reservation.findAllReservation(sid)
        // use the exists method to get top styles.
        val bestRsved = findTopStylesInSalon(rsvs, topN)
        // If there is no reservation styles yet, get the latest styles in the salon.
        val others = Salon.getAllStyles(sid)
        (bestRsved ::: others).distinct
    }

    /**
     * 前台检索逻辑
     * 前台详细检索
     */
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

    /**
     * 通过发型中的技师ID查询其绑定的店铺
     */
    def findSalonByStyle(stylistId: ObjectId): Option[models.Salon] = {
        val salonAndStylist = SalonAndStylist.findByStylistId(stylistId)
        var salonOne: Option[models.Salon] = None
        salonAndStylist match {
            case Some(salonAndStylist) => {
                salonOne = Salon.findOneById(salonAndStylist.salonId)
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
            MongoDBObject("isValid" -> true, "stylistId" -> stylistId))).toList.sortBy(_.createDate).reverse
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
        //利用传过来的stylistId判断后台检索是检索某一发型师的发型，还是检索店铺全部发型师的发型
        val stylists = SalonAndStylist.findBySalonId(salonId)
        var stylistIds: List[ObjectId] = Nil
        stylists.map { stylist =>
            stylistIds :::= List(stylist.stylistId)
        }
        if (stylistIds.contains(style.stylistId)) {
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
                MongoDBObject("stylistId" -> style.stylistId, "isValid" -> true))).toList.sortBy(_.createDate).reverse
        } else {
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
                MongoDBObject("isValid" -> true))).toList.sortBy(_.createDate).reverse
        }
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
        val paraServiceType = ServiceType.findAllServiceType("Hairdressing").toList
        var paraServiceTypes: List[String] = Nil
        paraServiceType.map { para =>
            paraServiceTypes :::= List(para)
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
            MongoDBObject("$set" -> (MongoDBObject("stylePic.$.fileObjId" -> imgId))), false, true)
    }

    def saveStyleImage(style: Style, imgId: ObjectId) = {
        dao.update(MongoDBObject("_id" -> style.id, "stylePic.showPriority" -> style.stylePic.last.showPriority.get),
            MongoDBObject("$set" -> (MongoDBObject("stylePic.$.fileObjId" -> imgId))), false, true)
    }
    
    def isExist(value:String, stylistId:String, f:(String,String) => Option[Style]) = f(value,stylistId).map(style => true).getOrElse(false)
    
    def checkStyleIsExist(name:String,stylistId:ObjectId): Boolean= {
        dao.find(MongoDBObject("styleName" -> name, "stylistId" -> stylistId)).hasNext
    }
}

