package models

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import se.radley.plugin.salat.Binders._

case class ServiceType(
		id: ObjectId = new ObjectId,
		industryName : String,
		serviceTypeName : String,
		description : String
		)

object ServiceType extends ModelCompanion[ServiceType, ObjectId]{
	val dao = new SalatDAO[ServiceType, ObjectId](collection = mongoCollection("ServiceType")){}

	/**
	 * 添加服务类型
	 */
	def addServiceType(serviceType : ServiceType) = dao.save(serviceType, WriteConcern.Safe)
	
	/**
	 * 根据服务类型名获得此服务
	 */
	def findOneByTypeName(serviceTypeName: String): Option[ServiceType] = dao.findOne(MongoDBObject("serviceTypeName" -> serviceTypeName))
	
	/**
	 * 根据行业名获取该行业所有服务类型名
	 */
	def findAllServiceType(industryName: String) = dao.find(MongoDBObject("industryName" -> industryName)).toList.map {
		serviceType =>serviceType.serviceTypeName
	}
}
