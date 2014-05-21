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

import java.util.Date
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.meifannet.framework.db._

/**
 * this class is to reset password by sending email
 *
 * @param id mongodb自带的id
 * @param uuid 用于生成的链接的唯一性
 * @param objId 用户指向user或salon表的id这个字段
 * @param endTime 用于记录当前链接的有效时间
 * @param objType 用户区分obj的对象 1代表user 2代表salon
 */
case class Mail(id: ObjectId = new ObjectId,
  uuid: String,
  objId: ObjectId,
  endTime: Date = new Date,
  objType: Int)

object Mail extends MeifanNetModelCompanion[Mail] {
  val dao = new MeifanNetDAO[Mail](collection = loadCollection()) {}

  /**
   * Insert data
   *
   * @param uuid 用于生成的链接的唯一性
   * @param objId 用户指向user或salon表的id这个字段
   * @param objType 用户区分obj的对象 1代表user 2代表salon
   * @param endTime 用于记录当前链接的有效时间
   * @return
   */
  def save(uuid: String, objId: ObjectId, objType: Int, endTime: Date) = {
    dao.save(Mail(uuid = uuid, objId = objId, objType = objType, endTime = endTime))
  }

  /**
   * get one document by objId
   *
   * @param objId the objectId of mail
   * @return
   */
  def findOneByObjId(objId: ObjectId) = {
    dao.findOne(MongoDBObject("objId" -> objId))
  }

  /**
   * get one document by uuid
   *
   * @param uuid the uuid of mail
   * @return
   */
  def findOneByUuid(uuid: String) = {
    dao.findOne(MongoDBObject("uuid" -> uuid))
  }

}
