package controllers

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import models._
import com.mongodb.casbah.Imports.ObjectId


object Comments extends Controller {
  

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
//      val username = User.getUserName(userId)
//      val username = User.findOneById(userId).get.userId
    clean() 
    Ok(views.html.comment.comment(userId, user_Id, Comment.all(commentedId)))
  }
  
  /**
   * 清除list
   */
  implicit def clean() = {
    Comment.list = Nil
  }
  
//  /**
//   * 增加评论，跳转
//   */
//  def addComment(commentedId : ObjectId, commentedType : Int) = Action {
//    Ok(views.html.comment.addComment(commentedId, formAddComment, commentedType))
//  }
  
  /**
   * 前台展示
   */
  def test = Action {
    // 这是数据库中的被评论对象的ObjectId的编号
    val commentedId = new ObjectId("53195fb4a89e175858abce82")
    clean() 
    val list = Comment.all(commentedId)
    Ok(views.html.comment.commentTest(list))
  }
  
  // 模块化代码
  def findBySalon(salonId: ObjectId) = Action {
    Comment.commentList = Nil
    val salon: Option[Salon] = Salon.findById(salonId)    
    val comments: List[Comment] = Comment.findBySalon(salonId)    

    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments))
  }
  
  /**
   * 店铺查看自己店铺的所有评论
   */
  def findBySalonAdmin(salonId: ObjectId) = Action {
    Comment.commentList = Nil
    val salon: Option[Salon] = Salon.findById(salonId)    
    val comments: Seq[Comment] = Comment.findBySalon(salonId)    

    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments))
  }
  
  /**
   * 增加评论，后台逻辑
   */
  def addComment(commentObjId : ObjectId, commentObjType : Int) = Action {
    implicit request =>
      val userId = request.session.get("userId").get      // TODO这边需要分类。。。！！！
      formAddComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.errorMsg("")),
        {
          case (content) =>         
	        Comment.addComment(userId, content, commentObjId, commentObjType)
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
  def complaint(id : ObjectId) = Action {
    Ok(Html("我要申诉的评论Id是" + id))
  }
  
  /**
   * 回复，跳转
   */
  def answer(id : ObjectId, commentedId : ObjectId) = Action {
    Ok(views.html.comment.answer(id, commentedId, formHuifuComment))
  }
  
//  /**
//   * 管理员的功能，删除评论
//   */
//  def delete(id : ObjectId, commentedId : ObjectId) = Action {
//    Comment.delete(id)
//    Redirect(routes.Comments.find(commentedId))
//  }
  
  /**
   * 回复，后台逻辑
   */
  def reply(commentObjId : ObjectId, id : ObjectId, commentObjType : Int) = Action {
    implicit request =>
      val userId = request.session.get("userId").get
      formHuifuComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.errorMsg("")),
        {
          case (content) =>
	        Comment.reply(userId, content, commentObjId, commentObjType) 
	        Redirect(routes.Blogs.showBlogById(id))	
        } 
      )
  }
  
  
}
