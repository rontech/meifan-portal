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
package models.portal.common

import java.io.File
import com.mongodb.casbah.gridfs.GridFS
import com.mongodb.casbah.commons.Imports._
import mongoContext._
import java.io.InputStream
import java.io.ByteArrayOutputStream
import com.meifannet.framework.db._

/*
object ImageDAO extends MeifanNetDAO[Image](
  collection = DBDelegate.db("Picture"))
*/

/**
 *
 * @param id
 * @param file
 * @param label
 */
case class Image(
  id: ObjectId,
  file: File,
  label: String)

object Image {
  import com.mongodb.casbah.Implicits._
  val db = DBDelegate.picDB

  val gridFs = GridFS(db)

  /**
   *
   * @param file
   * @return
   */
  def findById(file: ObjectId) = {
    gridFs.findOne(Map("_id" -> file))
  }

  /**
   *
   * @return
   */
  def findAll() = {
    gridFs.find(MongoDBObject.empty).toList
  }

  /**
   * get the file by name, fuzzy search.
   *
   * @param name
   * @return
   */
  def fuzzyFindByName(name: String) = {
    //gridFs.find(com.mongodb.casbah.query.Imports.MongoDBObject("filename" -> name)).toList
    //gridFs.find(MongoDBObject("filename" -> name)).toList
    //gridFs.find(com.mongodb.casbah.query.Imports.MongoDBObject("filename" -> (".*" + name + ".*").r)).toList
    gridFs.find(MongoDBObject("filename" -> (".*" + name + ".*").r)).toList
  }

  /**
   *
   * @param file
   * @return
   */
  def save(file: File): ObjectId = {
    val uploadedFile = gridFs.createFile(file)
    uploadedFile.save()
    uploadedFile._id.get
  }

  /**
   * 将文件夹下所有图片都存放至文件集合
   *
   * @return
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
   * @param folder
   * @return
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

  /**
   *
   * @param inStream
   * @return
   */
  def fileToBytes(inStream: InputStream): Array[Byte] = {
    val outStream = new ByteArrayOutputStream
    try {
      var reading = true
      while (reading) {
        inStream.read() match {
          case -1 => reading = false
          case c => outStream.write(c)
        }
      }
      outStream.flush()
    } finally {
      inStream.close()
    }

    return outStream.toByteArray
  }
}

/**
 * 图片处理相关的辅助类: 用于图片剪裁的结构
 *
 * @param x1
 * @param y1
 * @param x2
 * @param y2
 * @param w
 * @param h
 */
case class ImgForCrop(
  x1: BigDecimal,
  y1: BigDecimal,
  x2: BigDecimal,
  y2: BigDecimal,
  w: BigDecimal,
  h: BigDecimal)
