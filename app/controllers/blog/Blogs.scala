package controllers

import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import com.mongodb.casbah.WriteConcern
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Calendar
import com.mongodb.casbah.commons.MongoDBObject
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import scala.concurrent.ExecutionContext.Implicits.global

object Blogs extends Controller with LoginLogout with AuthElement with AuthConfigImpl {
  
  def newBlogForm(userId : String, id : ObjectId = new ObjectId) = Form(
      mapping(
      "id" -> ignored(id),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "authorId" -> ignored(userId),
      "blogCategory" -> text,
      "blogPics" -> optional(list(text)), // TODO
      "tags" -> text,
      "isVisible" -> boolean,
      "pushToSalon" -> optional(boolean),
      "allowComment" -> boolean) {
      (id, title, content, authorId, blogCategory, blogPics, tags, isVisible, pushToSalon, allowComment)
        => Blog(id, title, content, authorId, new Date(), new Date(), blogCategory, blogPics, tags.split(",").toList, isVisible, pushToSalon, allowComment, true)
      } 
      {
        blog => Some((blog.id, blog.title, blog.content, blog.authorId, blog.blogCategory, blog.blogPics, listToString(blog.tags), blog.isVisible, blog.pushToSalon, blog.allowComment))
      }
      )
      
  /**
   * 把model和mapping，view统一
   */
  def listToString(list : List[String]) ={
    var strs : String = ""
    list.foreach(str =>
    strs += str + "," 
    )
    strs
  }
      
   def blogForm(userId : String, id : ObjectId = new ObjectId) = Form(
      mapping(
      "id" -> ignored(id),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "authorId" -> ignored(userId),
      "createTime" -> date,
      "blogCategory" -> text,
      "blogPics" -> optional(list(text)), // TODO
      "tags" -> text,
      "isVisible" -> boolean,
      "pushToSalon" -> optional(boolean),
      "allowComment" -> boolean) {
      (id, title, content, authorId, createTime, blogCategory, blogPics, tags, isVisible, pushToSalon, allowComment)
        => Blog(id, title, content, authorId, createTime, new Date(), blogCategory, blogPics, tags.split(",").toList, isVisible, pushToSalon, allowComment, true)
      } 
      {
        blog => Some((blog.id, blog.title, blog.content, blog.authorId, blog.createTime, blog.blogCategory, blog.blogPics, listToString(blog.tags), blog.isVisible, blog.pushToSalon, blog.allowComment))
      }
      )
  
