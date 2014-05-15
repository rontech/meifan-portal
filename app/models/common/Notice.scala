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

/**
 * Created by YS-HAN on 14/04/16.
 */

import play.api.Play.current
import java.util.Date
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.meifannet.framework.db._

case class Notice(
           id : ObjectId,
           title: String,
           content: String,
           author: String,
           createdTime: Date,
           isValid: Boolean
           )

object Notice extends MeifanNetModelCompanion[Notice] {

  val dao = new MeifanNetDAO[Notice](collection = loadCollection()){}
}
