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
import com.novus.salat.Context
import com.mongodb.casbah.commons.Imports._
import mongoContext._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import java.util.Date
import com.meifannet.framework.db._

/**
 * [Master Table]
 * 图片的用途主表
 * @param id 
 * @param picUseName use purpose of the picture
 * @param division division of the picture use.
 */
case class PictureUse(
  id: ObjectId = new ObjectId,
  picUseName: String,
  division: Int)

object PictureUse extends MeifanNetModelCompanion[PictureUse] {

  val dao = new MeifanNetDAO[PictureUse](collection = loadCollection()) {}

}

