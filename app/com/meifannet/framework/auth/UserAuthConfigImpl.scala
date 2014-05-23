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
 * trait for the authorization and authentication of customer.
 *
 * @since 1.0
 * @see jp.t2v.lab.play2.auth.AuthConfig
 */
trait UserAuthConfigImpl extends AuthConfig {
  /**
   * String type is used to identify a user.
   */
  type Id = String

  /**
   * Salon is used to represent a user.
   */
  type User = models.User

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
   * 
   * @param userId ID
   * @param ctx ExecutionContext
   * @return User
   */
  def resolveUser(userId: Id)(implicit ctx: ExecutionContext) = Future.successful(User.findOneByUserId(userId))

  /**
   * Where to redirect the user after a successful login.
   * 
   * @param request RequestHeader
   * @param ctx ExecutionContext
   */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = {
    val uri = request.session.get("user_access_uri").getOrElse(routes.Application.index.url.toString)
    Future.successful(Redirect(uri).withSession(request.session - "user_access_uri"))
  }

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.index))

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows.
   * 
   * @param request RequestHeader
   * @param ctx ExecutionContext
   */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful {
    request.headers.get("X-Requested-With") match {
      case Some("XMLHttpRequest") =>
        Unauthorized("Authentication failed")
      case _ =>
        Redirect(routes.Application.login).withSession("user_access_uri" -> request.uri)
    }
  }

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows.
   * 
   * @param request RequestHeader
   * @param ctx ExecutionContext
   */
  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful {
    request.headers.get("X-Requested-With") match {
      case Some("XMLHttpRequest") =>
        Status(play.api.http.Status.FORBIDDEN).withSession("uri" -> request.headers.get("Referer").getOrElse(""))
      case _ =>
        val follow_uri = request.session.get("uri").getOrElse(routes.Application.index.url.toString)
        Redirect(follow_uri).withSession(request.session - "uri")
    }

  }

  /**
   * A function that determines what `Authority` a user has.
   * 
   * @param user User
   * @param authority Authority 
   * @param ctx ExecutionContext
   * @return Boolean authorization result.
   */
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = authority(user)

  /**
   * Function that concludes if a user is administrator.
   * 
   * @param user User
   * @return Boolean true when a user is administrator.
   */
  def isAdministrator(user: User): Future[Boolean] = Future.successful(user.permission == "Administrator")

  /**
   * Function that concludes if a user has logged in.
   */
  def isLoggedIn(user: User): Future[Boolean] = Future.successful(true)

  /**
   * A Function that does something.
   * 
   * @param permission Permission
   * @param user User
   * @param ctx ExecutionContext
   * 
   */
  def authorization(permission: Permission)(user: User)(implicit ctx: ExecutionContext) = Future.successful((permission, user.permission) match {
    case (_, "Administrator") => true
    case (LoggedIn, "LoggedIn") => true
    case (GuestUser, "LoggedIn") => true
    case (GuestUser, "GuestUser") => true
    case _ => false
  })
}
