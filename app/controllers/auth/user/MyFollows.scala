package controllers.auth

import play.api.mvc._
import models._
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc._
import scala.concurrent._
import ExecutionContext.Implicits.global
import controllers._
import java.util.Date
import java.util.Calendar

object MyFollows extends Controller with AuthElement with UserAuthConfigImpl {

    /**
     * 取消关注
     */
    def cancelFollow(followedId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        MyFollow.delete(user.id, followedId)
        Redirect(request.headers.get("Referer").getOrElse(""))
    }

    /**
     * 添加关注或收藏
     */
    def addFollow(followId: ObjectId, followObjType: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
        val user = loggedIn
        MyFollow.checkIfFollow(user.id, followId) match {
            case false => {
                MyFollow.create(user.id, followId, followObjType)
                if (followObjType == FollowType.FOLLOW_SALON || followObjType == FollowType.FOLLOW_STYLIST || followObjType == FollowType.FOLLOW_USER)
                	UserMessage.sendFollowMsg(user, followId, followObjType)
                Ok("false")
            }
            case true => 
                Ok("true")
        }
    }
    
    /**
     * 收藏的优惠劵
     */
    def followedCoupon(userId : ObjectId) = StackAction(AuthorityKey -> User.isFriend(userId) _) { implicit request =>
        val loginUser = loggedIn
        val user = User.findOneById(userId).get
        val followInfo = MyFollow.getAllFollowInfo(userId)
        
        // 获取当前时间的前7天的日期，用于判断是否为新券还是旧券
        var beforeSevernDate = Calendar.getInstance()
        beforeSevernDate.setTime(new Date())
        beforeSevernDate.add(Calendar.DAY_OF_YEAR, -7)
        
        Ok(views.html.user.followedCoupon(user,followInfo,loginUser.id,beforeSevernDate.getTime()))
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
    	Ok(views.html.user.followedStyle(user, followInfo, loginUser.id))
    }
}