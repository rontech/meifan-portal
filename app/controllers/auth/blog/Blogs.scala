package controllers.auth

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
import jp.t2v.lab.play2.auth._
import scala.concurrent.ExecutionContext.Implicits.global
import controllers._

object Blogs extends Controller with AuthElement with AuthConfigImpl {
  
  def newBlogForm(userId : String, id : ObjectId = new ObjectId) = Form(
      mapping(
      "id" -> ignored(id),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "authorId" -> ignored(userId),
      "blogCategory" -> text,
      "blogPics" -> optional(list(text)), // TODO
      "tags" -> text,
      "isVisible" -> boolean,
      "pushToSalon" -> optional(boolean),
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
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "authorId" -> ignored(userId),
      "createTime" -> date,
      "blogCategory" -> text,
      "blogPics" -> optional(list(text)), // TODO
      "tags" -> text,
      "isVisible" -> boolean,
      "pushToSalon" -> optional(boolean),
      "allowComment" -> boolean) {
      (id, title, content, authorId, createTime, blogCategory, blogPics, tags, isVisible, pushToSalon, allowComment)
        => Blog(id, title, content, authorId, createTime, new Date(), blogCategory, blogPics, tags.split(",").toList, isVisible, pushToSalon, allowComment, true)
      } 
      {
        blog => Some((blog.id, blog.title, blog.content, blog.authorId, blog.createTime, blog.blogCategory, blog.blogPics, listToString(blog.tags), blog.isVisible, blog.pushToSalon, blog.allowComment))
      }
      )
  
   /**
   * 创建blog，跳转
   */
  def newBlog(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user: Option[User] = User.findOneByUserId(userId)
    user match {
      case Some(user) => {
        if(user.equals(loggedIn)){
	        val listBlogCategory = BlogCategory.getCategory
			val followInfo = MyFollow.getAllFollowInfo(user.id)
			Ok(views.html.blog.admin.newBlog(newBlogForm(userId), listBlogCategory, user = user, followInfo))
        }
        else{
          Redirect(auth.routes.Blogs.newBlog(loggedIn.userId))
        }
      }
      case None => NotFound  
    }
  }
  
   /**
   * 新建blog，后台逻辑
   */
  def writeBlog(userId : String) = Action { implicit request =>
      val user: Option[User] = User.findOneByUserId(userId)
      user match {
        case Some(user) => {
          val listBlogCategory = BlogCategory.getCategory
	      val followInfo = MyFollow.getAllFollowInfo(user.id)
	      newBlogForm(userId).bindFromRequest.fold(
        //处理错误        
        errors => BadRequest(views.html.blog.admin.newBlog(errors,listBlogCategory, user = user, followInfo)),
        {
          blog =>
            Blog.save(blog, WriteConcern.Safe)
            Redirect(noAuth.routes.Blogs.showBlogById(blog.id))
        }             
        )          
        }
        case None => NotFound
      }
  }  
  
   /**
   * 编辑blog
   */
  def editBlog(blogId : ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val blog = Blog.findOneById(blogId)
    val list = BlogCategory.getCategory
    blog.map { blog =>
      val user = User.findOneByUserId(blog.authorId).get
      if(user.equals(loggedIn)) {
		  val followInfo = MyFollow.getAllFollowInfo(user.id)
		  val formEditBlog = blogForm(user.userId).fill(blog)
		  Ok(views.html.blog.admin.editBlog(formEditBlog, list, user, followInfo,blog))
      }
      else
       Ok("没有此操作的权限") 
    } getOrElse {
      NotFound
    }
  }
  
   /**
   * 编辑blog，后台逻辑
   */
  def modBlog(blogId : ObjectId) = Action { implicit request =>
      val blog = Blog.findOneById(blogId)
      blog match {
        case Some(blog) => {
          val user: Option[User] = User.findOneByUserId(blog.authorId)
	      val listBlogCategory = BlogCategory.getCategory
	      val followInfo = MyFollow.getAllFollowInfo(user.get.id)
	      blogForm(blog.authorId,blogId).bindFromRequest.fold(
	        //处理错误        
	        errors => BadRequest(views.html.blog.admin.editBlog(errors,listBlogCategory,user.get, followInfo, blog)),
	        {
	          blog =>            
	            Blog.save(blog, WriteConcern.Safe)
	            Redirect(noAuth.routes.Blogs.showBlogById(blogId))
	        }             
        )         
        }
        case None => NotFound
        
      }
  }  
  
   /**
   * 用户删除blog
   */
  def deleteBlog(blogId : ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    var blog = Blog.findOneById(blogId)
    blog match {
      case Some(blog) => {
        if(blog.authorId.equals(loggedIn.userId)){
          Blog.delete(blogId)
		  Redirect(noAuth.routes.Blogs.showBlog(blog.authorId))
        }
        else
          Ok("没有此操作的权限")
      }
      case None => NotFound
    }
  }
  
}
