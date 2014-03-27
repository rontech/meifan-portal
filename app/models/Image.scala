package models

import java.io.File
import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.gridfs.GridFS
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._

case class Image(
	id: ObjectId,	
    file: File,
    label: String
)

object ImageDAO extends SalatDAO[Image, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Picture"))
  
object Image {
	import com.mongodb.casbah.Implicits._
   val db = MongoConnection()("Picture")
   val gridFs = GridFS(db)
   def findById(file: ObjectId) = {
    gridFs.findOne(Map("_id" -> file)) 
	}
	def findAll() = {
	  gridFs.find(MongoDBObject.empty).toList
	}
	def save(file: File): ObjectId = {
	  val uploadedFile = gridFs.createFile(file)
	  uploadedFile.save()
	  uploadedFile._id.get
	}
	/**
	 * 将文件夹下所有图片都存放至文件集合
	 */
	var files:List[File] = Nil
	def listAllFiles(file: File):List[File] = {
	  if(!file.isDirectory){
	      files:::=List(file)
	    }
	  if(file.isDirectory) {
	    val subFiles = file.listFiles()
	    for(sub <- subFiles){
	      listAllFiles(sub)
	    }
	  }
	  files
	}
}
