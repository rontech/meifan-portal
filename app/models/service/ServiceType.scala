package models

import com.mongodb.casbah.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import com.meifannet.framework.db._

case class ServiceType(
		id: ObjectId = new ObjectId,
		industryName : String,
		serviceTypeName : String,
		description : String
		)

object ServiceType extends MeifanNetModelCompanion[ServiceType]{

    val dao = new MeifanNetDAO[ServiceType](collection = loadCollection()){}

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
	
	/**
	 * 根据行业名获得所有服务类型
	 */
	def findAllServiceTypes(salonIndustrys: List[String]) = {
	  var serviceTypes: List[ServiceType] = Nil
	  
	  for(salonIndustry <- salonIndustrys) {
	    val serviceType: List[ServiceType] = dao.find(MongoDBObject("industryName" -> salonIndustry)).toList
	    serviceTypes = serviceTypes ::: serviceType
	  }
	  
	  serviceTypes
	}
}
