/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
package models

import java.util.Date
import com.mongodb.casbah.query.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import play.api.Play._
import play.api.PlayException
import models._
import com.meifannet.framework.db._
import com.mongodb.casbah.commons.Imports.{ DBObject => commonsDBObject }

trait StyleIdUsed {
  val styleId: Option[ObjectId]
}

/*------------------------
 * Assistant Class: For Style
 *------------------------*/
/**
 * 发型主表字段整合类
 * @param styleImpression 发型风格主表数据
 * @param serviceType 发型技术类别主表数据
 * @param styleLength 发型适合长度主表数据
 * @param styleColor 发型颜色主表数据
 * @param styleAmount 发量主表数据
 * @param styleQuality 发质主表数据
 * @param styleDiameter 头发直径主表数据
 * @param faceShape 适合脸型主表数据
 * @param consumerAgeGroup 发型适合年龄段主表数据
 * @param consumerSex 发型适合性别主表数据
 * @param consumerSocialStatus 发型适合场合主表数据
 * @param stylePicDescription 发型图片描述主表数据（用于发型展示）
 */
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
  consumerSocialStatus: List[String],
  stylePicDescription: List[String])

/**
 * 发型与店铺数据整合类
 * @param style 单发型所有数据
 * @param salon 单店铺所有数据
 */
case class StyleAndSalon(
  style: Style,
  salon: Salon)

/**
 * 发型数据及其他一些表中字段整合类
 * @param style 发型数据
 * @param salonId 店铺ID
 * @param salonName 店铺名
 * @param salonNameAbbr 店铺昵称
 * @param stylistId 技师ID
 * @param stylistNickname 技师昵称
 */
case class StyleWithAllInfo(
  style: Style,
  salonId: ObjectId,
  salonName: String,
  salonNameAbbr: String,
  stylistId: ObjectId,
  stylistNickname: String)

/*------------------------
 * Main Class: Style
 *------------------------*/
