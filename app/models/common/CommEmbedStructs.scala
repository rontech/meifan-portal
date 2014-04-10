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
import java.io.File
import com.mongodb.casbah.gridfs.Imports._
import com.mongodb.casbah.gridfs.GridFS
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import play.api.i18n.Messages

/**
 * Embed Structure.
*/
case class OnUsePicture(
    fileObjId: ObjectId,
    picUse: String,                           // Ref to the PictureUse Master Table, but we only use the [picUseName] field as a key.
    showPriority: Option[Int],
    description: Option[String]
)

object OnUsePicture extends OnUsePictureDAO

trait OnUsePictureDAO extends ModelCompanion[OnUsePicture, ObjectId] {
      def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("OnUsePicture")
  
  val dao = new SalatDAO[OnUsePicture, ObjectId](collection){}
      
  collection.ensureIndex(DBObject("fileObjId" -> 1), "id", unique = true)
  
  
}


/**
 * Embed Structure.
*/
case class OptContactMethod (
    contMethodType: String,
    accounts: List[String]
)

case class ContMethodType (
    id: ObjectId = new ObjectId,
	contMethodTypeName : String,
	description : String
	)
	

object ContMethodType extends ContMethodTypeDAO

trait ContMethodTypeDAO extends ModelCompanion[ContMethodType, ObjectId] {
    def collection = MongoConnection()(
        current.configuration.getString("mongodb.default.db")
            .getOrElse(throw new PlayException(
                "Configuration error",
                "Could not find mongodb.default.db in settings")))("ContMethodType")

    val dao = new SalatDAO[ContMethodType, ObjectId](collection) {}
    
    def getAllContMethodTypes  = dao.find(MongoDBObject.empty).toSeq.map{
        	contMethodType => contMethodType.contMethodTypeName ->Messages("ContMethodType.contMethodTypeName."+ contMethodType.contMethodTypeName)}
}