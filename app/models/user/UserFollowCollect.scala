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

case class UserFollowCollect(
		id: ObjectId = new ObjectId,
		userId : ObjectId,
		followCollectId : ObjectId,
		relationTypeId : Int
		)

object UserFollowCollect extends ModelCompanion[UserFollowCollect, ObjectId]{

	val dao = new SalatDAO[UserFollowCollect, ObjectId](collection = mongoCollection("UserFollowCollect")){}
	
	def getAllFollowCollectId(relationTypeId : Int, userId : ObjectId) : List[ObjectId] = dao.find(MongoDBObject("relationTypeId" -> relationTypeId, "userId" -> userId)).toList.map{userFollowCollect => userFollowCollect.followCollectId}

}
