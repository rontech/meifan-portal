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

import com.mongodb.casbah.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import com.meifannet.framework.db._

/**
 * 定义服务类别class
 * @param id
 * @param industryName 行业名称
 * @param serviceTypeName 服务类别名
 * @param description 描述
 */
case class ServiceType(
  id: ObjectId = new ObjectId,
  industryName: String,
  serviceTypeName: String,
  description: String)

object ServiceType extends MeifanNetModelCompanion[ServiceType] {

  val dao = new MeifanNetDAO[ServiceType](collection = loadCollection()) {}

  /**
   * 添加服务类型
   * @param serviceType 服务类型对象
   * @return
   */
  def addServiceType(serviceType: ServiceType) = dao.save(serviceType, WriteConcern.Safe)

  /**
   * 根据服务类型名获得此服务
   * @param serviceTypeName 服务类型名
   * @return
   */
  def findOneByTypeName(serviceTypeName: String): Option[ServiceType] = dao.findOne(MongoDBObject("serviceTypeName" -> serviceTypeName))

  /**
   * 根据行业名获取该行业所有服务类型名
   * @param industryName 行业名
   * @return
   */
  def findAllServiceType(industryName: String) = dao.find(MongoDBObject("industryName" -> industryName)).toList.map {
    serviceType => serviceType.serviceTypeName
  }

  /**
   * 根据行业名获得所有服务类型
   * @param salonIndustrys
   * @return
   */
  def findAllServiceTypes(salonIndustrys: List[String]) = {
    var serviceTypes: List[ServiceType] = Nil

    for (salonIndustry <- salonIndustrys) {
      val serviceType: List[ServiceType] = dao.find(MongoDBObject("industryName" -> salonIndustry)).toList
      serviceTypes = serviceTypes ::: serviceType
    }

    serviceTypes
  }
}
