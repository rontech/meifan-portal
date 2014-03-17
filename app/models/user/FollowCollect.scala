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

case class FollowCollect(
  id: ObjectId = new ObjectId,
  userId: ObjectId,
  followCollectAtId: ObjectId,
  relationTypeId: Int,    //1、关注店铺；2、关注技师；3、收藏风格；4、收藏优惠劵；5、收藏博客;6、关注用户
  status: Boolean  	      //0、无效；1、有效
  )

object FollowCollect extends ModelCompanion[FollowCollect, ObjectId] {

  val dao = new SalatDAO[FollowCollect, ObjectId](collection = mongoCollection("FollowCollect")) {}

  //根据用户Id和关系类型获取有效被关注或收藏的对象
  def getAllFollowCollectAtId(relationTypeId: Int, userId: ObjectId): 
     List[ObjectId] = dao.find(MongoDBObject("relationTypeId" -> relationTypeId, "userId" -> userId, "status" -> true)).toList.map { userFollowCollect => userFollowCollect.followCollectAtId }
  
  //添加收藏或关注
  def create(userId: ObjectId,followCollectAtId: ObjectId, relationTypeId: Int) {
    dao.insert(FollowCollect(userId = userId,followCollectAtId = followCollectAtId,relationTypeId = relationTypeId,status = true))
  }
  
  //取消收藏或关注
  def delete(userId: ObjectId,followCollectAtId: ObjectId, relationTypeId: Int) {
      dao.update(MongoDBObject("relationTypeId" -> relationTypeId, "userId" -> userId, "followCollectAtId" -> followCollectAtId),MongoDBObject("relationTypeId" -> relationTypeId, "userId" -> userId, "followCollectAtId" -> followCollectAtId,"status" ->false))
  }
  
  //查看我被关注的对象，即我的粉丝
  def getFollowers(userId:ObjectId):
	  List[ObjectId] = dao.find(MongoDBObject("relationTypeId" -> 6, "followCollectAtId" -> userId, "status" -> true)).toList.map { userFollowCollect => userFollowCollect.userId }
  
  //检验是否已关注或收藏
  def checkIfFollow(userId: ObjectId, followCollectAtId:ObjectId): Boolean ={
    val isFollow = dao.findOne(MongoDBObject("userId" -> userId, "followCollectAtId" -> followCollectAtId))
    return isFollow.nonEmpty
  }
}
