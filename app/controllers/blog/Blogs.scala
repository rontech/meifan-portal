package controllers

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import models._
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.Imports.ObjectId

object Blogs extends Controller {

  val formBlog = Form(tuple(
    "title" -> nonEmptyText,
    "content" -> nonEmptyText,
    "blogTyp" -> nonEmptyText,
    "tags" -> nonEmptyText
  ))
  
  def newBlogForm(userId : String, id : ObjectId = new ObjectId) = Form(
      mapping(
      "id" -> ignored(id),
      "userId" -> ignored(userId),
      "commentType" -> text,
      "blogAvailable" -> list(text),
      "title" ->text,
      "content" ->text,
      "blogTyp" ->text,
      "tags" -> text) {
      (id, userId, commentType, blogAvailable, title, content, blogTyp, tags)
        => Blog(id, userId, new Date(), new Date(), commentType, blogAvailable, 0, title, blogTyp, tags, content)
      } 
      {
        blog => Some((blog.id, blog.userId, blog.commentType, blog.blogAvailable, blog.title, blog.content, blog.blogTyp, blog.tags))
      }
      )

   def blogForm(userId : String, id : ObjectId = new ObjectId) = Form(
      mapping(
      "id" -> ignored(id),
      "userId" -> ignored(userId),
      "commentType" -> text,
      "createdTime" -> date, 
      "blogAvailable" -> list(text),
      "title" ->text,
      "content" ->text,
      "blogTyp" ->text,
      "tags" -> text) {
        (id, userId, commentType, createdTime, blogAvailable, title, content, blogTyp, tags)
        => Blog(id, userId, createdTime, new Date(), commentType, blogAvailable, 0, title, blogTyp, tags, content)
      } 
      {
        blog => Some((blog.id, blog.userId, blog.commentType, blog.createdTime, blog.blogAvailable, blog.title, blog.content, blog.blogTyp, blog.tags))
      }
      )
  
