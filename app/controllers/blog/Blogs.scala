package controllers

import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import com.mongodb.casbah.WriteConcern
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
     val user = User.findOneById(stylist.get.id).get
     val blogList = Blog.getBlogByUserId(user.userId)
     Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogList))
   }    
      
  /**
   * 取得指定店铺的指定blog
   */
  def getBlogInfoOfSalon(salonId: ObjectId, blogId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val blog: Option[Blog] = Blog.findOneById(blogId)
    Ok(views.html.salon.store.salonInfoBlog(salon = salon.get, blog = blog.get))
  }
  /**
   * 取得店铺所有的blog
   */
  def findBySalon(salonId: ObjectId) = Action {
    Blog.blogList = Nil
    val salon: Option[Salon] = Salon.findById(salonId)
    val blogs: List[Blog] = Blog.findBySalon(salonId)
    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogs))
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
