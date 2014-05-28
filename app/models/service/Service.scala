/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */

package models.portal.service

import java.util.Date
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.meifannet.framework.db._

/**
 * 定义服务class
 * @param id mongodb自动生成的objectId
 * @param serviceName 服务名
 * @param description 服务描述
 * @param serviceType 服务类型
 * @param salonId 所属店铺
 * @param price 价格
 * @param duration 服务时长
 * @param createdTime 服务创建日
 * @param expireTime 服务到期日
 * @param isValid 服务有效状态
 */
case class Service(
  id: ObjectId = new ObjectId,
  serviceName: String,
  description: String,
  serviceType: String,
  salonId: ObjectId,
  price: BigDecimal,
  duration: Int,
  createdTime: Date,
  expireTime: Option[Date],
  isValid: Boolean)

/**
 * 定义同一服务类型的服务的class
 * @param serviceTypeName 服务类别名
 * @param serviceItems 服务列表
 */
case class ServiceByType(
  serviceTypeName: String,
  serviceItems: Seq[Service])

object Service extends MeifanNetModelCompanion[Service] {

  val dao = new MeifanNetDAO[Service](collection = loadCollection()) {}

  /**
   * 添加服务
   * @param service 服务对象
   * @return
   */
  def addService(service: Service) = dao.save(service, WriteConcern.Safe)

  /**
   * 获取所有服务类型名列表
   * @return
   */
  def getServiceTypeList: List[String] = ServiceType.dao.find(MongoDBObject.empty).toList.map {
    serviceType => serviceType.serviceTypeName
  }

  /**
   * 获取所有服务列表
   * @return
   */
  def findAllServices: List[Service] = dao.find(MongoDBObject.empty).toList

  /**
   * 根据服务类型获取服务列表
   * @param serviceType 服务类型名
   * @return
   */
  def getTypeList(serviceType: String): List[Service] = {
    findAllServices.filter(s => s.serviceType == serviceType)
  }

  /**
   * 获取某店铺指定服务类型的最低价格，比如取得最低剪发价格。
   * Get the lowest price of a serviceType in a salon.
   * for example, get the lowest CUT price.
   * @param salonId
   * @param srvType
   * @return
   */
  def getLowestPriceOfSrvType(salonId: ObjectId, srvType: String): Option[BigDecimal] = {
    val srvs = dao.find(MongoDBObject("salonId" -> salonId, "serviceType" -> srvType)).sort(MongoDBObject("price" -> -1)).toList
    if (srvs.length > 0) Some(srvs(0).price) else None
  }

  /**
   * 根据服务类型和店铺ID获取服务列表
   * @param salonId 店铺ObjectId
   * @param serviceType 服务类型
   * @return
   */
  def getTypeListBySalonId(salonId: ObjectId, serviceType: String): List[Service] = {
    findBySalonId(salonId).filter(s => s.serviceType == serviceType)
  }

  /**
   * 根据店铺ID获取服务列表
   * @param salonId 店铺ObjectId
   * @return
   */
  def findBySalonId(salonId: ObjectId): List[Service] = dao.find(MongoDBObject("salonId" -> salonId)).toList

  /**
   * 删除服务
   * @param id 服务ObjectId
   * @return
   */
  def deleteService(id: ObjectId) = dao.remove(MongoDBObject("_id" -> id))

  /**
   * 检验该店铺是否已登录此服务
   * @param serviceNm 服务名
   * @param salonId 店铺ObjectId
   * @return
   */
  def checkServiceIsExist(serviceNm: String, salonId: ObjectId): Boolean = dao.find(MongoDBObject("serviceName" -> serviceNm, "salonId" -> salonId)).hasNext

  /**
   * 获取服务列表中的所有服务id
   * @param service
   * @return
   */
  def getServiceIdList(service: List[Service]): List[ObjectId] = service.map {
    service => service.id
  }

  /**
   * 利用salonId检索出其所有的服务类别
   * @param salonId
   * @return
   */
  def findServiceTypeBySalonId(salonId: ObjectId): List[String] = {
    val serviceAll = findBySalonId(salonId)
    val services = serviceAll.groupBy(_.serviceType)
    var serviceTypes: List[String] = Nil
    services.map { service =>
      serviceTypes = service._1 :: serviceTypes
    }
    serviceTypes
  }
}
