package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import models._



    
case class BlogCatagory(
  id : ObjectId = new ObjectId,
  userId : String,
  catagory : List[String],
  status : Int,
  isCommon : Int
  )

object BlogCatagory extends ModelCompanion[BlogCatagory, ObjectId] {
  val dao = new SalatDAO[BlogCatagory, ObjectId](collection = mongoCollection("BlogCatagory")) {}
  
  /**
   *  创建新的分类  status=0代表新建
   */ 
  var list : List[String] = Nil
  def newBlogCatagory(userId : String, catagory : String) = {
    

//    list = findOneByUserId(userId).get
    if(!findOneByUserId(userId).isEmpty){
      list = findOneByUserId(userId).get.catagory
    }
    list :::= List(catagory)

    if (findOneByUserId(userId).isEmpty){
      dao.save(BlogCatagory(userId = userId, catagory = list, status = 0, isCommon = 1)) 
    }else{
      dao.save(BlogCatagory(id = findOneByUserId(userId).get.id, userId = userId, status = 0, catagory = list.distinct, isCommon = 1)) 
    }

  }
//  
//  /**
//   * 通过userId取得主键
//   */
//  def getIdByUserId(userId : ObjectId) = {
//    val id = dao.find(MongoDBObject("userId" -> userId))
//    if(id.hasNext){
//       id.next.id
//    }else
//      None     
//  }
//  
//  /**
//   * 取得分类
//   */
//  // 这个好像有问题啊，和下面的方法有点重复啊
//  def getCatagory(userId : ObjectId) : List[String] = {
//    val catagory = dao.find(MongoDBObject("userId" -> userId))
//     if(catagory.hasNext){
//       catagory.next.catagory
//    }else
//      Nil 
//  }
//  
  /**
   * 查找分类
   */
  def findBlogCatagory(userId : String) = {
    dao.find(MongoDBObject("userId" -> userId)).toList
  }
//  
  /**
   * 删除分类
   */
  def delBlogCatagory(userId : String, blogCatagory : String) = {
	val listCatagory = findOneByUserId(userId).get.catagory
	val newLisrCatagory = listCatagory.diff(List(blogCatagory))
	if(newLisrCatagory.isEmpty){
	  dao.removeById(findOneByUserId(userId).get.id, WriteConcern.Safe)
	}
	else
	  dao.save(BlogCatagory(id = findOneByUserId(userId).get.id, userId = userId, status = 0, catagory = newLisrCatagory, isCommon = 1))
	
  }
  
  /**
   * 修改分类
   */
  def modCatagory(userId : String, blogCatagory : String, catagory : String) = {
    delBlogCatagory(userId, blogCatagory)
    newBlogCatagory(userId, catagory)
    
  }
  
  def findOneByUserId(userId : String) = {
    dao.findOne(MongoDBObject("userId" -> userId))
//    val p = dao.findOne(MongoDBObject("userId" -> userId))
//    p match {
//      case Some(blogCatagory) => blogCatagory
//      case None => "Not exits BlogCatagory."
//    }
  }
  
  def getCommonCatagory() = {
    dao.findOne(MongoDBObject("isCommon" -> 0))
  }
  
  def getAllCatagory(userId : String) = {
    var list : List[String] = Nil
    if(!findOneByUserId(userId).isEmpty){
      list = findOneByUserId(userId).get.catagory
    }
    else 
      list = Nil
    list :::= getCommonCatagory.get.catagory
    list
  }
  
}