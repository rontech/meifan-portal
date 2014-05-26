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

import java.util.Date
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import com.meifannet.framework.db._

/**
 * A All Info structs of blog including belows
 *   1. basic info as a user.
 *   2. basic info as a salon.
 */
case class BlogOfSalon(blogInfo: Blog, salonInfo: Option[Salon]) {
  def apply(blogInfo: Blog, salonInfo: Option[Salon]) = new BlogOfSalon(blogInfo, salonInfo)
}

/**
 * A All Info structs of blog including belows
 *   1. basic info as a user.
 *   2. basic info as a stylist linked the user above.
 *   3. a blog list of the stylist above
 */
case class BlogOfStylist(userInfo: User, stylistInfo: Stylist, blogListOfStylist: List[Blog]) {
  def apply(userInfo: User, stylistInfo: Stylist, blogListOfStylist: List[Blog]) = new BlogOfStylist(userInfo, stylistInfo, blogListOfStylist)
}

/**
 * Blog class ,now only user and stylist can write blog. todo salon write blog
 *
 * @param id
 * @param title 标题
 * @param content 内容
 * @param authorId 作者 user's userId
 * @param createTime 创建时间
 * @param updateTime 更新时间
 * @param blogCategory 博客分类
 * @param blogPics todo is needed? 图片
 * @param tags 标签
 * @param isVisible 是否可见
 * @param pushToSalon 是否推送到店铺,针对技师的选项
 * @param allowComment 是否允许评论
 * @param isValid 有效状态
 */
case class Blog(
  id: ObjectId = new ObjectId,
  title: String,
  content: String,
  authorId: String,
  createTime: Date,
  updateTime: Date,
  blogCategory: String,
  blogPics: Option[List[String]], // TODO
  tags: List[String],
  isVisible: Boolean,
  pushToSalon: Option[Boolean],
  allowComment: Boolean,
  isValid: Boolean)

object Blog extends MeifanNetModelCompanion[Blog] {
  val dao = new MeifanNetDAO[Blog](collection = loadCollection()) {}

  /**
   * 找到该店铺下面所有发型师的Blog
   * @param salonId salon的id，key
   */
  def findBySalon(salonId: ObjectId): List[Blog] = {
    var blogList: List[Blog] = Nil
    val stylistList = Stylist.findBySalon(salonId)
    var blog: List[Blog] = Nil
    stylistList.foreach({ row =>
      var user = User.findOneById(row.stylistId).get
      blog = Blog.find(DBObject("authorId" -> user.userId, "isValid" -> true, "pushToSalon" -> true)).toList
      if (!blog.isEmpty)
        blogList :::= blog
    })
    blogList.sortBy(blog => blog.createTime).reverse
  }

  /**
   * 查找店铺的最新blog（显示不多于5条）
   * @param salonId salon的id
   */
  def getNewestBlogsOfSalon(salonId: ObjectId) = {
    if (findBySalon(salonId).size <= 5) {
      findBySalon(salonId)
    } else {
      findBySalon(salonId).dropRight(findBySalon(salonId).size - 5)
    }
  }

  /**
   * 通过该用户的UserId找到该用户的blog
   * @param userId user的userId
   */
  def getBlogByUserId(userId: String) = {
    dao.find(MongoDBObject("authorId" -> userId, "isValid" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
  }

  /**
   * 权限控制，查看其他用户的blog,显示可见的blog
   * 通过该用户的UserId找到该用户的blog
   * @param userId user的userId
   */
  def getOtherBlogByUserId(userId: String) = {
    dao.find(MongoDBObject("authorId" -> userId, "isValid" -> true, "isVisible" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
  }

  /**
   * 删除指定的blog
   * @param id key
   */
  def delete(id: ObjectId) = {
    val blog = findOneById(id).get
    dao.update(MongoDBObject("_id" -> blog.id), MongoDBObject("$set" -> MongoDBObject("isValid" -> false)))
  }

  /**
   * 通过User ObjectId 找到该发型师的blog
   * @param objId user的id
   */
  def getStylistBlogByStylistId(objId: ObjectId) = {
    val user = User.findOneById(objId)
    user match {
      case None => Nil
      case Some(usr) => getStylistBlogByUserId(usr.userId)
    }
  }

  /**
   * 通过UserId找到该发型师的blog
   * @param userId user的userId
   */
  def getStylistBlogByUserId(userId: String) = {
    dao.find(MongoDBObject("authorId" -> userId, "isValid" -> true, "pushToSalon" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
  }

  /**
   * 查找blog表中最新的num条blog
   * 这边这是查找发型师的blog，并且他人可见和推送至店铺
   * @param num 在前台显示的数量
   */
  // TODO
  def findBlogForHome(num: Int): List[BlogOfSalon] = {
    var blogOfSalonList: List[BlogOfSalon] = Nil
    val blogList = dao.find(MongoDBObject("isVisible" -> true, "isValid" -> true, "pushToSalon" -> true)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
    blogList.foreach({
      row =>
        val user = User.findOneByUserId(row.authorId)
        user match {
          case None => None
          case Some(u) => {
            val stylist = Stylist.findOneById(u.id)
            stylist match {
              case None => None
              case Some(st) => {
                val salonAndStylist = SalonAndStylist.findByStylistId(st.stylistId)
                salonAndStylist match {
                  case None => None
                  case Some(salonSt) => {
                    val salon = Salon.findOneById(salonSt.salonId)
                    val blogOfSalon = BlogOfSalon(row, salon)
                    blogOfSalonList :::= List(blogOfSalon)
                  }
                }
              }
            }
          }
        }
    })
    blogOfSalonList.sortBy(blogOfSalon => blogOfSalon.blogInfo.createTime).reverse
  }

  /**
   * 这边也是通过店铺找到该店铺下技师的blog，这边返回的是一个BlogOfStylist的对象
   * @param salonId salon的id
   */
  //TODO 上面取的店铺的blog的方法可能需要重构
  def getBlogsOfStylistInSalon(salonId: ObjectId): List[BlogOfStylist] = {
    var blogsOfStylistList: List[BlogOfStylist] = Nil
    val stylistList = Stylist.findBySalon(salonId)
    stylistList.foreach({ stl =>
      var user = User.findOneById(stl.stylistId)
      user match {
        case None => None
        case Some(u) => {
          val blogList = getStylistBlogByUserId(u.userId)
          val blogOfStylist = BlogOfStylist(u, stl, blogList)
          blogsOfStylistList :::= List(blogOfStylist)
        }
      }
    })

    blogsOfStylistList
  }

}
