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


import com.mongodb.casbah.commons.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date
import com.meifannet.framework.db._

case class Question(
  id: ObjectId = new ObjectId,
  questName: String,
  questContent: String,
  questedDate: Date = new Date,
  questedNum: Int = 1,
  isValid: Boolean = true
)


object Question extends MeifanNetModelCompanion[Question] {
  val dao = new MeifanNetDAO[Question](collection = loadCollection()) {}
}

