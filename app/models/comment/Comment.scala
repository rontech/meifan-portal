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



    
case class Comment(
    id : ObjectId = new ObjectId,
    userId: ObjectId, 
    time: Date = new Date, 
    status : Int, 
//    refCommentId: ObjectId, 
    commentedId : ObjectId, 
    relevantUser : ObjectId,
    commentedType : Int,
    content : String)

object Comment extends ModelCompanion[Comment, ObjectId] {
  val dao = new SalatDAO[Comment, ObjectId](collection = mongoCollection("comment")) {}
  
  implicit var list = List.empty[Comment]
  def all(id : ObjectId): List[Comment] =   
    {
//     val l = dao.find(MongoDBObject("commentedId" -> id, "status" -> 0)).toList
    //以时间降序排序
    val l = dao.find(MongoDBObject("commentedId" -> id, "status" -> 0)).sort(MongoDBObject("time" -> -1)).toList
     if (!l.isEmpty){
     l.foreach(
       {
           r =>list :::= List(r)
           all(r.id)
       })
     }
    // 把店家的回复放在下面
     list.reverse
    }
  
  // 模块化的代码，通过店铺Id找到评论,应该是通过店铺找到coupon，再找到coupon下面的评论
  var commentlist : List[Comment] = Nil
  def findBySalon(salonId: ObjectId): List[Comment] = {
    val stylist = Stylist.findBySalon(salonId)
    println("stylist" + stylist)
    var comment : List[Comment] = Nil
    stylist.foreach(
      {
      r => 
      comment = Comment.find(DBObject("userId" -> r.id)).toList
      println("blog" + comment)
      if(!comment.isEmpty)
//        blog :::= bloglist
          commentlist :::= comment
      }
    )
    commentlist
    
  }
  
  def addComment(userId : ObjectId, content : String, commentedId : ObjectId, relevantUser : ObjectId) = {
    dao.save(Comment(userId = userId, status = 0, commentedId = commentedId, relevantUser = relevantUser, commentedType = 1, content = content))    
  }
  
  
  def delete(id : ObjectId) = {
    val comment = dao.findOneById(id).get
    dao.save(Comment(id = id, userId = comment.userId, status = 1, commentedId = comment.commentedId, relevantUser = comment.relevantUser, commentedType = comment.commentedType, content = comment.content), WriteConcern.Safe)
  }
  
  def huifu(id : ObjectId, content : String, userId : ObjectId) {
    val model = dao.findOneById(id)
    
//    val model = dao.findOne(MongoDBObject("id" -> new ObjectId(id))).get
    dao.save(Comment(userId = userId, status = 0, commentedId = id, relevantUser = userId, commentedType = 3, content = content))
    
  }
}