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
import com.meifannet.framework.db._
import mongoContext._

case class FollowType(
    id: ObjectId = new ObjectId,
    followTypeName : String
    )

object FollowType extends MeifanNetModelCompanion[FollowType]{

  val FOLLOW_SALON = "salon"
  val FOLLOW_STYLIST = "stylist"
  val FOLLOW_USER = "user"
  val FOLLOW_STYLE = "style"
  val FOLLOW_BLOG = "blog"
  val FOLLOW_COUPON = "coupon"

  val dao = new MeifanNetDAO[FollowType](collection = loadCollection()){}
  
  def addFollowType(FollowType : FollowType) = dao.save(FollowType, WriteConcern.Safe)
  
  def getFollowTypeList : List[String] = dao.find(MongoDBObject.empty).toList.map {
    FollowType =>FollowType.followTypeName
  }
}
