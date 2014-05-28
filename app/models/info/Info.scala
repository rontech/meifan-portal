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
package models.portal.info

import com.mongodb.casbah.commons.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date
import com.meifannet.framework.db._
import models.portal.common.OnUsePicture

/**
 * 资讯
 *
 * @param id
 * @param title
 * @param content
 * @param authorId
 * @param infoPics
 * @param createTime
 * @param infoType
 * @param isValid
 */
case class Info(
  id: ObjectId = new ObjectId,
  title: String,
  content: String,
  authorId: ObjectId,
  //    infoPics: ObjectId,
  infoPics: List[OnUsePicture],
  createTime: Date = new Date,
  infoType: Int, // 暂定 1：美范 ，2：美容整形，3：利用规约， 4： 使用须知 ，5： 隐私政策 //TODO 6: 广告
  isValid: Boolean = true)

object Info extends MeifanNetModelCompanion[Info] {

  val dao = new MeifanNetDAO[Info](collection = loadCollection()) {}

  /**
   * meifan资讯
   *
   * @param num
   * @return
   */
  def findInfoForHome(num: Int): List[Info] = {
    val infoList = dao.find(MongoDBObject("isValid" -> true, "infoType" -> 1)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
    infoList.sortBy(info => info.createTime)
  }

  /**
   * 整形资讯
   *
   * @param num
   * @return
   */
  def findEstheInfo(num: Int): List[Info] = {
    val infoList = dao.find(MongoDBObject("isValid" -> true, "infoType" -> 2)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
    infoList
  }

  /**
   * 取得ID利用规约
   * TODO 网站footer信息的表结构可能会调整，暂定数据存在资讯中
   *
   * @return
   */
  def findIdUsePolicyInfo: List[Info] = {
    val infoList = dao.find(MongoDBObject("isValid" -> true, "infoType" -> 3)).sort(MongoDBObject("createTime" -> -1)).toList
    infoList
  }

  /**
   * 取得ID利用规约
   * TODO 网站footer信息的表结构可能会调整，暂定数据存在资讯中
   *
   * @return
   */
  def findUsePolicyInfo: List[Info] = {
    val infoList = dao.find(MongoDBObject("isValid" -> true, "infoType" -> 4)).sort(MongoDBObject("createTime" -> -1)).toList
    infoList
  }

  /**
   * 取得ID利用规约
   * TODO 网站footer信息的表结构可能会调整，暂定数据存在资讯中
   *
   * @return
   */
  def findSecurityPolicyInfo: List[Info] = {
    val infoList = dao.find(MongoDBObject("isValid" -> true, "infoType" -> 5)).sort(MongoDBObject("createTime" -> -1)).toList
    infoList
  }

  /**
   * 取得ID利用规约
   * TODO 网站footer信息的表结构可能会调整，暂定数据存在资讯中
   *
   * @param num
   * @return
   */
  def findAdInfo(num: Int): List[Info] = {
    val infoList = dao.find(MongoDBObject("isValid" -> true, "infoType" -> 6)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
    infoList
  }

  /**
   *
   * @param info
   * @param imgId
   * @return
   */
  def updateInfoImage(info: Info, imgId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> info.id, "infoPics.picUse" -> "logo"),
      MongoDBObject("$set" -> (MongoDBObject("infoPics.$.fileObjId" -> imgId))), false, true)
  }
}

