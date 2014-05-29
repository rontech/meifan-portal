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
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc._
import scala.concurrent._
import ExecutionContext.Implicits.global
import controllers._
import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import models.portal.user.{MyFollow, User}

object MyFollows extends MeifanNetCustomerOptionalApplication {

  /**
   * 列表显示关注的沙龙
   * @param id 用户objectId
   * @return
   */
  def followedSalon(id: ObjectId) = StackAction { implicit request =>
    val user = User.findOneById(id).get
    val followInfo = MyFollow.getAllFollowInfo(id)
    loggedIn.map { loginUser =>
      Ok(views.html.user.followedSalon(user, followInfo, loginUser.id, true))
    } getOrElse {
      Ok(views.html.user.followedSalon(user, followInfo))
    }
  }

  /**
   * 列表显示关注的技师
   * @param id 用户objectId
   * @return
   */
  def followedStylist(id: ObjectId) = StackAction { implicit request =>
    val user = User.findOneById(id).get
    val followInfo = MyFollow.getAllFollowInfo(id)
    loggedIn.map { loginUser =>
      Ok(views.html.user.followedStylist(user, followInfo, loginUser.id, true))
    } getOrElse {
      Ok(views.html.user.followedStylist(user, followInfo))
    }
  }

  /**
   * 列表显示关注的其他用户
   * @param id 用户objectId
   * @return
   */
  def followedUser(id: ObjectId) = StackAction { implicit request =>
    val user = User.findOneById(id).get
    val followInfo = MyFollow.getAllFollowInfo(id)
    loggedIn.map { loginUser =>
      Ok(views.html.user.followedUser(user, followInfo, false, loginUser.id, true))
    } getOrElse {
      Ok(views.html.user.followedUser(user, followInfo, false))
    }
  }

  /**
   * 列表显示的粉丝
   * @param id 用户objectId
   * @return
   */
  def followers(id: ObjectId) = StackAction { implicit request =>
    val user = User.findOneById(id).get
    val followInfo = MyFollow.getAllFollowInfo(id)
    loggedIn.map { loginUser =>
      Ok(views.html.user.followedUser(user, followInfo, true, loginUser.id, true))
    } getOrElse {
      Ok(views.html.user.followedUser(user, followInfo, true))
    }
  }

}