  /**
   * 取得店铺指定理发师的blogs
   */    
  def getBlogByStylist(salonId: ObjectId, stylistId: ObjectId) = Action {
     val salon: Option[Salon] = Salon.findById(salonId)
    
     var blogList = Blog.getStylistBlogByStylistId(stylistId)
     val listYM = getListYM(salon.get)

     Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogList, listYM = listYM))
   }    
   
   /**
   * 取得店铺指定理发师的blogs
   */    
  def getBlogByYM(salonId: ObjectId, yM: String) = Action {
     val salon: Option[Salon] = Salon.findById(salonId)
     var blogList = getBlogBySalonAndYM(salonId, yM)
     val listYM = getListYM(salon.get)
     Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogList, listYM = listYM))
   }  
  
  /**
   * 根据一个年月找到blog的更新时间在一个范围内的blog
   */
  def getBlogBySalonAndYM(salonId: ObjectId, yM : String) = {   
    var listBlogYM : List[Blog] = Nil
    var blogList = Blog.findBySalon(salonId)
    blogList.foreach({
       row => 
         var blogDate = Calendar.getInstance()
         var ymd : SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd")
         var ym : SimpleDateFormat = new SimpleDateFormat("yyyyMM")
         var blogUpdateTime = DateFormat.getDateInstance.format(row.createTime)
         blogDate.setTime(ymd.parse(blogUpdateTime))
         var blogYM = ym.format(blogDate.getTime())
         if(blogYM.equals(yM)){
           listBlogYM :::= List(row)
         }        
    }     
    )
    listBlogYM 
  }
      
  /**
   * 店铺的单篇博客显示画面:
   * 除了本篇博客内容外，还需要显示：最新博客列表，按店铺技师分类，按日期分类等数据...
   */
  def getBlogInfoOfSalon(salonId: ObjectId, blogId: ObjectId) = Action {
      val salon: Option[Salon] = Salon.findById(salonId)
      // Check If the salon is exist or active.
      salon match {
          case None => NotFound
          case Some(sl) => {
              // get the required blog.
              val blog: Option[Blog] = Blog.findOneById(blogId)
              blog match {
                  case None => NotFound
                  case Some(blog) => {
                      // get the blog category by [DATE].  
                      val listYM = getListYM(sl)
                      // get the blog category by [SALON NEWEST] 
                      val newest = Blog.getNewestBlogsOfSalon(salonId)
                      val user: Option[User] = User.findOneByUserId(blog.authorId)
                      val stylist: Option[Stylist] = Stylist.findOneByStylistId(user.get.id)
                      // Jump
                      Ok(views.html.salon.store.salonInfoBlog(salon = sl, blog = blog, listYM = listYM, 
                          newestBlogsOfSalon = newest, stylist = stylist.get))
                  }
              }
          }
      }
 }

 
  /**
   * 查找blog的作者
   */
  def getBlogAuthor(salonId: ObjectId, userId: String) = Action {
    var user = User.findOneByUserId(userId).get
    var stylist = Stylist.findOne(MongoDBObject("stylistId" -> user.id)).get
    Redirect(routes.Salons.getOneStylist(salonId, stylist.stylistId))
  }
  
  /**
   * 取得沙龙注册时到当前时间的年月的List
   */
  def getListYM(salon : Salon) = {
    val startTime = DateFormat.getDateInstance.format(salon.registerDate)
    val endTime = DateFormat.getDateInstance.format(new Date)
    getTimeList(startTime, endTime)
  }
  
  /**
   * 根据起始时间和终止时间得到他们中间的时间List
   */
  def getTimeList(startTime : String, endTime : String) = {
    var list : List[String] = Nil
    var ymd : SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd")
    var ym : SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    var startDate = Calendar.getInstance()
    var endDate = Calendar.getInstance()

    startDate.setTime(ymd.parse(startTime))
    endDate.setTime(ymd.parse(endTime))
    var endYm = ym.format(endDate.getTime())
    
    var flg = true
    while (flg) {
        var strYm = ym.format(startDate.getTime())
        list :::= List(strYm)
        if (strYm.equals(endYm)) {
            flg = false
        }
        startDate.add(Calendar.MONTH, 1)
    }
    list
  }
  
  /**
   * 取得店铺所有的blog
   */
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val blogs: List[Blog] = Blog.findBySalon(salonId)
    val listYM = getListYM(salon.get)
    // TODO: process the salon not exist pattern.
    Ok(views.html.salon.store.salonInfoBlogAll(salon = salon.get, blogs = blogs, listYM = listYM))
  }
  
  /**
   * 查看用户的blog
   */
  def showBlog(userId : String) = Action { implicit request =>
    val user: Option[User] = User.findOneByUserId(userId)
    user match {
      case Some(user) => {
        val blogList = Blog.getBlogByUserId(userId)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        Ok(views.html.blog.admin.findBlogs(user = user, blogList, followInfo))
      }
      case None => NotFound
    }
    
  }
  
  
   /**
   * 创建blog，跳转
   */
  def newBlog(userId: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user: Option[User] = User.findOneByUserId(userId)
    user match {
      case Some(user) => {
        if(user == loggedIn){
	        val listBlogCategory = BlogCategory.getCategory
			val followInfo = MyFollow.getAllFollowInfo(user.id)
			Ok(views.html.blog.admin.newBlog(newBlogForm(userId), listBlogCategory, user = user, followInfo))
        }
        else{
          Redirect(routes.Blogs.newBlog(loggedIn.userId))
        }
      }
      case None => NotFound  
    }
  }
  
   /**
   * 新建blog，后台逻辑
   */
  def writeBlog(userId : String) = Action { implicit request =>
      val user: Option[User] = User.findOneByUserId(userId)
      user match {
        case Some(user) => {
          val listBlogCategory = BlogCategory.getCategory
	      val followInfo = MyFollow.getAllFollowInfo(user.id)
	      newBlogForm(userId).bindFromRequest.fold(
        //处理错误        
        errors => BadRequest(views.html.blog.admin.newBlog(errors,listBlogCategory, user = user, followInfo)),
        {
          blog =>
            Blog.save(blog, WriteConcern.Safe)
            Redirect(routes.Blogs.showBlogById(blog.id))
        }             
        )
          
        }
        case None => NotFound
      }
  }  
  
   /**
   * 编辑blog
   */
  def editBlog(blogId : ObjectId) = Action {
    val blog = Blog.findOneById(blogId)
    val list = BlogCategory.getCategory
    blog.map { blog =>
      val user = User.findOneByUserId(blog.authorId).get
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      val formEditBlog = blogForm(user.userId).fill(blog)
//      Ok(views.html.blog.admin.editBlog(formEditBlog, list, user, blog))
      Ok(views.html.blog.admin.editBlog(formEditBlog, list, user, followInfo,blog))
    } getOrElse {
      NotFound
    }
  }
  
   /**
   * 编辑blog，后台逻辑
   */
  def modBlog(blogId : ObjectId) = Action { implicit request =>
      val blog = Blog.findOneById(blogId)
      blog match {
        case Some(blog) => {
          val user: Option[User] = User.findOneByUserId(blog.authorId)
	      val listBlogCategory = BlogCategory.getCategory
	      val followInfo = MyFollow.getAllFollowInfo(user.get.id)
	      blogForm(blog.authorId,blogId).bindFromRequest.fold(
	        //处理错误        
	        errors => BadRequest(views.html.blog.admin.editBlog(errors,listBlogCategory,user.get, followInfo, blog)),
	        {
	          blog =>            
	            Blog.save(blog, WriteConcern.Safe)
	//	        Redirect(routes.Blogs.showBlog(userId))
	            Redirect(routes.Blogs.showBlogById(blogId))
	        }             
        )         
        }
        case None => NotFound
        
      }
  }  
  
   /**
   * 用户删除blog
   */
  def deleteBlog(blogId : ObjectId) = Action {
    var blog = Blog.findOneById(blogId)
    blog match {
      case Some(blog) => {
		Blog.delete(blogId)
		Redirect(routes.Blogs.showBlog(blog.authorId))
      }
      case None => NotFound
    }
  }
  
   /**
   * 显示某一条blog
   * 通过blog的id找到blog
   */
  def showBlogById(blogId: ObjectId) = Action { implicit request =>
    val blog = Blog.findOneById(blogId)
    blog match {
      case Some(blog) => {
    	val user = User.findOneByUserId(blog.authorId).get
	    Comment.list = Nil
	    val commentList = Comment.all(blogId)
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    Ok(views.html.blog.admin.blogDetail(blog, user, commentList, followInfo))
      }
      case None => NotFound
    } 

  }
}
