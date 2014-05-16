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
import mongoContext._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import com.meifannet.framework.db._

case class Message(
  id: ObjectId,
  title: String,
  content: String,
  createdTime: Date)

object Message extends MeifanNetModelCompanion[Message] {

  val dao = new MeifanNetDAO[Message](collection = loadCollection()) {}

  def findById(id: ObjectId): Option[Message] = dao.findOne(MongoDBObject("_id" -> id))
}
