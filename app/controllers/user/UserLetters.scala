package controllers

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

object UserLetters extends Controller with AuthElement with AuthConfigImpl {
  val pageSize: Int = 5 //每页显示记录

  val userLetterForm: Form[UserLetter] = Form(
    mapping(
      "userMessage" -> mapping(
        "sender" -> text,
        "senderNm" -> text,
        "addressee" -> text,
        "addresseeNm" -> text,
        "createdTime" -> text) { (sender, senderNm, addressee, addresseeNm, time) =>
          UserMessage(new ObjectId, sender, senderNm, addressee, addresseeNm, new ObjectId(), "sended", "normal", "unRead", new Date)
        } {
          userMessage => Some((userMessage.sender, userMessage.senderNm, userMessage.addressee, userMessage.addresseeNm, userMessage.createdTime.toString))
        },
      "message" -> mapping(
        "title" -> text,
        "content" -> text) { (title, content) => Message(new ObjectId, title, content, new Date) } { message => Some((message.title, message.content)) })(UserLetter.apply)(UserLetter.unapply))

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
          userMessage.setAddr()

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
       val inBoxMsgs =UserMessage.findByQuery("inBox", user.userId, 1, pageSize)
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
    UserMessage.readed(userMsg)
    Ok(views.html.user.message(UserLetter(userMsg, msg),user,followInfo))
  }
  
}