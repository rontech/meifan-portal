package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Menu (
        id: ObjectId = new ObjectId,
        salonId: ObjectId,
        serviceItems: Seq[Service],
        categories: Seq[ServiceType],
        originalPrice: BigDecimal,
        status: String
)

object Menu extends ModelCompanion[Menu, ObjectId]{

    val dao = new SalatDAO[Menu, ObjectId](collection = mongoCollection("Menu")){}

    def addMenu (menu :Menu) = dao.save(menu, WriteConcern.Safe)

    def findAllMenus : List[Menu] = dao.find(MongoDBObject.empty).toList
    
    def findBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId)).toList

}