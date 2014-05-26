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
package controllers.noAuth

import play.api.mvc._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import com.meifannet.framework.MeifanNetApplication

// TODO
// should be moved to under the noAuth folder.

object Questions extends MeifanNetApplication {

  /**
   * Get the required question.
   */
  def getOneQuestion(qId: ObjectId) = Action {
    val quests: List[Question] = Question.findOneById(qId).toList
    Ok(html.question.general.overview(quests))
  }

  /**
   * Get All the questions.
   */
  def getAllQuestions() = Action {
    val quests: List[Question] = Question.findAll().toList
    Ok(html.question.general.overview(quests))
  }

}
