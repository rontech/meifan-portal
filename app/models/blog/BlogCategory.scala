package models

import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import com.meifannet.framework.db._


case class BlogCategory(
  id : ObjectId = new ObjectId,
  categoryName : String,
  isValid : Boolean
)

object BlogCategory extends MeifanNetModelCompanion[BlogCategory] {
  val dao = new MeifanNetDAO[BlogCategory](collection = loadCollection()){}
  
  /**
   * 取得分类
   */
  def getCategory = {
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