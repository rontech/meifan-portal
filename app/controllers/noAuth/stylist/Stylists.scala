package controllers.noAuth

import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent._
import play.api.i18n.Messages
import jp.t2v.lab.play2.auth._
import controllers.AuthConfigImpl

object Stylists extends Controller with OptionalAuthElement with AuthConfigImpl{
	
  /*def getOneStylist(stylistId: ObjectId) = StackAction{ implicit request =>
    User.findOneByUserId(stylistId).map{user =>
      val userFollowInfo = MyFollow.getAllFollowInfo(stylistId)
      loggedIn.map{loggedUser =>
         Ok(views.html.user.otherPage(user, userFollowInfo, loggedUser.id, true))      
      }getOrElse{
    	 Ok(views.html.user.otherPage(user, userFollowInfo))
      }
    }getOrElse{
      NotFound
    }
  }*/


}