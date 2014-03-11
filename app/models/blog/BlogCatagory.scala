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
  userId : ObjectId,
  catagory : List[String],
  status : Int
  )

object BlogCatagory extends ModelCompanion[BlogCatagory, ObjectId] {
  val dao = new SalatDAO[BlogCatagory, ObjectId](collection = mongoCollection("blogCatagory")) {}
  
  /**
   *  创建新的分类  status=0代表新建
   */ 
  var list : List[String] = Nil
  def newBlogCatagory(userId : ObjectId, catagory : String) = {
    
//    catagory :: list
    list = getCatagory(userId)
    list :::= List(catagory)
//    println("----------" + getIdByUserId(userId))
//    
////    println("++++++" + new ObjectId(getIdByUserId(userId).toString))
    if (getIdByUserId(userId).equals(None)){
      dao.save(BlogCatagory(userId = userId, status = 0, catagory = list)) 
    }else{
      dao.save(BlogCatagory(id = new ObjectId(getIdByUserId(userId).toString), userId = userId, status = 0, catagory = list.distinct)) 
    }

  }
  
  /**
   * 通过userId取得主键
   */
  def getIdByUserId(userId : ObjectId) = {
    val id = dao.find(MongoDBObject("userId" -> userId))
    if(id.hasNext){
       id.next.id
    }else
      None     
  }
  
  /**
   * 取得分类
   */
  // 这个好像有问题啊，和下面的方法有点重复啊
  def getCatagory(userId : ObjectId) : List[String] = {
    val catagory = dao.find(MongoDBObject("userId" -> userId))
     if(catagory.hasNext){
       catagory.next.catagory
    }else
      Nil 
  }
  
  /**
   * 查找分类
   */
  def findBlogCatagory(userId : ObjectId) = {
    dao.find(MongoDBObject("userId" -> userId)).toList
  }
  
  /**
   * 删除分类
   */
  def delBlogCatagory(userId : ObjectId, blogCatagory : String) = {
	val listCatagory = getCatagory(userId)
	val newLisrCatagory = listCatagory.diff(List(blogCatagory))
	if(newLisrCatagory.isEmpty){
	  dao.removeById(new ObjectId(getIdByUserId(userId).toString()), WriteConcern.Safe)
	}
	else
	  dao.save(BlogCatagory(id = new ObjectId(getIdByUserId(userId).toString), userId = userId, status = 0, catagory = newLisrCatagory))
	
  }
  
  /**
   * 修改分类
   */
  def modCatagory(userId : ObjectId, blogCatagory : String, catagory : String) = {
    delBlogCatagory(userId, blogCatagory)
    newBlogCatagory(userId, catagory)
    
  }
  
}