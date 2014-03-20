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

object Services extends Controller {
  def serviceForm(id: ObjectId = new ObjectId): Form[Service] = Form(
    mapping(
      "id" -> ignored(id),
      "serviceName" -> nonEmptyText,
      "description" -> text,
      "serviceType" -> text,
      "salonId" -> text,
      "price" -> bigDecimal,
      "duration" -> number,
      "createdTime" -> date,
      "expireTime" -> date
      )
      {(id, serviceName, description, serviceType, salonId, price, duration, createdTime, expireTime) => Service(new ObjectId(),  serviceName,
          description, serviceType, new ObjectId(salonId), price, duration, createdTime, Some(expireTime), true)}
      {
        service => Some((service.id, service.serviceName, service.description, service.serviceType, service.salonId.toString(), service.price, service.duration, service.createdTime, service.expireTime.get))
      }.verifying(
        "This name has been used!",
        service => !Service.checkService(service.serviceName)   
    )
  )
  
  def serviceShowForm(id: ObjectId = new ObjectId): Form[Service] = Form(
    mapping(
      "id" -> ignored(id),
      "serviceName" -> nonEmptyText,
      "description" -> text,
      "serviceType" -> text,
      "salonId" -> text,
      "price" -> bigDecimal,
      "duration" -> number,
      "createdTime" -> date,
      "expireTime" -> date
      )
      {(id, serviceName, description, serviceType, salonId, price, duration, createdTime, expireTime) => Service(new ObjectId(),  serviceName,
          description, serviceType, new ObjectId(salonId), price, duration, createdTime, Some(expireTime), true)}
      {
        service => Some((service.id, service.serviceName, service.description, service.serviceType, service.salonId.toString(), service.price, service.duration, service.createdTime, service.expireTime.get))
      }
  )
  
  def serviceMain = Action{
  	  Ok(views.html.service.addService(serviceForm(),Service.getServiceTypeList))
  }
  
  def addService = Action { implicit request =>
    serviceForm().bindFromRequest.fold(
      errors => BadRequest(views.html.service.addService(errors,Service.getServiceTypeList)),
      {
        service =>
          Service.addService(service)
          Redirect(routes.Services.servicesList)                  
      })
  }
  
  def servicesList = Action {
    Ok(views.html.service.showAllServices())
  }
  
  def deleteService(id: ObjectId) = Action{
    Service.deleteService(id)
    Ok(views.html.service.showAllServices())
  }

  def showService(id: ObjectId) = Action{
    Service.findOneByServiceId(id).map { service =>
      val serviceForm = Services.serviceShowForm().fill(service)
      Ok(views.html.service.serviceInformation(serviceForm,service))
    } getOrElse {
      NotFound
    }
  }
  
  def updateService(id: ObjectId) = Action { implicit request =>
    serviceShowForm().bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        service =>
          Service.save(service.copy(id = id), WriteConcern.Safe)
          Ok(views.html.service.showAllServices())
      })
  }
}
