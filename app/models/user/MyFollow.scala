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

case class MyFollow(
  id: ObjectId = new ObjectId,
  userId: ObjectId,
  followObjId: ObjectId,
  followObjType: String   //关注店铺:salon；关注技师:stylist；收藏风格:style；收藏优惠劵:coupon；收藏博客:blog;关注用户:user
  )

object MyFollow extends ModelCompanion[MyFollow, ObjectId] {

  val dao = new SalatDAO[MyFollow, ObjectId](collection = mongoCollection("MyFollow")) {}

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
     List[ObjectId] = dao.find(MongoDBObject("followObjType" -> followObjType, "userId" -> userId)).toList.map { MyFollow => MyFollow.followObjId }

  /**
   *添加收藏或关注 
   */
  def create(userId: ObjectId,followObjId: ObjectId, followObjType: String) {
    dao.insert(MyFollow(userId = userId,followObjId = followObjId,followObjType = followObjType))
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
  
  /**
   * 获取关注收藏信息
   */
  def getAllFollowInfo(id:ObjectId): FollowInfomation = {
    //关注的店铺列表
    val salonIdL: List[ObjectId] = getAllFollowObjId(FOLLOWSALON, id)
    val salonL = salonIdL.map(salonId =>
    	Salon.findById(salonId).get
    )
    //关注的技师列表
    val stylistIdL: List[ObjectId] = getAllFollowObjId(FOLLOWSTYLIST, id)
    val stylistL = stylistIdL.map(stylistId =>
    	Stylist.findOneById(stylistId).get
    )
    //关注的用户
    val userIdL: List[ObjectId] = getAllFollowObjId(FOLLOWUSER, id)
    val userL = userIdL.map(userId =>
    	User.findOneById(userId).get
    )
    
    val followerIdL: List[ObjectId] = getFollowers(id)
    val followerL = followerIdL.map(userId =>
    	User.findOneById(userId).get
    )
    
    val couponIdL: List[ObjectId] = getAllFollowObjId(FOLLOWCOUPON, id)
    val couponL = couponIdL.map(couponId =>
    	Coupon.findOneById(couponId).get
    )
    
    val blogIdL: List[ObjectId] = getAllFollowObjId(FOLLOWBLOG, id)
    val blogL = blogIdL.map(blogId =>
    	Blog.findOneById(blogId).get
    )
    
    val styleIdL: List[ObjectId] = getAllFollowObjId(FOLLOWSTYLE, id)
    val styleL = styleIdL.map(styleId =>
    	Style.findOneByID(styleId).get
    )
    
    FollowInfomation(salonL,stylistL,userL,couponL,blogL,styleL,followerL)
  }
}

case class FollowInfomation(
     followSalon: List[Salon],
     followStylist: List[Stylist],
     followUser: List[User],
     followCoupon:List[Coupon],
     followBlog: List[Blog],
     followStyle: List[Style],
     follower:List[User]
)     
