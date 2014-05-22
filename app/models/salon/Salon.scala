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

import play.api.Play.current
import play.api.PlayException
import play.Configuration
import java.util.Date
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import com.mongodb.casbah.query.Imports._
import com.mongodb.casbah.commons.Imports.{ DBObject => commonsDBObject }
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import org.mindrot.jbcrypt.BCrypt
import scala.util.matching.Regex
import com.meifannet.framework.db._
import com.meifannet.framework.utils.ReflectionOperations._


/**
 * Main Class: Salon.
 *
 * @param id ObjectId of salon in mongodb.
 * @param salonAccount The loginId and password for a salon.
 * @param salonName The name of a salon, used to display.
 * @param salonNameAbbr The abbreviation name of a salon, used to display, prior to salonName.
 * @param salonIndustry Ref to Master [Industry] table.
 * @param homepage The home address of a salon, optional.
 * @param salonAppeal An appeal of a salon to show to users, which will be shown at the salon basic info page.
 * @param salonIntroduction A brief introduction to a salon, which should include the title info, the content and the footer info .
 * @param contactMethod The primary contact phoneNumber and contact person of a salon.
 * @param optContactMethods The optional contact method of a salon, which refer to Master [OptContactMethods] table.
 * @param establishDate The establish Date of a salon.
 * @param salonAddress The address of a salon.
 * @param workTime The work time, including th openTime and closeTime for salon.
 * @param restDays The restDays of a salon.
 * @param seatNums The seat numbers of a salon.
 * @param salonFacilities The salon facilities information. Ref to Master [SalonFacilities] table.
 * @param salonPics Various pictures a salon used. Ref to Master [OnUsePicture] table.
 * @param registerDate The date a salon register in our site.
 */
case class Salon(
  id: ObjectId = new ObjectId,
  salonAccount: SalonAccount,
  salonName: String,
  salonNameAbbr: Option[String],
  salonIndustry: List[String],
  homepage: Option[String],
  salonAppeal: Option[String],
  salonIntroduction: Option[BriefIntroduction],
  contactMethod: Contact,
  optContactMethods: List[OptContactMethod],
  establishDate: Option[Date],
  salonAddress: Option[Address],
  workTime: Option[WorkTime],
  restDays: Option[RestDay],
  seatNums: Option[Int],
  salonFacilities: Option[SalonFacilities],
  salonPics: List[OnUsePicture],
  registerDate: Date)

object Salon extends MeifanNetModelCompanion[Salon] {

  val dao = new MeifanNetDAO[Salon](collection = loadCollection()) {}

  /**
   * 根据accountId查找沙龙
   * @param salonAccountId accountId是该沙龙的用户名
   * @return
   */
  def findOneByAccountId(salonAccountId: String): Option[Salon] = {
    dao.findOne(MongoDBObject("salonAccount.accountId" -> salonAccountId))
  }

  /**
   * 根据accoutId和邮箱查看是否有该店铺
   * @param salonAccountId login id
   * @param salonEmail salon's email
   * @return
   */
  def findOneByAccountIdAndEmail(salonAccountId: String, salonEmail: String) = {
    dao.findOne(MongoDBObject("salonAccount.accountId" -> salonAccountId, "contactMethod.email" -> salonEmail))
  }

  /**
   * 登录时，用户名和密码一致性检查
   * @param salonAccount salon's accountId and password
   * @return
   */
  def loginCheck(salonAccount: SalonAccount): Option[Salon] = {
    val salon = dao.findOne(MongoDBObject("salonAccount.accountId" -> salonAccount.accountId))
    if (salon.nonEmpty && BCrypt.checkpw(salonAccount.password, salon.get.salonAccount.password)) {
      return salon
    } else {
      return None
    }
  }

  /**
   * 根据沙龙名称查找沙龙
   *   判断沙龙名称是否被使用时用到该方法
   * @param salonName salon's name
   * @return
   */
  def findOneBySalonName(salonName: String): Option[Salon] = {
    dao.findOne(MongoDBObject("salonName" -> salonName))
  }

  /**
   * 根据沙龙简称查找沙龙
   *  判断沙龙简称是否被使用时用到该方法
   * @param salonNameAbbr salon's shortName
   * @return
   */
  def findOneBySalonNameAbbr(salonNameAbbr: String): Option[Salon] = {
    dao.findOne(MongoDBObject("salonNameAbbr" -> salonNameAbbr))
  }

