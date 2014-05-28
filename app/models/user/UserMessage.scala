/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
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
import com.mongodb.casbah.query.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.mongodb.casbah.WriteConcern
import com.meifannet.framework.db._
import models.portal.stylist.Stylist
import models.portal.salon.Salon

/**
 * Class for UserMessage
 * @param id    ObjectId of record in mongodb
 * @param sender    UserMessage's sender
 * @param senderNm    sender's name
 * @param addressee   UserMessage's addressee
 * @param addresseeNm   addressee's name
 * @param msgId   Message's ObjectId
 * @param outBoxStatus    UserMessage's Status
 * @param inBoxStatus   UserMessage's Status
 * @param createdTime
 */
case class UserMessage(
  id: ObjectId,
  sender: String,
  senderNm: String,
  var addressee: String,
  var addresseeNm: String,
  msgId: ObjectId,
  outBoxStatus: String,
  inBoxStatus: String,
  createdTime: Date) {
  def setAdd() = {
    if (this.addressee.isEmpty)
      this.addressee = User.findOneByNickNm(this.addresseeNm).get.userId
    else
      this.addresseeNm = User.findOneByUserId(this.addressee).get.nickName
  }
}

object UserMessage extends MeifanNetModelCompanion[UserMessage] {

  val OUTBOX_SENT = "sent"
  val OUTBOX_SAVE = "save"
  val OUTBOX_DEL = "delete"
  val INBOX_READ = "read"
  val INBOX_UNREAD = "unRead"
  val INBOX_DEL = "delete"
  val INBOX_ALL = "all"

  val dao = new MeifanNetDAO[UserMessage](collection = loadCollection()) {}

  /**
   * Update UserMessage's status to READ
   * @param userMessage   update userMessage
   * @return
   */
  def read(userMessage: UserMessage) = dao.save(userMessage.copy(inBoxStatus = INBOX_READ), WriteConcern.Safe)

  /**
   * Update UserMessage's status to SENT
   * @param userMessage   update userMessage
   * @return
   */
  def sent(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = OUTBOX_SENT), WriteConcern.Safe)

  /**
   * Update UserMessage's status to SAVE
   * @param userMessage   update userMessage
   * @return
   */
  def saved(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = OUTBOX_SAVE), WriteConcern.Safe)

  /**
   * Update UserMessage's status to DEL in outBox
   * @param userMessage   update userMessage
   * @return
   */
  def delFromOutBox(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = OUTBOX_DEL), WriteConcern.Safe)

  /**
   * Update UserMessage's status to DEL in inBox
   * @param userMessage   update userMessage
   * @return
   */
  def delFromInBox(userMessage: UserMessage) = dao.save(userMessage.copy(inBoxStatus = INBOX_DEL), WriteConcern.Safe)

  /**
   * The common method for finding userMessage according to requirement
   * @param requirement   types of finding
   * @param userId    logged user's userId
   * @param page    page numbers
   * @param pageSize    numbers of userMessage display in every page
   * @return list of userMessages
   */
  def findByCondition(requirement: String, userId: String, page: Int, pageSize: Int) = {
    val query = getCondition(requirement, userId: String)
    dao.find(query).sort(MongoDBObject("createdTime" -> -1)).skip((page - 1) * pageSize).limit(pageSize).toList
  }

  /**
   * The common method for counting userMessage according to requirement
   * @param requirement   types of counting
   * @param userId    logged user's userId
   * @return    number of userMessages witch fit to requirement
   */
  def countByCondition(requirement: String, userId: String) = {
    val query = getCondition(requirement, userId: String)
    dao.count(query)
  }

  /**
   * Get condition by requirement
   * @param requirement   request e.g. INBOX_UNREAD、OUTBOX_SAVE
   * @param userId    logged user's userId
   * @return
   */
  def getCondition(requirement: String, userId: String) = {
    requirement match {
      case INBOX_UNREAD => MongoDBObject("addressee" -> userId, "inBoxStatus" -> INBOX_UNREAD)
      case INBOX_ALL => $and(MongoDBObject("addressee" -> userId), "inBoxStatus" $ne INBOX_DEL)
      case OUTBOX_SENT => MongoDBObject("seeder" -> userId, "outBoxStatus" -> OUTBOX_SENT)
      case OUTBOX_SAVE => MongoDBObject("seeder" -> userId, "outBoxStatus" -> OUTBOX_SAVE)
    }

  }

  /**
   * Send message when user followed salon, user, stylist
   * @param sender    user
   * @param followId    ObjectId of salon,user or stylist
   * @param followObjType   type of followed e.g. FOLLOW_STYLIST or FOLLOW_USER
   * @return
   */
  def sendFollowMsg(sender: User, followId: ObjectId, followObjType: String) = {
    val letter = followObjType match {
       //TODO
      case FollowType.FOLLOW_SALON =>
        val salon = Salon.findOneById(followId).get
        UserMessage(new ObjectId, sender.userId, sender.nickName, "demo01", "demo01", new ObjectId("531964e0d4d57d0a43771811"), OUTBOX_SENT, INBOX_UNREAD, new Date)
      case FollowType.FOLLOW_STYLIST =>
        val stylist = Stylist.findOneByStylistId(followId).get
        val user = Stylist.findUser(stylist.stylistId)
        UserMessage(new ObjectId, sender.userId, sender.nickName, user.userId, user.nickName, new ObjectId("531964e0d4d57d0a43771811"), OUTBOX_SENT, INBOX_UNREAD, new Date)
      case FollowType.FOLLOW_USER =>
        val addressee = User.findOneById(followId).get
        UserMessage(new ObjectId, sender.userId, sender.nickName, addressee.userId, addressee.nickName, new ObjectId("531964e0d4d57d0a43771812"), OUTBOX_SENT, INBOX_UNREAD, new Date)
    }
    UserMessage.save(letter, WriteConcern.Safe)
  }
}

/**
 * Class for userLetter (for storing message and userMessage between server and client)
 * @param userMessage   userMessage
 * @param message   the message of userMessage
 */
case class UserLetter(
  userMessage: UserMessage,
  message: Message)

