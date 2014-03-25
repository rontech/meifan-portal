package models

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import se.radley.plugin.salat.Binders._

case class ServiceType(
		id: ObjectId = new ObjectId,
		serviceTypeName : String,
		description : String
		)

object ServiceType extends ModelCompanion[ServiceType, ObjectId]{
	val dao = new SalatDAO[ServiceType, ObjectId](collection = mongoCollection("ServiceType")){}

	def addServiceType(serviceType : ServiceType) = dao.save(serviceType, WriteConcern.Safe)
	
	def findOneByTypeId(id: ObjectId): Option[ServiceType] = dao.findOne(MongoDBObject("_id" -> id))
}