  /**
   * 根据沙龙邮箱查找沙龙
   *   判断邮箱是否被使用时用到该方法
   * @param email salon's email
   * @return
   */
  def findOneByEmail(email: String): Option[Salon] = {
    dao.findOne(MongoDBObject("contactMethod.email" -> email))
  }

  /**
   * 根据沙龙联系电话查找沙龙
   *   判断电话是否被使用时用到该方法
   * @param phone salon's mainPhone
   * @return
   */
  def findOneByMainPhone(phone: String): Option[Salon] = {
    dao.findOne(MongoDBObject("contactMethod.mainPhone" -> phone))
  }

  /**
   * Get all styles of a salon.
   * 取得指定沙龙的所有发型
   *
   * @param salonId 沙龙id
   * @return 发型列表
   */
  def getAllStyles(salonId: ObjectId): List[Style] = {
    var styles: List[Style] = Nil

    // find styles of all stylists via the relationship between [salon] and [stylist]. 
    val stylists = SalonAndStylist.findBySalonId(salonId)
    stylists.map { stls =>
      var style = Style.findByStylistId(stls.stylistId)
      styles :::= style
    }
    // order by create time desc.
    styles.sortBy(_.createDate).reverse
  }

  /**
   * Get a specified Style from a salon.
   * 根据指定的店铺id, 发型id 来查找指定的发型
   *
   * @param salonId 沙龙id
   * @param styleId 发型id
   * @return 发型信息
   */
  def getOneStyle(salonId: ObjectId, styleId: ObjectId): Option[Style] = {
    // First of all, check that if the salon is acitve.
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon match {
      case None => None
      case Some(sl) => {
        // Second, check if the style is exist.
        val style: Option[Style] = Style.findOneById(styleId)
        style match {
          // If style is not exist, show nothing but must in the salon's page.
          case None => None
          case Some(st) => {
            // Third, we need to check the relationship between slaon and stylist to check if the style is active.
            if (SalonAndStylist.isStylistActive(salonId, st.stylistId)) {
              // If style is active, jump to the style show page in salon.
              Some(st)
            } else {
              // If style is not active, show nothing but must in the salon's page.
              None
            }
          }
        }
      }
    }
  }

  /**
   * Get the stylists count of a salon.
   *
   * @param salonId
   * @return
   */
  def getCountOfStylists(salonId: ObjectId) = {
    SalonAndStylist.findBySalonId(salonId).length
  }

  /**
   * Get the lowest price of CUT of a salon.
   *
   * @param salonId
   * @return
   */
  def getLowestPriceOfCut(salonId: ObjectId): Option[BigDecimal] = {
    val cutSrvKey = "Cut"
    Service.getLowestPriceOfSrvType(salonId, cutSrvKey)
  }

