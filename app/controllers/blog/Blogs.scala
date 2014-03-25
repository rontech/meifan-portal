package controllers

import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import com.mongodb.casbah.WriteConcern
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Calendar
import com.mongodb.casbah.commons.MongoDBObject
import se.radley.plugin.salat.Binders._

object Blogs extends Controller {
  
  def newBlogForm(userId : String, id : ObjectId = new ObjectId) = Form(
      mapping(
      "id" -> ignored(id),
      "title" -> text,
      "content" -> text,
      "authorId" -> ignored(userId),
      "blogCategory" -> text,
      "blogPics" -> optional(list(text)), // TODO
      "tags" -> text,
      "isVisible" -> boolean,
      "pushToSalon" -> optional(default(boolean, true)),
      "allowComment" -> boolean) {
      (id, title, content, authorId, blogCategory, blogPics, tags, isVisible, pushToSalon, allowComment)
        => Blog(id, title, content, authorId, new Date(), new Date(), blogCategory, blogPics, tags.split(",").toList, isVisible, pushToSalon, allowComment, true)
      } 
      {
        blog => Some((blog.id, blog.title, blog.content, blog.authorId, blog.blogCategory, blog.blogPics, listToString(blog.tags), blog.isVisible, blog.pushToSalon, blog.allowComment))
      }
      )
      
  /**
   * 把model和mapping，view统一
   */
  def listToString(list : List[String]) ={
    var strs : String = ""
    list.foreach(str =>
    strs += str + "," 
    )
    strs
  }
      
   def blogForm(userId : String, id : ObjectId = new ObjectId) = Form(
      mapping(
      "id" -> ignored(id),
      "title" -> text,
      "content" -> text,
      "authorId" -> ignored(userId),
      "createTime" -> date,
      "blogCategory" -> text,
      "blogPics" -> optional(list(text)), // TODO
      "tags" -> text,
      "isVisible" -> default(boolean, true),
      "pushToSalon" -> optional(default(boolean, true)),
      "allowComment" -> default(boolean, true)) {
      (id, title, content, authorId, createTime, blogCategory, blogPics, tags, isVisible, pushToSalon, allowComment)
        => Blog(id, title, content, authorId, createTime, new Date(), blogCategory, blogPics, tags.split(",").toList, isVisible, pushToSalon, allowComment, true)
      } 
      {
        blog => Some((blog.id, blog.title, blog.content, blog.authorId, blog.createTime, blog.blogCategory, blog.blogPics, listToString(blog.tags), blog.isVisible, blog.pushToSalon, blog.allowComment))
      }
      )
  
