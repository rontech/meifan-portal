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

/**
 * Main Class: Salon.
 *
 * @param id                      ObjectId of salon in mongodb.
 * @param salonAccount          loginId and password for salon.
 * @param salonName             use for display.
 * @param salonNameAbbr
 * @param salonIndustry        Ref to Master [Industry] table.
 * @param homepage
 * @param salonDescription     description for salon.
 * @param picDescription       description for salonFirstPage.
 * @param contactMethod        phoneNumber and contact person of salon.
 * @param optContactMethods   Ref to Master [OptContactMethods] table.
 * @param establishDate
 * @param salonAddress
 * @param workTime             openTime and closeTime for salon.
 * @param restDays
 * @param seatNums
 * @param salonFacilities     Ref to Master [SalonFacilities] table.
 * @param salonPics            Ref to Master [OnUsePicture] table.
 * @param registerDate
 */

case class Salon(
  id: ObjectId = new ObjectId,
  salonAccount: SalonAccount,
  salonName: String,
  salonNameAbbr: Option[String],
  salonIndustry: List[String],
  homepage: Option[String],
  salonDescription: Option[String],
  picDescription: Option[PicDescription],
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
  def findByAccountId(salonAccountId: String): Option[Salon] = {
    dao.findOne(MongoDBObject("salonAccount.accountId" -> salonAccountId))
  }

  /**
   * 根据accoutId和邮箱查看是否有该店铺
   * @param salonAccountId
   * @param salonEmail
   * @return
   */
  def findOneByAccountIdAndEmail(salonAccountId: String, salonEmail: String) = {
    dao.findOne(MongoDBObject("salonAccount.accountId" -> salonAccountId, "contactMethod.email" -> salonEmail))
  }

  /**
   * 登录时，用户名和密码一致性检查
   * @param salonAccount
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
   * @param salonName
   * @return
   */
  def findOneBySalonName(salonName: String): Option[Salon] = {
    dao.findOne(MongoDBObject("salonName" -> salonName))
  }

  /**
   * 根据沙龙简称查找沙龙
   * @param salonNameAbbr
   * @return
   */
  def findOneBySalonNameAbbr(salonNameAbbr: String): Option[Salon] = {
    dao.findOne(MongoDBObject("salonNameAbbr" -> salonNameAbbr))
  }

  /**
   * 根据沙龙邮箱查找沙龙
   * @param email
   * @return
   */
  def findOneByEmail(email: String): Option[Salon] = {
    dao.findOne(MongoDBObject("contactMethod.email" -> email))
  }

  /**
   * 根据沙龙联系电话查找沙龙
   * @param phone
   * @return
   */
  def findOneByMainPhone(phone: String): Option[Salon] = {
    dao.findOne(MongoDBObject("contactMethod.mainPhone" -> phone))
  }

  /**
   * Get all styles of a salon.
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
   */
  def getCountOfStylists(salonId: ObjectId) = {
    SalonAndStylist.findBySalonId(salonId).length
  }

  /**
   * Get the lowest price of CUT of a salon.
   */
  def getLowestPriceOfCut(salonId: ObjectId): Option[BigDecimal] = {
    val cutSrvKey = "Cut"
    Service.getLowestPriceOfSrvType(salonId, cutSrvKey)
  }

  /**
   * 沙龙头像（LOGO）更新
   * @param salon
   * @param imgId
   * @return
   */
  def updateSalonLogo(salon: Salon, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "LOGO"),
      MongoDBObject("$set" -> (MongoDBObject("salonPics.$.fileObjId" -> imgId))), false, true)
  }

  /**
   * 初始化数据（Global中沙龙展示图片上传）
   * @param salon
   * @param imgIdList
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
   * @param salon
   * @return
   */
  def checkBasicInfoIsFill(salon: Salon): Boolean = {
    salon.salonNameAbbr.nonEmpty && salon.salonDescription.nonEmpty &&
      salon.restDays.nonEmpty && salon.workTime.nonEmpty && salon.establishDate.ne(None) &&
      salon.salonAddress.map(add => add.addrDetail.nonEmpty).getOrElse(true)
  }

  /**
   * 沙龙详细信息是否完善检查
   * 检查字段:沙龙席位、沙龙描述
   * @param salon
   * @return
   */
  def checkDetailIsFill(salon: Salon): Boolean = {
    salon.seatNums.nonEmpty &&
      salon.picDescription.exists(pic => pic.picTitle.nonEmpty) && salon.picDescription.exists(pic => pic.picContent.nonEmpty) &&
      salon.picDescription.exists(pic => pic.picFoot.nonEmpty)
  }

  /**
   * 沙龙图片是否完善
   * 检查字段：沙龙展示图片、沙龙环境图片、沙龙营业执照
   * @param salon
   * @return
   */
  def checkImgIsExist(salon: Salon): Boolean = {
    salon.salonPics.exists(a => a.picUse.equals("Navigate")) && salon.salonPics.exists(a => a.picUse.equals("Atmosphere")) &&
      salon.salonPics.exists(a => a.picUse.equals("SalonCheck"))
  }

  /**
   * 权限认证
   * 用于判断accountId是否为当前店铺
   * @param accountId
   * @param salon
   * @return
   */
  def isOwner(accountId: String)(salon: Salon): Future[Boolean] = Future { Salon.findByAccountId(accountId).map(_ == salon).get }

  /*--------------------------
   - 沙龙首页: 沙龙通用检索
   --------------------------*/
  /**
   * 前台店铺检索
   */
  def findSalonBySearchPara(searchParaForSalon: SearchParaForSalon) = {
    var salonSrchRst: List[SalonGeneralSrchRst] = Nil
    var salons: List[Salon] = Nil
    var salonList: List[Salon] = Nil

    // List to save search conditions.
    var srchConds: List[commonsDBObject] = Nil

    // process parameters for salon facilities.
    // if the checkbox in the general search page is checked, use it as a condition; otherwise, not use it. 
    var faclty = searchParaForSalon.salonFacilities
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
      var kwsHits: List[String] = getKeywordsHit(sl, targetFields, exactRegex)

      salonSrchRst :::= List(SalonGeneralSrchRst(salonInfo = sl, selectedStyles = selStyles, selectedCoupons = selCoupons,
        priceForCut = priceOfCut, reviewsStat = rvwStat, keywordsHitStrs = kwsHits))
    }

    // Sort the result by sort conditions.
    sortRstByConditions(salonSrchRst, searchParaForSalon.sortByConditions)
  }

  /**
   * Get keywords hit result.
   */
  def getKeywordsHit(sl: Salon, fields: Array[String], reg: Regex): List[String] = {
    var kwsHits: List[String] = Nil
    // when it is searched without keyword, list the .
    if (reg.toString == "") {
      kwsHits = getSalonPresentationAbbr(sl.picDescription)
    } else {
      kwsHits = getKeywordsHitStrs(sl, fields, reg)
    }

    kwsHits
  }

  /**
   * Sort the search result by required order conditions.
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
   *
   */
  def getSalonPresentationAbbr(present: Option[PicDescription]): List[String] = {
    var abbrPres: List[String] = Nil
    present match {
      case None => abbrPres
      case Some(pres) =>
        abbrPres :::= List(makeAbbrStr(pres.picTitle, 0, 30))
        abbrPres :::= List(makeAbbrStr(pres.picContent, 0, 30))
        abbrPres :::= List(makeAbbrStr(pres.picFoot, 0, 30))
    }

    abbrPres
  }

  /**
   * Get keywords hit strings.
   *   Slice
   */
  /*
  def getKeywordsHitStrs1(fuzzyKwds: List[commonsDBObject], exactKwds: Regex): List[String] = {
   var fuzzyHits: List[String] = Nil

   for(fz <- fuzzyKwds) { 
    // TODO can below done with reflection?
    var hit: String = ""
    if(fz.contains("salonName")) hit = dao.find(fz).map {_.salonName}.mkString
    if(fz.contains("salonNameAbbr")) hit = dao.find(fz).map {_.salonNameAbbr.getOrElse("")}.mkString
    if(fz.contains("salonDescription")) hit = dao.find(fz).map {_.salonDescription.getOrElse("")}.mkString
    // for a salon valid, it is impossible that field [picDescription] is null.
    if(fz.contains("picDescription.picTitle")) hit = dao.find(fz).map {_.picDescription.get.picTitle}.mkString
    if(fz.contains("picDescription.picContent")) hit = dao.find(fz).map {_.picDescription.get.picContent}.mkString
    if(fz.contains("picDescription.picFoot")) hit = dao.find(fz).map {_.picDescription.get.picFoot}.mkString

    // first hit words, fz's value is a Regex type variable.
    val regx = exactKwds 
    var firstHit = regx.findFirstIn(hit)
    firstHit match {
     case None => fuzzyHits
     case Some(fstHit) => {
      // first hit index.
      var mtch = regx.findAllIn(hit)
      // cut out the search rst.
      if(!mtch.isEmpty) {
       fuzzyHits :::= List("..." + hit.slice(mtch.start, mtch.start + 30) + "...")
      }
      else {
       fuzzyHits
      }
     } 
    }
   }

   // println("fuzzyHits" + fuzzyHits)
   fuzzyHits.toList
  }
  */

  /**
   * Get the slice of keyword matched fields.
   *   # Check if the fields contains the keyword by fuzzy match, but get the match slice by exact match. #
   *
   */
  def getKeywordsHitStrs(salon: Salon, targetFields: Array[String], exactKwds: Regex): List[String] = {
    var fuzzyHits: List[String] = Nil
    for (tgtf <- targetFields) {
      var hit: String = ""

      if (exactKwds != "") {

        // TODO can below done with reflection?
        //if(tgtf == "salonName") hit = salon.salonName
        //if(tgtf == "salonNameAbbr") hit = salon.salonNameAbbr.getOrElse("")
        if (tgtf == "salonDescription") hit = salon.salonDescription.getOrElse("")
        // for a salon valid, it is impossible that field [picDescription] is null.
        if (tgtf == "picDescription.picTitle") hit = salon.picDescription.get.picTitle
        if (tgtf == "picDescription.picContent") hit = salon.picDescription.get.picContent
        if (tgtf == "picDescription.picFoot") hit = salon.picDescription.get.picFoot

        // Use the Regex type method to get the first hit words in the target string.
        var firstHit = exactKwds.findFirstIn(hit)
        firstHit match {
          case None => fuzzyHits
          case Some(fstHit) => {
            // get the first hit index by Regex method.
            var mtch = exactKwds.findAllIn(hit)
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

    // println("fuzzyKwds = " + fuzzyKwds)
    // println("exactKwds = " + exactKwds)
    // println("fuzzyHits = " + fuzzyHits)
    fuzzyHits.toList
  }

  /**
   * Get the general search target fields.
   */
  def getSrchTargetFields(): Array[String] = {
    val srchFields = Array("salonName", "salonNameAbbr", "salonDescription",
      "picDescription.picTitle", "picDescription.picContent", "picDescription.picFoot")

    srchFields
  }

  /**
   * Make Abbr string for salon's various names and descriptions.
   */
  def makeAbbrStr(source: String, start: Int, end: Int): String = {
    if (!source.isEmpty && start < end)
      "..." + source.slice(start, end) + "..."
    else
      ""
  }

  /**
   * Fuzzy search to salon: can search by salon name, salon abbr name, salonDescription, picDescription......
   * TODO:
   *     For now, only consider multi-keywords separated by blank,
   *     Not consider the way that deviding keyword into multi-keywords automatically.
   */
  /*
  def findSalonByFuzzyConds(keyword: String): List[Salon] = {
    var rst: List[Salon] = Nil
    // pre process for keyword: process the double byte blank to single byte blank.
    val kws = keyword.replace("　"," ")
    if(kws.replace(" ","").length == 0) {
      // when keyword is not exist, return Nil.
      rst
    } else {
      // when keyword is exist, convert it to regular expression.
      val kwsAry = kws.split(" ").map { x => (".*" + x.trim + ".*|")}
      val kwsRegex =  kwsAry.mkString.dropRight(1).r
      // fields which search from 
      val searchFields = getSrchTargetFields() 
      searchFields.map { sf => 
        var s = dao.find(MongoDBObject(sf -> kwsRegex)).toList
        rst :::= s
      }
      rst.distinct
    }
  }
  */

  /**
   * :
   * Casbah Logical Operators: http://mongodb.github.io/casbah/guide/query_dsl.html
   * for DSL $or/$and/$nor, we can join the conditions into a list:
   *     $or( "price" $lt 5 $gt 1, "promotion" $eq true )
   *     $or( ( "price" $lt 5 $gt 1 ) :: ( "stock" $gte 1 ) )
   * So, we use this feature to make the search conditions.
   *
   * Make salon general search keyword to fuzzy search conditions.
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
   *
   * For example.
   *     1. f = x => (".*" + x + ".*|")
   *     2. f = x => (x|")
   *     3. ....
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
  def isValid(value: String,
    loggedSalon: Salon,
    f: String => Option[Salon]) = f(value).map(_.id == loggedSalon.id).getOrElse(true)
}

/*----------------------------
 * Embed Structure of Salon.
 -----------------------------*/

/**
 * 沙龙地址（内嵌于沙龙主表）
 * @param province
 * @param city
 * @param region
 * @param town
 * @param addrDetail
 * @param longitude
 * @param latitude
 * @param accessMethodDesc
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
 * @param canOnlineOrder
 * @param canImmediatelyOrder
 * @param canNominateOrder
 * @param canCurntDayOrder
 * @param canMaleUse
 * @param isPointAvailable
 * @param isPosAvailable
 * @param isWifiAvailable
 * @param hasParkingNearby
 * @param parkingDesc
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
 * @param openTime
 * @param closeTime
 */
case class WorkTime(
  openTime: String,
  closeTime: String)

/**
 * 沙龙休息日（内嵌于沙龙主表)
 * @param restWay
 * @param restDay
 */
case class RestDay(
  restWay: String,
  restDay: List[String])

/**
 * 沙龙登录信息（内嵌于沙龙主表）
 * @param accountId
 * @param password
 */
case class SalonAccount(
  accountId: String,
  password: String)

/**
 * 沙龙描述（内嵌于沙龙主表）
 * @param picTitle
 * @param picContent
 * @param picFoot
 */
case class PicDescription(
  picTitle: String,
  picContent: String,
  picFoot: String)

/**
 * 沙龙主要联系方式（内嵌于沙龙主表）
 * @param mainPhone
 * @param contact
 * @param email
 */
case class Contact(
  mainPhone: String,
  contact: String,
  email: String)

/**
 * 沙龙图片（内嵌于沙龙主表）
 * @param salonPics
 */
case class SalonPics(
  salonPics: List[OnUsePicture])
