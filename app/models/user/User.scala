package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.PlayException
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

case class User(
  id: ObjectId = new ObjectId,
  userId: String,
  nickName: String,
  password: String,
  sex: String,
  birthDay: Date,
  city: String,
  userPics: ObjectId,
  tel: String,
  email: String,
  optContactMethods: Seq[OptContactMethod],
  socialStatus: String,
  userTyp: String,
  userBehaviorLevel: String,
  point: Int,
  registerTime: Date,
  permission: String,
  isValid: Boolean)

object User extends UserDAO

trait UserDAO extends ModelCompanion[User, ObjectId] {
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("User")
  val dao = new SalatDAO[User, ObjectId](collection) {}

  // Indexes
  collection.ensureIndex(DBObject("userId" -> 1), "userId", unique = true)

  // Queries
  // Find a user according to userId
  def findOneByUserId(userId: String): Option[User] = dao.findOne(MongoDBObject("userId" -> userId))

  //
  def findOneByNickNm(nickName: String) = dao.findOne(MongoDBObject("nickName" -> nickName))
  def findOneByEmail(email: String) = dao.findOne(MongoDBObject("email" -> email))

  // Check the password when logining
  def authenticate(userId: String, password: String): Option[User] = dao.findOne(MongoDBObject("userId" -> userId, "password" -> password))
  
  /**
   * 权限认证
   * 用于判断userId是否为当前用户
   */
  def isOwner(userId:String)(user:User) : Future[Boolean] = Future{User.findOneByUserId(userId).map(_ == user).get}
}
