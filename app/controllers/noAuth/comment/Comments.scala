package controllers.noAuth

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.templates._
import models._
import com.mongodb.casbah.Imports.ObjectId
import jp.t2v.lab.play2.auth._
import scala.concurrent.ExecutionContext.Implicits.global
import controllers._

object Comments extends Controller with OptionalAuthElement with UserAuthConfigImpl {
  
  /**
   * 查找店铺下的评论，现在只是对coupon做评论，还没有对预约做评论
   */
  def findBySalon(salonId: ObjectId) = StackAction { implicit request =>
      val user = loggedIn
      val salon: Option[Salon] = Salon.findOneById(salonId)
      val comments: List[Comment] = Comment.findBySalon(salonId)
      // navigation bar
      val navBar = SalonNavigation.getSalonNavBar(salon) ::: List((Messages("salon.comments"), ""))
      // Jump to blogs page in salon.
      // TODO: process the salon not exist pattern.
      Ok(views.html.salon.store.salonInfoCommentAll(salon = salon.get, comments = comments, navBar = navBar, user=user))
  }
  
  def clean() = {
    Comment.list = Nil
  }
  
}
