package controllers

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

object Comments extends Controller with AuthElement with UserAuthConfigImpl {
  

  val formAddComment = Form((
    "content" -> text
  ))
  
  val formHuifuComment = Form((
    "content" -> text
  ))
  
  val commentToCouponForm = Form(tuple(
      "complex" -> number, 
      "atmosphere" -> number,
      "service" -> number,
      "skill" -> number, 
      "price" -> number,
      "content" -> text
      ))
  
//  def commentToCouponForm(id : ObjectId = new ObjectId) = Form(
//	  mapping(
//	  "id" -> ignored(id),
//	  "title" -> nonEmptyText,
//	  "content" -> nonEmptyText,
//	  "authorId" -> ignored(userId),
//	  "blogCategory" -> text,
//	  "blogPics" -> optional(list(text)), // TODO
//	  "tags" -> text,
//	  "isVisible" -> boolean,
//	  "pushToSalon" -> optional(boolean),
//	  "allowComment" -> boolean) {
//	  (id, title, content, authorId, blogCategory, blogPics, tags, isVisible, pushToSalon, allowComment)
//	    => Blog(id, title, content, authorId, new Date(), new Date(), blogCategory, blogPics, tags.split(",").toList, isVisible, pushToSalon, allowComment, true)
//	  } 
//	  {
//	    blog => Some((blog.id, blog.title, blog.content, blog.authorId, blog.blogCategory, blog.blogPics, listToString(blog.tags), blog.isVisible, blog.pushToSalon, blog.allowComment))
//	  }
//	  )

  /**
   * 查找数据库中关于该评论的所有数据，包括回复的。
   */
  def find(commentedId : ObjectId) = Action {    
    implicit request =>      
      val userId = request.session.get("userId").get
      val user_Id = User.findOneByUserId(userId).get.id
    Ok(views.html.comment.comment(userId, user_Id, Comment.all(commentedId)))
  }
  
  /**
   * 查找店铺下的评论，现在只是对coupon做评论，还没有对预约做评论
   */
  def findBySalon(salonId: ObjectId) = StackAction { implicit request =>
      val user = Option(loggedIn)
      val salon: Option[Salon] = Salon.findOneById(salonId)
      val comments: List[Comment] = Comment.findBySalon(salonId)
      // navigation bar
      val navBar = SalonNavigation.getSalonNavBar(salon) ::: List((Messages("salon.comments"), ""))
      // Jump to blogs page in salon.
      // TODO: process the salon not exist pattern.
      Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments, navBar = navBar, user=user))
  }
  
  def clean() = {
    Comment.list = Nil
  }
  
  /**
   * 店铺查看自己店铺的所有评论
   */
  def findBySalonAdmin(salonId: ObjectId) =StackAction { implicit request =>
      val user = Option(loggedIn)
      val salon: Option[Salon] = Salon.findOneById(salonId)
    val comments: List[Comment] = Comment.findBySalon(salonId)    

    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments, user = user))
  }
  
  /**
   * 增加评论，后台逻辑
   */
  def addComment(commentObjId : ObjectId, commentObjType : Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
      val user = loggedIn 
      formAddComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.errorMsg("")),
        {
          case (content) =>    
            if (commentObjType == 1) {
              val blog = Blog.findOneById(commentObjId)
              blog match {
	              case Some(blog) =>{
	                Comment.addComment(user.userId, content, commentObjId, commentObjType)		        
			        Redirect(noAuth.routes.Blogs.getOneBlogById(commentObjId))
	              }
	              case None=>{
	                Unauthorized
	              }
              }
            }
            else{
            Unauthorized
            }
        } 
      )
  }
  
  /**
   * 对coupon做评论
   */
  def addCommentToCoupon(commentObjId : ObjectId, commentObjType : Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
      val user = loggedIn 
      commentToCouponForm.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.errorMsg1(commentToCouponForm)),
        {
          case (complex, atmosphere, service, skill, price, content) =>         
	        Comment.addCommentToCoupon(user.userId, content, commentObjId, commentObjType, complex, atmosphere, service, skill, price)
	        if (commentObjType == 2) { 
	          // TODO 等到预约做好后，由于预约表中有与用户相关的字段，到时候可以跳转
	          Ok("成功插入到数据库中了啦，哈哈！")
	        }
	        else {
	          Ok("")
	        }
        } 
      )
  }
  
  /**
   * 店家的申诉
   */
  // TODO
  def complaint(id : ObjectId) = Action {
    Ok(Html("我要申诉的评论Id是" + id))
  }
  
  /**
   * 回复，跳转
   */
  def answer(id : ObjectId, commentedId : ObjectId) = Action {
    Ok(views.html.comment.answer(id, commentedId, formHuifuComment))
  }
  
  /**
   * 博客回复，后台逻辑
   */
  def reply(commentObjId : ObjectId, id : ObjectId, commentObjType : Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
//      val userId = request.session.get("userId").get
      // TODO
      val user = loggedIn
      formHuifuComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.errorMsg("")),
        {
          case (content) =>
	        Comment.reply(user.userId, content, commentObjId, commentObjType) 
	        Redirect(noAuth.routes.Blogs.getOneBlogById(id))	
        } 
      )
  }
  
   /**
   * 店铺回复消费者的评论，后台逻辑
   */
  // 这边的权限有点问题啊，应该需要的是店铺登陆的权限
  def replyAdmin(commentObjId : ObjectId, id : ObjectId, commentObjType : Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
//      val userId = request.session.get("userId").get
      // TODO
      val user = loggedIn
      formHuifuComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.errorMsg("")),
        {
          case (content) =>
	        Comment.reply(user.userId, content, commentObjId, commentObjType) 
	        Redirect(auth.routes.Salons.myComment)
        } 
      )
  }
  
  /**
   * blog的作者删除评论
   */
  def delete(id : ObjectId, commentObjId : ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    Comment.delete(id)
//    Redirect(routes.Comments.find(commentedId))
    Redirect(noAuth.routes.Blogs.getOneBlogById(commentObjId))
  }
  
  /**
   * 管理员删除后台删除评论
   */
  def deleteAdmin() = TODO
  
}
