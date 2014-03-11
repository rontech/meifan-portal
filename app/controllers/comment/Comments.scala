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
    "content" -> nonEmptyText
  ))
  
  val formHuifuComment = Form((
    "content" -> text
  ))

  /**
   * 查找数据库中关于该评论的所有数据，包括回复的。
   */
  def find(commentedId : ObjectId) = Action {    
    implicit request =>      
      val user_id = request.session.get("user_id").get
      val userId = new ObjectId(user_id)
      val username = User.getUserName(userId)
    clean() 
    Ok(views.html.comment.comment(username, userId, Comment.all(commentedId)))
  }
  
  /**
   * 清除list
   */
  implicit def clean() = {
    Comment.list = Nil
  }
  /**
   * 增加评论，跳转
   */
  def addComment(commentedId : ObjectId) = Action {
    Ok(views.html.comment.addComment(commentedId, formAddComment))
  }
  
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
    Comment.commentlist = Nil
    val salon: Option[Salon] = Salon.findById(salonId)    
    val comments: Seq[Comment] = Comment.findBySalon(salonId)    

    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments))
  }
  
  /**
   * 增加评论，后台逻辑
   */
  def addC(commentedId : ObjectId) = Action {
    implicit request =>
      val user_id = request.session.get("user_id").get      // TODO这边需要分类。。。！！！
      formAddComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.addComment(commentedId, errors)),
        {
          case (content) =>
            val userId = new ObjectId(user_id) // 这边需要用session取得用户名之类的东西
            val relevantUser = new ObjectId(user_id)            
	        Comment.addComment(userId, content, commentedId, relevantUser)
//	        Redirect(routes.Comments.find(commentedId))
	        Redirect(routes.Comments.test)
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
   * 店家回复，跳转
   */
  def answer(id : ObjectId, commentedId : ObjectId) = Action {
    Ok(views.html.comment.answer(id, commentedId, formHuifuComment))
  }
  
  /**
   * 管理员的功能，删除评论
   */
  def delete(id : ObjectId, commentedId : ObjectId) = Action {
    Comment.delete(id)
    Redirect(routes.Comments.find(commentedId))
  }
  
  /**
   * 店家回复，后台逻辑
   */
  def huifu(id : ObjectId, commentedId : ObjectId) = Action {
    implicit request =>
      val user_id = request.session.get("user_id").get
      formHuifuComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.answer(id, commentedId, errors)),
        {
          case (content) =>
            val username = new ObjectId(user_id)
	        Comment.huifu(id, content, username)
	        Redirect(routes.Comments.find(commentedId))
        } 
        )
  }
}
