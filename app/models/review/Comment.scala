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
package models.portal.review

import java.util.Date
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import com.meifannet.framework.db._
import models.portal.coupon.Coupon
import models.portal.salon.Salon

/**
 * A All Info structs of comment including belows
 *
 * @param commentInfo basic info as a comment
 * @param salonInfo basic info as a salon
 */
case class CommentOfSalon(commentInfo: Comment, salonInfo: Option[Salon]) {
  def apply(commentInfo: Comment, salonInfo: Option[Salon]) = new CommentOfSalon(commentInfo, salonInfo)
}

/**
 * 评价状况：好评率, 总评价数
 */
case class ReviewsStat(
  reviewRate: BigDecimal, // 好评率
  reviewTotalCnt: Long // 总评价数
  )

/**
 * Enumeration for reviews: positive reviews|average reviews|negetive reviews
 */
object ReviewRst extends Enumeration {
  type ReviewRst = Value
  val Good, Average, Bad = Value
}

/**
 * Enumeration for review type: to salon| to blog| replay.
 */
object CommentType extends Enumeration {
  type CommentType = Value
  val ToBlog = Value(1)
  val ToSalon = Value(2)
  val Replay = Value(3)
}

/**
 * Comment class for blog'comment, reservation's commnet and reply
 *
 * @param id ObjectId of record in mongodb
 * @param commentObjType 评论对象类型:  暂定 1: 对博客; 2: 对店铺; 3: 回复
 * @param commentObjId 评论对象Id
 * @param content 内容
 * @param complex 综合分数
 * @param atmosphere 氛围分数
 * @param service 服务态度分数
 * @param skill 技术分数
 * @param price 价格分数
 * @param authorId 评论人的userId
 * @param createTime 评论时间
 * @param isValid 有效状态
 */
case class Comment(
  id: ObjectId = new ObjectId,
  commentObjType: Int,
  commentObjId: ObjectId,
  content: String,
  complex: Int,
  atmosphere: Int,
  service: Int,
  skill: Int,
  price: Int,
  authorId: String,
  createTime: Date = new Date,
  isValid: Boolean)

object Comment extends MeifanNetModelCompanion[Comment] {
  val dao = new MeifanNetDAO[Comment](collection = loadCollection()) {}

  // the average review judgement stardands: for [1-5] is the available review values, use the middle value.
  val CONST_AVG_REVIEW = 3

  /**
   * 根据评论对象的ObjectId查找评论，包括对这条评论所做的评论。
   * @param id 评论对象的id
   * TODO 方法名
   */
  var list = List.empty[Comment]
  def all(id: ObjectId): List[Comment] = {
    val l = dao.find(MongoDBObject("commentObjId" -> id, "isValid" -> true)).toList
    if (!l.isEmpty) {
      l.foreach(
        {
          r =>
            list :::= List(r)
            all(r.id)
        })
    }
    list
  }

