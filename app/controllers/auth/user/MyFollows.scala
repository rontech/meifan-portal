package controllers.auth

import play.api.mvc._
import models._
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc._
import scala.concurrent._
import ExecutionContext.Implicits.global
import controllers._

object MyFollows extends Controller with AuthElement with AuthConfigImpl {

    /**
     * 取消关注
     */
    def cancelFollow(followedId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        MyFollow.delete(user.id, followedId)
        //    Redirect(routes.Users.myFollowedSalon(userId))
        Redirect(noAuth.routes.MyFollows.followedSalon(user.id))
    }

    /**
     * 添加关注或收藏
     */
    def addFollow(followId: ObjectId, followObjType: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        if (!MyFollow.checkIfFollow(user.id, followId)) {
            MyFollow.create(user.id, followId, followObjType)
        }
        if (followObjType == FollowType.FOLLOW_SALON || followObjType == FollowType.FOLLOW_STYLIST || followObjType == FollowType.FOLLOW_USER)
            UserMessage.sendFollowMsg(user, followId, followObjType)
        Redirect(auth.routes.Users.myPage())
    }

    /**
     * 收藏的优惠劵
     */
    def followedCoupon(userId : ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
        val loginUser = loggedIn
        val user = User.findOneById(userId).get
        val followInfo = MyFollow.getAllFollowInfo(userId)
        Ok(views.html.user.followedCoupon(user,followInfo,loginUser.id))
    }
    
    /**
     * 收藏的博客
     */
    def followedBlog(userId : ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
        val loginUser = loggedIn
        val user = User.findOneById(userId).get
        val followInfo = MyFollow.getAllFollowInfo(user.id)
    	Ok(views.html.user.followedBlog(user, followInfo, loginUser.id))
    }

    /**
     * 收藏的风格
     */
    def followedStyle(userId : ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
        val loginUser = loggedIn
        val user = User.findOneById(userId).get
        val followInfo = MyFollow.getAllFollowInfo(user.id)
    	Ok(views.html.user.myFollowStyle(user, followInfo, loginUser.id))
    }
}