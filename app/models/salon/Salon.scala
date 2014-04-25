package models

import play.api.Play.current
import play.api.PlayException
import play.Configuration
import java.util.Date
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.query.Imports._
import com.mongodb.casbah.commons.Imports.{ DBObject => commonsDBObject }
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import com.novus.salat.dao._
import mongoContext._
import org.mindrot.jbcrypt.BCrypt
import scala.util.matching.Regex

/*
 * Main Class: Salon.
*/

case class Salon(
    id: ObjectId = new ObjectId,   	
    salonAccount: SalonAccount,
    salonName: String,                  
    salonNameAbbr: Option[String],      
    salonIndustry: List[String],       // Ref to Master [Industry] table.           
    homepage: Option[String],           
    salonDescription: Option[String], 
    picDescription: Option[PicDescription],
    contactMethod:Contact,
    optContactMethods: List[OptContactMethod],
    establishDate: Option[Date],
    salonAddress: Option[Address],
    workTime: Option[WorkTime],
    restDays: Option[RestDay],
    seatNums: Option[Int],
    salonFacilities: Option[SalonFacilities],
    salonPics: List[OnUsePicture],             
    registerDate: Date
)

object Salon extends SalonDAO

trait SalonDAO extends ModelCompanion[Salon, ObjectId] {

    val dao = new SalatDAO[Salon, ObjectId](collection = mongoCollection("Salon")) {}
    
    //// Indexes
    //  collection.ensureIndex(DBObject("accountId" -> 1), "userId", unique = true)
  
    def findByAccountId(salonAccountId: String): Option[Salon] = {
        dao.findOne(MongoDBObject("salonAccount.accountId" -> salonAccountId))
    } 
    
    /**
     *  根据accoutId和邮箱查看是否有该店铺
     */ 
    def findOneByAccountIdAndEmail(salonAccountId: String, salonEmail : String) = {
      dao.findOne(MongoDBObject("salonAccount.accountId" -> salonAccountId, "contactMethod.email" -> salonEmail))
    }

    def loginCheck(salonAccount: SalonAccount): Option[Salon] = {
//        SalonDAO.findOne(MongoDBObject("salonAccount.accountId" -> salonAccount.accountId,"salonAccount.password" -> salonAccount.password))
        val salon = dao.findOne(MongoDBObject("salonAccount.accountId" -> salonAccount.accountId))
        if(salon.nonEmpty && BCrypt.checkpw(salonAccount.password, salon.get.salonAccount.password)){
            return salon
        }else{
            return None
        }
    }

    def findOneBySalonName(salonName: String): Option[Salon] = {
        dao.findOne(MongoDBObject("salonName" -> salonName))
    }

    def findOneBySalonNameAbbr(salonNameAbbr: String): Option[Salon] = {
        dao.findOne(MongoDBObject("salonNameAbbr" -> salonNameAbbr))
    }

    def findOneByEmail(email: String): Option[Salon] = {
        dao.findOne(MongoDBObject("contactMethod.email" -> email))
    }

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

