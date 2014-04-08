package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._

/**
 * A All Info structs of blog including belows
 *   1. basic info as a user.   
 *   2. basic info as a salon.
 */
case class BlogOfSalon(blogInfo: Blog, salonInfo: Option[Salon]) {
  def apply(blogInfo: Blog, salonInfo: Option[Salon]) = new BlogOfSalon(blogInfo, salonInfo) 
}

/**
 * A All Info structs of blog including belows
 *   1. basic info as a user.   
 *   2. basic info as a stylist linked the user above.
 *   3. a blog list of the stylist above
 */
case class BlogOfStylist(userInfo : User, stylistInfo : Stylist, blogListOfStylist : List[Blog]) {
  def apply(userInfo : User, stylistInfo : Stylist, blogListOfStylist : List[Blog]) = new BlogOfStylist(userInfo, stylistInfo, blogListOfStylist)
}




case class Blog(
    id : ObjectId = new ObjectId,
    title : String, 
    content : String,
    authorId: String, 
    createTime: Date, 
    updateTime : Date,
    blogCategory : String,
    blogPics : Option[List[String]], // TODO
    tags : List[String],
    isVisible : Boolean,
    pushToSalon : Option[Boolean],
    allowComment : Boolean,
    isValid : Boolean)

object Blog extends ModelCompanion[Blog, ObjectId] {
  val dao = new SalatDAO[Blog, ObjectId](collection = mongoCollection("Blog")) {}
  
  /**
   * 找到该店铺下面所有发型师的Blog
   */
  def findBySalon(salonId: ObjectId): List[Blog] = {
    var blogList : List[Blog] = Nil
    val stylistList = Stylist.findBySalon(salonId)
    var blog : List[Blog] = Nil
    stylistList.foreach({ row => 
        var user = User.findOneById(row.stylistId).get
        blog = Blog.find(DBObject("authorId" -> user.userId, "isValid" -> true, "pushToSalon" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
        if(!blog.isEmpty)
          blogList :::= blog
      }
    )
    blogList.sortBy(blog => blog.createTime).reverse
  }
  
  /**
   * 查找店铺的最新blog（显示不多于5条）
   */
  def getNewestBlogsOfSalon(salonId: ObjectId) = {
    if(findBySalon(salonId).size <= 5){
      findBySalon(salonId)
    }else{
      findBySalon(salonId).dropRight(findBySalon(salonId).size-5)
    }
  }

  /**
   * 通过该用户的UserId找到该用户的blog
   */
  def getBlogByUserId(userId : String) = {
    dao.find(MongoDBObject("authorId" -> userId, "isValid" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
  }
  
  /**
   * 权限控制，查看其他用户的blog
   * 通过该用户的UserId找到该用户的blog
   */
  def getOtherBlogByUserId(userId : String) = {
    dao.find(MongoDBObject("authorId" -> userId, "isValid" -> true, "isVisible" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
  }
  
  override
  def findOneById(id: ObjectId): Option[Blog] = dao.findOne(MongoDBObject("_id" -> id))
  
  /**
   * 删除指定的blog
   */
  def delete(id : ObjectId) = {
    val blog = findOneById(id).get
    dao.update(MongoDBObject("_id" -> blog.id), MongoDBObject("$set" -> MongoDBObject("isValid" -> false)))
  }
  
  /**
   * 通过User ObjectId 找到该发型师的blog
   */
  def getStylistBlogByStylistId(objId: ObjectId) = {
    val user = User.findOneById(objId)
    user match {
      case None => Nil
      case Some(usr) => getStylistBlogByUserId(usr.userId)
    }
  }

  /**
   * 通过UserId找到该发型师的blog
   */
  def getStylistBlogByUserId(userId : String) = {
    dao.find(MongoDBObject("authorId" -> userId, "isValid" -> true, "pushToSalon" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
  }
  
  /**
   * 查找blog表中最新的num条blog
   * 这边这是查找发型师的blog，并且他人可见和推送至店铺
   */
  // TODO
  def findBlogForHome(num : Int) : List[BlogOfSalon]= {
    var blogOfSalonList : List[BlogOfSalon] = Nil
    val blogList= dao.find(MongoDBObject("isVisible" -> true, "isValid" -> true, "pushToSalon" -> true)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
    blogList.foreach({
      row =>
        val user = User.findOneByUserId(row.authorId)
		user match {
		    case None => None
		    case Some(u) => {
		      val stylist = Stylist.findOneById(u.id)
		      stylist match {
			      case None => None
			      case Some(st) => {
			        val salonAndStylist= SalonAndStylist.findByStylistId(st.stylistId)
			        salonAndStylist match {
			          case None => None
			          case Some(salonSt) => {
			            val salon = Salon.findOneById(salonSt.salonId)
			            val blogOfSalon = BlogOfSalon(row, salon)			            
			            blogOfSalonList :::= List(blogOfSalon)
			          }
			        }
			        }
			      }
		      }
		    }
        }    
    )
    blogOfSalonList
  }
  
  /**
   * 这边也是通过店铺找到该店铺下技师的blog，这边返回的是一个BlogOfStylist的对象
   */
  //TODO 上面取的店铺的blog的方法可能需要重构
  def getBlogsOfStylistInSalon(salonId: ObjectId) : List[BlogOfStylist] = {
    var blogsOfStylistList : List[BlogOfStylist] = Nil
    val stylistList = Stylist.findBySalon(salonId)
    stylistList.foreach({ stl => 
        var user = User.findOneById(stl.stylistId)
        user match {
          case None => None
          case Some(u) => {
            val blogList = getStylistBlogByUserId(u.userId)
            val blogOfStylist = BlogOfStylist(u, stl, blogList)
            blogsOfStylistList :::= List(blogOfStylist)
          }
        }
      }
    )
    
    blogsOfStylistList
  }
  
  
  
  
}
