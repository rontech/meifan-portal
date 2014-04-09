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
import play.api.i18n.Messages

object Services extends Controller {
  def serviceForm(id: ObjectId = new ObjectId): Form[Service] = Form(
    mapping(
      "id" -> ignored(id),
      "serviceName" -> nonEmptyText,
      "description" -> text,
      "serviceType" -> text,
      "salonId" -> text,
      "price" -> bigDecimal,
      "duration" -> number
      )
      {(id, serviceName, description, serviceType, salonId, price, duration) => Service(new ObjectId(),  serviceName,
          description, serviceType, new ObjectId(salonId), price, duration, new Date(), None, true)}
      {
        service => Some((service.id, service.serviceName, service.description, service.serviceType, service.salonId.toString(), service.price, service.duration))
      }.verifying(
        Messages("service.serviceNameNotAvalid"),
        service => !Service.checkService(service.serviceName, service.salonId)   
    )
  )
  
  def serviceUpdateForm(id: ObjectId = new ObjectId): Form[Service] = Form(
    mapping(
      "id" -> ignored(id),
      "serviceName" -> nonEmptyText,
      "description" -> text,
      "serviceType" -> text,
      "salonId" -> text,
      "price" -> bigDecimal,
      "duration" -> number
      )
      {(id, serviceName, description, serviceType, salonId, price, duration) => Service(new ObjectId(),  serviceName,
          description, serviceType, new ObjectId(salonId), price, duration, new Date(), None, true)}
      {
        service => Some((service.id, service.serviceName, service.description, service.serviceType, service.salonId.toString(), service.price, service.duration))
      }  
    )
/**
 * 添加服务
 */
  def addService(salonId: ObjectId) = Action { implicit request =>
    serviceForm().bindFromRequest.fold(
      errors => BadRequest(views.html.salon.admin.createSalonService(Salon.findOneById(salonId).get, errors, ServiceType.findAll.toList.map{serviceType =>serviceType.serviceTypeName})),
      {
        service =>
          Service.addService(service)
          Redirect(routes.SalonsAdmin.myService(salonId))                  
      })
  }
  
/**
 *删除服务 
 */
  def deleteService(id: ObjectId, salonId: ObjectId) = Action{
    Service.deleteService(id)
    Redirect(routes.SalonsAdmin.myService(salonId))
  }
  
 /**
  *更新服务  
  */ 
  def showService(id: ObjectId, salonId: ObjectId) = Action{
    Service.findOneById(id).map { service =>
      val serviceUpdateForm = Services.serviceUpdateForm().fill(service)
      Ok(views.html.salon.admin.editSalonService(Salon.findOneById(salonId).get,serviceUpdateForm,service))
    } getOrElse {
      NotFound
    }
  }
  
/**
 * 更新服务动作
 */  
  def updateService(id: ObjectId, salonId: ObjectId) = Action { implicit request =>
    serviceUpdateForm().bindFromRequest.fold(
      errors => BadRequest(views.html.salon.admin.editSalonService(Salon.findOneById(salonId).get,errors,Service.findOneById(id).get)),
      {
        service =>
          Service.save(service.copy(id = id), WriteConcern.Safe)
          Redirect(routes.SalonsAdmin.myService(salonId))
      })
  }
  
   def serviceMain(salonId: ObjectId) = Action{
	  val salon: Option[Salon] = Salon.findOneById(salonId)
	  val serviceType = ServiceType.findAll.toList.map{serviceType =>serviceType.serviceTypeName}
	  
	  salon match {
	    case Some(s) => Ok(views.html.salon.admin.createSalonService(s, serviceForm(), serviceType))
        case None => NotFound
	  }
  }
}
