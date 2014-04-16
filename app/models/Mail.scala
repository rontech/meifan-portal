package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._

case class Mail (id: ObjectId = new ObjectId,
			  uuid: String,
			  objId : ObjectId,
			  endTime : Date = new Date,
			  objType : Int 
			  )

object Mail extends ModelCompanion[Mail, ObjectId]{
  val dao = new SalatDAO[Mail, ObjectId](collection = mongoCollection("Mail")){}
  
  def save(uuid : String, objId : ObjectId, objType : Int, endTime : Date) = {
    dao.save(Mail(uuid = uuid, objId = objId, objType = objType, endTime = endTime))
  } 
  
  def findOneByObjId(objId : ObjectId) = {
    dao.findOne(MongoDBObject("objId" -> objId))
  }
  
  def findOneByUuid(uuid : String) = {
    dao.findOne(MongoDBObject("uuid" -> uuid))
  }
  

}