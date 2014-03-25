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



    
case class BlogCategory(
  id : ObjectId = new ObjectId,
  categoryName : String,
  isValid : Boolean
)

object BlogCategory extends ModelCompanion[BlogCategory, ObjectId] {
  val dao = new SalatDAO[BlogCategory, ObjectId](collection = mongoCollection("BlogCategory")) {}
  
  /**
   * 取得分类
   */
  def getCategory() = {
    var list : List[String] = Nil
    val category = dao.find(MongoDBObject("isValid" -> true)).toList
    if(!category.isEmpty){
      category.foreach(
        r => list :::= List(r.categoryName)   
      )
    }
    list.reverse
  } 
}