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
package models.portal.industry

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.Context
import com.mongodb.casbah.commons.Imports._
import mongoContext._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import java.util.Date
import com.meifannet.framework.db._

/**
 * [Master Table]
 */
case class Industry(
  id: ObjectId = new ObjectId,
  industryName: String)

object Industry extends MeifanNetModelCompanion[Industry] {
  val dao = new MeifanNetDAO[Industry](collection = loadCollection()) {}

  def findById(id: ObjectId): Option[Industry] = dao.findOne(MongoDBObject("_id" -> id))

  def findAllIndustryName = dao.find(MongoDBObject.empty).toList.map {
    industry => industry.industryName
  }

}

