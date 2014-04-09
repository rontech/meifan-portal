package controllers.noAuth

import play.api.mvc._
import models._
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc._
import scala.concurrent._
import ExecutionContext.Implicits.global
import controllers._

object MyFollows extends Controller with OptionalAuthElement with UserAuthConfigImpl {

    /**
     * 列表显示关注的沙龙
     */
    def followedSalon(id: ObjectId) = StackAction{ implicit request =>
        val user = User.findOneById(id).get
        val followInfo = MyFollow.getAllFollowInfo(id)
        loggedIn.map{loginUser =>
        Ok(views.html.user.followedSalon(user, followInfo, loginUser.id, true))
      }getOrElse{
    	Ok(views.html.user.followedSalon(user, followInfo))
      }
    }

    /**
     * 列表显示关注的技师
     */
    def followedStylist(id: ObjectId) = StackAction{ implicit request =>
      	val user = User.findOneById(id).get
        val followInfo = MyFollow.getAllFollowInfo(id)
        loggedIn.map{loginUser =>
        Ok(views.html.user.followedStylist(user, followInfo, loginUser.id, true))
      }getOrElse{
    	Ok(views.html.user.followedStylist(user, followInfo))
      }
    }

    /**
     * 列表显示关注的其他用户
     */
    def followedUser(id: ObjectId) = StackAction{ implicit request =>
       val user = User.findOneById(id).get
        val followInfo = MyFollow.getAllFollowInfo(id)
        loggedIn.map{loginUser =>
        Ok(views.html.user.followedUser(user, followInfo, false, loginUser.id, true))
      }getOrElse{
    	Ok(views.html.user.followedUser(user, followInfo, false))
      }
    }
    
     /**
     * 列表显示的粉丝
     */
    def followers(id: ObjectId) = StackAction { implicit request =>
        val user = User.findOneById(id).get
        val followInfo = MyFollow.getAllFollowInfo(id)
        loggedIn.map{loginUser =>
            Ok(views.html.user.followedUser(user, followInfo, true, loginUser.id, true))
        }getOrElse{
            Ok(views.html.user.followedUser(user, followInfo, true))
        }
    }

}