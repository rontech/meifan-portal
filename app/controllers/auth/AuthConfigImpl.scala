package controllers

import jp.t2v.lab.play2.auth._
import scala.concurrent._
import reflect.{ClassTag, classTag}
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

import models._

trait AuthConfigImpl extends AuthConfig {

  type Id = String

  type User = models.User

  type Authority = User => Future[Boolean]

  val idTag: ClassTag[Id] = classTag[Id]

  val sessionTimeoutInSeconds = 3600

  def resolveUser(userId: Id)(implicit ctx: ExecutionContext) = Future.successful(User.findOneByUserId(userId))

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index.url.toString)
    Future.successful(Redirect(uri).withSession(request.session - "access_uri"))
  }
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.index))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) =
		  Future.successful(Redirect(routes.Application.login).withSession("access_uri" -> request.uri))

  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Forbidden("no permission"))

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = authority(user)

  def isAuthor(username : String)(user : User) : Future[Boolean] = Future{User.findOneByUserId(username).map(_ == user).get}
  
  def requireAdminUser(user: User): Future[Boolean] = Future.successful(user.permission == "Administrator")
 
  def authorization(permission: Permission)(user : User)(implicit ctx: ExecutionContext) = Future.successful((permission, user.permission) match {
    case ( _, "Administrator") => true
    case (LoggedIn, "LoggedIn") => true
    case (GuestUser, "LoggedIn") => true
    case (GuestUser, "GuestUser") => true
    case _ => false
  })
}