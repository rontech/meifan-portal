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
import models.portal.user._
import com.meifannet.portal.MeifanNetCustomerApplication

object MyFollows extends MeifanNetCustomerApplication {

  /**
   * Cancel something that has been followed
   * @param followedId the object's objectId to be cancelled
   * @return refresh the current page
   */
  def cancelFollow(followedId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    MyFollow.delete(user.id, followedId)
    Redirect(request.headers.get("Referer").getOrElse(""))
  }

  /**
   * Create a new followed object
   * @param followId the object's objectId to be followed
   * @param followObjType the object's type to be followed
   * @param date the current time of following action
   * @return
   */
  def addFollow(followId: ObjectId, followObjType: String, date: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    MyFollow.checkIfFollow(user.id, followId) match {
      case false => {
        // if you have not followed this object, create a new followed object
        MyFollow.create(user.id, followId, followObjType)
        // if the object you will follow is salon, or stylist, or user, send a message to him while you follow
        if (followObjType == FollowType.FOLLOW_SALON || followObjType == FollowType.FOLLOW_STYLIST || followObjType == FollowType.FOLLOW_USER)
          UserMessage.sendFollowMsg(user, followId, followObjType)
        Ok("false")
      }
      case true =>
        // if you have followed this object, then do nothing
        Ok("true")
    }
  }

  /**
   * Get the user's followed coupons
   * @param userId user's objectId
   * @return
   */
  def followedCoupon(userId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
    val loginUser = loggedIn
    val user = User.findOneById(userId).get

    // get the user's all followed information based on the user's objectId
    val followInfo = MyFollow.getAllFollowInfo(userId)

    //get 7 days before the current time to judge the coupon is new or old
    val beforeSevernDate = Calendar.getInstance()
    beforeSevernDate.setTime(new Date())
    beforeSevernDate.add(Calendar.DAY_OF_YEAR, -7)

    Ok(views.html.user.followedCoupon(user, followInfo, loginUser.id, beforeSevernDate.getTime()))
  }

  /**
   * Get the user's followed blog
   * @param userId user's objectId
   * @return
   */
  def followedBlog(userId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
    val loginUser = loggedIn
    val user = User.findOneById(userId).get

    // get the user's all followed information based on the user's objectId
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.user.followedBlog(user, followInfo, loginUser.id))
  }

  /**
   * Get the user's followed style
   * @param userId user's objectId
   * @return
   */
  def followedStyle(userId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
    val loginUser = loggedIn
    val user = User.findOneById(userId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)

    // get the user's all followed information based on the user's objectId
    Ok(views.html.user.followedStyle(user, followInfo, loginUser.id))
  }
}
