
package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._

case class Service(
		id: ObjectId = new ObjectId,
		serviceName : String,
		description : String,
		serviceType : String,
		salonId : ObjectId,
		price : BigDecimal,
		duration : Int,
		createdTime : Date,
		expireTime : Option[Date],
		isValid : Boolean
		)
		
case class ServiceByType(
		serviceTypeName: String,
		serviceItems: Seq[Service]
)

object Service extends ModelCompanion[Service, ObjectId]{

	val dao = new SalatDAO[Service, ObjectId](collection = mongoCollection("Service")){}

	/**
	 * 添加服务
	 */
	def addService (service :Service) = dao.save(service, WriteConcern.Safe)

	/**
	 * 获取所有服务类型名列表
	 */
	def getServiceTypeList : List[String] = ServiceType.dao.find(MongoDBObject.empty).toList.map {
		serviceType =>serviceType.serviceTypeName
	}
	
	def getTypeByCondition(servicesTypes: List[String]) : List[String] = ServiceType.dao.find("serviceTypeName" $in servicesTypes).toList.map {
		serviceType =>serviceType.serviceTypeName
	}

	/**
	 * 获取所有服务列表
	 */
	def findAllServices : List[Service] = dao.find(MongoDBObject.empty).toList

	/**
	 * 根据服务类型获取服务列表
	 */
	def getTypeList(serviceType:String) : List[Service] = {
		findAllServices.filter(s => s.serviceType == serviceType)
	}

        /**
         * 获取某店铺指定服务类型的最低价格，比如取得最低剪发价格。
         * Get the lowest price of a serviceType in a salon. 
         *     for example, get the lowest CUT price.
         */
        def getLowestPriceOfSrvType(salonId: ObjectId, srvType: String): Option[BigDecimal] = {
               val srvs = dao.find(MongoDBObject("salonId" -> salonId, "serviceType" -> srvType)).sort(MongoDBObject("price" -> -1)).toList
               if(srvs.length > 0) Some(srvs(0).price) else None
        }

	
	/**
	 * 根据服务类型和店铺ID获取服务列表
	 */
	def getTypeListBySalonId(salonId: ObjectId, serviceType:String) : List[Service] = {
		findBySalonId(salonId).filter(s => s.serviceType == serviceType)
	}
	
	/**
	 * 根据店铺ID获取服务列表
	 */
	def findBySalonId(salonId: ObjectId): List[Service] = dao.find(MongoDBObject("salonId" -> salonId)).toList

	/**
	 * 删除服务
	 */
	def deleteService(id: ObjectId) = dao.remove(MongoDBObject("_id" -> id))

	/**
	 * 检验该店铺是否已登录此服务
	 */
	def checkService(serviceNm:String, salonId: ObjectId): Boolean = dao.find(MongoDBObject("serviceName" -> serviceNm, "salonId"->salonId)).hasNext

	/**
     * 获取服务列表中的所有服务id
     */
	def getServiceIdList(service: List[Service]) : List[ObjectId] = service.map {
		service => service.id
	}
}
