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
package models.portal.common

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
 * 用于保存图片的内嵌表结构: 包括图片id，用途，显示优先级，和描述；
 *
 * @param fileObjId 图片Object Id
 * @param picUse 图片用途
 * @param showPriority 显示时的优先顺序
 * @param description 描述
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
 * 备选联系方式
 *
 * @param contMethodType 联系方式类型，如QQ, Skype, Mail 等等
 * @param accounts 联系方式帐号列表
 */
case class OptContactMethod(
  contMethodType: String,
  accounts: List[String])

/**
 * 联系方式类型
 * @param id
 * @param contMethodTypeName 联系方式类型名
 * @param description 类型描述
 */
case class ContMethodType(
  id: ObjectId = new ObjectId,
  contMethodTypeName: String,
  description: String)

object ContMethodType extends MeifanNetModelCompanion[ContMethodType] {

  val dao = new MeifanNetDAO[ContMethodType](collection = loadCollection()) {}

  /**
   * 取得所有联系方式的种类
   * @return
   */
  def getAllContMethodTypes = dao.find(MongoDBObject.empty).toSeq.map {
    contMethodType => contMethodType.contMethodTypeName -> Messages("ContMethodType.contMethodTypeName." + contMethodType.contMethodTypeName)
  }
}


/**
 * 默认Log
 *
 * @param id
 * @param imgId
 */
case class DefaultLog(
  id: ObjectId = new ObjectId,
  imgId: ObjectId)

object DefaultLog extends MeifanNetModelCompanion[DefaultLog] {

  val dao = new MeifanNetDAO[DefaultLog](collection = loadCollection()) {}

  /**
   * 保存图片
   *
   * @param defaultLog
   * @param imgId
   * @return
   */
  def saveLogImg(defaultLog: DefaultLog, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> defaultLog.id), MongoDBObject("$set" -> (MongoDBObject("imgId" -> imgId))), false, true)
  }

  /**
   * 获取图片的ObjectId
   *
   * @return
   */
  def getImgId = dao.find(MongoDBObject.empty).toList.head.imgId
}


/*----------------------------
 * Embed Structure of Salon.
 -----------------------------*/

/**
 * 地址结构
 * @param province 省
 * @param city 市
 * @param region 区
 * @param town 镇
 * @param addrDetail 详细地址
 * @param longitude 经度
 * @param latitude 纬度
 * @param accessMethodDesc 交通方法
 */
case class Address(
  province: String,
  city: Option[String],
  region: Option[String],
  town: Option[String],
  addrDetail: String,
  longitude: Option[BigDecimal],
  latitude: Option[BigDecimal],
  accessMethodDesc: String)