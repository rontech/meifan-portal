package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._

case class CommentOfSalon(commentInfo: Comment, salonInfo: Option[Salon]) {
  def apply(commentInfo: Comment, salonInfo: Option[Salon]) = new CommentOfSalon(commentInfo, salonInfo) 
}

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
  
  /**
   * 根据评论对象的ObjectId查找评论，包括对这条评论所做的评论。
   * TODO 方法名
   */
  var list = List.empty[Comment]
  def all(id : ObjectId): List[Comment] = {   
    val l = dao.find(MongoDBObject("commentObjId" -> id, "isValid" -> true)).sort(MongoDBObject("time" -> 1)).toList
     if (!l.isEmpty){
     l.foreach(
       {
           r =>list :::= List(r)
           all(r.id)
       })
     }
     list.reverse
    }
  
  /**
   *  模块化的代码，通过店铺Id找到评论,应该是通过店铺找到coupon，再找到对coupon做的评论 
   *  目前是对coupon做的评论，到时候应该对预约做评论
   */ 
  def findBySalon(salonId: ObjectId): List[Comment] = {    
    var commentList : List[Comment] = Nil
    val couponList = Coupon.findBySalon(salonId)
    var comment : List[Comment] = Nil
    couponList.foreach(
      {
      r => 
        list = Nil
//      comment = Comment.find(DBObject("commentObjId" -> r.id, "isValid" -> true)).toList
        comment = Comment.all(r.id)
      if(!comment.isEmpty)
          commentList :::= comment
      }
    )
    commentList
  }
  
  def addComment(userId : String, content : String, commentObjId : ObjectId, commentObjType : Int) = {
    dao.save(Comment(commentObjType = commentObjType, commentObjId = commentObjId, content = content, authorId = userId, isValid = true))    
  }
  
  def reply(userId : String, content : String, commentObjId : ObjectId, commentObjType : Int) = {
    dao.save(Comment(commentObjType = commentObjType, commentObjId = commentObjId, content = content, authorId = userId, isValid = true))      
  }
  
  override
  def findOneById(id: ObjectId): Option[Comment] = dao.findOne(MongoDBObject("_id" -> id))
  
  def delete(id : ObjectId) = {
    val comment = findOneById(id).get
    dao.update(MongoDBObject("_id" -> comment.id), MongoDBObject("$set" -> MongoDBObject("isValid" -> false)))
  }
  
  /**
   * 查找评论表中最新的num条记录，该方法用于在首页上显示最新的评论。
   * 只是针对店铺中coupon（以后是预约）做的评论
   * 这边的2代表对coupon做的评论
   */
  // TODO
  def findCommentForHome(num : Int) : List[CommentOfSalon]= {
    var commentOfSalonList : List[CommentOfSalon] = Nil
    val commentList= dao.find(MongoDBObject("isValid" -> true, "commentObjType" -> 2)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
    commentList.foreach({
      row =>
        val coupon = Coupon.findOneById(row.commentObjId)
		coupon match {
		    case None => None
		    case Some(coupon) => {
	            val salon = Salon.findOneById(coupon.salonId)
	            val commentOfSalon = CommentOfSalon(row, salon)			            
	            commentOfSalonList :::= List(commentOfSalon)
		    }
        }
        }    
    )
    commentOfSalonList
  }
  
}

