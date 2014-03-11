package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._

case class User(
     id: ObjectId = new ObjectId,    
         userId: String,
         password: String,
         nickNm: String,
         birthDay: Date,
         sex: String,
         city: String,
         job: String,
         email: String,
         tel: String,
         qq: String,
         msn: String,
         weChat: String,
         userTyp: String,
         userBehaviorLevel: String,
         point: Int,
         added: Date,
         status: String
         )

object User extends UserDAO

trait UserDAO extends ModelCompanion[User, ObjectId] {
      def collection = mongoCollection("users")
      val dao = new SalatDAO[User, ObjectId](collection) {}

      // Indexes
      collection.ensureIndex(DBObject("userId" -> 1), "userId", unique = true)

      // Queries
      def findOneByUserId(userId: String): Option[User] = dao.findOne(MongoDBObject("userId" -> userId))
      def findByNickNm(nickNm: String) = dao.find(MongoDBObject("nickNm" -> nickNm))
      def findByEmail(email: String) = dao.find(MongoDBObject("email" -> email))
      def authenticate(userId: String, password: String): Option[User] = findOne(MongoDBObject("userId" -> userId, "password" -> password))
      
      def getUserName(id : Object) = 
      {
          val p = dao.findOne(MongoDBObject("_id" -> id))
          p match {
            case Some(user) => user.userId
            case None => "Not Exists User."
          }
      }

      def findId(name : String) = {      
          val p = dao.find(MongoDBObject("userId" -> name))
          val id = p.next.id
          id      
      }
      
      def findById(id: ObjectId): Option[User] = {
        dao.findOne(MongoDBObject("_id" -> id))
    }
}
