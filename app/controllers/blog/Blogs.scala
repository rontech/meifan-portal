package controllers

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import models._
import com.mongodb.casbah.Imports.ObjectId



object Blogs extends Controller {
  
  val formBlog = Form(tuple(
    "title" -> nonEmptyText,   
    "content" -> nonEmptyText,
    "blogTyp" -> nonEmptyText,
    "tags" -> nonEmptyText
  ))

  /**
   * 创建blog，跳转
   */
  def newBlog(userId : ObjectId) = Action {
    // list是一个blog分类的list
    var list = BlogCatagory.getCatagory(userId)
//    if(list.isEmpty){
    // 创建两个不变的分类，这两个是默认的，不可编辑，不可删除
      list :::= List("选择分类","私密博文")
//    }
    Ok(views.html.blog.blog(userId, formBlog, list))
  }
  
  /**
   * 用户删除blog
   */
  def deleteBlog(id : ObjectId) = Action {
     implicit request =>
      val user_id = request.session.get("user_id").get
      val userId = new ObjectId(user_id)
    Blog.delete(id)
    Redirect(routes.Blogs.showBlog(userId))
  }
  
  /**
   * 前台显示
   */
  // 这是数据库中用户的ObjectId
  def test = Action {
    val userId = new ObjectId("530d8010d7f2861457771bf8")
    val list = Blog.findByUserId(userId)
    // 目前传递的是username，可能需要传真实姓名或者是昵称，待完善
    val name = User.getUserName(userId)
    println("name" + name)
    // 这边时间的format需要调整，目前的格式是2014/03/05 9:04:08，，，，可能需要调整
    Ok(views.html.blog.blogTest(name, list))
  }
  
  /**
   * 取得指定店铺的指定blog
   */
  def getBlogInfoOfSalon(slnId: ObjectId, blogId: ObjectId) = Action {
    //目前是为了造数据使用的 ，下面两句
    val userId = new ObjectId("530d8010d7f2861457771bf8")
    val list = Blog.findByUserId(userId)
    
    val salon: Option[Salon] = Salon.findById(slnId)    
    val blog: Option[Blog] = Blog.findBySalon(slnId, blogId) 
//    println("salon" + salon)
//    println("blog" + blog)

    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoBlog(salon = salon.get, blog = blog.get))
 }
  /**
   * 取得店铺所有的blog
   */
  def findBySalon(salonId: ObjectId) = Action {
    //目前是为了造数据使用的，下面两句
    val userId = new ObjectId("530d8010d7f2861457771bf8")
    val list = Blog.findByUserId(userId)
    
    Blog.bloglist = Nil
    val salon: Option[Salon] = Salon.findById(salonId)    
    val blogs: Seq[Blog] = Blog.findBySalon(salonId)    
    
//    println("blogs" + blogs)
    

    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogs))
  }
  

  
  /**
   * 这个参数是userid的意思 showBlogByUserId
   * 显示一个用户的所有blog
   */
  def showBlog(id : ObjectId) = Action {
    val list = Blog.showBlog(id)
    Ok(views.html.blog.findBlogs(id, list))
  }
  
  /**
   * 显示某一条blog
   * 通过blog的id找到blog
   */
  def showBlogById(id : ObjectId) = Action {
    val list = Blog.showBlogById(id)
//    Ok(views.html.blog.findBlogs(id, list))
    Ok(views.html.blog.blogDetail(id, list))
  }
  
  /**
   * 新建blog，后台逻辑
   */
  def writeBlog(id : ObjectId) = Action {   
      implicit request =>
      formBlog.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.blog.blog(id, errors, BlogCatagory.getCatagory(id))),
        {
          case (title,content,blogTyp,tags) => 
	        Blog.newBlog(id, title,content,blogTyp,tags)
	        Ok(views.html.blog.showBlog(id))
        }             
        )
  }  
}
