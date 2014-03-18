package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.PlayException

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
      def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("User")
      val dao = new SalatDAO[User, ObjectId](collection){}
      
      
      // Indexes
      collection.ensureIndex(DBObject("userId" -> 1), "userId", unique = true)
      
      // Queries
      // Find a user according to userId
      def findOneByUserId(userId: String): Option[User] = dao.findOne(MongoDBObject("userId" -> userId))
      
      // Find a user according to ObjectId
      def findById(id: ObjectId): Option[User] = dao.findOne(MongoDBObject("_id" -> id))
      
      //
      def findByNickNm(nickNm: String) = dao.find(MongoDBObject("nickNm" -> nickNm))
      def findByEmail(email: String) = dao.find(MongoDBObject("email" -> email))
      
      // Check the password when logining
      def authenticate(userId: String, password: String): Option[User] = dao.findOne(MongoDBObject("userId" -> userId, "password" -> password))
}
