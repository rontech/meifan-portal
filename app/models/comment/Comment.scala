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
    commentObjType : Int,
    commentObjId : ObjectId, 
    content : String,
    authorId : String, 
    createTime : Date = new Date,
    isValid : Boolean)

object Comment extends ModelCompanion[Comment, ObjectId] {
  val dao = new SalatDAO[Comment, ObjectId](collection = mongoCollection("Comment")) {}
  
  implicit var list = List.empty[Comment]
  def all(id : ObjectId): List[Comment] =   
    {
//     val l = dao.find(MongoDBObject("commentedId" -> id, "status" -> 0)).toList
    //以时间降序排序
    val l = dao.find(MongoDBObject("commentObjId" -> id, "isValid" -> true)).sort(MongoDBObject("time" -> 1)).toList
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
  
  // 模块化的代码，通过店铺Id找到评论,应该是通过店铺找到coupon，再找到对coupon做的评论
  var commentList : List[Comment] = Nil
  def findBySalon(salonId: ObjectId): List[Comment] = {
    val couponList = Coupon.findBySalon(salonId)
    var comment : List[Comment] = Nil
    couponList.foreach(
      {
      r => 
      comment = Comment.find(DBObject("commentObjId" -> r.id)).toList
      if(!comment.isEmpty)
          commentList :::= comment
      }
    )
    commentList
  }
  
  def addComment(userId : String, content : String, commentObjId : ObjectId, commentObjType : Int) = {
    dao.save(Comment(commentObjType = commentObjType, commentObjId = commentObjId, content = content, authorId = userId, isValid = true))    
  }
  
  
//  def delete(id : ObjectId) = {
//    val comment = dao.findOneById(id).get
//    dao.save(Comment(id = id, userId = comment.userId, status = 1, commentedId = comment.commentedId, relevantUser = comment.relevantUser, commentedType = comment.commentedType, content = comment.content), WriteConcern.Safe)
//  }
  
  def reply(userId : String, content : String, commentObjId : ObjectId, commentObjType : Int) = {
//    val model = dao.findOneById(id)
//    val model = dao.findOne(MongoDBObject("id" -> new ObjectId(id))).get
    dao.save(Comment(commentObjType = commentObjType, commentObjId = commentObjId, content = content, authorId = userId, isValid = true))      
  }
  
  def findById(id : ObjectId) = {
    dao.findOneById(id).get
  }
}

