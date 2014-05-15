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

case class Advert(
     id: ObjectId = new ObjectId,    
         description: String,     // advert name.
         advertContents: String,  // advert contents.
         thumbnailImg: String,    // 
         filePath: String,        // if we save the advertisements as static files, we should use the path to show them.
         advertType: String,      // TODO issued by our site or by one salon?
         salonId: ObjectId,       // TODO with the salonId, U can do much more thing, say get the geo area of this advert... 
         isOnService: Boolean,    // is this advert in the status of service? say, if U pay the money....
         issueStartDate: Date,
         issueEndDate: Date       // the interval this advert be issue.
         )

object Advert extends AdvertDAO

trait AdvertDAO extends MeifanNetModelCompanion[Advert]{

  val dao = new MeifanNetDAO[Advert](collection = loadCollection()){}

}
