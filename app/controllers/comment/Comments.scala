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
  
  /**
   * 增加评论，跳转
   */
  def addComment(commentedId : ObjectId, commentedType : Int) = Action {
    Ok(views.html.comment.addComment(commentedId, formAddComment, commentedType))
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
  def addC(commentedId : ObjectId, commentedType : Int) = Action {
    implicit request =>
      val userId = request.session.get("userId").get      // TODO这边需要分类。。。！！！
      formAddComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.addComment(commentedId, errors, commentedType)),
        {
          case (content) =>
//            val userId = User.findOneByUserId(user_id).get.id // 这边需要用session取得用户名之类的东西
            var relevantUser = ""
            // 这边可以根据参数commentedType的值来判断到哪张表中取相关的人员
            // 暂定1代表blog，2代表优惠券
            if(commentedType == 1) {             
              relevantUser = Blog.findById(commentedId).get.userId  // TODO relevantUser 最好是String型
            }           
	        Comment.addComment(userId, content, commentedId, relevantUser)
	        Redirect(routes.Blogs.showBlogById(commentedId))
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
      val userId = request.session.get("userId").get
      formHuifuComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.answer(id, commentedId, errors)),
        {
          case (content) =>
            val username = User.findOneByUserId(userId).get.userId
	        Comment.huifu(id, content, username)
//	        Redirect(routes.Comments.find(commentedId))
            Redirect(routes.Blogs.showBlogById(commentedId))
        } 
        )
  }
}
