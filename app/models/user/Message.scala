package models

import java.util.Date
import mongoContext._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import com.meifannet.framework.db._

case class Message(
		id : ObjectId,
		title: String,
		content: String,
		createdTime: Date
)

object Message extends MeifanNetModelCompanion[Message] {

  val dao = new MeifanNetDAO[Message](collection = loadCollection()){}
  
  def findById(id: ObjectId): Option[Message] = dao.findOne(MongoDBObject("_id" -> id))
}