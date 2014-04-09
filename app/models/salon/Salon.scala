package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date
import models._
import org.mindrot.jbcrypt.BCrypt

/*----------------------------
 * Embed Structure of Salon.
 -----------------------------*/
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
    picDescription: PicDescription,
    mainPhone: String,
    contact: String, 
    optContactMethod: List[OptContactMethod],
    establishDate: Date,
    salonAddress: Address,
    workTime: WorkTime,
    restDays: RestDay,
    seatNums: Int,
    salonFacilities: SalonFacilities,    
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
    
    def updateSalonShow(salon: Salon, imgId: ObjectId) = {    
          dao.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "Navigate"), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgId))),false,true)
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

case class PicDescription(
	picTitle:String,
	picContent:String,
	picFoot:String	
)