    def updateSalonLogo(salon: Salon, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "LOGO"), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgId))),false,true)
    }
    
    /**
     * Temp Method for initial sample data in Global.scala.
     */
    def updateSalonShow(salon: Salon, imgIdList: List[ObjectId]) = {
           
      if(imgIdList.length > 2 && !salon.salonPics.isEmpty && salon.salonPics.length > 3) {
          dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Navigate", "salonPics.fileObjId" -> salon.salonPics(3).fileObjId), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgIdList(2)))),false,true)
      }
      
      if(imgIdList.length > 1 && !salon.salonPics.isEmpty && salon.salonPics.length > 2) {
          dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Navigate", "salonPics.fileObjId" -> salon.salonPics(2).fileObjId), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgIdList(1)))),false,true)
      }
      
      if(imgIdList.length > 0 && !salon.salonPics.isEmpty && salon.salonPics.length > 1) {
          dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Navigate", "salonPics.fileObjId" -> salon.salonPics(1).fileObjId), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgIdList(0)))),false,true)
      }
            
    }    
    
    /**
     * Temp Method for initial sample data in Global.scala.
     */
    def updateSalonAtom(salon: Salon, imgIdList: List[ObjectId]) = {
           
      if(imgIdList.length > 2 && !salon.salonPics.isEmpty && salon.salonPics.length > 6) {
          dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Atmosphere", "salonPics.fileObjId" -> salon.salonPics(6).fileObjId), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgIdList(2)))),false,true)
      }
      
      if(imgIdList.length > 1 && !salon.salonPics.isEmpty && salon.salonPics.length > 5) {
          dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Atmosphere", "salonPics.fileObjId" -> salon.salonPics(5).fileObjId), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgIdList(1)))),false,true)
      }
      
      if(imgIdList.length > 0 && !salon.salonPics.isEmpty && salon.salonPics.length > 4) {
          dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Atmosphere", "salonPics.fileObjId" -> salon.salonPics(4).fileObjId), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgIdList(0)))),false,true)
      }
            
    }
    
    /**
     * 查看基本信息是否填写
     */
    def checkBasicInfoIsFill(salon: Salon): Boolean = {
        salon.salonNameAbbr.nonEmpty && salon.salonDescription.nonEmpty &&
        salon.restDays.nonEmpty && salon.workTime.nonEmpty && salon.establishDate.ne(None) &&
        salon.salonAddress.map(add=>add.addrDetail.nonEmpty).getOrElse(true)
    }
    
    /**
     * 查看详细基本信息是否填写
     */
    def checkDetailIsFill(salon: Salon): Boolean = {
    	salon.seatNums.nonEmpty  &&
        salon.picDescription.exists(pic=>pic.picTitle.nonEmpty) && salon.picDescription.exists(pic=>pic.picContent.nonEmpty) && 
        salon.picDescription.exists(pic=>pic.picFoot.nonEmpty) 
    }
    
    /**
     * 查看是否有店铺图片
     */
    def checkImgIsExist(salon: Salon): Boolean = {
        salon.salonPics.exists(a => a.picUse.equals("Navigate")) && salon.salonPics.exists(a => a.picUse.equals("Atmosphere")) &&
        salon.salonPics.exists(a => a.picUse.equals("SalonCheck"))
    }

    /**
     * 权限认证
     * 用于判断accountId是否为当前店铺
     */
    def isOwner(accountId: String)(salon: Salon): Future[Boolean] = Future { Salon.findByAccountId(accountId).map(_ == salon).get }
    


    /*--------------------------
     - 沙龙首页: 沙龙通用检索
     --------------------------*/
    /**
     * 前台店铺检索
     */
    def findSalonBySearchPara(searchParaForSalon : SearchParaForSalon) = {
        var salonSrchRst : List[SalonGeneralSrchRst] = Nil
        var salons : List[Salon] = Nil
        var salonList : List[Salon] = Nil

        // List to save search conditions.
        var srchConds: List[commonsDBObject] = Nil

        // process parameters for salon facilities.
        // if the checkbox in the general search page is checked, use it as a condition; otherwise, not use it. 
        var faclty = searchParaForSalon.salonFacilities
        if(faclty.canCurntDayOrder) { 
          srchConds :::= List(commonsDBObject("salonFacilities.canCurntDayOrder" -> faclty.canCurntDayOrder))
        }
        if(faclty.canImmediatelyOrder) {
          srchConds :::= List(commonsDBObject("salonFacilities.canImmediatelyOrder" -> faclty.canImmediatelyOrder))
        }
        if(faclty.canMaleUse) {
          srchConds :::= List(commonsDBObject("salonFacilities.canMaleUse" -> faclty.canMaleUse))
        }
        if(faclty.canNominateOrder) {
          srchConds :::= List(commonsDBObject("salonFacilities.canNominateOrder" -> faclty.canNominateOrder))
        }
        if(faclty.canOnlineOrder) {
          srchConds :::= List(commonsDBObject("salonFacilities.canOnlineOrder" -> faclty.canOnlineOrder))
        }
        if(faclty.hasParkingNearby) {
          srchConds :::= List(commonsDBObject("salonFacilities.hasParkingNearby" -> faclty.hasParkingNearby))
        }
        if(faclty.isPointAvailable) {
          srchConds :::= List(commonsDBObject("salonFacilities.isPointAvailable" -> faclty.isPointAvailable))
        }
        if(faclty.isPosAvailable) {
          srchConds :::= List(commonsDBObject("salonFacilities.isPosAvailable" -> faclty.isPosAvailable))
        }
        if(faclty.isWifiAvailable) {
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
        srchConds :::= List(commonsDBObject("salonAddress.city" -> searchParaForSalon.city))

        // keywords Array for Fuzzy search: will be joined with "$or" DSL operation.  
        val fuzzyRegex = convertKwdsToFuzzyRegex(searchParaForSalon.keyWord.getOrElse(""))(x => (".*" + x.trim + ".*|"))
        // from which fields the keyword be searched.
        val targetFields = getSrchTargetFields()
        val fuzzyConds = searchByFuzzyConds(targetFields, fuzzyRegex)
        // Join the normal conditions and fuzzy search keyword condition. 
        val srchCondsWithFuzzyKws = if(!fuzzyConds.isEmpty) $and(srchConds ::: List($or(fuzzyConds))) else $and(srchConds)

        // ------------------------------
        // -  Do salon general search. 
        // ------------------------------
        salonList = dao.find(srchCondsWithFuzzyKws).toList

        // 以serviceType/haircutPrice作为check条件
        if (searchParaForSalon.serviceType.nonEmpty) {
            salonList.map { salon =>
                if((Service.findServiceTypeBySalonId(salon.id).intersect(searchParaForSalon.serviceType).length == searchParaForSalon.serviceType.length) 
                   && (searchParaForSalon.priceRange.minPrice <= findLowestPriceBySalonId(salon.id)) 
                   && (findLowestPriceBySalonId(salon.id) <= searchParaForSalon.priceRange.maxPrice)) {
                    salons ::= salon
                }
            }
        } else {
            salonList.map { salon =>
                if((searchParaForSalon.priceRange.minPrice <= findLowestPriceBySalonId(salon.id)) 
                   && (findLowestPriceBySalonId(salon.id) <= searchParaForSalon.priceRange.maxPrice)) {
                    salons ::= salon
                }
            }
        }
 
        // exact regex keyword which used to get the exact words matched to the keyword.
        val exactRegex = convertKwdsToFuzzyRegex(searchParaForSalon.keyWord.getOrElse(""))(x => (x.trim + "|")) 
        for(sl <- salons) {
          // get top 2 styles of salon.
          val selStyles = Style.getBestRsvedStylesInSalon(sl.id, 2)
          val selCoupons = Coupon.findBySalon(sl.id)
          val rvwStat = Comment.getGoodReviewsRate(sl.id)
          // get the keywords hit strings.
          var kwsHits: List[String] = getKeywordsHitStrs(targetFields, fuzzyRegex, exactRegex)
          if(kwsHits.isEmpty) {
            kwsHits = getSalonPresentationAbbr(sl.picDescription)
          }

          salonSrchRst :::= List(SalonGeneralSrchRst(salonInfo = sl, selectedStyles = selStyles, selectedCoupons = selCoupons,
              reviewsStat = rvwStat, keywordsHitStrs = kwsHits))
        }

        salonSrchRst        
    }

 
    /**
     *
     */
    def getSalonPresentationAbbr(present: Option[PicDescription]): List[String] = {
      var abbrPres: List[String] = Nil
      present match {
        case None => abbrPres
        case Some(pres) => 
          abbrPres :::= List("..." + pres.picTitle.slice(0, 30) + "...")
          abbrPres :::= List("..." + pres.picContent.slice(0, 30) + "...")
          abbrPres :::= List("..." + pres.picFoot.slice(0, 30) + "...")
      }

      abbrPres
    }

    /**
     * Get keywords hit strings.
     *   Slice 
     */
    def getKeywordsHitStrs1(fuzzyKwds: List[commonsDBObject], exactKwds: Regex): List[String] = {
      var fuzzyHits: List[String] = Nil

      for(fz <- fuzzyKwds) { 
        // TODO can below done with reflection?
        var hit: String = ""
        if(fz.contains("salonName")) hit = dao.find(fz).map {_.salonName}.mkString
        if(fz.contains("salonNameAbbr")) hit = dao.find(fz).map {_.salonNameAbbr}.mkString
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

    /**
     * Get the slice of keyword matched fields.  
     *   # Check if the fields contains the keyword by fuzzy match, but get the match slice by exact match. #
     *
     */
    def getKeywordsHitStrs(targetFields: Array[String], fuzzyKwds: Regex, exactKwds: Regex): List[String] = {
      var fuzzyHits: List[String] = Nil

      for(tgtf <- targetFields) { 
        var hit: String = ""

        if((fuzzyKwds.toString != "") && (exactKwds != "")) {
          val fzKws = commonsDBObject(tgtf -> fuzzyKwds)
  
          // TODO can below done with reflection?
          if(tgtf == "salonName") hit = dao.find(fzKws).map {_.salonName}.mkString
          if(tgtf == "salonNameAbbr") hit = dao.find(fzKws).map {_.salonNameAbbr}.mkString
          if(tgtf == "salonDescription") hit = dao.find(fzKws).map {_.salonDescription}.mkString
          if(tgtf == "picDescription.picTitle") hit = dao.find(fzKws).map {_.picDescription.get.picTitle}.mkString
          if(tgtf == "picDescription.picContent") hit = dao.find(fzKws).map {_.picDescription.get.picContent}.mkString
          if(tgtf == "picDescription.picFoot") hit = dao.find(fzKws).map {_.picDescription.get.picFoot}.mkString
  
          // Use the Regex type method to get the first hit words in the target string.
          var firstHit = exactKwds.findFirstIn(hit)
          firstHit match {
            case None => fuzzyHits
            case Some(fstHit) => {
              // get the first hit index by Regex method.
              var mtch = exactKwds.findAllIn(hit)
              // cut out the search rst.
              if(!mtch.isEmpty) {
                fuzzyHits :::= List("..." + hit.slice(mtch.start, mtch.start + 30) + "...")
              }
            } 
          }
        }
      }

      println("fuzzyKwds = " + fuzzyKwds)
      println("exactKwds = " + exactKwds)
      println("fuzzyHits = " + fuzzyHits)
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
     * Fuzzy search to salon: can search by salon name, salon abbr name, salonDescription, picDescription...... 
     * TODO:
     *     For now, only consider multi-keywords separated by blank, 
     *     Not consider the way that deviding keyword into multi-keywords automatically. 
     */
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
        if(kwdsRegex.toString == "") {
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
        val kws = keyword.replace("　"," ")
        if(kws.replace(" ","").length == 0) {
            // when keyword is not exist, return Nil.
            "".r
        } else {
            // when keyword is exist, convert it to regular expression.
            val kwsStr = kws.split(" ").map { x => f(x.trim)}.mkString
            if(kwsStr.endsWith("|")) 
                kwsStr.dropRight(1).r
            else
                kwsStr.r
       }
    }


    /**
     * 检索某一店铺服务的最低价格
     */
    def findLowestPriceBySalonId(salonId : ObjectId) : BigDecimal = {
        var lowestPrice : BigDecimal = 0
        //获取最低剪发价格
        Service.getLowestPriceOfSrvType(salonId,"Cut") match {
            case Some(lowPrice) => { lowestPrice = lowPrice }
            case None => None
        }
        lowestPrice
    }

    def isExist(value:String, f:String => Option[Salon]) = f(value).map(salon => true).getOrElse(false)
}

/*----------------------------
 * Embed Structure of Salon.
 -----------------------------*/

/**
 * Embed Structure.
*/
case class Address (
    province: String,
    city: Option[String],
    region: Option[String],
    town: Option[String],
    addrDetail: String,
    longitude: Option[BigDecimal],
    latitude: Option[BigDecimal],
    accessMethodDesc: String
)

/**
 * Embed Structure.
*/
case class SalonFacilities (
   canOnlineOrder: Boolean,          
   canImmediatelyOrder: Boolean,
   canNominateOrder: Boolean,  
   canCurntDayOrder: Boolean,  
   canMaleUse: Boolean,
   isPointAvailable: Boolean,
   isPosAvailable: Boolean,
   isWifiAvailable: Boolean,
   hasParkingNearby: Boolean,
   parkingDesc: String
)

object SalonFacilities extends ModelCompanion[SalonFacilities, ObjectId] {
    def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("SalonFacilities")
        
        val dao = new SalatDAO[SalonFacilities, ObjectId](collection) {}
}

/**
 * Embed Structure.
*/
case class WorkTime(
    openTime: String,
    closeTime: String
)

/**
 * Embed Structure.
*/
case class RestDay(
    restWay: String,
    restDay: List[String]
)

/**
 * Embed Structure.
*/
case class SalonAccount(
    accountId:String,
    password:String
)

/**
 * Embed Structure.
*/
case class PicDescription(
	picTitle:String,
	picContent:String,
	picFoot:String	
)

/**
 * Embed Structure.
*/
case class Contact(
    mainPhone: String,
    contact: String, 
    email: String
)

case class SalonPics(
	salonPics: List[OnUsePicture]
)
