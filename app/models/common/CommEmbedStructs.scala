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
import play.api.PlayException
import com.novus.salat._
import com.mongodb.casbah.commons.Imports._
import com.novus.salat.Context
import mongoContext._
import org.bson.types.ObjectId
import java.io.File
import com.mongodb.casbah.gridfs.Imports._
import com.mongodb.casbah.gridfs.GridFS
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import play.api.i18n.Messages
import com.meifannet.framework.db._
/**
 * Embed Structure.
 */
case class OnUsePicture(
  fileObjId: ObjectId,
  picUse: String, // Ref to the PictureUse Master Table, but we only use the [picUseName] field as a key.
  showPriority: Option[Int],
  description: Option[String])

object OnUsePicture extends MeifanNetModelCompanion[OnUsePicture] {

  val dao = new MeifanNetDAO[OnUsePicture](collection = loadCollection()) {}

  //  collection.ensureIndex(DBObject("fileObjId" -> 1), "id", unique = true)

}

/**
 * Embed Structure.
 */
case class OptContactMethod(
  contMethodType: String,
  accounts: List[String])

case class ContMethodType(
  id: ObjectId = new ObjectId,
  contMethodTypeName: String,
  description: String)

object ContMethodType extends MeifanNetModelCompanion[ContMethodType] {

  val dao = new MeifanNetDAO[ContMethodType](collection = loadCollection()) {}

  def getAllContMethodTypes = dao.find(MongoDBObject.empty).toSeq.map {
    contMethodType => contMethodType.contMethodTypeName -> Messages("ContMethodType.contMethodTypeName." + contMethodType.contMethodTypeName)
  }
}

/**
 * 默认Log
 */
case class DefaultLog(
  id: ObjectId = new ObjectId,
  imgId: ObjectId)

object DefaultLog extends MeifanNetModelCompanion[DefaultLog] {

  val dao = new MeifanNetDAO[DefaultLog](collection = loadCollection()) {}

  /**
   * 保存图片
   */
  def saveLogImg(defaultLog: DefaultLog, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> defaultLog.id), MongoDBObject("$set" -> (MongoDBObject("imgId" -> imgId))), false, true)
  }

  /**
   * 获取图片的ObjectId
   */
  def getImgId = dao.find(MongoDBObject.empty).toList.head.imgId
}
