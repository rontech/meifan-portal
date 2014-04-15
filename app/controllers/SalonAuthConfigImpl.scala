package controllers

import jp.t2v.lab.play2.auth._
import reflect.{ClassTag, classTag}
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.{ExecutionContext, Future}
import models._


trait SalonAuthConfigImpl extends AuthConfig {

  type Id = String

  type User = models.Salon

  type Authority = User => Future[Boolean]

  val idTag: ClassTag[Id] = classTag[Id]

  val sessionTimeoutInSeconds = 3600

  def resolveUser(accountId: Id)(implicit ctx: ExecutionContext) = Future.successful(Salon.findByAccountId(accountId))

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = {
    val uri = request.session.get("salon_access_uri").getOrElse(auth.routes.Salons.salonInfoManage.url.toString)
    Future.successful(Redirect(uri).withSession(request.session - "salon_access_uri"))
  }
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.index))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) =
		  Future.successful(Redirect(routes.Application.salonLogin).withSession("salon_access_uri" -> request.uri))

  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Forbidden("no permission"))

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = authority(user)

  def isLoggedIn(user : User) :Future[Boolean] = Future.successful(true)

  /*def authorization(permission: Permission)(user : User)(implicit ctx: ExecutionContext) = Future.successful((permission, user.permission) match {
    case ( _, "Administrator") => true
    case (LoggedIn, "LoggedIn") => true
    case (GuestUser, "LoggedIn") => true
    case (GuestUser, "GuestUser") => true
    case _ => false
  })*/
}