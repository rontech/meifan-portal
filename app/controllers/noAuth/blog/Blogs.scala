package controllers.noAuth

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
import controllers._

object Blogs extends Controller with OptionalAuthElement with AuthConfigImpl {
  
  /**
   * 取得店铺指定理发师的blogs
   */    
  def getBlogByStylist(salonId: ObjectId, stylistId: ObjectId) = Action {
     val salon: Option[Salon] = Salon.findById(salonId)
     val stylist = Stylist.findOneById(stylistId)
     var user = User.findOneById(stylist.get.stylistId).get
     var blogList = Blog.getStylistBlogByUserId(user.userId)
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
         var blogCreateTime = DateFormat.getDateInstance.format(row.createTime)
         blogDate.setTime(ymd.parse(blogCreateTime))
         var blogYM = ym.format(blogDate.getTime())
         if(blogYM.equals(yM)){
           listBlogYM :::= List(row)
         }        
    }     
    )
    listBlogYM 
  }
      
  /**
   * 取得指定店铺的指定blog
   */
  def getBlogInfoOfSalon(salonId: ObjectId, blogId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val blog: Option[Blog] = Blog.findOneById(blogId)
    salon match {
      case Some(salon) => {
        val listYM = getListYM(salon)
		val newestBlogsOfSalon = Blog.getNewestBlogsOfSalon(salonId)
		blog match {
          case Some(blog) => {
	          val user : Option[User] = User.findOneByUserId(blog.authorId)
			  val stylist : Option[Stylist] = Stylist.findOne(MongoDBObject("publicId" -> user.get.id))
			  Ok(views.html.salon.store.salonInfoBlog(salon = salon, blog = blog, listYM = listYM, newestBlogsOfSalon = newestBlogsOfSalon, stylist = stylist.get))
          }
          case None => NotFound
        }       
      }
      case None => NotFound
    }
  }
  
  /**
   * 查找blog的作者
   */
  def getBlogAuthor(salonId: ObjectId, userId: String) = Action {
    var user = User.findOneByUserId(userId).get
    var stylist = Stylist.findOne(MongoDBObject("publicId" -> user.id)).get
    Redirect(controllers.routes.Salons.getOneStylist(salonId, stylist.id))
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
  def showBlog(userId : String) = StackAction { implicit request =>
    var blogList : List[Blog] = Nil
    val user: Option[User] = User.findOneByUserId(userId)
    user match {
      case Some(user) => {
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        loggedIn.map{ LoggedUser => 
          if(LoggedUser.id.equals(user.id))
        	blogList = Blog.getBlogByUserId(userId)
    	  else
    	    blogList = Blog.getOtherBlogByUserId(userId)    	    
          Ok(views.html.blog.admin.findBlogs(user, blogList, followInfo, LoggedUser.id, true))
        }getOrElse{
          blogList = Blog.getOtherBlogByUserId(userId)
    	  Ok(views.html.blog.admin.findBlogs(user, blogList, followInfo))
        }
      }
      case None => NotFound
    }
    
  }
  
   /**
   * 显示某一条blog
   * 通过blog的id找到blog
   */
  def showBlogById(blogId: ObjectId) = StackAction { implicit request =>
    val blog = Blog.findOneById(blogId)
    blog match {
      case Some(blog) => {
    	val user = User.findOneByUserId(blog.authorId).get
	    Comment.list = Nil
	    val commentList = Comment.all(blogId)
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    loggedIn.map{ LoggedUser => 
	      Ok(views.html.blog.admin.blogDetail(blog, user, commentList, followInfo, LoggedUser.id, true))
	    }getOrElse{
    	  Ok(views.html.blog.admin.blogDetail(blog, user, commentList, followInfo))
        }
      }
      case None => NotFound
    } 
  }
  
}
