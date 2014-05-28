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
package models.portal.style

import play.api.Play.current
import play.api.PlayException
import com.mongodb.casbah.commons.Imports._
import mongoContext._
import com.meifannet.framework.db._

/**
 * The table of styleLength
 * @param id ObjectId ObjectId
 * @param styleLength 发型适合长度
 * @param description 描述
 */
case class StyleLength(
  id: ObjectId = new ObjectId,
  styleLength: String,
  description: String)

object StyleLength extends MeifanNetModelCompanion[StyleLength] {
  val dao = new MeifanNetDAO[StyleLength](collection = loadCollection()) {}
}

/**
 * The table of styleColor
 * @param id ObjectId ObjectId
 * @param styleColor 发型适合颜色
 * @param description 描述
 */
case class StyleColor(
  id: ObjectId = new ObjectId,
  styleColor: String,
  description: String)

object StyleColor extends MeifanNetModelCompanion[StyleColor] {
  val dao = new MeifanNetDAO[StyleColor](collection = loadCollection()) {}
}

/**
 * The table of StyleStyleImpression
 * @param id ObjectId ObjectId
 * @param styleImpression 发型适合风格
 * @param description 描述
 */
case class StyleImpression(
  id: ObjectId = new ObjectId,
  styleImpression: String,
  description: String)

object StyleImpression extends MeifanNetModelCompanion[StyleImpression] {
  val dao = new MeifanNetDAO[StyleImpression](collection = loadCollection()) {}
}

/**
 * The table of styleAmount
 * @param id ObjectId ObjectId
 * @param styleAmount 发型适合发量
 * @param description 描述
 */
case class StyleAmount(
  id: ObjectId = new ObjectId,
  styleAmount: String,
  description: String)

object StyleAmount extends MeifanNetModelCompanion[StyleAmount] {
  val dao = new MeifanNetDAO[StyleAmount](collection = loadCollection()) {}
}

/**
 * The table of styleQuality
 * @param id ObjectId
 * @param styleQuality 发型适合发质
 * @param description 描述
 */
case class StyleQuality(
  id: ObjectId = new ObjectId,
  styleQuality: String,
  description: String)

object StyleQuality extends MeifanNetModelCompanion[StyleQuality] {
  val dao = new MeifanNetDAO[StyleQuality](collection = loadCollection()) {}
}

/**
 * The table of styleDiameter
 * @param id ObjectId
 * @param styleDiameter 发型适合直径
 * @param description 描述
 */
case class StyleDiameter(
  id: ObjectId = new ObjectId,
  styleDiameter: String,
  description: String)

object StyleDiameter extends MeifanNetModelCompanion[StyleDiameter] {
  val dao = new MeifanNetDAO[StyleDiameter](collection = loadCollection()) {}
}

/**
 * The table of faceShape
 * @param id ObjectId
 * @param faceShape 发型适合脸型
 * @param description 描述
 */
case class FaceShape(
  id: ObjectId = new ObjectId,
  faceShape: String,
  description: String)

object FaceShape extends MeifanNetModelCompanion[FaceShape] {
  val dao = new MeifanNetDAO[FaceShape](collection = loadCollection()) {}
}

/**
 * The table of socialStatus
 * @param id ObjectId
 * @param socialStatus 发型适合场合
 * @param description 描述
 */
case class SocialStatus(
  id: ObjectId = new ObjectId,
  socialStatus: String,
  description: String)

object SocialStatus extends MeifanNetModelCompanion[SocialStatus] {
  val dao = new MeifanNetDAO[SocialStatus](collection = loadCollection()) {}
}

/**
 * The table of ageGroup
 * @param id ObjectId
 * @param ageGroup 发型适合年龄段
 * @param description 描述
 */
case class AgeGroup(
  id: ObjectId = new ObjectId,
  ageGroup: String,
  description: String)

object AgeGroup extends MeifanNetModelCompanion[AgeGroup] {
  val dao = new MeifanNetDAO[AgeGroup](collection = loadCollection()) {}
}

/**
 * The table of sex
 * @param id ObjectId
 * @param sex 发型适合性别
 */
case class Sex(
  id: ObjectId = new ObjectId,
  sex: String)

object Sex extends MeifanNetModelCompanion[Sex] {
  val dao = new MeifanNetDAO[Sex](collection = loadCollection()) {}
}

/**
 * The table of stylePicDescription
 * @param id ObjectId
 * @param stylePicDescription 发型图片描述
 * @param description 描述
 */
case class StylePicDescription(
  id: ObjectId = new ObjectId,
  stylePicDescription: String,
  description: String)

object StylePicDescription extends MeifanNetModelCompanion[StylePicDescription] {
  val dao = new MeifanNetDAO[StylePicDescription](collection = loadCollection()) {}
}

/**
 * The table of searchByLengthForF
 * 用于存放发型导航女性长度检索一栏
 * @param id ObjectId
 * @param sex 适合性别
 * @param styleLength 发长
 * @param stylePic 发型图片
 * @param description 描述
 */
case class SearchByLengthForF(
  id: ObjectId = new ObjectId,
  sex: String,
  styleLength: String,
  stylePic: ObjectId,
  description: String)

object SearchByLengthForF extends MeifanNetModelCompanion[SearchByLengthForF] {
  val dao = new MeifanNetDAO[SearchByLengthForF](collection = loadCollection()) {}
  //保存图片
  def saveSearchByLengthForFImage(searchByLengthForF: SearchByLengthForF, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> searchByLengthForF.id),
      MongoDBObject("$set" -> (MongoDBObject("stylePic" -> imgId))), false, true)
  }
}

/**
 * The table of searchByLengthForM
 * 用于存放发型导航男性长度检索一栏
 * @param id ObjectId
 * @param sex 适合性别
 * @param styleLength 发长
 * @param stylePic 发型图片
 * @param description 描述
 */
case class SearchByLengthForM(
  id: ObjectId = new ObjectId,
  sex: String,
  styleLength: String,
  stylePic: ObjectId,
  description: String)

object SearchByLengthForM extends MeifanNetModelCompanion[SearchByLengthForM] {
  val dao = new MeifanNetDAO[SearchByLengthForM](collection = loadCollection()) {}
  //保存图片
  def saveSearchByLengthForMImage(searchByLengthForM: SearchByLengthForM, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> searchByLengthForM.id),
      MongoDBObject("$set" -> (MongoDBObject("stylePic" -> imgId))), false, true)
  }
}

/**
 * The table of searchByImpression
 * 用于存放发型导航风格检索一栏
 * @param id ObjectId
 * @param sex 适合性别
 * @param styleImpression 发型适合风格
 * @param stylePic 发型图片
 * @param description 描述
 */
case class SearchByImpression(
  id: ObjectId = new ObjectId,
  sex: String,
  styleImpression: String,
  stylePic: ObjectId,
  description: String)

object SearchByImpression extends MeifanNetModelCompanion[SearchByImpression] {
  val dao = new MeifanNetDAO[SearchByImpression](collection = loadCollection()) {}
  //保存图片
  def saveSearchByImpressionImage(searchByImpression: SearchByImpression, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> searchByImpression.id),
      MongoDBObject("$set" -> (MongoDBObject("stylePic" -> imgId))), false, true)
  }
}