/**
 * 发型主类
 * @param id 发型ID
 * @param styleName 发型名
 * @param stylistId 该发型的技师ID
 * @param stylePic 发型图片
 * @param styleImpression 发型风格
 * @param serviceType 发型技术类别
 * @param styleLength 发型适合长度
 * @param styleColor 发型颜色
 * @param styleAmount 发量
 * @param styleQuality 发质
 * @param styleDiameter 头发直径-粗细
 * @param faceShape 适合脸型
 * @param description 发型描述
 * @param consumerAgeGroup 适合年龄段
 * @param consumerSex 适合性别
 * @param consumerSocialStatus 适合场合
 * @param createDate 发型创建时间
 * @param isValid 是否有效
 */
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
   * @param stylistId 技师ID
   * @return 符合条件的所有发型
   */
  def findByStylistId(stylistId: ObjectId): List[Style] = {
    dao.find(DBObject("stylistId" -> stylistId, "isValid" -> true)).toList.sortBy(_.createDate).reverse
  }

  /**
   * 通过styleId判断该发型是否有效
   * @param id 发型ID
   * @return 符合条件的所有发型
   */
  def findByStyleId(id: ObjectId) = {
    dao.findOne(DBObject("_id" -> id, "isValid" -> true))
  }

  /**
   * 通过发型师ID和发型适合性别检索该发型师所有发型
   * @param stylistId 技师ID
   * @param sex 适合性别
   * @return List[Style] 符合条件的所有发型
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
   * @param salonId 店铺ID
   * @return List[Stylist] 该店铺所有技师
   */
  def findStylistBySalonId(salonId: ObjectId): List[Stylist] = {
    //获取店铺绑与技师关系表中该店铺相关的所有有效数据
    val salonAndStylists = SalonAndStylist.findBySalonId(salonId)
    var stylists: List[Stylist] = Nil
    salonAndStylists.map { salonAndStylist =>
      //通过技师ID获取技师信息
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
   * 通过店铺ID和发型适合性别检索该店铺所有符合条件的发型
   * @param salonId 店铺ID
   * @param sex 适合性别
   * @return List[Style] 该店铺所有符合性别要求的发型
   */
  def findStylesBySalonAndSex(salonId: ObjectId, sex: String): List[Style] = {
    //查询该店铺绑定的所有技师
    val stylists = Style.findStylistBySalonId(salonId)
    var styles: List[Style] = Nil
    //查询每个技师的所有有效发型，并将发型整合
    stylists.map { sty =>
      var style = Style.findByStylistIdAndSex(sty.stylistId, sex)
      styles :::= style
    }
    // order by create time desc.
    styles.sortBy(_.createDate).reverse
  }

  /**
   * 前台检索逻辑
   * 用于前台导航栏根据性别和长度检索发型
   * @param styleLength 适合长度
   * @param consumerSex 适合性别
   * @param limitCnt 检索结果限制数量
   * @return List[StyleWithAllInfo] 发型及相关表中数据整合类
   */
  def findByLength(styleLength: String, consumerSex: String, limitCnt: Int = 0): List[StyleWithAllInfo] = {
    val bstLength = dao.find(MongoDBObject("styleLength" -> styleLength, "consumerSex" -> consumerSex, "isValid" -> true)).toList
    val lenStyles = bstLength.map { _.id }
    val styleInfo: List[StyleWithAllInfo] = getStyleInfoFromRanking(lenStyles)(limitCnt)(x => true)

    styleInfo
  }

  /**
   * 前台检索逻辑
   * 用于前台导航栏根据性别和风格检索发型
   * @param styleImpression 适合风格
   * @param consumerSex 适合性别
   * @param limitCnt 检索结果限制数量
   * @return List[StyleWithAllInfo] 发型及相关表中数据整合类
   */
  def findByImpression(styleImpression: String, consumerSex: String, limitCnt: Int = 0): List[StyleWithAllInfo] = {
    val bstImp = dao.find(MongoDBObject("styleImpression" -> styleImpression, "consumerSex" -> consumerSex, "isValid" -> true)).toList
    val impStyles = bstImp.map { _.id }
    val styleInfo: List[StyleWithAllInfo] = getStyleInfoFromRanking(impStyles)(limitCnt)(x => true)

    styleInfo
  }

  /**
   * 前台检索逻辑
   * 前台综合排名检索-按热度
   * @param limitCnt 检索结果限制数量
   * @return List[StyleWithAllInfo] 发型及相关表中数据整合类
   */
  def findByRanking(limitCnt: Int = 0): List[StyleWithAllInfo] = {
    // get all reservations with styleId, ignore the data without style.
    val bestRsv = Reservation.findBestReservedStyles(0)
    val styleInfo: List[StyleWithAllInfo] = getStyleInfoFromRanking(bestRsv)(limitCnt)(x => true)

    styleInfo
  }

  /**
   * 前台检索逻辑
   * 前台排名检索-按热度、长度、性别
   * @param styleLength 发型长度
   * @param consumerSex 适合性别
   * @param limitCnt 检索结果限制数量
   * @return List[StyleWithAllInfo] 发型及相关表中数据整合类
   */
  def findByRankingAndLenAndSex(styleLength: String, consumerSex: String, limitCnt: Int = 0): List[StyleWithAllInfo] = {
    // get all reservations with styleId, ignore the data without style.
    val bestRsv = Reservation.findBestReservedStyles(0)

    val styleInfo: List[StyleWithAllInfo] = getStyleInfoFromRanking(bestRsv)(limitCnt)(x =>
      (x.styleLength == styleLength) && (x.consumerSex == consumerSex))

    styleInfo
  }

  /**
   * Get Style Info with salon id/name, stylist id/name
   * @param styleIds the sorted list of stylist id
   * @param limitCnt the required cnt of result, Top N.
   * @param filter function to filter the result.
   * @return List[StyleWithAllInfo] 发型及相关表中数据整合类
   */
  def getStyleInfoFromRanking(styleIds: List[ObjectId])(limitCnt: Int = 0)(filter: Style => Boolean): List[StyleWithAllInfo] = {
    var styleInfo: List[StyleWithAllInfo] = Nil
    var cnt: Int = 0
    for (styleId <- styleIds) {
      if (limitCnt == 0 || cnt <= limitCnt) {
        val style = Style.findOneById(styleId)
        // Filter the data by function filter.
        if (style != None && filter(style.get)) {
          // only when the stylist is in workship with a salon, the style can be searched by ranking.
          val stlstInSalon = SalonAndStylist.findByStylistId(style.get.stylistId)
          if (stlstInSalon != None) {
            val stylistName = Stylist.findUserName(stlstInSalon.get.stylistId)
            val salon = Salon.findOneById(stlstInSalon.get.salonId)
            val st = StyleWithAllInfo(style.get, salon.get.id, salon.get.salonName, salon.get.salonNameAbbr.getOrElse(salon.get.salonName),
              stlstInSalon.get.stylistId, stylistName)
            styleInfo = styleInfo ::: List(st)
            cnt += 1
          }
        }
      }
    }

    styleInfo
  }

  /**
   * 前台检索逻辑
   * 前台排名检索-按热度、性别
   * @param consumerSex 适合性别
   * @param limitCnt 检索结果限制数量
   * @return List[StyleWithAllInfo] 发型及相关表中数据整合类
   */
  def findByRankingAndSex(consumerSex: String, limitCnt: Int = 0): List[StyleWithAllInfo] = {
    // get all reservations with styleId, ignore the data without style.
    val bestRsv = Reservation.findBestReservedStyles(0)
    var styleInfo: List[StyleWithAllInfo] = getStyleInfoFromRanking(bestRsv)(limitCnt)(x =>
      (x.consumerSex == consumerSex))

    styleInfo
  }

  /**
   * 前台检索逻辑
   * ranking分组排序 用于依据预约次数来获得热门发型
   * @param reservationAll 预约结果
   * @return
   */
  /*def sortForRanking(reservationAll: List[Reservation]): List[Style] = {
    var styles: List[Style] = Nil
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
  }*/

  /**
   * 取得指定店铺的最热发型前N名
   * data: 统计依据数据，比如预约的数据，或者消费的数据
   * N = 0, 默认值，为取得所有
   */
  /*
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
  */

  def findTopStylesInSalon(hottestStyles: List[ObjectId], topN: Int = 0): List[Style] = {
    var hotStyles: List[Style] = Nil
    // get all styles of a salon.  
    hottestStyles.map { stid =>
      val stl = Style.findOneById(stid)
      stl match {
        case Some(style) => hotStyles = hotStyles ::: List(style)
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
    //val rsvs = Reservation.findAllReservation(sid)
    val rsvs = Reservation.findBestReservedStylesInSalon(sid, topN)
    // use the exists method to get top styles.
    val bestRsved = findTopStylesInSalon(rsvs, topN)
    // If there is no reservation styles yet, get the latest styles in the salon.
    val others = Salon.getAllStyles(sid)
    (bestRsved ::: others).distinct
  }

  /**
   * 前台检索逻辑
   * 前台详细检索 -通过输入的检索条件
   * @param style 发型检索条件
   * @param limitCnt 检索结果限制数量
   * @return List[StyleWithAllInfo] 发型及相关表中数据整合类
   */
  def findByPara(style: Style, limitCnt: Int = 0): List[StyleWithAllInfo] = {
    //发型主要检索条件
    var srchConds = commonSrchConds(style)
    val srchedStls = dao.find($and(srchConds)).toList
    val srchStyleIds = srchedStls.map { _.id }
    val styleInfo: List[StyleWithAllInfo] = getStyleInfoFromRanking(srchStyleIds)(limitCnt)(x => true)

    styleInfo
  }

  /**
   * 发型主要检索条件整合
   * @param style 发型检索条件
   * @return List[commonsDBObject] 主要的检索字段整合结果
   */
  def commonSrchConds(style: Style): List[commonsDBObject] = {
    var srchConds: List[commonsDBObject] = Nil
    if (!style.styleLength.equals("all")) {
      srchConds :::= List(commonsDBObject("styleLength" -> style.styleLength))
    }
    if (!style.styleImpression.equals("all")) {
      srchConds :::= List(commonsDBObject("styleImpression" -> style.styleImpression))
    }
    if (style.styleColor.nonEmpty) {
      srchConds :::= List("styleColor" $in style.styleColor)
    }
    if (style.serviceType.nonEmpty) {
      srchConds :::= List("serviceType" $in style.serviceType)
    }
    if (style.styleAmount.nonEmpty) {
      srchConds :::= List("styleAmount" $in style.styleAmount)
    }
    if (style.styleQuality.nonEmpty) {
      srchConds :::= List("styleQuality" $in style.styleQuality)
    }
    if (style.styleDiameter.nonEmpty) {
      srchConds :::= List("styleDiameter" $in style.styleDiameter)
    }
    if (style.faceShape.nonEmpty) {
      srchConds :::= List("faceShape" $in style.faceShape)
    }
    if (style.consumerSocialStatus.nonEmpty) {
      srchConds :::= List("consumerSocialStatus" $in style.consumerSocialStatus)
    }
    if (!style.consumerSex.equals("all")) {
      srchConds :::= List(commonsDBObject("consumerSex" -> style.consumerSex))
    }
    if (style.consumerAgeGroup.nonEmpty) {
      srchConds :::= List("consumerAgeGroup" $in style.consumerAgeGroup)
    }
    srchConds :::= List(commonsDBObject("isValid" -> true))
    srchConds
  }

  /**
   * 通过发型中的技师ID查询其绑定的店铺
   * @param stylistId 技师ID
   * @return Option[Salon] 该技师绑定的店铺
   */
  def findSalonByStyle(stylistId: ObjectId): Option[Salon] = {
    val salonAndStylist = SalonAndStylist.findByStylistId(stylistId)
    var salonOne: Option[Salon] = None
    salonAndStylist match {
      case None => None
      case Some(salonAndStylist) => {
        salonOne = Salon.findOneById(salonAndStylist.salonId)
      }
    }
    salonOne
  }

  /**
   * 后台检索逻辑
   * 通过技师ID和检索条件检索该技师的发型
   * @param style 发型检索条件
   * @param stylistId 技师ID
   * @return List[Style] 符合条件的所有发型
   */
  def findStylesByStylistBack(style: Style, stylistId: ObjectId): List[Style] = {
    //发型主要检索条件
    var srchConds = commonSrchConds(style)
    //追加检索条件技师ID
    srchConds :::= List(commonsDBObject("stylistId" -> stylistId))
    dao.find($and(srchConds)).toList.sortBy(_.createDate).reverse
  }

  /**
   * 后台检索逻辑
   * 通过店铺ID和检索条件检索该技师的发型
   * @param style 发型检索条件
   * @param salonId 店铺ID
   * @return List[Style] 符合条件的所有发型
   */
  def findStylesBySalonBack(style: Style, salonId: ObjectId): List[Style] = {
    //发型主要检索条件
    var srchConds = commonSrchConds(style)
    //利用传过来的stylistId判断后台检索是检索某一发型师的发型，还是检索店铺全部发型师的发型
    val stylists = SalonAndStylist.findBySalonId(salonId)
    var stylistIds: List[ObjectId] = Nil
    stylists.map { stylist =>
      stylistIds :::= List(stylist.stylistId)
    }
    //检索条件中包含技师ID时，将技师ID作为检索条件
    if (stylistIds.contains(style.stylistId)) {
      srchConds :::= List(commonsDBObject("stylistId" -> style.stylistId))
    }
    dao.find($and(srchConds)).toList.sortBy(_.createDate).reverse
  }

  /**
   * 获取发型检索字段、图片描述的主表数据,并将它们放入整合类StylePara中
   * @return StylePara
   */
  def findParaAll = {
    //获得相应主表数据
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
    val paraStylePicDescription = StylePicDescription.findAll().toList
    var paraStylePicDescriptions: List[String] = Nil
    paraStylePicDescription.map { para =>
      paraStylePicDescriptions :::= List(para.stylePicDescription)
    }
    //将检索出来的主表数据放到发型主表字段整合类中
    val styleList = new StylePara(paraStyleImpressions, paraServiceTypes, paraStyleLengths, paraStyleColors, paraStyleAmounts, paraStyleQualitys, paraStyleDiameters, paraFaceShapes, paraConsumerAgeGroups, paraConsumerSexs, paraConsumerSocialStatuss, paraStylePicDescriptions)
    styleList
  }

  /**
   * 后台发型删除 -将发型无效
   * @param id 发型ID
   * @return
   */
  def styleToInvalid(id: ObjectId) = {
    dao.update(MongoDBObject("_id" -> id), MongoDBObject("$set" -> (
      MongoDBObject("isValid" -> false))))
  }

  /**
   * Global初始化发型表时，图片上传
   * @param style 该条发型数据
   * @param imgIdList 初始化数据时，传过来的图片ID的List（含3个参数）
   * @return
   */
  def updateStyleImage(style: Style, imgIdList: List[ObjectId]) = {
    dao.update(MongoDBObject("_id" -> style.id, "stylePic.fileObjId" -> style.stylePic(0).fileObjId),
      MongoDBObject("$set" -> (MongoDBObject("stylePic.$.fileObjId" -> imgIdList(2)))), false, true)

    dao.update(MongoDBObject("_id" -> style.id, "stylePic.fileObjId" -> style.stylePic(1).fileObjId),
      MongoDBObject("$set" -> (MongoDBObject("stylePic.$.fileObjId" -> imgIdList(1)))), false, true)

    dao.update(MongoDBObject("_id" -> style.id, "stylePic.fileObjId" -> style.stylePic(2).fileObjId),
      MongoDBObject("$set" -> (MongoDBObject("stylePic.$.fileObjId" -> imgIdList(0)))), false, true)
  }

  /**
   * 发型图片保存
   * @param style 该条发型数据
   * @param imgId 图片ID
   * @return
   */
  def saveStyleImage(style: Style, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> style.id, "stylePic.showPriority" -> style.stylePic.last.showPriority.get),
      MongoDBObject("$set" -> (MongoDBObject("stylePic.$.fileObjId" -> imgId))), false, true)
  }

  def isExist(value: String, stylistId: String, f: (String, String) => Option[Style]) = f(value, stylistId).map(style => true).getOrElse(false)

  /**
   * 通过发型名和技师ID判断该发型是否存在
   * @param name 发型名
   * @param stylistId 技师ID
   * @return
   */
  def checkStyleIsExist(name: String, stylistId: ObjectId): Boolean = {
    dao.find(MongoDBObject("styleName" -> name, "stylistId" -> stylistId)).hasNext
  }
}

