package controllers

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import models._
import com.mongodb.casbah.Imports.ObjectId


object MyPages extends Controller {
  
  def dianpu_guanzhu = Action {
    Ok(views.html.myPage.dianpu_guanzhu(""))
  }
  
  def stylist_guanzhu = Action {
    Ok(views.html.myPage.stylist_guanzhu(""))
  }
  
  def myPageMain = Action {
    implicit request =>
      val user_id = request.session.get("user_id").get
      println(""""""" + user_id)
      val userId = new ObjectId(user_id)
    Ok(views.html.myPage.myPagemain(userId))
  }

}