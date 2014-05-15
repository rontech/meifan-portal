/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
package models

import java.io.File
import com.mongodb.casbah.gridfs.GridFS
import com.mongodb.casbah.commons.Imports._
import mongoContext._
import java.io.InputStream
import java.io.ByteArrayOutputStream
import com.meifannet.framework.db._


object ImageDAO extends MeifanNetDAO[Image](
  collection = DBDelegate.db("Picture"))

case class Image(
  id: ObjectId,
  file: File,
  label: String)


object Image {
  import com.mongodb.casbah.Implicits._
  val db = DBDelegate.picDB

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
  
  def fileToBytes(inStream: InputStream) : Array[Byte] = {
  val outStream = new ByteArrayOutputStream
  try {
   var reading = true
   while ( reading ) {
    inStream.read() match {
     case -1 => reading = false
     case c => outStream.write(c)
    }
   }
   outStream.flush()
  }
  finally
  {
   inStream.close()
  }

  return outStream.toByteArray
  }  
}

case class ImgForCrop(
         x1:BigDecimal,
         y1:BigDecimal,
         x2:BigDecimal,
         y2:BigDecimal,
         w:BigDecimal,
         h:BigDecimal)
