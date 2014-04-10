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
  label: String)

object ImageDAO extends SalatDAO[Image, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("Picture"))

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

  /**
   * get the file by name, fuzzy search.
   */
  def fuzzyFindByName(name: String) = {
    //gridFs.find(com.mongodb.casbah.query.Imports.MongoDBObject("filename" -> name)).toList
    //gridFs.find(MongoDBObject("filename" -> name)).toList
    //gridFs.find(com.mongodb.casbah.query.Imports.MongoDBObject("filename" -> (".*" + name + ".*").r)).toList
    gridFs.find(MongoDBObject("filename" -> (".*" + name + ".*").r)).toList
  }


  def save(file: File): ObjectId = {
    val uploadedFile = gridFs.createFile(file)
    uploadedFile.save()
    uploadedFile._id.get
  }

  /**
   * 将文件夹下所有图片都存放至文件集合 
   */  
  def listFilesRecursively(): File => List[File] = {
    var files: List[File] = Nil

    def listAllFiles(file: File): List[File] = {
      if (!file.isDirectory) {
        files :::= List(file)
      }
      if (file.isDirectory) {
        val subFiles = file.listFiles()
        for (sub <- subFiles) {
          listAllFiles(sub)
        }
      }
      files
    }   

    listAllFiles
  }

  /**
   * 
   */
  def listFilesInFolder(folder: File): List[File] = {
    if (folder.isDirectory) {
      val files = folder.listFiles().toList
//      files.sortWith(_.getName.compareTo(_.getName) < 0)
      val sortfiles = files.sortWith((s, t) => s.getName.compareTo(t.getName) < 0)
      sortfiles.toList
    } else {
      Nil
    }
  } 
}
