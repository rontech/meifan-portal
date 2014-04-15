package controllers.auth

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
import controllers._

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
   * 博客回复，后台逻辑
   */
  def reply(commentObjId : ObjectId, blogId : ObjectId, commentObjType : Int) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
//      val userId = request.session.get("userId").get
      // TODO
      val user = loggedIn
      formHuifuComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.errorMsg("")),
        {
          case (content) =>
	        Comment.reply(user.userId, content, commentObjId, commentObjType) 
	        Redirect(noAuth.routes.Blogs.getOneBlogById(blogId))
        } 
      )
  }
  
  /**
   * blog的作者删除评论
   */
  def delete(commentId : ObjectId, commentObjId : ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    Comment.delete(commentId)
//    Redirect(routes.Comments.find(commentedId))
    Redirect(noAuth.routes.Blogs.getOneBlogById(commentObjId))
  }
  
  /**
   * 管理员删除后台删除评论
   */
  def deleteAdmin() = TODO
  
}


