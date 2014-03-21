package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class MyFollows(
  id: ObjectId = new ObjectId,
  userId: ObjectId,
  followObjId: ObjectId,
  followObjType: String   //关注店铺:salon；关注技师:stylist；收藏风格:style；收藏优惠劵:coupon；收藏博客:blog;关注用户:user
  )

object MyFollows extends ModelCompanion[MyFollows, ObjectId] {

  val dao = new SalatDAO[MyFollows, ObjectId](collection = mongoCollection("MyFollows")) {}

  val FOLLOWUSER = "followUser"
  
  /**
   *根据用户Id和关系类型获取被关注或收藏的对象 
   */
  def getAllFollowObjId(followObjType: String, userId: ObjectId): 
     List[ObjectId] = dao.find(MongoDBObject("followObjType" -> followObjType, "userId" -> userId)).toList.map { myFollows => myFollows.followObjId }

  /**
   *添加收藏或关注 
   */
  def create(userId: ObjectId,followObjId: ObjectId, followObjType: String) {
    dao.insert(MyFollows(userId = userId,followObjId = followObjId,followObjType = followObjType))
  }
  
  /**
   *取消收藏或关注 
   */
  def delete(userId: ObjectId,followObjId: ObjectId) {
    dao.remove(MongoDBObject("userId" -> userId,"followObjId" -> followObjId))
  }
  
  /**
   *查看我被关注的对象，即我的粉丝 
   */
  def getFollowers(userId:ObjectId):
	  List[ObjectId] = dao.find(MongoDBObject("followObjType" -> FOLLOWUSER, "followObjId" -> userId)).toList.map { myFollowers => myFollowers.userId }
  
  /**
   *检验是否已关注或收藏 
   */
  def checkIfFollow(userId: ObjectId, followObjId:ObjectId): Boolean ={
    val isFollow = dao.findOne(MongoDBObject("userId" -> userId, "followObjId" -> followObjId))
    return isFollow.nonEmpty
  }
  
  /**
   *根据用户名检验是否已关注或收藏并且有效 
   */
  def checkIfFollowByName(userName:String,followObjId:ObjectId): Boolean={
    val userId = User.findOneByUserId(userName).get.id
    checkIfFollow(userId,followObjId)
  }
}
