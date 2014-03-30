package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date



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
    latitude: Option[BigDecimal]
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
    restDayDivision: Int,
    restDay: List[Int]
)

/**
 * Embed Structure.
*/
case class SalonAccount(
    accountId:String,
    password:String
)

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
    mainPhone: String,
    contact: String, 
    optContactMethod: List[OptContactMethod],
    establishDate: Date,
    salonAddress: Address,
    accessMethodDesc: String,
    workTime: WorkTime,
    restDays: List[RestDay],                    
    seatNums: Int,
    salonFacilities: SalonFacilities,    
    salonPics: List[OnUsePicture],             
    registerDate: Date
)


object SalonDAO extends SalatDAO[Salon, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Salon"))


object Salon {

    def findAll(): List[Salon] = {
        SalonDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[Salon] = {
        SalonDAO.findOne(MongoDBObject("_id" -> id))
    }
    
    def findByAccountId(salonAccount: SalonAccount): Option[Salon] = {
        SalonDAO.findOne(MongoDBObject("salonAccount.accountId" -> salonAccount.accountId))
    }    

    def loginCheck(salonAccount: SalonAccount): Option[Salon] = {
        SalonDAO.findOne(MongoDBObject("salonAccount.accountId" -> salonAccount.accountId,"salonAccount.password" -> salonAccount.password))
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

    def create(salon: Salon): Option[ObjectId] = {
        SalonDAO.insert(
            Salon(
                id = salon.id,                
                salonAccount = salon.salonAccount,  
                salonName = salon.salonName,
                salonNameAbbr = salon.salonNameAbbr,
                salonIndustry = salon.salonIndustry,
                homepage = salon.homepage,
                salonDescription = salon.salonDescription,
                mainPhone = salon.mainPhone,
                contact = salon.contact,
                optContactMethod = salon.optContactMethod,
                establishDate = salon.establishDate,
                salonAddress = salon.salonAddress,
                accessMethodDesc = salon.accessMethodDesc,
                workTime = salon.workTime,
                restDays = salon.restDays,
                seatNums = salon.seatNums,
                salonFacilities = salon.salonFacilities,
                salonPics = salon.salonPics,
                registerDate = salon.registerDate
             )
        )
    }

    def save(salon: Salon) = {
        SalonDAO.save(
            Salon(
                id = salon.id,
                salonAccount = salon.salonAccount,               
                salonName = salon.salonName,
                salonNameAbbr = salon.salonNameAbbr,
                salonIndustry = salon.salonIndustry,
                homepage = salon.homepage,
                salonDescription = salon.salonDescription,
                mainPhone = salon.mainPhone,
                contact = salon.contact,
                optContactMethod = salon.optContactMethod,
                establishDate = salon.establishDate,
                salonAddress = salon.salonAddress,
                accessMethodDesc = salon.accessMethodDesc,
                workTime = salon.workTime,
                restDays = salon.restDays,
                seatNums = salon.seatNums,
                salonFacilities = salon.salonFacilities,
                salonPics = salon.salonPics,
                registerDate = salon.registerDate
            )
        )

    }

    def delete(id: String) {
        SalonDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

    def updateSalonLogo(salon: Salon, imgId: ObjectId) = {
      SalonDAO.update(MongoDBObject("_id" -> salon.id, "salonPics.picUse" -> "logo", "salonPics.stylePic.showPriority" -> 0), 
            MongoDBObject("$set" -> ( MongoDBObject("salonPics.$.fileObjId" ->  imgId))),false,true)
    }
} 