  /**
   * 沙龙头像（LOGO）更新
   * @param salon 已登录的沙龙账号
   * @param imgId 上传图片的id
   * @return
   */
  def updateSalonLogo(salon: Salon, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "LOGO"),
      MongoDBObject("$set" -> (MongoDBObject("salonPics.$.fileObjId" -> imgId))), false, true)
  }

  /**
   * 初始化数据（Global中沙龙展示图片上传）
   * @param salon Global中的沙龙数据
   * @param imgIdList puclic文件中的图片上传ID
   * @return
   */
  def updateSalonShow(salon: Salon, imgIdList: List[ObjectId]) = {

    //第三张图片上传调用
    if (imgIdList.length > 2 && !salon.salonPics.isEmpty && salon.salonPics.length > 3) {
      dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Navigate", "salonPics.fileObjId" -> salon.salonPics(3).fileObjId),
        MongoDBObject("$set" -> (MongoDBObject("salonPics.$.fileObjId" -> imgIdList(2)))), false, true)
    }

    //第二张图片上传调用
    if (imgIdList.length > 1 && !salon.salonPics.isEmpty && salon.salonPics.length > 2) {
      dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Navigate", "salonPics.fileObjId" -> salon.salonPics(2).fileObjId),
        MongoDBObject("$set" -> (MongoDBObject("salonPics.$.fileObjId" -> imgIdList(1)))), false, true)
    }

    //第一张图片上传调用
    if (imgIdList.length > 0 && !salon.salonPics.isEmpty && salon.salonPics.length > 1) {
      dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Navigate", "salonPics.fileObjId" -> salon.salonPics(1).fileObjId),
        MongoDBObject("$set" -> (MongoDBObject("salonPics.$.fileObjId" -> imgIdList(0)))), false, true)
    }

  }

  /**
   * 初始化数据（Global中沙龙环境图片上传）
   * @param salon
   * @param imgIdList
   * @return
   */
  def updateSalonAtom(salon: Salon, imgIdList: List[ObjectId]) = {

    //第三张图片上传调用
    if (imgIdList.length > 2 && !salon.salonPics.isEmpty && salon.salonPics.length > 6) {
      dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Atmosphere", "salonPics.fileObjId" -> salon.salonPics(6).fileObjId),
        MongoDBObject("$set" -> (MongoDBObject("salonPics.$.fileObjId" -> imgIdList(2)))), false, true)
    }

    //第二张图片上传调用
    if (imgIdList.length > 1 && !salon.salonPics.isEmpty && salon.salonPics.length > 5) {
      dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Atmosphere", "salonPics.fileObjId" -> salon.salonPics(5).fileObjId),
        MongoDBObject("$set" -> (MongoDBObject("salonPics.$.fileObjId" -> imgIdList(1)))), false, true)
    }

    //第一张图片上传调用
    if (imgIdList.length > 0 && !salon.salonPics.isEmpty && salon.salonPics.length > 4) {
      dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Atmosphere", "salonPics.fileObjId" -> salon.salonPics(4).fileObjId),
        MongoDBObject("$set" -> (MongoDBObject("salonPics.$.fileObjId" -> imgIdList(0)))), false, true)
    }

  }

  /**
   * 沙龙基本信息是否完善检查
   * 检查字段：沙龙检查、沙龙简介、沙龙休息日、沙龙营业时间、沙龙成立日期、沙龙地址
   * @param salon 已登录的沙龙
   * @return
   */
  def checkBasicInfoIsFill(salon: Salon): Boolean = {
    salon.salonNameAbbr.nonEmpty && salon.salonAppeal.nonEmpty &&
      salon.restDays.nonEmpty && salon.workTime.nonEmpty && salon.establishDate.ne(None) &&
      salon.salonAddress.map(add => add.addrDetail.nonEmpty).getOrElse(true)
  }

  /**
   * 沙龙详细信息是否完善检查
   * 检查字段:沙龙席位、沙龙描述
   * @param salon 已登录的沙龙
   * @return
   */
  def checkDetailIsFill(salon: Salon): Boolean = {
    salon.seatNums.nonEmpty &&
      salon.salonIntroduction.exists(pic => pic.introHeader.nonEmpty) && salon.salonIntroduction.exists(pic => pic.introContent.nonEmpty) &&
      salon.salonIntroduction.exists(pic => pic.introFooter.nonEmpty)
  }

  /**
   * 沙龙图片是否完善
   * 检查字段：沙龙展示图片、沙龙环境图片、沙龙营业执照
   * @param salon 已登录的沙龙
   * @return
   */
  def checkImgIsExist(salon: Salon): Boolean = {
    salon.salonPics.exists(a => a.picUse.equals("Navigate")) && salon.salonPics.exists(a => a.picUse.equals("Atmosphere")) &&
      salon.salonPics.exists(a => a.picUse.equals("SalonCheck"))
  }

  /**
   * 权限认证
   * 用于判断accountId是否为当前店铺
   * @param accountId 已登录沙龙的账号
   * @param salon
   * @return
   */
  def isOwner(accountId: String)(salon: Salon): Future[Boolean] = Future { Salon.findOneByAccountId(accountId).map(_ == salon).get }

  /*--------------------------
   - 沙龙首页: 沙龙通用检索
   --------------------------*/
  /**
   * 前台店铺检索逻辑
   *
   * @param searchParaForSalon 前台检索条件
   * @return 所有符合条件的店铺
   */
  def findSalonBySearchPara(searchParaForSalon: SearchParaForSalon) = {
    var salonSrchRst: List[SalonGeneralSrchRst] = Nil
    var salons: List[Salon] = Nil
    var salonList: List[Salon] = Nil

    // List to save search conditions.
    var srchConds: List[commonsDBObject] = Nil

    // process parameters for salon facilities.
    // if the checkbox in the general search page is checked, use it as a condition; otherwise, not use it. 
    val faclty = searchParaForSalon.salonFacilities
    if (faclty.canCurntDayOrder) {
      srchConds :::= List(commonsDBObject("salonFacilities.canCurntDayOrder" -> faclty.canCurntDayOrder))
    }
    if (faclty.canImmediatelyOrder) {
      srchConds :::= List(commonsDBObject("salonFacilities.canImmediatelyOrder" -> faclty.canImmediatelyOrder))
    }
    if (faclty.canMaleUse) {
      srchConds :::= List(commonsDBObject("salonFacilities.canMaleUse" -> faclty.canMaleUse))
    }
    if (faclty.canNominateOrder) {
      srchConds :::= List(commonsDBObject("salonFacilities.canNominateOrder" -> faclty.canNominateOrder))
    }
    if (faclty.canOnlineOrder) {
      srchConds :::= List(commonsDBObject("salonFacilities.canOnlineOrder" -> faclty.canOnlineOrder))
    }
    if (faclty.hasParkingNearby) {
      srchConds :::= List(commonsDBObject("salonFacilities.hasParkingNearby" -> faclty.hasParkingNearby))
    }
    if (faclty.isPointAvailable) {
      srchConds :::= List(commonsDBObject("salonFacilities.isPointAvailable" -> faclty.isPointAvailable))
    }
    if (faclty.isPosAvailable) {
      srchConds :::= List(commonsDBObject("salonFacilities.isPosAvailable" -> faclty.isPosAvailable))
    }
    if (faclty.isWifiAvailable) {
      srchConds :::= List(commonsDBObject("salonFacilities.isWifiAvailable" -> faclty.isWifiAvailable))
    }

    // geo region
    if (!searchParaForSalon.region.equals("all")) {
      srchConds :::= List(commonsDBObject("salonAddress.region" -> searchParaForSalon.region))
    }
    // salon Industry: 
    if (!searchParaForSalon.salonIndustry.equals("all")) {
      srchConds :::= List("salonIndustry" $in List(searchParaForSalon.salonIndustry))
    }
    // salon Name: salon names shows in the search conditions checkboxs
    if (!searchParaForSalon.salonName.isEmpty) {
      srchConds :::= List("salonName" $in searchParaForSalon.salonName)
    }

    // salon seat numbers: Range between min and max.
    srchConds :::= List("seatNums" $lte searchParaForSalon.seatNums.maxNum $gte searchParaForSalon.seatNums.minNum)
    // City
    srchConds :::= List(commonsDBObject("salonAddress.city" -> searchParaForSalon.city.r))

    // keywords Array for Fuzzy search: will be joined with "$or" DSL operation.  
    val fuzzyRegex = convertKwdsToFuzzyRegex(searchParaForSalon.keyWord.getOrElse(""))(x => (".*" + x + ".*|"))
    // from which fields the keyword be searched.
    val targetFields = getSrchTargetFields()
    val fuzzyConds = searchByFuzzyConds(targetFields, fuzzyRegex)
    // Join the normal conditions and fuzzy search keyword condition. 
    val srchCondsWithFuzzyKws = if (!fuzzyConds.isEmpty) $and(srchConds ::: List($or(fuzzyConds))) else $and(srchConds)

    // ------------------------------
    // -  Do salon general search. 
    // ------------------------------
    salonList = dao.find(srchCondsWithFuzzyKws).toList

    // 以serviceType/haircutPrice作为check条件
    if (searchParaForSalon.serviceType.nonEmpty) {
      salonList.map { salon =>
        if ((Service.findServiceTypeBySalonId(salon.id).intersect(searchParaForSalon.serviceType).length == searchParaForSalon.serviceType.length)
          && (searchParaForSalon.priceRange.minPrice <= findLowestPriceBySalonId(salon.id))
          && (findLowestPriceBySalonId(salon.id) <= searchParaForSalon.priceRange.maxPrice)) {
          salons ::= salon
        }
      }
    } else {
      salonList.map { salon =>
        if ((searchParaForSalon.priceRange.minPrice <= findLowestPriceBySalonId(salon.id))
          && (findLowestPriceBySalonId(salon.id) <= searchParaForSalon.priceRange.maxPrice)) {
          salons ::= salon
        }
      }
    }

    // exact regex keyword which used to get the exact words matched to the keyword.
    val exactRegex = convertKwdsToFuzzyRegex(searchParaForSalon.keyWord.getOrElse(""))(x => (x + "|"))
    // from which fields the keyword be searched.
    for (sl <- salons) {
      // get the lowest price for cut.
      val priceOfCut = Salon.getLowestPriceOfCut(sl.id)
      // get top 2 styles of salon.
      val selStyles = Style.getBestRsvedStylesInSalon(sl.id, 2)
      val selCoupons = Coupon.findValidCouponBySalon(sl.id)
      val rvwStat = Comment.getGoodReviewsRate(sl.id)
      // get the keywords hit strings.
      val kwsHits: List[String] = getKeywordsHit(sl, targetFields, exactRegex)

      salonSrchRst :::= List(SalonGeneralSrchRst(salonInfo = sl, selectedStyles = selStyles, selectedCoupons = selCoupons,
        priceForCut = priceOfCut, reviewsStat = rvwStat, keywordsHitStrs = kwsHits))
    }

    // Sort the result by sort conditions.
    sortRstByConditions(salonSrchRst, searchParaForSalon.sortByConditions)
  }

  /**
   * Get keywords hit result.
   * 沙龙检索机能: 根据前台检索的关键字(字符数组)，找到沙龙信息中含有该关键字组的文字，并返回上下文字符串。
   *
   * @param sl 沙龙
   * @param fields 前台关键字按空格拆分后的字符数组
   * @param reg 关键字匹配的准则：正则表达式
   * @return 关键字匹配的内容上下文
   */
  def getKeywordsHit(sl: Salon, fields: Array[String], reg: Regex): List[String] = {
    var kwsHits: List[String] = Nil
    // when it is searched without keyword, list the .
    if (reg.toString == "") {
      kwsHits = getSalonIntroductionAbbr(sl.salonIntroduction)
    } else {
      kwsHits = getKeywordsHitStrs(sl, fields, reg)
    }

    kwsHits
  }

  /**
   * Sort the search result by required order conditions.
   * 对检索结果进行处理：根据前台点击选择的排序条件和顺序，返回排序后的检索结果（沙龙检索情报）
   *
   * @param orgRst 检索结果
   * @param sortConds 排序条件
   * @return 沙龙检索情报结构：包括店铺基本情报，发型，优惠券，最低剪发价格，评价等等
   */
  def sortRstByConditions(orgRst: List[SalonGeneralSrchRst], sortConds: SortByConditions): List[SalonGeneralSrchRst] = {

    // TODO should we consider the Group by functions to implement the sort function like that 
    // order by A asc, B desc, C asc.....
    var sortRst: List[SalonGeneralSrchRst] = Nil

    // Sort By price.
    if (sortConds.selSortKey == "price") {
      sortRst = orgRst.sortBy(_.priceForCut)
      if (!sortConds.sortByPriceAsc) {
        sortRst = sortRst.reverse
      }
    }

    // Sort By popularity.
    if (sortConds.selSortKey == "popu") {
      sortRst = orgRst.sortBy(_.reviewsStat.reviewTotalCnt)
      if (!sortConds.sortByPopuAsc) {
        sortRst = sortRst.reverse
      }
    }

    // Sort By review.
    if (sortConds.selSortKey == "review") {
      sortRst = orgRst.sortBy(_.reviewsStat.reviewRate)
      if (!sortConds.sortByReviewAsc) {
        sortRst = sortRst.reverse
      }
    }

    sortRst
  }

  /**
   * 取得沙龙简介的缩略：限定标题，内容，结尾分别截取头30个文字
   * @param intro 
   * @return
   */
  def getSalonIntroductionAbbr(intro: Option[BriefIntroduction]): List[String] = {
    var introAbbr: List[String] = Nil
    intro match {
      case None => introAbbr
      case Some(pres) =>
        introAbbr :::= List(makeAbbrStr(pres.introHeader, 0, 30))
        introAbbr :::= List(makeAbbrStr(pres.introContent, 0, 30))
        introAbbr :::= List(makeAbbrStr(pres.introFooter, 0, 30))
    }

    introAbbr
  }


  /**
   * Get the slice of keyword matched fields.
   *   # Check if the fields contains the keyword by fuzzy match, but get the match slice by exact match. #
   * 根据关键字匹配规则，查找指定字段中匹配的文字，并返回匹配文字的上下文(共30字)
   *
   * @param salon 被检索的沙龙
   * @param targetFields 指定匹配的目标字段(将来会从平台管理的设定值中读取)
   * @param exactKwds 文字匹配规则：正则表达式
   * @return 匹配的字段上下文
   */
  def getKeywordsHitStrs(salon: Salon, targetFields: Array[String], exactKwds: Regex): List[String] = {
    var fuzzyHits: List[String] = Nil
    for (tgtf <- targetFields) {
      var hit: String = ""
      if (exactKwds != "") {
        // use scala reflection method to get the value of required field.
        val fv = getFieldValueOfClass[Salon](salon, tgtf)
        // TODO should judge the type of fields to see if it is a Option type.
        hit = fv.toString

        // Use the Regex type method to get the first hit words in the target string.
        val firstHit = exactKwds.findFirstIn(hit)
        firstHit match {
          case None => fuzzyHits
          case Some(fstHit) => {
            // get the first hit index by Regex method.
            val mtch = exactKwds.findAllIn(hit)
            // cut out the search rst.
            if (!mtch.isEmpty) {
              var ht: String = ""
              if (hit.length < 10) {
                ht = makeAbbrStr(hit, 0, hit.length)
              } else if ((hit.length / 2) > mtch.start) {
                ht = makeAbbrStr(hit, mtch.start, mtch.start + 30)
              } else {
                ht = makeAbbrStr(hit, mtch.start + hit.length - 30, mtch.start + firstHit.getOrElse("").length)
              }

              if (!ht.isEmpty)
                fuzzyHits :::= List(ht)
            }
          }
        }
      }
    }

    fuzzyHits.toList
  }

  /**
   * Get the general search target fields.
   */
  def getSrchTargetFields(): Array[String] = {
    // for a salon valid, it is impossible that field [salonIntroduction] is null.
    val srchFields = Array("salonName", "salonNameAbbr", "salonAppeal",
      "salonIntroduction.introHeader", "salonIntroduction.introContent", "salonIntroduction.introFooter")

    srchFields
  }

  /**
   * Slice a given string to an Abbr string. the given string may be a salon's names or various descriptions.
   * 根据指定的起始终止值，截取给定字符串
   *
   * @param source 给定字符串
   * @param start 截取初始点
   * @param end 截取终止点
   * @return
   */
  def makeAbbrStr(source: String, start: Int, end: Int): String = {
    if (!source.isEmpty && start < end)
      "..." + source.slice(start, end) + "..."
    else
      ""
  }


  /**
   * Casbah Logical Operators: http://mongodb.github.io/casbah/guide/query_dsl.html
   * for DSL $or/$and/$nor, we can join the conditions into a list:
   *     $or( "price" $lt 5 $gt 1, "promotion" $eq true )
   *     $or( ( "price" $lt 5 $gt 1 ) :: ( "stock" $gte 1 ) )
   * So, we use this feature to make the search conditions.
   *
   * Make salon general search keyword to fuzzy search conditions.
   * 顺次将指定的检索字段和匹配条件（正则表达式）组成 符合casbah规则的检索条件。
   *   根据casbah文档，检索条件可以是List类型， 之间可以用::符号来组合
   *     这样我们可以动态决定检索条件的个数
   *
   * @param searchFields
   * @param kwdsRegex
   * @return
   */
  def searchByFuzzyConds(searchFields: Array[String], kwdsRegex: Regex): List[commonsDBObject] = {
    var rst: List[commonsDBObject] = Nil
    // convert keyword to Regex type string.
    if (kwdsRegex.toString == "") {
      // when keyword is not exist, return Nil.
      rst
    } else {
      // when keyword is exist, convert it to regular expression.
      // param searchFields contains the fields which search from 
      searchFields.map { sf =>
        var s = commonsDBObject(sf -> kwdsRegex)
        rst :::= List(s)
      }
      rst
    }
  }

  /**
   * 根据前台传递过来的关键字（一个或是多个全角或是半角空格分割），
   * 组合成正则表达式的检索条件群
   *   可以根据检索规则f 组成精确查找或是模糊查找
   * For example.
   *     1. f = x => (".*" + x + ".*|")
   *     2. f = x => (x|")
   *     3. ....
   *
   * @param keyword 原生检索字符串
   * @param f 正则表达式格式检索规则
   * @return List[Regex]
   */
  def convertKwdsToFuzzyRegex(keyword: String)(f: String => String) = {
    // pre process for keyword: process the double byte blank to single byte blank.
    val kws = keyword.replace("　", " ").trim
    if (kws.replace(" ", "").length == 0) {
      // when keyword is not exist, return Nil.
      "".r
    } else {
      // when keyword is exist, convert it to regular expression.
      val kwsStr = kws.split(" ").map { x => if (!x.trim.isEmpty) f(x.trim) }.mkString
      if (kwsStr.endsWith("|"))
        kwsStr.dropRight(1).r
      else
        kwsStr.r
    }
  }

  /**
   * 检索某一店铺服务的最低价格
   *
   * @param salonId
   * @return
   */
  def findLowestPriceBySalonId(salonId: ObjectId): BigDecimal = {
    var lowestPrice: BigDecimal = 0
    //获取最低剪发价格
    Service.getLowestPriceOfSrvType(salonId, "Cut") match {
      case Some(lowPrice) => { lowestPrice = lowPrice }
      case None => None
    }
    lowestPrice
  }

  /**
   * checks for accountId,salonName
   * @param value
   * @param f
   * @return
   */
  def isExist(value: String, f: String => Option[Salon]) = f(value).map(salon => true).getOrElse(false)

  /**
   * checks for salonNameAbbr
   * @param value
   * @param loggedSalon
   * @param f
   * @return
   */
  def isValid(value: String, loggedSalon: Salon, f: String => Option[Salon]) = f(value).map(_.id == loggedSalon.id).getOrElse(true)

}

