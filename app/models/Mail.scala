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

case class Mail (id: ObjectId = new ObjectId,
              uuid: String,
              objId : ObjectId,
              endTime : Date = new Date,
              objType : Int 
              )

object Mail extends MeifanNetModelCompanion[Mail]{
  val dao = new MeifanNetDAO[Mail](collection = loadCollection()){}
  
  def save(uuid : String, objId : ObjectId, objType : Int, endTime : Date) = {
    dao.save(Mail(uuid = uuid, objId = objId, objType = objType, endTime = endTime))
  } 
  
  def findOneByObjId(objId : ObjectId) = {
    dao.findOne(MongoDBObject("objId" -> objId))
  }
  
  def findOneByUuid(uuid : String) = {
    dao.findOne(MongoDBObject("uuid" -> uuid))
  }
  

}
