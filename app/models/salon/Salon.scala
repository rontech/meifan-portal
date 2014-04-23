package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date
import models._
import org.mindrot.jbcrypt.BCrypt
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import com.mongodb.casbah.query.Imports._
import com.mongodb.casbah.commons.Imports.{ DBObject => commonsDBObject }
import play.Configuration

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

object Salon extends ModelCompanion[Salon, ObjectId] {
  
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("Salon")
        
  val dao = new SalatDAO[Salon, ObjectId](collection) {}
  
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
        salon.salonPics.exists(a => a.picUse.equals("Navigate")) && salon.salonPics.exists(a => a.picUse.equals("Atmosphere"))
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
        var salons : List[models.Salon] = Nil
        var salonList : List[models.Salon] = Nil
        //对salonFacilities条件进行变形
        var canOnlineOrder = List(true,false)
        var canImmediatelyOrder = List(true,false)
        var canNominateOrder = List(true,false)
        var canCurntDayOrder = List(true,false)
        var canMaleUse = List(true,false)
        var isPointAvailable = List(true,false)
        var isPosAvailable = List(true,false)
        var isWifiAvailable = List(true,false)
        var hasParkingNearby = List(true,false)
        if(searchParaForSalon.salonFacilities.canCurntDayOrder) {
            canCurntDayOrder = List(true)
        }
        if(searchParaForSalon.salonFacilities.canImmediatelyOrder) {
            canImmediatelyOrder = List(true)
        }
        if(searchParaForSalon.salonFacilities.canMaleUse) {
            canMaleUse = List(true)
        }
        if(searchParaForSalon.salonFacilities.canNominateOrder) {
            canNominateOrder = List(true)
        }
        if(searchParaForSalon.salonFacilities.canOnlineOrder) {
            canOnlineOrder = List(true)
        }
        if(searchParaForSalon.salonFacilities.hasParkingNearby) {
            hasParkingNearby = List(true)
        }
        if(searchParaForSalon.salonFacilities.isPointAvailable) {
            isPointAvailable = List(true)
        }
        if(searchParaForSalon.salonFacilities.isPosAvailable) {
            isPosAvailable = List(true)
        }
        if(searchParaForSalon.salonFacilities.isWifiAvailable) {
            isWifiAvailable = List(true)
        }
        //对region/salonName/salonIndustry/seatNums检索条件变形
        //"_id" -> MongoDBObject("$exists" -> true)为恒成立条件
        val region = if (searchParaForSalon.region.equals("all")) { "_id" -> MongoDBObject("$exists" -> true) } else { "salonAddress.region" -> searchParaForSalon.region }
        val salonIndustry = if (searchParaForSalon.salonIndustry.equals("all")) { MongoDBObject.empty } else { "salonIndustry" $in List(searchParaForSalon.salonIndustry) }
        val salonName = if (searchParaForSalon.salonName.isEmpty) { MongoDBObject.empty } else { "salonName" $in searchParaForSalon.salonName }
        //以city/region/salonName/salonIndustry/seatNums/salonFacilities作为check条件

        val srchConds = salonName ::
           salonIndustry ::
           ("salonFacilities.canOnlineOrder" $in canOnlineOrder) ::
           ("salonFacilities.canImmediatelyOrder" $in canImmediatelyOrder) ::
           ("salonFacilities.canNominateOrder" $in canNominateOrder) ::
           ("salonFacilities.canCurntDayOrder" $in canCurntDayOrder) ::
           ("salonFacilities.canMaleUse" $in canMaleUse) ::
           ("salonFacilities.isPointAvailable" $in isPointAvailable) ::
           ("salonFacilities.isPosAvailable" $in isPosAvailable) ::
           ("salonFacilities.isWifiAvailable" $in isWifiAvailable) ::
           ("salonFacilities.hasParkingNearby" $in hasParkingNearby) ::
           ("seatNums" $lte searchParaForSalon.seatNums.maxNum $gte searchParaForSalon.seatNums.minNum) ::
           MongoDBObject("salonAddress.city" -> searchParaForSalon.city, region) :: Nil

        // keywords for Fuzzy search  
        val fuzzyKws = contactKwdsToFuzzyConds(searchParaForSalon.keyWord.getOrElse(""))

        val srchCondsWithFuzzyKws = if(!fuzzyKws.isEmpty) $and(srchConds ::: List($or(fuzzyKws))) else $and(srchConds)
        salonList = dao.find(srchCondsWithFuzzyKws).toList

        //以serviceType/haircutPrice作为check条件
        if (searchParaForSalon.serviceType.nonEmpty) {
            salonList.map { salon =>
                if(Service.findServiceTypeBySalonId(salon.id).intersect(searchParaForSalon.serviceType).length == searchParaForSalon.serviceType.length 
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
        salons
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
            val searchFields = Array("salonName", "salonNameAbbr", "salonDescription", 
                "picDescription.picTitle", "picDescription.picContent", "picDescription.picFoot")
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
    def contactKwdsToFuzzyConds(keyword: String): List[commonsDBObject] = {
        var rst: List[commonsDBObject] = Nil 
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
            val searchFields = Array("salonName", "salonNameAbbr", "salonDescription", 
                "picDescription.picTitle", "picDescription.picContent", "picDescription.picFoot")
            searchFields.map { sf => 
                var s = commonsDBObject(sf -> kwsRegex)
                rst :::= List(s)  
            }
            rst
        }
        // println($or(rst))
        rst
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
