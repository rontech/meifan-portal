package models

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date
import play.api.PlayException

case class Menu (
        id: ObjectId = new ObjectId,
        menuName: String,
        description: String,
        salonId: ObjectId,
        serviceItems: Seq[Service],
        serviceDuration: Int,
        originalPrice: BigDecimal,
        createDate: Date,
        expireDate: Option[Date],
        isValid: Boolean
)

object Menu extends ModelCompanion[Menu, ObjectId]{
    
    def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("Menu")

    val dao = new SalatDAO[Menu, ObjectId](collection){}
    
    // Indexes
    collection.ensureIndex(DBObject("menuName" -> 1), "menuName", unique = true)

    def addMenu (menu :Menu) = dao.save(menu, WriteConcern.Safe)

    def findAllMenus : List[Menu] = dao.find(MongoDBObject.empty).toList
    
    def findBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId)).toList
    
    def findContainCondtions(serviceTypes: Seq[String]): List[Menu] = {
    	dao.find("serviceItems.serviceType" $all serviceTypes).toList
    }
    
    def findByName(menuName: String): List[Menu] = {
    	dao.find("menuName" $eq menuName).toList
    }
    
    def findByCondtions(serviceTypes: Seq[String], menuName: String): List[Menu] = {
    	dao.find($and("serviceItems.serviceType" $all serviceTypes, "menuName" $eq menuName)).toList
    }

}
