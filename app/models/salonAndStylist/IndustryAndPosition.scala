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
import play.api.PlayException
import com.meifannet.framework.db._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date

/**
 * the class for position and Industry
 * @param id
 * @param positionName
 * @param industryName
 */
case class IndustryAndPosition(
  id: ObjectId,
  positionName: String,
  industryName: String)

object IndustryAndPosition extends MeifanNetModelCompanion[IndustryAndPosition] {
  val dao = new MeifanNetDAO[IndustryAndPosition](collection = loadCollection()) {}
}

/**
 *
 * @param id
 * @param positionName
 */
case class Position(
  id: ObjectId,
  positionName: String)

object Position extends MeifanNetModelCompanion[Position] {
  val dao = new MeifanNetDAO[Position](collection = loadCollection()) {}
}
