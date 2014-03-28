package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._

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
    dao.find(MongoDBObject("authorId" -> userId, "isValid" -> true, "pushToSalon" -> true)).toList
  }
  
  /**
   * 查找blog表中最新的3条blog
   */
  // TODO
  def findBlogForHome() = {
    dao.find(MongoDBObject("isVisible" -> true, "isValid" -> true, "pushToSalon" -> true)).sort(MongoDBObject("createTime" -> -1)).toList
  }
}
