package models

import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import com.meifannet.framework.db._
import mongoContext._

case class FollowType(
		id: ObjectId = new ObjectId,
		followTypeName : String
		)

object FollowType extends MeifanNetModelCompanion[FollowType]{

  val FOLLOW_SALON = "salon"
  val FOLLOW_STYLIST = "stylist"
  val FOLLOW_USER = "user"
  val FOLLOW_STYLE = "style"
  val FOLLOW_BLOG = "blog"
  val FOLLOW_COUPON = "coupon"

    val dao = new MeifanNetDAO[FollowType](collection = loadCollection()){}
	
	def addFollowType(FollowType : FollowType) = dao.save(FollowType, WriteConcern.Safe)
	
	def getFollowTypeList : List[String] = dao.find(MongoDBObject.empty).toList.map {
		FollowType =>FollowType.followTypeName
	}
}
