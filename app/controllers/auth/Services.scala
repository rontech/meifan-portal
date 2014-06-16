/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
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
import com.meifannet.portal.MeifanNetSalonApplication
import models.portal.service.{ServiceType, Service}
import models.portal.salon.Salon

object Services extends MeifanNetSalonApplication {
  /**
   * 定义服务form,用于服务添加
   * @param id
   * @return
   */
  def serviceForm(id: ObjectId = new ObjectId): Form[Service] = Form(
    mapping(
      "id" -> ignored(id),
      "serviceName" -> text,
      "description" -> text,
      "serviceType" -> text,
      "salonId" -> text,
      "price" -> bigDecimal,
      "duration" -> number) { (id, serviceName, description, serviceType, salonId, price, duration) =>
        Service(new ObjectId(), serviceName,
          description, serviceType, new ObjectId(salonId), price, duration, new Date(), None, true)
      } {
        service => Some((service.id, service.serviceName, service.description, service.serviceType, service.salonId.toString(), service.price, service.duration))
      }.verifying(
        Messages("service.serviceNameNotAvalid"),
        service => !Service.checkServiceIsExist(service.serviceName, service.salonId)))

  /**
   * 定义用于更新服务的form
   * @param id
   * @return
   */
  def serviceUpdateForm(id: ObjectId = new ObjectId): Form[Service] = Form(
    mapping(
      "id" -> ignored(id),
      "serviceName" -> text,
      "description" -> text,
      "serviceType" -> text,
      "salonId" -> text,
      "price" -> bigDecimal,
      "duration" -> number) { (id, serviceName, description, serviceType, salonId, price, duration) =>
        Service(new ObjectId(), serviceName,
          description, serviceType, new ObjectId(salonId), price, duration, new Date(), None, true)
      } {
        service => Some((service.id, service.serviceName, service.description, service.serviceType, service.salonId.toString(), service.price, service.duration))
      })

  /**
   * 添加服务
   * @return
   */
  def addService = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    serviceForm().bindFromRequest.fold(
      errors => BadRequest(views.html.salon.admin.createSalonService(salon, errors, ServiceType.findAll.toList.map { serviceType => serviceType.serviceTypeName })),
      {
        service =>
          Service.addService(service)
          Redirect(auth.routes.Salons.myService)
      })
  }

  /**
   * 删除服务
   * @param id 服务ObjectId
   * @return
   */
  def deleteService(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Service.deleteService(id)
    Redirect(auth.routes.Salons.myService)
  }

  /**
   * 跳转某个服务页面
   * @param id 服务ObjectId
   * @return
   */
  def showService(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Service.findOneById(id).map { service =>
      val serviceUpdateForm = Services.serviceUpdateForm().fill(service)
      Ok(views.html.salon.admin.editSalonService(salon, serviceUpdateForm, service))
    } getOrElse {
      NotFound
    }
  }

  /**
   * 更新服务动作
   * @param id 服务ObjectId
   * @return
   */
  def updateService(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    serviceUpdateForm().bindFromRequest.fold(
      errors => BadRequest(views.html.salon.admin.editSalonService(salon, errors, Service.findOneById(id).get)),
      {
        service =>
          Service.save(service.copy(id = id), WriteConcern.Safe)
          Redirect(auth.routes.Salons.myService)
      })
  }

  /**
   * 跳转至添加服务的页面
   * @return
   */
  def serviceMain = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val industry=Salon.findIndustryBySalonId(salon.id)
    val serviceType = ServiceType.findAllServiceType(industry.toString)
    Ok(views.html.salon.admin.createSalonService(salon, serviceForm(), serviceType))
  }
}
