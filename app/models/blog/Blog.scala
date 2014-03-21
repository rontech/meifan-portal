package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import models._




    
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
    pushToSlaon : Option[Boolean],
    allowComment : Boolean,
    isValid : Boolean)

object Blog extends ModelCompanion[Blog, ObjectId] {
  val dao = new SalatDAO[Blog, ObjectId](collection = mongoCollection("Blog")) {}
  
  /**
   * 指定店铺和Blog的Id精确找到一篇blog
   */
  def findBySalon(salonId: ObjectId, blogId: ObjectId): Option[Blog] = {
    val stylistList = Stylist.findBySalon(salonId)
    var blog : Option[Blog] = None
    stylistList.foreach(
      {
      row => 
      blog = Blog.findOne(DBObject("authorId" -> row, "_id" -> blogId))
      if(blog != None)
        return blog  
      }
   )
   blog
   }
  
  /**
   * 找到该店铺下面所有发型师的Blog
   */
  var blogList : List[Blog] = Nil
  def findBySalon(salonId: ObjectId): List[Blog] = {
    val stylistList = Stylist.findBySalon(salonId)
    var blog : List[Blog] = Nil
    stylistList.foreach(
      {
      row => 
        var user = User.findById(row.userId).get
        blog = Blog.find(DBObject("authorId" -> user.userId)).toList
        if(!blog.isEmpty)
          blogList :::= blog
      }
    )
    blogList
  }

  /**
   * 通过该用户的UserId找到该用户的blog
   */
  def getBlogByUserId(userId : String) = {
    dao.find(MongoDBObject("authorId" -> userId, "isValid" -> true)).toList
  }
  
  override
  def findOneById(id: ObjectId): Option[Blog] = dao.findOne(MongoDBObject("_id" -> id))
  
  def delete(id : ObjectId) = {
    val blog = findOneById(id).get
    dao.update(MongoDBObject("_id" -> blog.id), MongoDBObject("$set" -> (MongoDBObject("isValid" -> false))))
  }
}