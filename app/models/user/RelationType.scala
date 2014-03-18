package models

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import se.radley.plugin.salat.Binders._

case class RelationType(
		id: ObjectId = new ObjectId,
		relationTypeName : String,
		relationTypeId : Int
		)

object RelationType extends ModelCompanion[RelationType, ObjectId]{
	val dao = new SalatDAO[RelationType, ObjectId](collection = mongoCollection("RelationType")){}
	
	def addRelationType(relationType : RelationType) = dao.save(relationType, WriteConcern.Safe)
	
	def getRelationTypeList : List[String] = dao.find(MongoDBObject.empty).toList.map {
		relationType =>relationType.relationTypeName
	}
	
	def relationTypeNewId: Int = {
	 val idList = dao.find(MongoDBObject.empty).toList.map {relationType =>relationType.relationTypeId}
	 if(idList.isEmpty){
	   return 1
	 }else{
	   return idList.last + 1
	 }	 
	}
}
