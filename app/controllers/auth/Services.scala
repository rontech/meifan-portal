package controllers.auth

import play.api.mvc._
import models._
import java.util.Date
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import controllers._
import jp.t2v.lab.play2.auth._

object Services extends Controller with AuthElement with SalonAuthConfigImpl{
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
  def addService = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    serviceForm().bindFromRequest.fold(
      errors => BadRequest(views.html.salon.admin.createSalonService(salon, errors, ServiceType.findAll.toList.map{serviceType =>serviceType.serviceTypeName})),
      {
        service =>
          Service.addService(service)
          Redirect(auth.routes.Salons.myService)                  
      })
  }
  
/**
 *删除服务 
 */
  def deleteService(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Service.deleteService(id)
    Redirect(auth.routes.Salons.myService)
  }
  
 /**
  *更新服务  
  */ 
  def showService(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
     val salon = loggedIn
    Service.findOneById(id).map { service =>
      val serviceUpdateForm = Services.serviceUpdateForm().fill(service)
      Ok(views.html.salon.admin.editSalonService(salon,serviceUpdateForm,service))
    } getOrElse {
      NotFound
    }
  }
  
/**
 * 更新服务动作
 */  
  def updateService(id: ObjectId)  = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    serviceUpdateForm().bindFromRequest.fold(
      errors => BadRequest(views.html.salon.admin.editSalonService(salon,errors,Service.findOneById(id).get)),
      {
        service =>
          Service.save(service.copy(id = id), WriteConcern.Safe)
          Redirect(auth.routes.Salons.myService)
      })
  }
  
   def serviceMain = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
	  val serviceType = ServiceType.findAll.toList.map{serviceType =>serviceType.serviceTypeName}
	  Ok(views.html.salon.admin.createSalonService(salon, serviceForm(), serviceType))
  }
}
