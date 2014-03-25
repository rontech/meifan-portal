package models

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date

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

    val dao = new SalatDAO[Menu, ObjectId](collection = mongoCollection("Menu")){}

    def addMenu (menu :Menu) = dao.save(menu, WriteConcern.Safe)

    def findAllMenus : List[Menu] = dao.find(MongoDBObject.empty).toList
    
    def findBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId)).toList
    
    def findContainCondtions(serviceTypes: Seq[String]): List[Menu] = {
    	dao.find("serviceItems.serviceType" $all serviceTypes).toList
    }

}
