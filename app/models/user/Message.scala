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

case class Message(
		id : ObjectId,
		title: String,
		content: String,
		createdTime: Date
)

object Message extends ModelCompanion[Message, ObjectId] {

  val dao = new SalatDAO[Message, ObjectId](collection = mongoCollection("Message")) {}
  
  def findById(id: ObjectId): Option[Message] = dao.findOne(MongoDBObject("_id" -> id))
}