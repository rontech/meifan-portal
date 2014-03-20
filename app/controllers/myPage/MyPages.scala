package controllers

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import models._
import com.mongodb.casbah.Imports.ObjectId
import jp.t2v.lab.play2.auth._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MyPages extends Controller with LoginLogout with AuthElement with AuthConfigImpl{
  
  def dianpu_guanzhu = Action {
    Ok(views.html.myPage.dianpu_guanzhu(""))
  }
  
  def stylist_guanzhu = Action {
    Ok(views.html.myPage.stylist_guanzhu(""))
  }
  
  def myPageMain = StackAction(AuthorityKey -> authorization(LoggedIn) _) {implicit request =>
    val user = loggedIn
      Ok(views.html.myPage.myPagemain(user.id))
  }

}