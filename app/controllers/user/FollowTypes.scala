package controllers

import play.api.mvc._
import play.api.libs.json._
import models._
import java.util.Date
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import play.api.templates._

object FollowTypes extends Controller {
  def followTypeTypeForm(id: ObjectId = new ObjectId): Form[FollowType] = Form(
    mapping(
      "id" -> ignored(id),
      "followTypeName" -> nonEmptyText
      )(FollowType.apply)(FollowType.unapply)   
  )
  
  def followTypeMain = Action{
  	  Ok(views.html.user.addFollowType(followTypeTypeForm()))
  }
  
  def addFollowType = Action { implicit request =>
    followTypeTypeForm().bindFromRequest.fold(
        errors => BadRequest(Html(errors.toString)),
        {
        followType =>
          FollowType.addFollowType(followType)
          Ok(Html("successful!</p>"))                   
      })
  }
}