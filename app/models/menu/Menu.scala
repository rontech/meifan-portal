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
        serviceItems: List[Service],
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
    
    // 查找出该沙龙中所有菜单
    def findBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId)).toList
    
    // 查找出该沙龙中所用有效的菜单
    def findValidMenusBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId, "isValid" -> true)).toList
    
    // 查找沙龙中是否已存在该菜单
    def checkMenuIsExist(menuName: String, salonId: ObjectId) = dao.find(DBObject("menuName" -> menuName, "salonId" -> salonId)).hasNext
    
    // 查找出该沙龙符合条件的所有菜单
    def findContainConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Menu] = {
    	dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId))).toList
    }
    
    // 查找出该沙龙符合条件的有效的菜单
    def findValidMenusByConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Menu] = {
    	dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId, "isValid" -> true))).toList
    }
    
    def findByName(menuName: String): List[Menu] = {
    	dao.find("menuName" $eq menuName).toList
    }
    
    def findByConditions(serviceTypes: Seq[String], menuName: String): List[Menu] = {
    	dao.find($and("serviceItems.serviceType" $all serviceTypes, "menuName" $eq menuName)).toList
    }

}
