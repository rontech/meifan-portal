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



    
case class Blog(
    id : ObjectId = new ObjectId,
    userId: ObjectId, 
    createdTime: Date = new Date, 
    status : Int, 
    title : String, 
    blogTyp : String,
    tags : String,
    content : String)

object Blog extends ModelCompanion[Blog, ObjectId] {
  val dao = new SalatDAO[Blog, ObjectId](collection = mongoCollection("blog")) {}
  
  def newBlog(userId : ObjectId, title : String, content : String, blogTyp : String,tags: String) = {
    dao.save(Blog(userId = userId, status = 0, title = title, blogTyp = blogTyp, tags = tags, content = content))    
  }
  
  def modBlog(blogId : ObjectId, title : String, content : String, blogTyp : String,tags: String) = {
    val blog = findById(blogId).get
    dao.save(Blog(id = blogId,userId = blog.userId, status = blog.status, title = title, blogTyp = blogTyp, tags = tags, content = content))    
  }
  
  def showBlog(userId : ObjectId) = {
    val blog = dao.find(MongoDBObject("userId" -> userId, "status" -> 0)).toList
    blog
  }
  

  def showBlogById(id : ObjectId) = {
    val blog = dao.find(MongoDBObject("_id" -> id, "status" -> 0)).toList
    blog
  }
  
  def findByUserId(userId : ObjectId) : List[Blog] = {
    dao.find(MongoDBObject("userId" -> userId, "status" -> 0)).toList
  }
  
  def delete(id : ObjectId) = {
    val blog = dao.findOneById(id).get
    dao.save(Blog(id = id, userId = blog.userId, status = 1, title = blog.title, blogTyp = blog.blogTyp, tags = blog.tags, content = blog.content), WriteConcern.Safe)
  }
  
  def findBySalon(salonId: ObjectId, blogId: ObjectId): Option[Blog] = {
    val stylist = Stylist.findBySalon(salonId)
    var blog : Option[Blog] = None
    stylist.foreach(
      {
      r => 
      blog = Blog.findOne(DBObject("userId" -> r.id, "_id" -> blogId))
      if(blog != None)
        return blog  
      }
   )
   blog
   }
  
  var bloglist : List[Blog] = Nil
  def findBySalon(salonId: ObjectId): List[Blog] = {
    val stylist = Stylist.findBySalon(salonId)
    var blog : List[Blog] = Nil
    stylist.foreach(
      {
      r => 
      blog = Blog.find(DBObject("userId" -> r.id)).toList
      if(!blog.isEmpty)
//        blog :::= bloglist
          bloglist :::= blog
      }
    )
    bloglist
  }
  
  /**
   * 修改分类后，如果该分类下有blog，那么blog表中该用户的这个分类下的blog的类型要变化成新的的类型
   */
  def modCatagory(userId : ObjectId, blogCatagory : String, catagory : String) = {
    val list = dao.find(MongoDBObject("userId" -> userId, "blogTyp" -> blogCatagory)).toList
    if(list.isEmpty){
      
    } 
    else{
      list.foreach({
        r => dao.save(Blog(id = r.id, userId = userId, status = 0, title = r.title, blogTyp = catagory, tags = r.tags, content = r.content), WriteConcern.Safe)
      })
    }
  }
  
  /**
   * 删除分类后，如果该分类下有blog，那么blog表中该用户的这个分类下的blog的类型要变化成特定的类型
   * （现在暂定“选择分类”）
   * 选择分类  这个类型就是指代的是未分类的blog
   */
  def delBlogCatarory(userId : ObjectId, blogCatagory : String) = {
    val list = dao.find(MongoDBObject("userId" -> userId, "blogTyp" -> blogCatagory)).toList
    if(list.isEmpty){
      
    } 
    else{
      list.foreach({
        r => dao.save(Blog(id = r.id, userId = userId, status = 0, title = r.title, blogTyp = "选择分类", tags = r.tags, content = r.content), WriteConcern.Safe)
      })
    }
  }
  
  /**
   * 统计blog各个分类的数量
   */
  def getBlogCountByCatagory(userId : ObjectId, catagory : String) : Int= {
    if(catagory == "全部博文") {
      return dao.find(MongoDBObject("userId" -> userId, "status" -> 0)).toList.size
    }
    if(catagory == "未分类博文") {
      return  dao.find(MongoDBObject("userId" -> userId, "blogTyp" -> "选择分类", "status" -> 0)).toList.size
    }
    else
       return dao.find(MongoDBObject("userId" -> userId, "blogTyp" -> catagory, "status" -> 0)).toList.size
  }
  
  /**
   * 查看各个分类下的blog
   */
  def showBlogsByCatagory(userId : ObjectId, catagory : String) : List[Blog]= {
    if(catagory == "全部博文") {
      return dao.find(MongoDBObject("userId" -> userId, "status" -> 0)).toList
    }
    if(catagory == "未分类博文") {
      return  dao.find(MongoDBObject("userId" -> userId, "blogTyp" -> "选择分类", "status" -> 0)).toList
    }
    else
       return dao.find(MongoDBObject("userId" -> userId, "blogTyp" -> catagory, "status" -> 0)).toList
  }
  
  def findById(id: ObjectId): Option[Blog] = {
    dao.findOne(MongoDBObject("_id" -> id))
  }
  
  /**
   * 修改单个blog的分类
   */
  def changeCatagory(blogId : ObjectId,blogTyp : String) = {
    val blog = findById(blogId).get
    dao.save(Blog(id = blogId, userId = blog.userId, status = blog.status, title = blog.title, blogTyp = blogTyp, tags = blog.tags, content = blog.content), WriteConcern.Safe)
  }

}