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

import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import com.mongodb.casbah.WriteConcern
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Calendar
import java.util.TimeZone
import com.mongodb.casbah.commons.MongoDBObject
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import scala.concurrent.ExecutionContext.Implicits.global
import controllers._
import models._

object Blogs extends Controller with OptionalAuthElement with UserAuthConfigImpl {

  // Will set timezone according to locale the user selected later if 
  //     we need to make our site as an international size which can 
  //     select the country & language by the user.
  // ----------------------------------------------- 
  // For now, we handle the china time zone only.
  val chinaTz = TimeZone.getTimeZone("GMT+8")
  val ymdFormat = new SimpleDateFormat("yyyy/MM/dd")
  val ymFormat = new SimpleDateFormat("yyyyMM")
  ymdFormat.setTimeZone(chinaTz)
  ymFormat.setTimeZone(chinaTz)

  /**
   * 取得店铺指定理发师的blogs
   * Get all the blogs of a required stylist salon.
   */
  def getAllBlogsOfStylist(salonId: ObjectId, stylistId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon match {
      case None => NotFound
      case Some(sl) => {
        val stylistDtl = Stylist.findStylistDtlByUserObjId(stylistId)
        stylistDtl match {
          case None => NotFound
          case Some(st) => {
            var blogList = Blog.getStylistBlogByUserId(st.basicInfo.userId)
            val listYM = getYMCatesOfSalon(sl) // TODO
            // navigation bar
            val navBar = SalonNavigation.getSalonNavBar(salon) ::: List((Messages("salon.blogs"), controllers.noAuth.Blogs.getAllBlogsOfSalon(sl.id).toString()))
            // Jump to blogs page in salon. 
            Ok(views.html.salon.store.salonInfoBlogAll(salon = sl, blogs = blogList, listYM = listYM, navBar = navBar, user = user))
          }
        }
      }
    }
  }

