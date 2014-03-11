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
    
  
    val formBlogCatagory = Form((
    "catagory" -> nonEmptyText
    ))
    
    /**
     * 创建一个新的分类，跳转
     */
    def newBlogCatagory(userId : ObjectId) = Action {
      Ok(views.html.blog.newBlogCatagory(userId, formBlogCatagory))
    }
    
    /**
     * 创建一个新的分类，后台逻辑
     */
    def writeBlogCatagory(userId : ObjectId) = Action {
      implicit request =>
      formBlogCatagory.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.blog.newBlogCatagory(userId, errors)),
        {
          case (catagory) =>   
            BlogCatagory.list = Nil
	        BlogCatagory.newBlogCatagory(userId, catagory)
	        Ok("创建成功！")
        }
        )
    }
    
    /**
     * 编辑分类
     */
    def editBlogCatagory(userId : ObjectId) = Action {
      val listBlogCatagory = BlogCatagory.findBlogCatagory(userId)
//      val catarory = listBlogCatagory(0).catagory
      var catarory : List[String] = Nil
      if(listBlogCatagory.isEmpty){
        catarory = Nil
      }else 
        catarory = listBlogCatagory(0).catagory
      Ok(views.html.blog.editBlogCatagory(userId, catarory))
    }
    
    /**
     * 删除分类
     */
    // TODO 这边的代码有点问题，要把该分类下的blog的类型转换成默认的一种，类似没有分类后blog自动划归到全部的tab下
    def delBlogCatarory(userId : ObjectId, blogCatagory : String) = Action {
      BlogCatagory.delBlogCatagory(userId, blogCatagory)
      // 把该分类下的blog的类型转换成“选择分类”
      Blog.delBlogCatarory(userId, blogCatagory)
      var listBlogCatagory = BlogCatagory.findBlogCatagory(userId)
      var catarory : List[String] = Nil
      if(listBlogCatagory.isEmpty){
        catarory = Nil
      }else 
        catarory = listBlogCatagory(0).catagory
      Ok(views.html.blog.editBlogCatagory(userId, catarory))
    }
    
    /**
     * 修改分类，跳转
     */
    def modBlogCatarory(userId : ObjectId, blogCatagory : String) = Action {
      Ok(views.html.blog.modBlogCatagory(formBlogCatagory, blogCatagory, userId))
    }
    
    /**
     * 修改分类，后台逻辑
     */
    // TODO 这边的代码有点问题，要把该分类下的blog的类型转换成新的分类,OK
    def modCatagory(userId : ObjectId, blogCatagory : String) = Action {
            implicit request =>
      formBlogCatagory.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.blog.modBlogCatagory(errors, blogCatagory, userId)),
        {
          case (catagory) =>        
	        BlogCatagory.modCatagory(userId, blogCatagory, catagory)
	        //修改blog的分类
	        Blog.modCatagory(userId, blogCatagory, catagory)
	        Redirect(routes.BlogCatagories.editBlogCatagory(userId))

        } 
        )
    }
}