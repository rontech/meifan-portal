/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 * [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
package models.portal.user

import java.util.Date
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import org.mindrot.jbcrypt.BCrypt
import com.meifannet.framework.db._
import models.portal.common.{OptContactMethod, Address}
import models.portal.reservation.Reservation

/**
 * the class for user
 * @param id        ObjectId of record in mongodb
 * @param userId        loginId for user
 * @param nickName      use for display
 * @param password
 * @param sex
 * @param birthDay
 * @param address
 * @param userPics      logo of user
 * @param tel
 * @param email
 * @param optContactMethods
 * @param socialStatus
 * @param userTyp
 * @param userBehaviorLevel
 * @param point
 * @param activity
 * @param registerTime
 * @param permission
 * @param isValid
 */
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

  val dao = new MeifanNetDAO[User](collection = loadCollection()) {}

  // Indexes
  //collection.ensureIndex(DBObject("userId" -> 1), "userId", unique = true)

  // Queries
  /**
   * Find a user according to userId
   * @param userId login Id/account Id
   * @return
   */
  def findOneByUserId(userId: String) = dao.findOne(MongoDBObject("userId" -> userId))

  /**
   * Find a user according to nickName
   * @param nickName user's nick name
   * @return
   */
  def findOneByNickNm(nickName: String) = dao.findOne(MongoDBObject("nickName" -> nickName))

  /**
   * Find a user according to email
   * @param email user's email
   * @return
   */
  def findOneByEmail(email: String) = dao.findOne(MongoDBObject("email" -> email))

  def findOneByTel(tel: String) = dao.findOne(MongoDBObject("tel" -> tel))

  /**
   * Find a user according to userId and password (for login)
   * @param userId login Id
   * @param password password
   * @return
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
   * Find a user according to userId and email (for reset password)
   * @param userId login id
   * @param email user's email
   * @return
   */
  def findOneByUserIdAndEmail(userId: String, email: String): Option[User] = {
    dao.findOne(MongoDBObject("userId" -> userId, "email" -> email))
  }

  /**
   * 权限认证
   * 用于判断userId是否为当前用户
   * @param userId userId
   * @param user logged user
   */
  def isOwner(userId: String)(user: User): Future[Boolean] = Future {
    User.findOneByUserId(userId).map(_ == user).get
  }

  /**
   * 权限认证
   * 用于判断userId与当前用户是否互相关注(强关系)
   * @param userId userId
   * @param user logged user
   */
  def isFriend(userId: ObjectId)(user: User): Future[Boolean] = Future {
    (userId == user.id) || MyFollow.followEachOther(userId, user.id)
  }

  /**
   * Find users and sort by activity
   * @return list of users
   */
  def findBeautyUsers = dao.find(MongoDBObject("userTyp" -> NORMAL_USER)).toList.sortBy(user => user.activity)

  /**
   * checks for accountId is existed
   *
   * 用户、店铺注册时，登录ID唯一性检查
   * @param value accountId
   * @param f function of checking
   * @return boolean
   */
  def isExist(value: String,
              f: String => Option[User]) = f(value).map(user => true).getOrElse(false)

  /**
   * checks for nickName,email,tel is valid
   *
   * 用户注册、用户信息修改时，昵称、邮箱、手机号唯一性检查
   * @param value value
   * @param loggedUser logged user
   * @param f function of checking
   * @return boolean
   */
  def isValid(value: String,
              loggedUser: Option[User],
              f: String => Option[User]) =
    f(value).map(
      user =>
        loggedUser.map(_.id == user.id).getOrElse(false)).getOrElse(true)
        
  /**
   * 根据预约的手机号码判断，如果修改了那么更新会员中手机信息
   * @param reservation 预约信息
   */
  def updateUserPhone(reservation: Reservation) = {
    val user = findOneByUserId(reservation.userId)
    user match {
      case Some(u) => {
        if(!(u.tel.map(phone => phone == reservation.userPhone).getOrElse(false))) {
          val newUser = u.copy(tel = Some(reservation.userPhone))
          User.save(newUser)
        }
      }
      case None => {false}
    }
  }

}
