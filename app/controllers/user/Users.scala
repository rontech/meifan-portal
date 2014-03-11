package controllers

import play.api.mvc._
import java.util.Date
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import models._


object Users extends Controller {
  def registerForm(id :ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6,16),
      // Create a tuple mapping for the password/confirm
      "password" -> tuple(
        "main" -> text,
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          "Passwords don't match", passwords => passwords._1 == passwords._2),
      "nickNm" ->text,
      "birthDay" ->date,
      "sex" ->text,
      "city" ->text,
      "job" ->text,
      "email" -> email,
      "tel" -> text,
      "qq" -> text,
      "msn" -> text,
      "weChat" -> text,
      "accept" -> checked("")) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (id, userId, password, nickNm, birthDay, sex, city, job, email, tel, qq, msn, weChat, _)
        => User(id, userId, password._1, nickNm, birthDay, sex, city, job, email, tel, qq, msn, weChat, "userTyp.0", "userLevel.0", 20, new Date(), "status.0")
      } // Unbinding: Create the mapping values from an existing Hacker value
      {
        user => Some((user.id, user.userId, (user.password, ""), user.nickNm, user.birthDay, user.sex, user.city, user.job, user.email, user.tel, user.qq, user.msn, user.weChat, false))
      }.verifying(
        "This userId is not available",user => !User.findOneByUserId(user.userId).nonEmpty)
  )

  val loginForm = Form(tuple(
    "userId" -> nonEmptyText,
    "password" -> nonEmptyText).verifying(user => User.authenticate(user._1, user._2).nonEmpty)
    
  )

  def userForm(id :ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "userId" -> nonEmptyText(6,16),
      // Create a tuple mapping for the password/confirm
      "password" -> text,
      "nickNm" ->text,
      "birthDay" ->date,
      "sex" ->text,
      "city" ->text,
      "job" ->text,
      "email" -> email,
      "tel" -> text,
      "qq" -> text,
      "msn" -> text,
      "weChat" -> text,
      "userTyp" ->text,
      "userBehaviorLevel" ->text,
      "point" ->number,
      "added" ->date,
      "status" ->text
      ) {
      // Binding: Create a User from the mapping result (ignore the second password and the accept field)
      (id, userId, password, nickNm, birthDay, sex, city, job, email, tel, qq, msn, weChat, userTyp, userBehaviorLevel, point, added, status)
      => User(id, userId, password, nickNm, birthDay, sex, city, job, email, tel, qq, msn, weChat, userTyp, userBehaviorLevel, point, added, status)
    } // Unbinding: Create the mapping values from an existing Hacker value
    {
      user => Some((user.id, user.userId, user.password, user.nickNm, user.birthDay, user.sex, user.city, user.job, user.email, user.tel, user.qq, user.msn, user.weChat, user.userTyp, user.userBehaviorLevel, user.point, user.added, user.status))
    }.verifying(
        "This userId is not available",user => User.findByNickNm(user.nickNm).nonEmpty)
  )
//  val userForm: Form[User] = Form(
//    mapping(
//      "username" -> text,
//      "password" -> text,
//      "sex" -> text,
//      "age" -> number,
//      "tel" -> text,
//      "email" -> text,
//      "education" -> text,
//      "introduce" -> text,
//      "added" -> date,
//      "updated" -> date) {
//        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
//        (username, password, sex, age, tel, email, education, introduce, added, _) => User(new ObjectId, username, password, sex, age, tel, email, education, introduce, added, new Date())
//      } // Unbinding: Create the mapping values from an existing Hacker value
//      {
//        user => Some((user.username, user.password, user.sex, user.age, user.tel, user.email, user.education, user.introduce, user.added, user.updated))
//      }
//  )
  
  def login() = Action { implicit request =>
    Users.loginForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.login(errors)),
      {
        user =>
          Redirect(routes.Users.myPage(user._1))
//          val user_id = User.findId(user._1)
//          Redirect(routes.MyPages.myPageMain).withSession(request.session+("user_id" -> user_id.toString()))
      })
  }

  def register = Action { implicit request =>
    Users.registerForm().bindFromRequest.fold(
      errors => BadRequest(views.html.user.message1(errors)),
      {
        user =>
          User.save(user, WriteConcern.Safe)
          Ok(views.html.user.myPageRes(user))
      })
  }

  def update(id: ObjectId) = Action { implicit request =>
    Users.userForm(id).bindFromRequest.fold(
      errors => BadRequest(views.html.user.message1(errors)),
      {
        user =>
//          User.save(user.copy(id = id), WriteConcern.Safe)
          User.save(user, WriteConcern.Safe)
          Ok(views.html.user.success(user.userId))
      })
  }

  def show(userId: String) = Action {
    User.findOneByUserId(userId).map { user =>
      val userForm = Users.userForm().fill(user)
      Ok(views.html.user.Infomation(userForm))
    } getOrElse {
      NotFound
    }
  }

  def myPage(userId :String) = Action{
    val user = User.findOneByUserId(userId).get
    Ok(views.html.user.myPageRes(user))
 }
  
  def myReservation(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.myPageRes(user = user.get))
  }
  
  def mySaveCoupon(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.mySaveCoupon(user = user.get))
  }
  
  def mySaveBlog(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.mySaveBlog(user = user.get))
  }
  
  def mySaveStyle(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.mySaveStyle(user = user.get))
  }
  
  def mySaveSalonActi(userId: ObjectId) = Action {
    val user: Option[User] = User.findById(userId)
    Ok(views.html.user.mySaveSalonActi(user = user.get))
  }
}
