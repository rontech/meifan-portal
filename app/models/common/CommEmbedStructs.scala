package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.Context
import mongoContext._
import org.bson.types.ObjectId

/**
 * Embed Structure.
*/
case class OnUsePicture(
    fileObjId: ObjectId = new ObjectId,
    picUse: String,                           // Ref to the PictureUse Master Table, but we only use the [picUseName] field as a key.
    showPriority: Option[Int],
    description: Option[String]
)

trait OnUsePictureDAO extends ModelCompanion[OnUsePicture, ObjectId] {
      def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("OnUsePicture")
  
  val dao = new SalatDAO[OnUsePicture, ObjectId](collection){}
      
  collection.ensureIndex(DBObject("_id" -> 1), "id", unique = true)   
  
}


/**
 * Embed Structure.
*/
case class OptContactMethod (
    contMethmodType: String,
    account: List[String]
)
