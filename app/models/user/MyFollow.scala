package models

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import scala.collection.mutable.ListBuffer

case class MyFollow(
  id: ObjectId = new ObjectId,
  userId: ObjectId,
  followObjId: ObjectId,
  followObjType: String   //关注店铺:salon；关注技师:stylist；收藏风格:style；收藏优惠劵:coupon；收藏博客:blog;关注用户:user
  )

object MyFollow extends ModelCompanion[MyFollow, ObjectId] {

  val dao = new SalatDAO[MyFollow, ObjectId](collection = mongoCollection("MyFollow")) {}

  val FOLLOW_SALON = "salon"
  val FOLLOW_STYLIST = "stylist"
  val FOLLOW_USER= "user"
  val FOLLOW_STYLE="style"
  val FOLLOW_BLOG="blog"
  val FOLLOW_COUPON="coupon"
  
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
	  List[ObjectId] = dao.find(MongoDBObject("followObjType" -> FOLLOW_USER, "followObjId" -> userId)).toList.map { myFollowers => myFollowers.userId }
  
  /**
   *检验是否已关注或收藏 
   */
  def checkIfFollow(userId: ObjectId, followObjId:ObjectId): Boolean ={
    val isFollow = dao.findOne(MongoDBObject("userId" -> userId, "followObjId" -> followObjId))
    isFollow.nonEmpty
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
  def getAllFollowInfo(id:ObjectId): FollowInformation = {
    //关注的店铺列表
    val salonIdL: List[ObjectId] = getAllFollowObjId(FOLLOW_SALON, id)
    val salonL = salonIdL.map(salonId =>
    	Salon.findById(salonId).get
    )
    //关注的技师列表
    val stylistIdL: List[ObjectId] = getAllFollowObjId(FOLLOW_STYLIST, id)
    val stylistL = stylistIdL.map(stylistId =>
    	Stylist.findOneById(stylistId).get
    )
    //关注的用户
    val userIdL: List[ObjectId] = getAllFollowObjId(FOLLOW_USER, id)
    val userL = userIdL.map(userId =>
    	User.findOneById(userId).get
    )
    
    val followerIdL: List[ObjectId] = getFollowers(id)
    val followerL = followerIdL.map(userId =>
    	User.findOneById(userId).get
    )
    
    val couponIdL: List[ObjectId] = getAllFollowObjId(FOLLOW_COUPON, id)
    val couponL = couponIdL.map(couponId =>
    	Coupon.findOneById(couponId).get
    )
    
    val blogIdL: List[ObjectId] = getAllFollowObjId(FOLLOW_BLOG, id)
    val blogL = blogIdL.map(blogId =>
    	Blog.findOneById(blogId).get
    )
    
    val styleIdL: List[ObjectId] = getAllFollowObjId(FOLLOW_STYLE, id)
    val styleL = styleIdL.map(styleId =>
    	Style.findOneById(styleId).get
    )
    
    FollowInformation(salonL,stylistL,userL,couponL,blogL,styleL,followerL)

  }
}

case class FollowInformation(
     followSalon: List[Salon],
     followStylist: List[Stylist],
     followUser: List[User],
     followCoupon:List[Coupon],
     followBlog: List[Blog],
     followStyle: List[Style],
     follower:List[User]
)     
