package controllers

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import models._
import com.mongodb.casbah.Imports.ObjectId
import jp.t2v.lab.play2.auth._
import scala.concurrent.ExecutionContext.Implicits.global

object Comments extends Controller with LoginLogout with AuthElement with AuthConfigImpl {
  

  val formAddComment = Form((
    "content" -> text
  ))
  
  val formHuifuComment = Form((
    "content" -> text
  ))

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
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)    
    val comments: List[Comment] = Comment.findBySalon(salonId)    
    //println("comments" + comments)
    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments))
  }
  
  def clean() = {
    Comment.list = Nil
  }
  
  /**
   * 店铺查看自己店铺的所有评论
   */
  def findBySalonAdmin(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)    
    val comments: List[Comment] = Comment.findBySalon(salonId)    

    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments))
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
	        Comment.addComment(user.userId, content, commentObjId, commentObjType)
	        if (commentObjType == 1) { 
	          Redirect(routes.Blogs.showBlogById(commentObjId))
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
	        Redirect(routes.Blogs.showBlogById(id))	
        } 
      )
  }
  
   /**
   * 店铺回复消费者的评论，后台逻辑
   */
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
	        Redirect(routes.SalonsAdmin.myComment(id))	
        } 
      )
  }
  
  /**
   * blog的作者删除评论
   */
  def delete(id : ObjectId, commentObjId : ObjectId) = Action {
    Comment.delete(id)
//    Redirect(routes.Comments.find(commentedId))
    Redirect(routes.Blogs.showBlogById(commentObjId))
  }
  
  
}
