package models

import play.api.Play.current
import play.api.PlayException

import com.novus.salat._
import com.novus.salat.dao._
import com.novus.salat.Context 

import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection


import mongoContext._

import se.radley.plugin.salat._
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
    city: String,
    region: String,
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
    restDay: Int
)



/*----------------------------
 * Embed Structure of Salon.
 -----------------------------*/
/*
 * Main Class: Salon.
*/

case class Salon(
    id: ObjectId = new ObjectId,   	
    accountId:String,
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
    restDay: List[RestDay],                    
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

    def create(salon: Salon): Option[ObjectId] = {
        SalonDAO.insert(
            Salon(
                accountId = salon.accountId,
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
                restDay = salon.restDay,
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
                accountId = salon.accountId,
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
                restDay = salon.restDay,
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

} 