  /**
   *  模块化的代码，通过店铺Id找到评论,应该是通过店铺找到coupon，再找到对coupon做的评论
   *  目前是对coupon做的评论，到时候应该对预约做评论
   *  @param salonId salon的id
   */
  def findBySalon(salonId: ObjectId): List[Comment] = {
    var commentListOfSalon: List[Comment] = Nil
    var commentList: List[Comment] = Nil
    val couponList = Coupon.findBySalon(salonId)
    var comment: List[Comment] = Nil
    couponList.foreach(
      {
        r =>
          comment = Comment.find(DBObject("commentObjType" -> 2, "commentObjId" -> r.id, "isValid" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
          if (!comment.isEmpty)
            commentList :::= comment
      })
    commentList.foreach({
      row =>
        list = Nil
        val replyList = Comment.all(row.id)
        //        if(!replyList.isEmpty)
        commentListOfSalon :::= List(row)
        commentListOfSalon :::= replyList

    })
    commentListOfSalon.reverse
  }

  /**
   * Insert data to Comment table
   *
   * @param userId 评论人的userId
   * @param content 内容
   * @param commentObjId 评论对象Id
   * @param commentObjType 评论对象类型
   * @return
   */
  def addComment(userId: String, content: String, commentObjId: ObjectId, commentObjType: Int) = {
    dao.save(Comment(commentObjType = commentObjType, commentObjId = commentObjId, content = content, complex = 0, atmosphere = 0, service = 0, skill = 0, price = 0, authorId = userId, isValid = true))
  }

  /**
   * 对评论进行回复
   *
   * @param userId 评论人的userId
   * @param content 内容
   * @param commentObjId 评论对象Id
   * @param commentObjType 评论对象类型
   * @return
   */
  def reply(userId: String, content: String, commentObjId: ObjectId, commentObjType: Int) = {
    dao.save(Comment(commentObjType = commentObjType, commentObjId = commentObjId, content = content, complex = 0, atmosphere = 0, service = 0, skill = 0, price = 0, authorId = userId, isValid = true))
  }

  /**
   * 对coupon做评论，暂定，当预约模块完成后，修改成对预约完成的预约做评论
   *
   * @param userId 评论人的userId
   * @param content 内容
   * @param commentObjId 评论对象Id
   * @param commentObjType 评论对象类型
   * @param complex 综合分数
   * @param atmosphere 氛围分数
   * @param service 服务态度分数
   * @param skill 技术分数
   * @param price 价格分数
   * @return
   */
  def addCommentToCoupon(userId: String, content: String, commentObjId: ObjectId, commentObjType: Int, complex: Int, atmosphere: Int, service: Int, skill: Int, price: Int) = {
    dao.save(Comment(commentObjType = commentObjType, commentObjId = commentObjId, content = content, complex = complex, atmosphere = atmosphere, service = service, skill = skill, price = price, authorId = userId, isValid = true))
  }

  override def findOneById(id: ObjectId): Option[Comment] = dao.findOne(MongoDBObject("_id" -> id))

  /**
   * delete record from comment table
   *
   * @param id key
   * @return
   */
  def delete(id: ObjectId) = {
    val comment = findOneById(id).get
    dao.update(MongoDBObject("_id" -> comment.id), MongoDBObject("$set" -> MongoDBObject("isValid" -> false)))
  }

  /**
   * 查找评论表中最新的num条记录，该方法用于在首页上显示最新的评论。
   * 只是针对店铺中coupon（以后是预约）做的评论
   * 这边的2代表对coupon做的评论
   * @param num 首页显示评论的数量
   */
  // TODO
  def findCommentForHome(num: Int): List[CommentOfSalon] = {
    var commentOfSalonList: List[CommentOfSalon] = Nil
    val commentList = dao.find(MongoDBObject("isValid" -> true, "commentObjType" -> CommentType.ToSalon.id)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
    commentList.foreach({
      row =>
        val coupon = Coupon.findOneById(row.commentObjId)
        coupon match {
          case None => None
          case Some(coupon) => {
            val salon = Salon.findOneById(coupon.salonId)
            val commentOfSalon = CommentOfSalon(row, salon)
            commentOfSalonList :::= List(commentOfSalon)
          }
        }
    })
    commentOfSalonList.sortBy(commentOfSalon => commentOfSalon.commentInfo.createTime).reverse
  }

  /**
   * 取得指定店铺的好评率: 以综合评价(complex)为准
   * TODO: 是否应该在店铺里增加好评率字段，在评价时直接更新店铺好评率？--> 为查询时效率考虑。
   */
  def getGoodReviewsRate(salonId: ObjectId): ReviewsStat = {
    val reviews = findBySalon(salonId).filter(_.commentObjType == CommentType.ToSalon.id)
    val rate = if (reviews.isEmpty) 1.0 else reviews.filter(x => isGoodReview(x.complex) == ReviewRst.Good).length.toFloat / reviews.length
    ReviewsStat(rate, reviews.length)
  }

  /**
   * 判断一个评价是否是好评:
   * 基本思路: 评价值为1-5的数字。1-2为差评,3 为中评，4-5为好评
   */
  def isGoodReview(reviewScore: Int) = {
    reviewScore match {
      case x: Int if x < CONST_AVG_REVIEW => ReviewRst.Bad
      case x: Int if x == CONST_AVG_REVIEW => ReviewRst.Average
      case x: Int if x > CONST_AVG_REVIEW => ReviewRst.Good
      case _ => ReviewRst.Average
    }
  }

}

