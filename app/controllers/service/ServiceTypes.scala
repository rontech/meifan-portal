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
package controllers

import play.api.mvc._
import models._
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import play.api.templates._
import com.meifannet.framework.MeifanNetApplication

object ServiceTypes extends MeifanNetApplication {
  /**
   * 定义用于添加服务类别的form
   * @param id
   * @return
   */
  def serviceTypeForm(id: ObjectId = new ObjectId): Form[ServiceType] = Form(
    mapping(
      "id" -> ignored(id),
      "industryName" -> text,
      "serviceTypeName" -> nonEmptyText,
      "description" -> text)(ServiceType.apply)(ServiceType.unapply))

  /**
   * 跳转至添加服务类别的主页面
   * @return
   */
  def serviceTypeMain = Action {
    Ok(views.html.service.addServiceType(serviceTypeForm()))
  }

  /**
   * 添加服务类别动作
   * @return
   */
  def addServiceType = Action { implicit request =>
    serviceTypeForm().bindFromRequest.fold(
      errors => BadRequest(views.html.service.addServiceType(errors)),
      {
        serviceType =>
          ServiceType.addServiceType(serviceType)
          Ok(Html("<p>添加成功！</p>"))
      })
  }
}
