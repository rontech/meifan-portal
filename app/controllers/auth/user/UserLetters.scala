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
package controllers.auth

import play.api.mvc._
import java.util.Date
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import models._
import jp.t2v.lab.play2.auth._
import scala.concurrent._
import ExecutionContext.Implicits.global
import play.api.templates.Html
import controllers._

object UserLetters extends Controller with AuthElement with UserAuthConfigImpl {
  val pageSize: Int = 5 //每页显示记录

  val userLetterForm: Form[UserLetter] = Form(
  mapping(
   "userMessage" -> mapping(
    "sender" -> text,
    "senderNm" -> text,
    "addressee" -> text,
    "addresseeNm" -> text) { (sender, senderNm, addressee, addresseeNm) =>
     UserMessage(new ObjectId, sender, senderNm, addressee, addresseeNm, new ObjectId(), UserMessage.OUTBOX_SENT, UserMessage.INBOX_UNREAD, new Date)
    } {
     userMessage => Some((userMessage.sender, userMessage.senderNm, userMessage.addressee, userMessage.addresseeNm))
    },
   "message" -> mapping(
    "title" -> nonEmptyText,
    "content" -> nonEmptyText) { (title, content) => Message(new ObjectId, title, content, new Date) } { message => Some((message.title, message.content)) })(UserLetter.apply)(UserLetter.unapply))

//TODO
  def sendMessage() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
  UserLetters.userLetterForm.bindFromRequest.fold(
   //errors => BadRequest(views.html.user.userLetter(errors)),
   errors => BadRequest(""),
   {
    userLetter =>
     Message.save(userLetter.message, WriteConcern.Safe)
     val sender = loggedIn
     val userMessage = userLetter.userMessage.copy(
      msgId = userLetter.message.id,
      sender = sender.userId,
      senderNm = sender.nickName)
     userMessage.setAdd()

     UserMessage.save(userMessage, WriteConcern.Safe)
     Ok("")
   })
  }

  def messageList(requirement: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
  val user = loggedIn
  val count = UserMessage.countByCondition(requirement, user.userId)
  val unReadMsgs = UserMessage.findByQuery(requirement, user.userId, 1, pageSize)
  var userMsgs:List[models.UserMessage] = Nil
  if (unReadMsgs.isEmpty) {
    val inBoxMsgs =UserMessage.findByQuery(UserMessage.INBOX_ALL, user.userId, 1, pageSize)
    userMsgs = inBoxMsgs
  }
  else{
    userMsgs = unReadMsgs
  }
  val letters = userMsgs.map(userMsg => 
   UserLetter(userMsg, Message.findOneById(userMsg.msgId).get)
   ) 
  
  var pages: Int = 0
  if (count % pageSize == 0) {
   pages = count.toInt / pageSize
  } else {
   pages = count.toInt / pageSize + 1
  }
  val followInfo = MyFollow.getAllFollowInfo(user.id)
  Ok(views.html.user.myMessages(letters, count, pages, 1, user, followInfo))
  }
  
  def showMessage(id : ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
  val user = loggedIn
  val followInfo = MyFollow.getAllFollowInfo(user.id)
  val userMsg = UserMessage.findOneById(id).get
  val msg = Message.findOneById(userMsg.msgId).get
  UserMessage.read(userMsg)
  Ok(views.html.user.message(UserLetter(userMsg, msg),user,followInfo))
  }

  def sendLetterPage(id : String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
  val loginUser = loggedIn
  val user = User.findOneById(new ObjectId(id)).get
  Ok(views.html.user.myLetters(user,loginUser))
  }

  def sendLetter() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
  UserLetters.userLetterForm.bindFromRequest.fold(
  errors => BadRequest("发送失败，请重试！"),
  {
   userLetter =>
    Message.save(userLetter.message, WriteConcern.Safe)
    val userMessage = userLetter.userMessage.copy(msgId = userLetter.message.id)
    UserMessage.save(userMessage, WriteConcern.Safe)
    Ok(Html("<p><strong>发送成功！！</strong></p>"))
  })
  }
}
