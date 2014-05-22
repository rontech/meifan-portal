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
package models

import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date
import com.meifannet.framework.db._

/**
 *
 * @param id
 * @param menuName
 * @param description
 * @param salonId
 * @param serviceItems
 * @param serviceDuration
 * @param originalPrice
 * @param createDate
 * @param expireDate
 * @param isValid
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
   *
   * @param menu
   * @return
   */
  def addMenu(menu: Menu) = dao.save(menu, WriteConcern.Safe)

  def findAllMenus: List[Menu] = dao.find(MongoDBObject.empty).toList

  /**
   *  查找出该沙龙中所有菜单
   *
   * @param salonId
   * @return
   */
  def findBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId)).toList

  /**
   *  查找出该沙龙中所用有效的菜单
   *
   * @param salonId
   * @return
   */
  def findValidMenusBySalon(salonId: ObjectId): List[Menu] = dao.find(MongoDBObject("salonId" -> salonId, "isValid" -> true)).toList

  /**
   *  查找沙龙中是否已存在该菜单
   *
   * @param menuName
   * @param salonId
   * @return
   */
  def checkMenuIsExist(menuName: String, salonId: ObjectId) = dao.find(DBObject("menuName" -> menuName, "salonId" -> salonId)).hasNext

  /**
   *  查找出该沙龙符合条件的所有菜单
   *
   * @param serviceTypes
   * @param salonId
   * @return
   */
  def findContainConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Menu] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId))).toList
  }

  /**
   *  查找出该沙龙符合条件的有效的菜单
   *
   * @param serviceTypes
   * @param salonId
   * @return
   */
  def findValidMenusByConditions(serviceTypes: Seq[String], salonId: ObjectId): List[Menu] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, DBObject("salonId" -> salonId, "isValid" -> true))).toList
  }

  /**
   *
   * @param menuName
   * @return
   */
  def findByName(menuName: String): List[Menu] = {
    dao.find("menuName" $eq menuName).toList
  }

  /**
   *
   * @param serviceTypes
   * @param menuName
   * @return
   */
  def findByConditions(serviceTypes: Seq[String], menuName: String): List[Menu] = {
    dao.find($and("serviceItems.serviceType" $all serviceTypes, "menuName" $eq menuName)).toList
  }

}
