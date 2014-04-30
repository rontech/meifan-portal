package models

import java.util.Date
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import org.mindrot.jbcrypt.BCrypt
import com.meifannet.framework.db._

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
    registerTime: Long,
    permission: String,
    isValid: Boolean)

object User extends MeifanNetModelCompanion[User] {

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

    val dao = new MeifanNetDAO[User](collection = loadCollection()){}

    // Indexes
    collection.ensureIndex(DBObject("userId" -> 1), "userId", unique = true)

    // Queries
    // Find a user according to userId
    def findOneByUserId(userId: String) = dao.findOne(MongoDBObject("userId" -> userId))
    /**
     * 昵称不可以重复
     */
    def findOneByNickNm(nickName: String) = dao.findOne(MongoDBObject("nickName" -> nickName))
    
    /**
     * 邮箱不可以重复
     */
    def findOneByEmail(email: String) = dao.findOne(MongoDBObject("email" -> email))
    def findOneByTel(tel: String) = dao.findOne(MongoDBObject("tel" -> tel))

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

    /**
     * checks for accountId
     * @param value
     * @param f
     * @return
     */
    def isExist(value:String,
                f:String => Option[User]) = f(value).map(user => true).getOrElse(false)

    /**
     * checks for nickName,email,tel
     * @param value
     * @param loggedUser
     * @param f
     * @return
     */
    def isValid(value:String,
                loggedUser:Option[User],
                f:String => Option[User]) =
        f(value).map(
            user =>
                loggedUser.map(_.id ==user.id).getOrElse(false)
            ).getOrElse(true)
}