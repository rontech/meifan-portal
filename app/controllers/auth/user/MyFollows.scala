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
package controllers.auth

import play.api.mvc._
import models._
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc._
import scala.concurrent._
import ExecutionContext.Implicits.global
import controllers._
import java.util.Date
import java.util.Calendar

object MyFollows extends Controller with AuthElement with UserAuthConfigImpl {

  /**
   * 取消关注
   */
  def cancelFollow(followedId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    MyFollow.delete(user.id, followedId)
    Redirect(request.headers.get("Referer").getOrElse(""))
  }

  /**
   * 添加关注或收藏
   */
  def addFollow(followId: ObjectId, followObjType: String, date: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    MyFollow.checkIfFollow(user.id, followId) match {
      case false => {
        MyFollow.create(user.id, followId, followObjType)
        if (followObjType == FollowType.FOLLOW_SALON || followObjType == FollowType.FOLLOW_STYLIST || followObjType == FollowType.FOLLOW_USER)
          UserMessage.sendFollowMsg(user, followId, followObjType)
        Ok("false")
      }
      case true =>
        Ok("true")
    }
  }

  /**
   * 收藏的优惠劵
   */
  def followedCoupon(userId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
    val loginUser = loggedIn
    val user = User.findOneById(userId).get
    val followInfo = MyFollow.getAllFollowInfo(userId)

    // 获取当前时间的前7天的日期，用于判断是否为新券还是旧券
    var beforeSevernDate = Calendar.getInstance()
    beforeSevernDate.setTime(new Date())
    beforeSevernDate.add(Calendar.DAY_OF_YEAR, -7)

    Ok(views.html.user.followedCoupon(user, followInfo, loginUser.id, beforeSevernDate.getTime()))
  }

  /**
   * 收藏的博客
   */
  def followedBlog(userId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
    val loginUser = loggedIn
    val user = User.findOneById(userId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.followedBlog(user, followInfo, loginUser.id))
  }

  /**
   * 收藏的风格
   */
  def followedStyle(userId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
    val loginUser = loggedIn
    val user = User.findOneById(userId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.followedStyle(user, followInfo, loginUser.id))
  }
}
