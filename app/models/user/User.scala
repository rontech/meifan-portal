package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.PlayException
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import org.mindrot.jbcrypt.BCrypt

case class User(
    id: ObjectId = new ObjectId,
    userId: String,
    nickName: String,
    password: String,
    sex: String,
    birthDay: Option[Date],
    address: Option[Address],
    userPics: ObjectId,
    tel: Option[String],
    email: String,
    optContactMethods: Seq[OptContactMethod],
    socialStatus: Option[String],
    userTyp: String,
    userBehaviorLevel: String,
    point: Int,
    activity: Int,
    registerTime: Date,
    permission: String,
    isValid: Boolean)

object User extends ModelCompanion[User, ObjectId] {

    /*
    * 用户类别
    */

    //普通用户
    val NORMAL_USER = "normalUser"
    //专业技师
    val STYLIST = "stylist"

    /*
    * 用户行为等级
    */

    //高
    val HIGH = "high"
    //中
    val MIDDLE = "middle"
    //底
    val LOW = "low"

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

    /**
     * 昵称不可以重复
     */
    def findOneByNickNm(nickName: String) = dao.findOne(MongoDBObject("nickName" -> nickName))
    
    /**
     * 邮箱不可以重复
     */
    def findOneByEmail(email: String) = dao.findOne(MongoDBObject("email" -> email))

    /**
     * 登录时验证userId和password
     */
    def authenticate(userId: String, password: String): Option[User] = {
        val user = dao.findOne(MongoDBObject("userId" -> userId))
        if (user.nonEmpty && BCrypt.checkpw(password, user.get.password)) {
            return user
        } else {
            return None
        }
    }
    
    /**
     * 通过邮箱重置密码时需要输入以下两个值，判断他们在数据库中是否存在
     */
    def findOneByUserIdAndEmail(userId: String, email: String): Option[User] = {
        dao.findOne(MongoDBObject("userId" -> userId, "email" -> email))
    }

    /**
     * 权限认证
     * 用于判断userId是否为当前用户
     */
    def isOwner(userId: String)(user: User): Future[Boolean] = Future { User.findOneByUserId(userId).map(_ == user).get }

    /**
     * 权限认证
     * 用于判断userId与当前用户是否互相关注(强关系)
     */
    def isFriend(userId: ObjectId)(user: User): Future[Boolean] = Future { (userId == user.id) || MyFollow.followEachOther(userId, user.id) }
    
    /**
     * 用于显示首页中美丽达人
     */
    def findBeautyUsers = dao.find(MongoDBObject("userTyp" -> NORMAL_USER)).toList.sortBy(user =>user.activity)
}
