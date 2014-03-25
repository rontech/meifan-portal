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
import scala.collection.mutable.ListBuffer

case class MyFollows(
  id: ObjectId = new ObjectId,
  userId: ObjectId,
  followObjId: ObjectId,
  followObjType: String   //关注店铺:salon；关注技师:stylist；收藏风格:style；收藏优惠劵:coupon；收藏博客:blog;关注用户:user
  )

object MyFollows extends ModelCompanion[MyFollows, ObjectId] {

  val dao = new SalatDAO[MyFollows, ObjectId](collection = mongoCollection("MyFollows")) {}

  val FOLLOWSALON = "salon"
  val FOLLOWSTYLIST = "stylist"
  val FOLLOWUSER= "user"
  val FOLLOWSTYLE="style"
  val FOLLOWBLOG="blog"
  val FOLLOWCOUPON="coupon"
  
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
  
  def getAllFollowInfo(id:ObjectId): FollowInfomation = {
    val salonIdList: List[ObjectId] = MyFollows.getAllFollowObjId(FOLLOWSALON,id)
    val salonList = ListBuffer[Salon]()
    for (i <- 0 to salonIdList.length - 1) {
      val salon = Salon.findById(salonIdList(i)).get
      salonList += salon
    }
    val stylistIdList: List[ObjectId] = MyFollows.getAllFollowObjId(FOLLOWSTYLIST,id)
    val stylistList = ListBuffer[Stylist]()
    for (i <- 0 to stylistIdList.length - 1) {
      val stylist = Stylist.findOneById(stylistIdList(i)).get
      stylistList += stylist
    }
    val userIdList: List[ObjectId] = MyFollows.getAllFollowObjId(FOLLOWUSER,id)
    val userList = ListBuffer[User]()
    for (i <- 0 to userIdList.length - 1) {
      val followUser = User.findOneById(userIdList(i)).get
      userList += followUser
    } 
    FollowInfomation(salonList.toList,stylistList.toList,userList.toList)
     }
}

case class FollowInfomation(
     followSalon: List[Salon],
     followStylist: List[Stylist],
     followUser: List[User]
//     followCoupon:List[Coupon],
//     followBlog: List[Blog],
//     followStyle: List[Style]
)     
