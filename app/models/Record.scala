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

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._

case class Record(id: ObjectId = new ObjectId,
  store: String,
  serviceStatus: Int,
  serviceStart: Date,
  serviceEnd: Date,
  serviceDesigner: String,
  serviceItem: String,
  userName: String,
  userPhone: String,
  userLeaveMessage: String,
  costTotal: Int)

object Record extends ModelCompanion[Record, ObjectId] {
  val dao = new SalatDAO[Record, ObjectId](collection = mongoCollection("record")) {}

  def findById(id: ObjectId): Option[Record] = dao.findOne(MongoDBObject("_id" -> id))

  def findAllrecord(store: String, page: Int, pageSize: Int): List[Record] = dao.find(MongoDBObject("store" -> store)).sort(MongoDBObject("serviceStart" -> -1)).skip((page - 1) * pageSize).limit(pageSize).toList

  def createRecord(record: Record) = dao.save(record, WriteConcern.Safe)

  def counts(store: String) = dao.count(MongoDBObject("store" -> store))

  def findByCondition(store: String, designer: String) = dao.find(MongoDBObject("store" -> store, "designer" -> designer)).toList

  def findByQuery(query: DBObject, page: Int, pageSize: Int) = {
    dao.find(query).sort(MongoDBObject("serviceStart" -> -1)).skip((page - 1) * pageSize).limit(pageSize).toList
  }
  def countByCondition(query: DBObject) = {
    dao.count(query)
  }
}
