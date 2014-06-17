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
import com.meifannet.portal.MeifanNetCustomerApplication
import models.portal.blog.Blog
import models.portal.review.Comment
import models.portal.user.LoggedIn
import models.portal.reservation.Reservation

/**
 * this object is to add comment to blog and coupon and delete comment of blog
 */
object Comments extends MeifanNetCustomerApplication {

  /** form of adding comment */
  val formAddComment = Form((
    "content" -> text))

  /** form of replying comment */
  val formReplyComment = Form((
    "content" -> text))

  /** form of adding comment to coupon */
  val commentToCouponForm = Form(tuple(
    "complex" -> number,
    "atmosphere" -> number,
    "service" -> number,
    "skill" -> number,
    "price" -> number,
    "content" -> text))

  /**
   * handle to add comment
   *
   * the func is major to add comment to blog
   *
   * @param commentObjId  the objectId of comment record
   * @param commentObjType the objectType of comment record
   * @return
   */
  def addComment(commentObjId: ObjectId, commentObjType: Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    formAddComment.bindFromRequest.fold(
      //处理错误
      errors => BadRequest(views.html.comment.errorMsg("")),
      {
        case (content) =>
          if (commentObjType == 1) {
            val blog = Blog.findOneById(commentObjId)
            blog match {
              case Some(blog) => {
                Comment.addComment(user.userId, content, commentObjId, commentObjType)
                Redirect(noAuth.routes.Blogs.getOneBlogById(commentObjId))
              }
              case None => {
                Unauthorized
              }
            }
          } else {
            Unauthorized
          }
      })
  }

  /**
   * add comment to coupon
   *
   * when the reservation func is ok, change this func to add comment to reservation
   *
   * @param commentObjId the objectId of comment record
   * @param commentObjType the objectType of comment record
   * @return
   */
  def addCommentToCoupon(commentObjId: ObjectId, commentObjType: Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    commentToCouponForm.bindFromRequest.fold(
      //处理错误
      errors => BadRequest(views.html.comment.errorMsg1(commentToCouponForm)),
      {
        case (complex, atmosphere, service, skill, price, content) =>
          Comment.addCommentToCoupon(user.userId, content, commentObjId, commentObjType, complex, atmosphere, service, skill, price)
          Reservation.changeReservStatusToCommented(commentObjId)
          if (commentObjType == 2) {
            // TODO 等到预约做好后，由于预约表中有与用户相关的字段，到时候可以跳转
            //              Redirect(auth.routes.MyFollows.followedCoupon(user.id))
            // 目前暂时跳转到我的主页
            Redirect(auth.routes.Users.myPage)
          } else {
            Ok("")
          }
      })
  }

  /**
   * reply comment of one blog
   *
   * @param commentObjId the objectId of comment record
   * @param blogId the id of blog
   * @param commentObjType the objectType of comment record
   * @return
   */
  def reply(commentObjId: ObjectId, blogId: ObjectId, commentObjType: Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    formReplyComment.bindFromRequest.fold(
      //处理错误
      errors => BadRequest(views.html.comment.errorMsg("")),
      {
        case (content) =>
          Comment.reply(user.userId, content, commentObjId, commentObjType)
          Redirect(noAuth.routes.Blogs.getOneBlogById(blogId))

      })
  }

  /**
   * delete comment only the author of blog can
   *
   * @param commentId the id of comment record
   * @param commentObjId the objectId of comment record
   * @return
   */
  def delete(commentId: ObjectId, commentObjId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    Comment.delete(commentId)
    Redirect(noAuth.routes.Blogs.getOneBlogById(commentObjId))
  }

}