  /**
   * 取得店铺指定年月的所有博客
   * Get all the blogs of the required month of a salon.
   */
  def getAllBlogsOfSalonByMonth(salonId: ObjectId, month: String) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    var blogList = getBlogBySalonAndYM(salonId, month)
    salon match {
      case Some(s) => {
        val listYM = getYMCatesOfSalon(s)
        // navigation bar
        val navBar = SalonNavigation.getSalonNavBar(salon) ::: List((Messages("salon.blogs"), controllers.noAuth.Blogs.getAllBlogsOfSalon(s.id).toString()))
        // Jump to blogs page in salon. 
        Ok(views.html.salon.store.salonInfoBlogAll(salon = s, blogs = blogList, listYM = listYM, navBar = navBar, user = user))
      }
      case None => NotFound
    }

  }

  /**
   * 店铺的单篇博客显示画面:
   * 除了本篇博客内容外，还需要显示：最新博客列表，按店铺技师分类，按日期分类等数据...
   */
  def getOneBlogOfSalon(salonId: ObjectId, blogId: ObjectId) = StackAction { implicit request =>
    val loginUser = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    // Check If the salon is exist or active.
    salon match {
      case None => NotFound
      case Some(sl) => {
        // get the required blog.
        val blog: Option[Blog] = Blog.findOneById(blogId)
        blog match {
          case None => NotFound
          case Some(blg) => {
            // get the blog category by [DATE].  
            val listYM = getYMCatesOfSalon(sl)
            // get the blog category by [SALON NEWEST] 
            val newest = Blog.getNewestBlogsOfSalon(salonId)
            val user: Option[User] = User.findOneByUserId(blg.authorId)
            val stylist: Option[Stylist] = Stylist.findOneByStylistId(user.get.id)
            // navigation bar
            val navBar = SalonNavigation.getSalonNavBar(salon) :::
              List((Messages("salon.blogs"), controllers.noAuth.Blogs.getAllBlogsOfSalon(salon.get.id).toString()))
            // Jump to blogs page in salon. 
            Ok(views.html.salon.store.salonInfoBlog(salon = sl, blog = blg, listYM = listYM,
              newestBlogsOfSalon = newest, stylist = stylist.get, navBar = navBar, user = loginUser))
          }
        }
      }
    }
  }

  /**
   * 查找blog的作者
   */
  def getAuthorOfSalonBlog(salonId: ObjectId, userId: String) = Action {
    var stylist = Stylist.findStylistDtlByUserId(userId)
    stylist match {
      case None => NotFound
      case Some(st) => {
        // first, check that if the salon is still the stylist work in.
        st.workInfo match {
          case None => NotFound
          case Some(wk) => {
            if (wk.salonId == salonId) Redirect(controllers.noAuth.routes.Salons.getOneStylist(salonId, wk.stylistId))
            else NotFound
          }
        }
      }
    }
  }

  /**
   * 取得店铺所有的blog
   */
  def getAllBlogsOfSalon(salonId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    val blogs: List[Blog] = Blog.findBySalon(salonId)
    salon match {
      case Some(s) => {
        val listYM = getYMCatesOfSalon(s)
        // navigation bar
        val navBar = SalonNavigation.getSalonNavBar(salon) ::: List((Messages("salon.blogs"), ""))
        // Jump to blogs page in salon.
        // TODO: process the salon not exist pattern.
        Ok(views.html.salon.store.salonInfoBlogAll(salon = s, blogs = blogs, listYM = listYM, navBar = navBar, user = user))
      }
      case None => NotFound
    }
  }

  /**
   * 查看用户的blog
   */
  def getAllBlogsOfUser(userId: String) = StackAction { implicit request =>
    var blogList: List[Blog] = Nil
    val user: Option[User] = User.findOneByUserId(userId)
    user match {
      case Some(user) => {
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        loggedIn.map { loginUser =>
          if (loginUser.id.equals(user.id))
            blogList = Blog.getBlogByUserId(userId)
          else
            blogList = Blog.getOtherBlogByUserId(userId)
          Ok(views.html.blog.admin.searchBlogsOfUser(user, blogList, followInfo, loginUser.id, loginUser.id.equals(user.id)))
        } getOrElse {
          blogList = Blog.getOtherBlogByUserId(userId)
          Ok(views.html.blog.admin.searchBlogsOfUser(user, blogList, followInfo))
        }
      }
      case None => NotFound
    }

  }

  /**
   * 显示某一条blog
   * 通过blog的id找到blog
   */
  def getOneBlogById(blogId: ObjectId) = StackAction { implicit request =>
    val blog = Blog.findOneById(blogId)
    var blogList: List[Blog] = Nil
    blog match {
      case Some(blog) => {
        val user = User.findOneByUserId(blog.authorId).get
        Comment.list = Nil
        val commentList = Comment.all(blogId).sortBy(blog => blog.createTime)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        loggedIn.map { loginUser =>
          if (loginUser.id.equals(user.id))
            blogList = Blog.getBlogByUserId(user.userId)
          else
            blogList = Blog.getOtherBlogByUserId(user.userId)
          Ok(views.html.blog.admin.blogDetailOfUser(blog, user, commentList, followInfo, loginUser.id, true, blogList))
        } getOrElse {
          blogList = Blog.getOtherBlogByUserId(user.userId)
          Ok(views.html.blog.admin.blogDetailOfUser(blog, user, commentList, followInfo, blogList = blogList))
        }
      }
      case None => NotFound
    }
  }

  /*-------------------------
 * Internal Functions
 *------------------------*/
  /**
   * 取得沙龙注册日期到当前时间的年月列表，
   * 作为沙龙博客的年月分类目录
   */
  def getYMCatesOfSalon(salon: Salon) = {

    val startTime = ymdFormat.format(salon.registerDate)
    val endTime = ymdFormat.format(new Date)

    getYMCates(startTime, endTime)
  }

  /**
   * 输入参数: 起始日期，终止日期(yyyy/MM/dd)
   * 返回值: 给定期间内的年月列表，形如(yyyyMM)
   */
  def getYMCates(startTime: String, endTime: String) = {

    var startDate = Calendar.getInstance()
    startDate.setTime(ymdFormat.parse(startTime))

    var endDate = Calendar.getInstance()
    endDate.setTime(ymdFormat.parse(endTime))
    var endYm = ymFormat.format(endDate.getTime())

    var ymLists: List[String] = Nil
    var isCurr = false
    while (!isCurr) {
      val strYm = ymFormat.format(startDate.getTime())
      ymLists :::= List(strYm)
      if (strYm.equals(endYm)) {
        isCurr = true
      }
      startDate.add(Calendar.MONTH, 1)
    }

    ymLists
  }

  /**
   * 根据一个年月找到blog的更新时间在一个范围内的blog
   */
  def getBlogBySalonAndYM(salonId: ObjectId, yM: String) = {
    var listBlogYM: List[Blog] = Nil
    var blogList = Blog.findBySalon(salonId)
    blogList.foreach({
      row =>
        var blogDate = Calendar.getInstance()
        var blogCreateTime = ymdFormat.format(row.createTime)
        blogDate.setTime(ymdFormat.parse(blogCreateTime))
        var blogYM = ymFormat.format(blogDate.getTime())
        if (blogYM.equals(yM)) {
          listBlogYM :::= List(row)
        }
    })

    listBlogYM
  }

}
