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

object Blogs extends Controller with AuthElement with UserAuthConfigImpl {
  
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
   if(!list.last.equals(str)){
     strs += str + "," 
   }else{
    strs += str
   }
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
  def newBlog = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
  val user = loggedIn
  val listBlogCategory = BlogCategory.getCategory
  val followInfo = MyFollow.getAllFollowInfo(user.id)
  Ok(views.html.blog.admin.createBlog(newBlogForm(user.userId), listBlogCategory, user = user, followInfo))
  }
  
   /**
   * 新建blog，后台逻辑
   */
  def writeBlog = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val listBlogCategory = BlogCategory.getCategory
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    newBlogForm(user.userId).bindFromRequest.fold(
    //处理错误        
    errors => BadRequest(views.html.blog.admin.createBlog(errors,listBlogCategory, user = user, followInfo)),
    {
     blog =>
      Blog.save(blog, WriteConcern.Safe)
      Redirect(noAuth.routes.Blogs.getOneBlogById(blog.id))
    }             
    )          
  }
  
   /**
   * 编辑blog
   */
  def editBlog(blogId : ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
  val user = loggedIn
  val blog = Blog.findOneById(blogId)
  val list = BlogCategory.getCategory
  blog.map { blog =>
     val followInfo = MyFollow.getAllFollowInfo(user.id)
     val formEditBlog = blogForm(user.userId).fill(blog)
     Ok(views.html.blog.admin.editBlog(formEditBlog, list, user, followInfo,blog))
  } getOrElse {
   NotFound
  }
  }
  
   /**
   * 编辑blog，后台逻辑
   */
  def modBlog(blogId : ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
  val user = loggedIn
  val blog = Blog.findOneById(blogId)
  blog match {
    case Some(blog) => {
     val listBlogCategory = BlogCategory.getCategory
     val followInfo = MyFollow.getAllFollowInfo(user.id)
     blogForm(blog.authorId,blogId).bindFromRequest.fold(
      //处理错误        
      errors => BadRequest(views.html.blog.admin.editBlog(errors,listBlogCategory,user, followInfo, blog)),
      {
       blog =>            
        Blog.save(blog, WriteConcern.Safe)
        Redirect(noAuth.routes.Blogs.getOneBlogById(blogId))
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
  val user = loggedIn
  val blog = Blog.findOneById(blogId)
  blog match {
   case Some(blog) => {
    if(blog.authorId.equals(user.userId)){
     Blog.delete(blogId)
     Redirect(noAuth.routes.Blogs.getAllBlogsOfUser(blog.authorId))
    }
    else
     Ok("没有此操作的权限")
   }
   case None => NotFound
  }
  }
  
}
