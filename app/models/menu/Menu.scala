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
package models.portal.menu

import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date
import com.meifannet.framework.db._
import models.portal.service.Service

/**
 * 菜单表
 * @param id
 * @param menuName 菜单名
 * @param description 描述
 * @param salonId 沙龙id
 * @param serviceItems 菜单中所包含的服务列表
 * @param serviceDuration 服务总时长
 * @param originalPrice 原价
 * @param createDate 菜单创建日期
 * @param expireDate 菜单无效日期
 * @param isValid 菜单是否有效
 */
case class Menu(
  id: ObjectId = new ObjectId,
  menuName: String,
  description: String,
  salonId: ObjectId,
  serviceItems: List[Service],
  serviceDuration: Int,
  originalPrice: BigDecimal,
  createDate: Date,
  expireDate: Option[Date],
  isValid: Boolean)

object Menu extends MeifanNetModelCompanion[Menu] {

  val dao = new MeifanNetDAO[Menu](collection = loadCollection()) {}

  // Indexes
  //collection.ensureIndex(DBObject("menuName" -> 1), "menuName", unique = true)

  /**
   * 保存菜单
   * @param menu
   * @return
   */
  def addMenu(menu: Menu) = dao.save(menu, WriteConcern.Safe)

  /**
   * 查找所有的菜单
   * @return
   */
  def findAllMenus: List[Menu] = dao.find(MongoDBObject.empty).toList

  /**
   * 查找出该沙龙中所有菜单
   *
   * @param salonId 沙龙id
   * @return
   */
  def findBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId)).toList

  /**
   * 查找出该沙龙中所用有效的菜单
   *
   * @param salonId 沙龙id
   * @return
   */
  def findValidMenusBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId, "isValid" -> true)).toList

  /**
   * 查找沙龙中是否已存在该菜单
   * 用于创建菜单时根据菜单名检查是否已存在该菜单
   * @param menuName 菜单名称
   * @param salonId 沙龙id
   * @return
   */
  def checkMenuIsExist(menuName: String, salonId: ObjectId) = dao.find(DBObject("menuName" -> menuName, "salonId" -> salonId)).hasNext

  /**
   * 查找出该沙龙符合条件的所有菜单
   * 用于后台菜单检索
   * @param serviceTypes 服务列表
   * @param salonId 沙龙id
   * @return
   */
  def findContainConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Menu] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId))).toList
  }

  /**
   * 查找出该沙龙符合条件的有效的菜单
   * 用于前台菜单检索
   * @param serviceTypes 服务列表
   * @param salonId 沙龙id
   * @return
   */
  def findValidMenusByConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Menu] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId, "isValid" -> true))).toList
  }

  /**
   * 根据服务名查找服务
   * 
   * @param menuName 服务名
   * @return
   */
  def findByName(menuName: String): List[Menu] = {
    dao.find("menuName" $eq menuName).toList
  }

  /**
   * 根据服列表和菜单名查找服务条件的菜单
   * 
   * @param serviceTypes 服务列表
   * @param menuName 菜单名
   * @return
   */
  def findByConditions(serviceTypes: Seq[String], menuName: String): List[Menu] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, "menuName" $eq menuName)).toList
  }

}
