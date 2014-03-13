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
  
  /**
   * 定义用户申请技师的表单
   */
  def stylistForm: Form[Stylist] = Form(
    mapping(
    	"label" -> text,
    	"salonId" -> text,
    	"workYears" -> text,
    	"stylistStyle" -> list(text),
    	"imageId" ->list(text),
    	"consumerId" -> list(text),
    	"description" -> text
    ){
      (label,salonId,workYears,stylistStyle,imageId,consumerId,description)=>
        Stylist(new ObjectId, label, new ObjectId(salonId), new ObjectId, workYears, stylistStyle, imageId.map(i=>new ObjectId(i)),
            consumerId.map(c=>new ObjectId(c)), description, new String)
    }
    {
      stylist => Some((stylist.label, stylist.salonId.toString, stylist.workYears, stylist.stylistStyle, List(stylist.imageId.toString), List(stylist.consumerId.toString), stylist.description))
    }
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
  def login = Action {
    Ok(views.html.user.login(Users.loginForm))
  }

  def register = Action {
    Ok(views.html.user.register(Users.registerForm()))
  }
  
  def doLogin = Action { implicit request =>
    Users.loginForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.login(errors)),
      {
        user =>
          Redirect(routes.Users.myPage(user._1)).withSession(request.session + ("userId" -> user._1))
      })
  }

  def doRegister = Action { implicit request =>
    Users.registerForm().bindFromRequest.fold(
      errors => BadRequest(views.html.user.register(errors)),
      {
        user =>
          User.save(user, WriteConcern.Safe)
          Ok(views.html.user.myPageRes(user))
      })
  }

  def update(id: ObjectId) = Action { implicit request =>
    Users.userForm(id).bindFromRequest.fold(
//      errors => BadRequest(views.html.user.Infomation(errors,User.findOneByID(id).get)),
        errors => BadRequest(views.html.user.errorMsg(errors)),
      {
        user =>
          User.save(user, WriteConcern.Safe)
          Ok(views.html.user.myPageRes(user))
      })
  }

  def show(userId: String) = Action {
    User.findOneByUserId(userId).map { user =>
      val userForm = Users.userForm().fill(user)
      Ok(views.html.user.Infomation(userForm,user))
    } getOrElse {
      NotFound
    }
  }

  def index = Action{ implicit request =>
    if(!request.session.get("userId").nonEmpty){
      Redirect(routes.Users.login)
    }else
	  Redirect(routes.Users.myPage(request.session.get("userId").get))
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
  
  /**
   * 申请成为技师
   */
  def applyStylist = Action {
    val user = new User(new ObjectId, "123456576", "12333333", "adsad", new Date, "1",
        "jiangsu", "18606291469", "1324567987","729932232",
        "456d4sdsd", "..", "..", "1", "1", 1, new Date, ".")
    Ok(views.html.user.applyStylist(stylistForm,user))
  }
  
  /**
   * 店长或店铺管理者确认后才录入数据库
   */
  def agreeStylist() = Action {
     Ok(views.html.index(""))
  }
  
}