  val formCatagory = Form(
    "blogTyp" -> nonEmptyText
  )

//  /**
//   * 创建blog，跳转
//   */
//  def newBlog(userId: ObjectId) = Action {
//    // list是一个blog分类的list
//    var list = BlogCatagory.getCatagory(userId)
//   // 创建两个不变的分类，这两个是默认的，不可编辑，不可删除
//    list :::= List("选择分类","私密博文")
//    
//    val user: Option[User] = User.findById(userId)
//    Ok(views.html.blog.blog(formBlog, list, user = user.get))
//  }
//
//  /**
//   * 用户删除blog
//   */
// def deleteBlog(id : ObjectId) = Action {
//     implicit request =>
//      val user_id = request.session.get("userId").get
//      val userId = User.findOneByUserId(user_id).get.id
//    Blog.delete(id)
//    Redirect(routes.Blogs.showBlog(userId))
//  }
//
//  /**
//   * 前台显示
//   */
//  // 这是数据库中用户的ObjectId
//  def test = Action {
//    val userId = new ObjectId("530d8010d7f2861457771bf8")
//    val list = Blog.findByUserId(userId)
//    // 目前传递的是username，可能需要传真实姓名或者是昵称，待完善
//    val name = User.findOneById(userId).get.userId
//    // 这边时间的format需要调整，目前的格式是2014/03/05 9:04:08，，，，可能需要调整
//    Ok(views.html.blog.blogTest(name, list))
//  }
//
//  /**
//   * 取得指定店铺的指定blog
//   */
//  def getBlogInfoOfSalon(slnId: ObjectId, blogId: ObjectId) = Action {
//    //目前是为了造数据使用的 ，下面两句
//    val userId = new ObjectId("530d8010d7f2861457771bf8")
//    val list = Blog.findByUserId(userId)
//    val salon: Option[Salon] = Salon.findById(slnId)
//    val blog: Option[Blog] = Blog.findBySalon(slnId, blogId)
//    // TODO: process the salon not exist pattern.
//    Ok(views.html.salon.store.salonInfoBlog(salon = salon.get, blog = blog.get))
//  }
//  /**
//   * 取得店铺所有的blog
//   */
//  def findBySalon(salonId: ObjectId) = Action {
//    //目前是为了造数据使用的，下面两句
//    val userId = new ObjectId("530d8010d7f2861457771bf8")
//    val list = Blog.findByUserId(userId)
//    Blog.bloglist = Nil
//    val salon: Option[Salon] = Salon.findById(salonId)
//    val blogs: Seq[Blog] = Blog.findBySalon(salonId)
//    // TODO: process the salon not exist pattern.
//    Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogs))
//  }
//
//  /**
//   * 这个参数是userid的意思 showBlogByUserId
//   * 显示一个用户的所有blog
//   */
// def showBlog(userId : ObjectId) = Action {
//    // list是一个blog分类的list
//    var list = BlogCatagory.getCatagory(userId)
//    // 创建两个不变的分类，这两个是默认的，不可编辑，不可删除
//    list :::= List("全部博文","私密博文","未分类博文")
//    
////    val list = Blog.showBlog(userId)
//    val user: Option[User] = User.findById(userId)
//    Ok(views.html.blog.findBlogs(user = user.get, list))
//  }
//
//  /**
//   * 显示某一条blog
//   * 通过blog的id找到blog
//   */
//  def showBlogById(id: ObjectId) = Action {
//    val list = Blog.showBlogById(id)
//    //    Ok(views.html.blog.findBlogs(id, list))
//    Ok(views.html.blog.blogDetail(id, list))
//  }
//
//  /**
//   * 新建blog，后台逻辑
//   */
// def writeBlog(userId : ObjectId) = Action {   
//    implicit request =>
//      formBlog.bindFromRequest.fold(
//        //处理错误        
//        errors => BadRequest(views.html.blog.blog(errors, BlogCatagory.getCatagory(userId),User.findById(userId).get)),
//        {
//         case (title,content,blogTyp,tags) => 
//	        Blog.newBlog(userId, title,content,blogTyp,tags)
////	        Ok(views.html.blog.showBlog(userId))
//	        Redirect(routes.Blogs.showBlog(userId))
//        }             
//        )
//  }  
//  
//    /**
//   * 编辑blog，后台逻辑
//   */
//  def modBlog(blogId : ObjectId) = Action {   
//    implicit request =>
//      formBlog.bindFromRequest.fold(
//        //处理错误        
//        errors => BadRequest(views.html.blog.editBlog(blogId, errors, Nil,User.findById(Blog.findById(blogId).get.userId).get)),
//        {
//          case (title,content,blogTyp,tags) => 
//	        Blog.modBlog(blogId, title,content,blogTyp,tags)
//	        Ok("修改成功")
////	        Redirect(routes.Blogs.showBlog(userId))
//        }             
//        )
//  }  
//  
//  /**
//   * 根据分类去检索相关的blog
//   */
//  def findBlogByCatagory(catagory : String, userId : ObjectId) = Action {
//    val user: Option[User] = User.findById(userId)
//    Ok(views.html.blog.findBlogsByCatagory(user = user.get, catagory))
//  }
//  
//  /**
//   * 修改blog的分类，前台跳转
//   */
//  // blogTyp这个参数需要吗？
//  def changeBlogCatagory(blogId : ObjectId, blogTyp : String) = Action {
//    val blog = Blog.findById(blogId).get
//    val user: Option[User] = User.findById(blog.userId)
//    // list是一个blog分类的list
//    var list = BlogCatagory.getCatagory(blog.userId)
//    // 创建两个不变的分类，这两个是默认的，不可编辑，不可删除
//    list :::= List("选择分类","私密博文")
//    Ok(views.html.blog.changeBlogCatagory(formCatagory, list, user = user.get, blogId))
//  }
//  
//  /**
//   * 修改单个blog的分类
//   */
//  def changeCatagory(blogId : ObjectId) = Action {
//    implicit request =>
//      formCatagory.bindFromRequest.fold(
//        //处理错误       
//        errors => BadRequest(views.html.blog.changeBlogCatagory(errors, Nil, User.findById(Blog.findById(blogId).get.userId).get, blogId)),
//        {
//          case (blogTyp) => 
//	        Blog.changeCatagory(blogId,blogTyp)
//	        Redirect(routes.Blogs.showBlog(Blog.findById(blogId).get.userId))
//        }             
//        )
//  }
//  
//  
//  /**
//   * 编辑blog
//   */
//  def editBlog(blogId : ObjectId) = Action {
//    val userId = Blog.findById(blogId).get.userId
//    // list是一个blog分类的list
//    var list = BlogCatagory.getCatagory(userId)
//    // 创建两个不变的分类，这两个是默认的，不可编辑，不可删除
//    list :::= List("选择分类","私密博文")
//    
//    val user: Option[User] = User.findById(userId)
//    Blog.findById(blogId).map { blog =>
//      val formEditBlog = formBlog.fill(blog.title, blog.content, blog.blogTyp, blog.tags)
//      Ok(views.html.blog.editBlog(blogId, formEditBlog, list, user = user.get))
//    } getOrElse {
//      NotFound
//    }
//  }
  