/*----------------------------
 * Embed Structure of Salon.
 -----------------------------*/

/**
 * 沙龙地址（内嵌于沙龙主表）
 * @param province 省
 * @param city 市
 * @param region 区
 * @param town 镇
 * @param addrDetail 详细地址
 * @param longitude 经度
 * @param latitude 纬度
 * @param accessMethodDesc 交通方法
 */
case class Address(
  province: String,
  city: Option[String],
  region: Option[String],
  town: Option[String],
  addrDetail: String,
  longitude: Option[BigDecimal],
  latitude: Option[BigDecimal],
  accessMethodDesc: String)

/**
 * 沙龙功能支持（内嵌于沙龙主表）
 * @param canOnlineOrder 网上预约
 * @param canImmediatelyOrder 即时预约
 * @param canNominateOrder 指名预约
 * @param canCurntDayOrder 当日预约
 * @param canMaleUse 男性可用
 * @param isPointAvailable 积分加盟
 * @param isPosAvailable 刷卡
 * @param isWifiAvailable wifi
 * @param hasParkingNearby 停车场
 * @param parkingDesc 停车场描述
 */
case class SalonFacilities(
  canOnlineOrder: Boolean,
  canImmediatelyOrder: Boolean,
  canNominateOrder: Boolean,
  canCurntDayOrder: Boolean,
  canMaleUse: Boolean,
  isPointAvailable: Boolean,
  isPosAvailable: Boolean,
  isWifiAvailable: Boolean,
  hasParkingNearby: Boolean,
  parkingDesc: String)

