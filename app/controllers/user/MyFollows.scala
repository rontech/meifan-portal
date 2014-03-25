package controllers

import play.api.mvc._
import models._
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc._
import scala.concurrent._
import ExecutionContext.Implicits.global

object MyFollows extends Controller with AuthElement with AuthConfigImpl {

    val FOLLOW_SALON = "salon"
    val FOLLOW_STYLIST = "stylist"
    val FOLLOW_USER = "user"
    val FOLLOW_STYLE = "style"
    val FOLLOW_BLOG = "blog"
    val FOLLOW_COUPON = "coupon"

    /**
     * 取消关注
     */
    def cancelFollow(followedId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        MyFollow.delete(user.id, followedId)
        //    Redirect(routes.Users.myFollowedSalon(userId))
        Redirect(routes.MyFollows.followedSalon(user.id))
    }

    /**
     * 添加关注或收藏
     */
    def addFollow(followId: ObjectId, followObjType: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        if (!MyFollow.checkIfFollow(user.id, followId)) {
            MyFollow.create(user.id, followId, followObjType)
        }
        if (followObjType == FOLLOW_SALON || followObjType == FOLLOW_STYLIST || followObjType == FOLLOW_USER)
            UserMessage.sendFollowMsg(user, followId, followObjType)
        Redirect(routes.Users.myPage())
    }

    /**
     * 列表显示关注的沙龙
     */
    def followedSalon(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val loginUser = loggedIn
        val user = User.findOneById(id).get
        val followInfo = MyFollow.getAllFollowInfo(id)
        Ok(views.html.user.showAllFollowSalon(followInfo, user, loginUser.id))
    }

    /**
     * 列表显示关注的技师
     */
    def followedStylist(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val loginUser = loggedIn
        val user = User.findOneById(id).get
        val followInfo = MyFollow.getAllFollowInfo(id)
        Ok(views.html.user.showAllFollowStylist(followInfo, user, loginUser.id))
    }

    /**
     * 列表显示关注的其他用户
     */
    def followedUser(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val loginUser = loggedIn
        val user = User.findOneById(id).get
        val followInfo = MyFollow.getAllFollowInfo(id)
        Ok(views.html.user.showAllFollowUser(followInfo, user, loginUser.id))
    }

    /**
     * 列表显示我的粉丝
     */
    def followers(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val loginUser = loggedIn
        val user = User.findOneById(id).get
        val followInfo = MyFollow.getAllFollowInfo(id)
        Ok(views.html.user.showMyFollowers(followInfo, user, loginUser.id))
    }

    /**
     * 我收藏的优惠劵
     */
    def followedCoupon() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        Ok(views.html.user.myFollowCoupon(user,followInfo))
    }

    /**
     * 我收藏的博客
     */
    def followedBlog(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        val followInfo = MyFollow.getAllFollowInfo(id)
        Ok(views.html.user.myFollowBlog(user,followInfo))
    }

    /**
     * 我收藏的风格
     */
    def followedStyle(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        val followInfo = MyFollow.getAllFollowInfo(id)
        Ok(views.html.user.myFollowStyle(user,followInfo))
    }

    /**
     * 我收藏的店铺动态
     */
    def followedSalonActi() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        //TODO
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        Ok(views.html.user.myFollowSalonActi(user,followInfo))
    }
}