    def showBlog(userId : String) = Action {
    
    // list是一个blog分类的list
    var list = BlogCatagory.getAllCatagory(userId)
    // 创建两个不变的分类，这两个是默认的，不可编辑，不可删除
//    list :::= List("全部博文","私密博文","未分类博文")
    
//    val list = Blog.showBlog(userId)
    val user: Option[User] = User.findOneByUserId(userId)
//    val commentList = Comment.all(id)
//    Comment.list = Nil
    Ok(views.html.blog.findBlogs(user = user.get, list))

  }
  
  
   /**
   * 创建blog，跳转
   */
  def newBlog(userId: String) = Action {
    // list是一个blog分类的list
    var list = BlogCatagory.getAllCatagory(userId)
    // 创建两个不变的分类，这两个是默认的，不可编辑，不可删除
//    list :::= List("选择分类","私密博文")
    
    val user: Option[User] = User.findOneByUserId(userId)
    
    Ok(views.html.blog.newBlog(newBlogForm(userId), list, user = user.get))
  }
  
   /**
   * 新建blog，后台逻辑
   */
  def writeBlog(userId : String) = Action {   
    implicit request =>
      newBlogForm(userId).bindFromRequest.fold(
        //处理错误        
        errors => BadRequest(views.html.blog.errorMsg(errors)),
        {
//          case (title,content,blogTyp,tags) => 
//	        Blog.newBlog(userId, title,content,blogTyp,tags)
          blog =>
            Blog.save(blog, WriteConcern.Safe)
//	        Ok(views.html.blog.showBlog(userId))
	        Redirect(routes.Blogs.showBlog(userId))
        }             
        )
  }  
  
   /**
   * 编辑blog
   */
  def editBlog(blogId : ObjectId) = Action {
    val userId = Blog.findById(blogId).get.userId
    // list是一个blog分类的list
    var list = BlogCatagory.getAllCatagory(userId)
    // 创建两个不变的分类，这两个是默认的，不可编辑，不可删除
//    list :::= List("选择分类","私密博文")
    
    val user = User.findOneByUserId(userId).get
    Blog.findById(blogId).map { blog =>
      val formEditBlog = blogForm(userId).fill(blog)
      Ok(views.html.blog.editBlog(formEditBlog, list, user, blog))
    } getOrElse {
      NotFound
    }
  }
  
   /**
   * 编辑blog，后台逻辑
   */
  def modBlog(blogId : ObjectId) = Action {   
    implicit request =>
      val userId = Blog.findById(blogId).get.userId
      blogForm(userId,blogId).bindFromRequest.fold(
        //处理错误        
        errors => BadRequest(views.html.blog.errorMsg(errors)),
        {
          blog =>
            Blog.save(blog, WriteConcern.Safe)
//	        Ok(views.html.blog.showBlog(userId))
	        Redirect(routes.Blogs.showBlog(userId))
//	        Redirect(routes.Blogs.showBlog(userId))
        }             
        )
  }  
  
   /**
   * 用户删除blog
   */
  def deleteBlog(blogId : ObjectId) = Action {
     implicit request =>
      val userId = request.session.get("userId").get
//      val userId = User.findOneByUserId(user_id).get.id
    Blog.delete(blogId)
//    Blog.update(q, t, upsert, multi, wc)
    Redirect(routes.Blogs.showBlog(userId))

  }
  
   /**
   * 显示某一条blog
   * 通过blog的id找到blog
   */
  def showBlogById(blogId: ObjectId) = Action {
    val blog = Blog.findById(blogId).get
    val user = User.findOneByUserId(blog.userId).get
    Comment.list = Nil
    val commentList = Comment.all(blogId)
    //    Ok(views.html.blog.findBlogs(id, list))
    Ok(views.html.blog.blogDetail(blog, user, commentList))
  }
}
