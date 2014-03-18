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

object RelationTypes extends Controller {
  def relationTypeForm(id: ObjectId = new ObjectId): Form[RelationType] = Form(
    mapping(
      "id" -> ignored(id),
      "relationTypeName" -> nonEmptyText,
      "relationTypeId" -> number
      )(RelationType.apply)(RelationType.unapply)   
  )
  
  def relationTypeMain = Action{
  	  Ok(views.html.user.addRelationType(relationTypeForm()))
  }
  
  def addRelationType = Action { implicit request =>
    relationTypeForm().bindFromRequest.fold(
      errors => BadRequest(views.html.user.addRelationType(errors)),
      {
        relationType =>
          RelationType.addRelationType(relationType)
          Ok(Html("successful!</p>"))                   
      })
  }
}