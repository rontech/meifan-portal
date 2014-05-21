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

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.templates._
import models._
import com.mongodb.casbah.Imports.ObjectId
import jp.t2v.lab.play2.auth._
import scala.concurrent.ExecutionContext.Implicits.global
import controllers._

object Comments extends Controller with OptionalAuthElement with UserAuthConfigImpl {

  /**
   * get all comment of the given salonId
   * @param salonId the id of salon
   * @return
   */
  def findBySalon(salonId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    val comments: List[Comment] = Comment.findBySalon(salonId)
    // navigation bar
    val navBar = SalonNavigation.getSalonNavBar(salon) ::: List((Messages("salon.comments"), ""))
    // Jump to blogs page in salon.
    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments, navBar = navBar, user = user))
  }

  /** clear the list of comment */
  def clean() = {
    Comment.list = Nil
  }

}
