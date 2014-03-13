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
 * Associated Tables of Salon.
 -----------------------------*/

/**
 * The Industry Type of Salon. 
*/
case class Industry (
    id: ObjectId = new ObjectId,
    //indstId: Int,
    indstName: String
)

object Industry extends ModelCompanion[Industry, ObjectId]{
  val dao = new SalatDAO[Industry, ObjectId](collection = mongoCollection("Industry")){}
  
  def findById(id: ObjectId): Option[Industry] = dao.findOne(MongoDBObject("_id" -> id))
  
}

/**
 * 
*/
case class Address (
    province: String,
    city: String,
    region: String,
    town: Option[String],
    addrDetail: String,
    longitude: Option[Double],
    latitude: Option[Double]
)

/**
 * 
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
 * 
*/
case class OptContactMethod (
    contMethmodType: String,
    account: String
)


/*
case class SalonPictures {
    
}
*/



/*
 * Main Class: Salon.
*/

case class Salon(
    id: ObjectId = new ObjectId,        // Salon Id
    salonName: String,                  // Salon Name 
    salonNameAbbr: Option[String],      // Salon Name abbr
    salonIndst: String,               // Salon Industry
    homepage: Option[String],           //
    salonDescription: Option[String],   //
    mainPhone: String,
    contact: String,
    optContactMethod: Seq[OptContactMethod],
    establishDate: Date,
    address: Address,
    accessMethodDesc: String,
    openTime: String,                   // TODO
    closeTime: String,                  // TODO
    restDay: String,                    // TODO
    seatNums: Int,
    salonFacilities: SalonFacilities,   // 
    salonPictures: String,              // TODO SalonPictures
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
                salonName = salon.salonName,
                salonNameAbbr = salon.salonNameAbbr,
                salonIndst = salon.salonIndst,
                homepage = salon.homepage,
                salonDescription = salon.salonDescription,
                mainPhone = salon.mainPhone,
                contact = salon.contact,
                optContactMethod = salon.optContactMethod,
                establishDate = salon.establishDate,
                address = salon.address,
                accessMethodDesc = salon.accessMethodDesc,
                openTime = salon.openTime,
                closeTime = salon.closeTime,
                restDay = salon.restDay,
                seatNums = salon.seatNums,
                salonFacilities = salon.salonFacilities,
                salonPictures = salon.salonPictures,
                registerDate = salon.registerDate
             )
        )
    }

    def save(salon: Salon) = {
        SalonDAO.save(
            Salon(
                id = salon.id,
                salonName = salon.salonName,
                salonNameAbbr = salon.salonNameAbbr,
                salonIndst = salon.salonIndst,
                homepage = salon.homepage,
                salonDescription = salon.salonDescription,
                mainPhone = salon.mainPhone,
                contact = salon.contact,
                optContactMethod = salon.optContactMethod,
                establishDate = salon.establishDate,
                address = salon.address,
                accessMethodDesc = salon.accessMethodDesc,
                openTime = salon.openTime,
                closeTime = salon.closeTime,
                restDay = salon.restDay,
                seatNums = salon.seatNums,
                salonFacilities = salon.salonFacilities,
                salonPictures = salon.salonPictures,
                registerDate = salon.registerDate
            )
        )

    }

    def delete(id: String) {
        SalonDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }

} 

