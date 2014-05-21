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
 * 行业职位表
 *  
 * @param id 
 * @param positionName 职位名
 * @param industryName 行业名
 */
case class IndustryAndPosition(
  id: ObjectId,
  positionName: String,
  industryName: String)

object IndustryAndPosition extends MeifanNetModelCompanion[IndustryAndPosition] {
  val dao = new MeifanNetDAO[IndustryAndPosition](collection = loadCollection()) {}
}

/**
 * 职位主表
 * 
 * @param id 职位id
 * @param positionName 职位名称
 */
case class Position(
  id: ObjectId,
  positionName: String)

object Position extends MeifanNetModelCompanion[Position] {
  val dao = new MeifanNetDAO[Position](collection = loadCollection()) {}
}