object SalonFacilities extends MeifanNetModelCompanion[SalonFacilities] {
  val dao = new MeifanNetDAO[SalonFacilities](collection = loadCollection()) {}
}

/**
 * 沙龙营业时间（内嵌于沙龙主表）
 * @param openTime 沙龙营业开始时间
 * @param closeTime 沙龙营业结束时间
 */
case class WorkTime(
  openTime: String,
  closeTime: String)

/**
 * 沙龙休息日（内嵌于沙龙主表)
 * @param restWay 休息方式选择
 * @param restDay 休息日
 */
case class RestDay(
  restWay: String,
  restDay: List[String])

/**
 * 沙龙登录信息（内嵌于沙龙主表）
 * @param accountId 沙龙登录账号
 * @param password 沙龙登录密码
 */
case class SalonAccount(
  accountId: String,
  password: String)

/**
 * 沙龙描述（内嵌于沙龙主表）
 * @param introHeader 标题
 * @param introContent 内容
 * @param introFooter 结束语
 */
case class BriefIntroduction(
  introHeader: String,
  introContent: String,
  introFooter: String)

/**
 * 沙龙主要联系方式（内嵌于沙龙主表）
 * @param mainPhone 沙龙主要联系方式
 * @param contact 沙龙联系人
 * @param email 沙龙电子邮箱
 */
case class Contact(
  mainPhone: String,
  contact: String,
  email: String)

/**
 * 沙龙图片: 用于沙龙图片上传Form
 * @param salonPics
 */
// TODO why we need this case class?
case class SalonPics(
  salonPics: List[OnUsePicture])
