package models

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import se.radley.plugin.salat.Binders._

case class FollowType(
		id: ObjectId = new ObjectId,
		followTypeName : String
		)

object FollowType extends ModelCompanion[FollowType, ObjectId]{
	val dao = new SalatDAO[FollowType, ObjectId](collection = mongoCollection("FollowType")){}
	
	def addFollowType(FollowType : FollowType) = dao.save(FollowType, WriteConcern.Safe)
	
	def getFollowTypeList : List[String] = dao.find(MongoDBObject.empty).toList.map {
		FollowType =>FollowType.followTypeName
	}
}
