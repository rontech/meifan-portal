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
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.meifannet.framework.db._

/**
 * 广告的数据结构
 *
 * @param id
 * @param description The name or brief description of an advert..
 * @param advertContents Then contents of a advert.
 * @param thumbnailImg The preview thumbnail image of the advert.
 * @param filePath TODO: if we save the advertisements as static files, we should use the path to show them.
 * @param advertType TODO issued by our site or by one salon?
 * @param salonId TODO with the salonId, U can do much more thing, say get the geo area of this advert... 
 * @param isOnService TODO is this advert in the status of service? say, if U pay the money....
 * @param issueStartDate The interval(start) this advert be issue.
 * @param issueEndDate The interval(end) this advert be issue.
 */
case class Advert(
  id: ObjectId = new ObjectId,
  description: String, 
  advertContents: String,
  thumbnailImg: String,
  filePath: String,
  advertType: String,
  salonId: ObjectId,
  isOnService: Boolean,
  issueStartDate: Date,
  issueEndDate: Date
  )

object Advert extends AdvertDAO

trait AdvertDAO extends MeifanNetModelCompanion[Advert] {

  val dao = new MeifanNetDAO[Advert](collection = loadCollection()) {}

}
