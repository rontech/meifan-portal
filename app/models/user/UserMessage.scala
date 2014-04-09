package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.query.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.mongodb.casbah.WriteConcern

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

object UserMessage extends ModelCompanion[UserMessage, ObjectId] {

  val OUTBOX_SENT = "sent"
  val OUTBOX_SAVE = "save"
  val OUTBOX_DEL = "delete"
  val INBOX_READ = "read"
  val INBOX_UNREAD = "unRead"
  val INBOX_DEL = "delete"
  val INBOX_ALL = "all"

  val dao = new SalatDAO[UserMessage, ObjectId](collection = mongoCollection("UserMessage")) {}

  def read(userMessage: UserMessage) = dao.save(userMessage.copy(inBoxStatus = INBOX_READ), WriteConcern.Safe)

  def sent(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = OUTBOX_SENT), WriteConcern.Safe)

  def saved(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = OUTBOX_SAVE), WriteConcern.Safe)

  def delFromOutBox(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = OUTBOX_DEL), WriteConcern.Safe)

  def delFromInBox(userMessage: UserMessage) = dao.save(userMessage.copy(inBoxStatus = INBOX_DEL), WriteConcern.Safe)

  def findByQuery(requirement: String, userId: String, page: Int, pageSize: Int) = {
      val query = getQuery(requirement, userId: String)
      dao.find(query).sort(MongoDBObject("createdTime" -> -1)).skip((page - 1) * pageSize).limit(pageSize).toList
  }
 
  def countByCondition(requirement: String, userId: String) = {
    val query = getQuery(requirement, userId: String)
    dao.count(query)
  }

  def getQuery(requirement: String, userId: String) = {
    requirement match {
      case INBOX_UNREAD => MongoDBObject("addressee" -> userId, "inBoxStatus" -> INBOX_UNREAD)
      case INBOX_ALL =>    $and(MongoDBObject("addressee" -> userId), "inBoxStatus" $ne INBOX_DEL)
      case OUTBOX_SENT => MongoDBObject("seeder" -> userId, "outBoxStatus" -> OUTBOX_SENT)
      case OUTBOX_SAVE => MongoDBObject("seeder" -> userId, "outBoxStatus" -> OUTBOX_SAVE)
    }

  }
  
  def sendFollowMsg(sender :User, followId : ObjectId, followObjType:String) =  {
    val letter = followObjType match{
      case FollowType.FOLLOW_SALON =>
        val salon = Salon.findById(followId).get
        //TODO
        UserMessage(new ObjectId, sender.userId, sender.nickName, "zhenglu", "关雨", new ObjectId("531964e0d4d57d0a43771811"), OUTBOX_SENT, INBOX_UNREAD, new Date)
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

case class UserLetter(
  userMessage: UserMessage,
  message: Message)


