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

import jp.t2v.lab.play2.auth._
import reflect.{ ClassTag, classTag }
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.{ ExecutionContext, Future }
import models._

trait SalonAuthConfigImpl extends AuthConfig {

  type Id = String

  type User = models.Salon

  type Authority = User => Future[Boolean]

  val idTag: ClassTag[Id] = classTag[Id]

  val sessionTimeoutInSeconds = 3600

  def resolveUser(accountId: Id)(implicit ctx: ExecutionContext) = Future.successful(Salon.findByAccountId(accountId))

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = {
    val uri = request.session.get("access_uri").getOrElse(auth.routes.Salons.salonInfoBasic.url.toString)
    Future.successful(Redirect(uri).withSession(request.session - "salon_access_uri"))
  }
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.index))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Redirect(routes.Application.salonLogin).withSession("salon_access_uri" -> request.uri))

  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(auth.routes.Salons.checkInfoState))

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = authority(user)

  def isLoggedIn(user: User): Future[Boolean] = Future.successful(true)

  def authImproveInfo(user: User): Future[Boolean] = Future.successful(
    if(!Salon.checkBasicInfoIsFill(user) || !Salon.checkDetailIsFill(user) || !Salon.checkImgIsExist(user)){false}else{true}
  )

  /*def authorization(permission: Permission)(user : User)(implicit ctx: ExecutionContext) = Future.successful((permission, user.permission) match {
  case ( _, "Administrator") => true
  case (LoggedIn, "LoggedIn") => true
  case (GuestUser, "LoggedIn") => true
  case (GuestUser, "GuestUser") => true
  case _ => false
  })*/
}
