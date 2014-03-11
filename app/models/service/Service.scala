package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Service(
		id: ObjectId = new ObjectId,
		serviceName : String,
		serviceType : String,
		serviceTsCost : Int,
		servicePrice : Int
		)

object Service extends ModelCompanion[Service, ObjectId]{

	val dao = new SalatDAO[Service, ObjectId](collection = mongoCollection("Service")){}

	def addService (service :Service) = dao.save(service, WriteConcern.Safe)

	def getServiceTypeList : List[String] = ServiceType.dao.find(MongoDBObject.empty).toList.map {
		serviceType =>serviceType.serviceTypeName
	}

	def findAllServices : List[Service] = dao.find(MongoDBObject.empty).toList

	def getTypeList(serviceType:String) : List[Service] = {
		findAllServices.filter(s =>s.serviceType == serviceType)
	}

	def deleteService(id: ObjectId) = dao.remove(MongoDBObject("_id" -> id))

	def checkService(serviceNm:String): Boolean = dao.find(MongoDBObject("serviceName" -> serviceNm)).hasNext

	def findOneByServiceId(id: ObjectId): Option[Service] = dao.findOne(MongoDBObject("_id" -> id))
}
