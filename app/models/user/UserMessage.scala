package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class UserMessage(
  id: ObjectId,
  sender: String,
  senderNm: String,
  var addressee: String,
  var addresseeNm: String,
  msgId: ObjectId,
  outBoxStatus: String,
  inBoxStatus: String,
  readStatus: String,
  createdTime: Date) {
  def setAddr() = {
    if (this.addressee.isEmpty)
      this.addressee = User.findOneByNickNm(this.addresseeNm).get.userId
    else
      this.addresseeNm = User.findOneByUserId(this.addressee).get.nickName
  }
}

object UserMessage extends ModelCompanion[UserMessage, ObjectId] {

  val dao = new SalatDAO[UserMessage, ObjectId](collection = mongoCollection("UserMessage")) {}

  def readed(userMessage: UserMessage) = dao.save(userMessage.copy(readStatus = "readed"), WriteConcern.Safe)

  def unRead(userMessage: UserMessage) = dao.save(userMessage.copy(readStatus = "unRead"), WriteConcern.Safe)

  def sended(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = "sended"), WriteConcern.Safe)

  def saved(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = "saved"), WriteConcern.Safe)

  def delFromOutBox(userMessage: UserMessage) = dao.save(userMessage.copy(outBoxStatus = "deleted"), WriteConcern.Safe)

  def delFromInBox(userMessage: UserMessage) = dao.save(userMessage.copy(inBoxStatus = "deleted", readStatus = "readed"), WriteConcern.Safe)

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
      case "UnRead" => MongoDBObject("addressee" -> userId, "readStatus" -> "unRead")
      case "outBox" => MongoDBObject("seeder" -> userId, "outBoxStatus" -> "sended")
      case "inBox"  => MongoDBObject("addressee" -> userId, "inBoxStatus" -> "normal")
      case "drafts" => MongoDBObject("seeder" -> userId, "outBoxStatus" -> "saved")
    }

  }
  
  def sendFollowMsg(sender :User, followId : ObjectId,relationTypeId : Int) =  {
    val letter = relationTypeId match{
      case 1 => 
        val salon = Salon.findById(followId).get
        UserMessage(new ObjectId, sender.userId, sender.nickName, "zhenglu", "关雨", new ObjectId("531964e0d4d57d0a43771811"), "sended", "normal", "unRead", new Date)
      case _ =>
        val addressee = User.findOneById(followId).get
        UserMessage(new ObjectId, sender.userId, sender.nickName, addressee.userId, addressee.nickName, new ObjectId("531964e0d4d57d0a43771812"), "sended", "normal", "unRead", new Date)
    } 
    UserMessage.save(letter, WriteConcern.Safe)
  }
}

case class UserLetter(
  userMessage: UserMessage,
  message: Message)


