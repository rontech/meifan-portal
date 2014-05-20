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
package controllers

import play.api.mvc._
import models._
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import play.api.templates._

object FollowTypes extends Controller {
  def followTypeTypeForm(id: ObjectId = new ObjectId): Form[FollowType] = Form(
    mapping(
      "id" -> ignored(id),
      "followTypeName" -> nonEmptyText)(FollowType.apply)(FollowType.unapply))

  /**
   * 跳转添加新关注类型的页面
   * @return
   */
  def followTypeMain = Action {
    Ok(views.html.user.addFollowType(followTypeTypeForm()))
  }

  /**
   * 添加新关注类型
   * @return
   */
  def addFollowType() = Action { implicit request =>
    followTypeTypeForm().bindFromRequest.fold(
      errors => BadRequest(Html(errors.toString)),
      {
        followType =>
          FollowType.addFollowType(followType)
          Ok(Html("successful!</p>"))
      })
  }
}
