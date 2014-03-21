package controllers

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import models._
import com.mongodb.casbah.Imports.ObjectId


object BlogCatagories extends Controller {
    
//  // 这边要用js实现。现在只是个数据处理的例子
//  val formBlogCatagory = Form(
//      single(
//        "catagory" -> nonEmptyText
//      )
//  )
    
    // 感觉分类这个用mapping没有多大的便利，还是需要查出该用户原来的分类
//  def newBlogCatagoryForm(userId : String, id : ObjectId = new ObjectId) = Form(
//      mapping(
//        "id" -> ignored(id),
//        "userId" -> ignored(userId),
//        "catagory" -> list(text)
//      ){
//        (id, userId, catagory) => BlogCatagory(id,userId,catagory,0,1)
//      }
//      {
//        blogCatagory =>Some(blogCatagory.id, blogCatagory.userId, blogCatagory.catagory)
//      }
//  )
    
//    /**
//     * 创建一个新的分类，跳转
//     */
//    def newBlogCatagory(userId : ObjectId) = Action {
//      Ok(views.html.blog.newBlogCatagory(userId, formBlogCatagory))
//    }
//    
//    /**
//     * 创建一个新的分类，后台逻辑
//     */
//    def writeBlogCatagory(userId : ObjectId) = Action {
//      implicit request =>
//      formBlogCatagory.bindFromRequest.fold(
//        //处理错误
//        errors => BadRequest(views.html.blog.newBlogCatagory(userId, errors)),
//        {
//          case (catagory) =>   
//            BlogCatagory.list = Nil
//	        BlogCatagory.newBlogCatagory(userId, catagory)
//	        Ok("创建成功！")
//        }
//        )
//    }
//    
//    /**
//     * 编辑分类
//     */
//    def editBlogCatagory(userId : ObjectId) = Action {
//      val listBlogCatagory = BlogCatagory.findBlogCatagory(userId)
////      val catarory = listBlogCatagory(0).catagory
//      var catarory : List[String] = Nil
//      if(listBlogCatagory.isEmpty){
//        catarory = Nil
//      }else 
//        catarory = listBlogCatagory(0).catagory
//      Ok(views.html.blog.editBlogCatagory(userId, catarory))
//    }
//    
//    /**
//     * 删除分类
//     */
//    // TODO 这边的代码有点问题，要把该分类下的blog的类型转换成默认的一种，类似没有分类后blog自动划归到全部的tab下
//    def delBlogCatarory(userId : ObjectId, blogCatagory : String) = Action {
//      BlogCatagory.delBlogCatagory(userId, blogCatagory)
//      // 把该分类下的blog的类型转换成“选择分类”
//      Blog.delBlogCatarory(userId, blogCatagory)
//      var listBlogCatagory = BlogCatagory.findBlogCatagory(userId)
//      var catarory : List[String] = Nil
//      if(listBlogCatagory.isEmpty){
//        catarory = Nil
//      }else 
//        catarory = listBlogCatagory(0).catagory
//      Ok(views.html.blog.editBlogCatagory(userId, catarory))
//    }
//    
//    /**
//     * 修改分类，跳转
//     */
//    def modBlogCatarory(userId : ObjectId, blogCatagory : String) = Action {
//      Ok(views.html.blog.modBlogCatagory(formBlogCatagory, blogCatagory, userId))
//    }
//    
//    /**
//     * 修改分类，后台逻辑
//     */
//    // TODO 这边的代码有点问题，要把该分类下的blog的类型转换成新的分类,OK
//    def modCatagory(userId : ObjectId, blogCatagory : String) = Action {
//            implicit request =>
//      formBlogCatagory.bindFromRequest.fold(
//        //处理错误
//        errors => BadRequest(views.html.blog.modBlogCatagory(errors, blogCatagory, userId)),
//        {
//          case (catagory) =>        
//	        BlogCatagory.modCatagory(userId, blogCatagory, catagory)
//	        //修改blog的分类
//	        Blog.modCatagory(userId, blogCatagory, catagory)
//	        Redirect(routes.BlogCatagories.editBlogCatagory(userId))
//
//        } 
//        )
//    }
    
//     /**
//     * 编辑分类
//     */
//    def editBlogCatagory(userId : String) = Action {
//      val user = User.findOneByUserId(userId).get
//      val blogCatagory = BlogCatagory.findOneByUserId(userId)
////      val catarory = listBlogCatagory(0).catagory
//      var catarory : List[String] = Nil
//      if(blogCatagory.isEmpty){
//        catarory = Nil
//      }else 
//        catarory = blogCatagory.get.catagory
//      Ok(views.html.blog.editBlogCatagory(user, catarory))
//    }
//    
//     /**
//     * 创建一个新的分类，跳转
//     */
////   form not mapping
//    def newBlogCatagory(userId : String) = Action {
//      val user = User.findOneByUserId(userId).get
//      Ok(views.html.blog.newBlogCatagory(user, formBlogCatagory))
//    }
  
//    def newBlogCatagory(userId : String) = Action {
//      val user = User.findOneByUserId(userId).get
//      
//      Ok(views.html.blog.newBlogCatagory(user, newBlogCatagoryForm(userId, blogId)))
//    }
    
//     /**
//     * 创建一个新的分类，后台逻辑
//     */
//    def writeBlogCatagory(userId : String) = Action {
//      implicit request =>
//      val user = User.findOneByUserId(userId).get
//      formBlogCatagory.bindFromRequest.fold(
//        //处理错误
//        errors => BadRequest(views.html.blog.newBlogCatagory(user, errors)),
//        {
//          case (catagory) =>   
//            BlogCatagory.list = Nil
//	        BlogCatagory.newBlogCatagory(userId, catagory)
//	        Redirect(routes.BlogCatagories.editBlogCatagory(userId))
//        }
//        )
//    }
//    
//     /**
//     * 删除分类
//     */
//    // TODO 这边的代码有点问题，要把该分类下的blog的类型转换成默认的一种，类似没有分类后blog自动划归到全部的tab下
//    def delBlogCatagory(userId : String, blogCatagory : String) = Action {
//      val user = User.findOneByUserId(userId).get
//      BlogCatagory.delBlogCatagory(userId, blogCatagory)
//      // 把该分类下的blog的类型转换成“选择分类”
//      Blog.delBlogCatagory(userId, blogCatagory)
//      var listBlogCatagory = BlogCatagory.findBlogCatagory(userId)
//      var catarory : List[String] = Nil
//      if(listBlogCatagory.isEmpty){
//        catarory = Nil
//      }else 
//        catarory = listBlogCatagory(0).catagory
//      Ok(views.html.blog.editBlogCatagory(user, catarory))
//    }
//    
//     /**
//     * 修改分类，跳转
//     */
//    def modBlogCatagory(userId : String, blogCatagory : String) = Action {
//      val user = User.findOneByUserId(userId).get
//      Ok(views.html.blog.modBlogCatagory(formBlogCatagory, blogCatagory, user))
//    }
//    
//    /**
//     * 修改分类，后台逻辑
//     */
//    def modCatagory(userId : String, blogCatagory : String) = Action {
//      implicit request =>
//        val user = User.findOneByUserId(userId).get
//        formBlogCatagory.bindFromRequest.fold(
//        //处理错误
//          errors => BadRequest(views.html.blog.modBlogCatagory(errors, blogCatagory, user)),
//        {
//          case (catagory) =>        
//	        BlogCatagory.modCatagory(userId, blogCatagory, catagory)
//	        //修改blog的分类
//	        Blog.modCatagory(userId, blogCatagory, catagory)
//	        Redirect(routes.BlogCatagories.editBlogCatagory(userId))
//
//        } 
//        )
//    }
}