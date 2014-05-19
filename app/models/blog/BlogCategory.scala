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
import se.radley.plugin.salat._
import mongoContext._
import com.meifannet.framework.db._

/**
 * this class is the blogCategory
 * the category data is stored in master table
 *
 * @param id ObjectId of record in mongodb
 * @param categoryName
 * @param isValid
 */
case class BlogCategory(
  id: ObjectId = new ObjectId,
  categoryName: String,
  isValid: Boolean)

object BlogCategory extends MeifanNetModelCompanion[BlogCategory] {
  val dao = new MeifanNetDAO[BlogCategory](collection = loadCollection()) {}

  /** get the category stored in master table */
  def getCategory = {
    var list: List[String] = Nil
    val category = dao.find(MongoDBObject("isValid" -> true)).toList
    if (!category.isEmpty) {
      category.foreach(
        r => list :::= List(r.categoryName))
    }
    list.reverse
  }
}
