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
package com.meifannet.framework.auth

import jp.t2v.lab.play2.auth._
import reflect.{ ClassTag, classTag }
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.{ ExecutionContext, Future }
import models._
import controllers.auth
import controllers.routes

/**
 * trait for the authorization and authentication of salon user.
 *
 * @since 1.0
 * @see jp.t2v.lab.play2.auth.AuthConfig
 */
trait SalonAuthConfigImpl extends AuthConfig {

  /**
   * String type is used to identify a user.
   */
  type Id = String

  /**
   * Salon is used to represent a user.
   */
  type User = models.Salon

  /**
   * A type that is defined by every action for authorization.
   * This sample uses the following trait:
   *
   * sealed trait Permission
   * case object Administrator extends Permission
   * case object NormalUser extends Permission
   */
  type Authority = User => Future[Boolean]

  /**
   * A `ClassTag` is used to retrieve an id from the Cache API.
   */
  val idTag: ClassTag[Id] = classTag[Id]

  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds = 3600

  /**
   * A function that returns a `User` object from an `Id`.
   */
  /**
   * @param accountId ID
   * @param ctx ExecutionContext
   */
  def resolveUser(accountId: Id)(implicit ctx: ExecutionContext) = Future.successful(Salon.findOneByAccountId(accountId))

  /**
   * Where to redirect the user after a successful login.
   */
  /**
   * @param request RequestHeader
   * @param ctx ExecutionContext
   * @return
   */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = {
    val uri = request.session.get("access_uri").getOrElse(auth.routes.Salons.salonMainInfo.url.toString)
    Future.successful(Redirect(uri).withSession(request.session - "salon_access_uri"))
  }

  /**
   * Where to redirect the user after logging out
   * 
   * @param request RequestHeader
   * @param ctx ExecutionContext
   */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.index))

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   * 
   * @param request RequestHeader
   * @param ctx ExecutionContext
   */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) =
    Future.successful(Redirect(routes.Application.salonLogin).withSession("salon_access_uri" -> request.uri))

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   * 
   * @param request RequestHeader
   * @param ctx ExecutionContext
   */
  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(auth.routes.Salons.checkInfoState))

  /**
   * A function that determines what `Authority` a user has.
   * 
   * @param user User
   * @param authority Authority
   * @param ctx ExecutionContext
   * @return Boolean authorization result
   */
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = authority(user)

  /**
   * Function that concludes if a user has logged in.
   * 
   * @param user
   * @return true
   */
  def isLoggedIn(user: User): Future[Boolean] = Future.successful(true)

  /**
   * Function that concludes if a user has a full-filled profile.
   * 
   * @param user User
   * @return true when the profile is completely filled
   */
  def authImproveInfo(user: User): Future[Boolean] = Future.successful(
    Salon.checkBasicInfoIsFill(user) && Salon.checkDetailIsFill(user) && Salon.checkImgIsExist(user)
  )
}