  /**
   * 取得店铺指定理发师的blogs
   */    
  def getBlogByStylist(salonId: ObjectId, stylistId: ObjectId) = Action {
     val salon: Option[Salon] = Salon.findById(salonId)
     val stylist = Stylist.findOneById(stylistId)
     var user = User.findOneById(stylist.get.publicId).get
     var blogList = Blog.getBlogByUserId(user.userId)
     val listYM = getListYM(salon)
     Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogList, listYM = listYM))
   }    
   
   /**
   * 取得店铺指定理发师的blogs
   */    
  def getBlogByYM(salonId: ObjectId, yM: String) = Action {
     val salon: Option[Salon] = Salon.findById(salonId)
     var blogList = getBlogBySalonAndYM(salonId, yM)
     val listYM = getListYM(salon)
     Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogList, listYM = listYM))
   }  
  
  /**
   * 根据一个年月找到blog的更新时间在一个范围内的blog
   */
  def getBlogBySalonAndYM(salonId: ObjectId, yM : String) = {   
    var listBlogYM : List[Blog] = Nil
    var blogList = Blog.findBySalon(salonId)
    blogList.foreach({
       row => 
         var blogDate = Calendar.getInstance()
         var ymd : SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd")
         var ym : SimpleDateFormat = new SimpleDateFormat("yyyyMM")
         var blogUpdateTime = DateFormat.getDateInstance.format(row.updateTime)
         blogDate.setTime(ymd.parse(blogUpdateTime))
         var blogYM = ym.format(blogDate.getTime())
         if(blogYM.equals(yM)){
           listBlogYM :::= List(row)
         }        
    }     
    )
    listBlogYM 
  }
      
  /**
   * 取得指定店铺的指定blog
   */
  def getBlogInfoOfSalon(salonId: ObjectId, blogId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val blog: Option[Blog] = Blog.findOneById(blogId)
    val listYM = getListYM(salon)
    val newestBlogsOfSalon = Blog.getNewestBlogsOfSalon(salonId)
    val user : Option[User] = User.findOneByUserId(blog.get.authorId)
    val stylist : Option[Stylist] = Stylist.findOne(MongoDBObject("publicId" -> user.get.id))
    Ok(views.html.salon.store.salonInfoBlog(salon = salon.get, blog = blog.get, listYM = listYM, newestBlogsOfSalon = newestBlogsOfSalon, stylist = stylist.get))
  }
  
  /**
   * 查找blog的作者
   */
  def getBlogAuthor(salonId: ObjectId, userId: String) = Action {
    var user = User.findOneByUserId(userId).get
    var stylist = Stylist.findOne(MongoDBObject("publicId" -> user.id)).get
    Redirect(routes.Stylists.findStylistById(stylist.id))
  }
  
  /**
   * 取得沙龙注册时到当前时间的年月的List
   */
  def getListYM(salon : Option[Salon]) = {
    val startTime = DateFormat.getDateInstance.format(salon.get.registerDate)
    val endTime = DateFormat.getDateInstance.format(new Date)
    getTimeList(startTime, endTime)
  }
  
  /**
   * 根据起始时间和终止时间得到他们中间的时间List
   */
  def getTimeList(startTime : String, endTime : String) = {
    var list : List[String] = Nil
    var ymd : SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd")
    var ym : SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    var startDate = Calendar.getInstance()
    var endDate = Calendar.getInstance()

    startDate.setTime(ymd.parse(startTime))
    endDate.setTime(ymd.parse(endTime))
    var endYm = ym.format(endDate.getTime())
    
    var flg = true
    while (flg) {
        var strYm = ym.format(startDate.getTime())
        list :::= List(strYm)
        if (strYm.equals(endYm)) {
            flg = false
        }
        startDate.add(Calendar.MONTH, 1)
    }
    list
  }
  
  /**
   * 取得店铺所有的blog
   */
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val blogs: List[Blog] = Blog.findBySalon(salonId)
    val listYM = getListYM(salon)
    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogs, listYM = listYM))
  }

  def showBlog(userId : String) = Action {
    val user: Option[User] = User.findOneByUserId(userId)
    val blogList = Blog.getBlogByUserId(userId)
    val followInfo = MyFollow.getAllFollowInfo(user.get.id)
    Ok(views.html.blog.admin.findBlogs(user = user.get, blogList, followInfo))
  }
  
  
   /**
   * 创建blog，跳转
   */
  def newBlog(userId: String) = Action {
    val user: Option[User] = User.findOneByUserId(userId)
    val listBlogCategory = BlogCategory.getCategory
    val followInfo = MyFollow.getAllFollowInfo(user.get.id)
    Ok(views.html.blog.admin.newBlog(newBlogForm(userId), listBlogCategory, user = user.get, followInfo))
  }
  
   /**
   * 新建blog，后台逻辑
   */
  def writeBlog(userId : String) = Action {   
    implicit request =>
      newBlogForm(userId).bindFromRequest.fold(
        //处理错误        
        errors => BadRequest(views.html.blog.errorMsg(errors)),
        {
          blog =>
            Blog.save(blog, WriteConcern.Safe)
            Redirect(routes.Blogs.showBlogById(blog.id))
        }             
        )
  }  
  
   /**
   * 编辑blog
   */
  def editBlog(blogId : ObjectId) = Action {
    val blog = Blog.findOneById(blogId)
    val list = BlogCategory.getCategory
    val user = User.findOneByUserId(blog.get.authorId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    blog.map { blog =>
      val formEditBlog = blogForm(user.userId).fill(blog)
//      Ok(views.html.blog.admin.editBlog(formEditBlog, list, user, blog))
      Ok(views.html.blog.admin.editBlog(formEditBlog, list, user, followInfo))
    } getOrElse {
      NotFound
    }
  }
  
   /**
   * 编辑blog，后台逻辑
   */
  def modBlog(blogId : ObjectId) = Action {   
    implicit request =>
      val userId = Blog.findOneById(blogId).get.authorId
      blogForm(userId,blogId).bindFromRequest.fold(
        //处理错误        
        errors => BadRequest(views.html.blog.errorMsg(errors)),
        {
          blog =>            
            Blog.save(blog, WriteConcern.Safe)
//	        Redirect(routes.Blogs.showBlog(userId))
            Redirect(routes.Blogs.showBlogById(blogId))
        }             
        )
  }  
  
   /**
   * 用户删除blog
   */
  def deleteBlog(blogId : ObjectId) = Action {
    var userId = Blog.findOneById(blogId).get.authorId
    Blog.delete(blogId)
    Redirect(routes.Blogs.showBlog(userId))
  }
  
   /**
   * 显示某一条blog
   * 通过blog的id找到blog
   */
  def showBlogById(blogId: ObjectId) = Action {
    val blog = Blog.findOneById(blogId).get
    val user = User.findOneByUserId(blog.authorId).get
    Comment.list = Nil
    val commentList = Comment.all(blogId)
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.blog.admin.blogDetail(blog, user, commentList, followInfo))
  }
}
