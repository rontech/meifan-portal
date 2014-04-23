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
import controllers._

object Stylists extends Controller with OptionalAuthElement with UserAuthConfigImpl{
	
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
  
  /**
   *  查看技师所属店铺
   */
  def mySalonFromStylist(stylistId: ObjectId) = StackAction { implicit request =>
	    val user = User.findOneById(stylistId).get
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    val salon = Stylist.mySalon(user.id)
	    val stylist = Stylist.findOneByStylistId(user.id)
	    stylist match {
	      case Some(sty) => {
             loggedIn.map{loginUser =>
	            Ok(views.html.stylist.management.stylistMySalon(user, followInfo, loginUser.id, true, sty, salon))
	          }.getOrElse(
                Ok(views.html.stylist.management.stylistMySalon(user, followInfo, new ObjectId, false, sty, salon))
                 )
          }
	      case None => NotFound
	    }
  }
  
  /**
  *  查看技师发型
  */
  def findStylesByStylist(stylistId: ObjectId) = StackAction { implicit request =>
    val styles = Style.findByStylistId(stylistId)
    val user = User.findOneById(stylistId).get
    val stylist = Stylist.findOneByStylistId(stylistId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    loggedIn.map{loginUser =>
	Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = loginUser.id , logged = true, stylist = stylist, styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll, isFirstSearch = true, isStylist = true))
    }getOrElse{
	Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = new ObjectId , logged = false, stylist = stylist, styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll, isFirstSearch = true, isStylist = true))
    }
    
  }
  
  /**
   * 后台发型检索
   */
  def findStylesBySerach(stylistId: ObjectId) = StackAction { implicit request =>
    val user = User.findOneById(stylistId).get
    val stylist = Stylist.findOneByStylistId(stylistId)
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Styles.styleSearchForm.bindFromRequest.fold(
                errors => BadRequest(views.html.index()),
                {
                    case (styleSearch) => {
                        val styles = Style.findStylesByStylistBack(styleSearch,stylist.get.stylistId)
                        loggedIn.map{loginUser =>
                            Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = loginUser.id , logged = true, stylist = stylist.get, styles = styles, styleSearchForm = Styles.styleSearchForm.fill(styleSearch), styleParaAll = Style.findParaAll, isFirstSearch = false, isStylist = true))
                        }getOrElse{
                            Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = new ObjectId , logged = false, stylist = stylist.get, styles = styles, styleSearchForm = Styles.styleSearchForm.fill(styleSearch), styleParaAll = Style.findParaAll, isFirstSearch = false, isStylist = true))
                        }
                    }
                })
    }
  
  /**
   * 后台发型基本信息查看
   */
  def getbackstageStyleItem(styleId : ObjectId, stylistId : ObjectId) = StackAction {implicit request =>
    val user = User.findOneById(stylistId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val style = Style.findOneById(styleId)
    style match {
        case Some(style) => {
            loggedIn.map{loginUser =>
                Ok(views.html.stylist.management.styleItem(user = user, followInfo = followInfo, loginUserId = loginUser.id , logged = true, style = style))
            }getOrElse{
                Ok(views.html.stylist.management.styleItem(user = user, followInfo = followInfo, loginUserId = new ObjectId , logged = false, style = style))
                }
        }
        case None => NotFound
    }
    
  }
    
  def otherHomePage(stylistId: ObjectId) = StackAction { implicit request =>
     val user = User.findOneById(stylistId).get
     /*
     val followInfo = MyFollow.getAllFollowInfo(user.id)
     val stylist = Stylist.findOneByStylistId(stylistId).get
     val styles = Style.findByStylistId(stylistId)
     
     val blgs = Blog.getBlogByUserId(user.userId)
     val blog = if(blgs.length > 0) Some(blgs.head) else None
     loggedIn.map{loginUser =>
       Ok(views.html.stylist.management.OtherHomePage(user = user, followInfo = followInfo, loginUserId = loginUser.id, logged = true,  latestBlog = blog, stylist = stylist, styles = styles ))
     }getOrElse{
       Ok(views.html.stylist.management.OtherHomePage(user = user, followInfo = followInfo, loginUserId = new ObjectId, logged = false,  latestBlog = blog, stylist = stylist, styles = styles ))
     }*/
     Redirect(noAuth.routes.Stylists.findStylesByStylist(user.id))
     
  }
  
  def checkSalonIsExit(salonAccountId: String) = Action {
      Salon.findByAccountId(salonAccountId).map{ salon =>
          Ok("YES")
      }getOrElse{
          Ok("NO")
      }
  }
  
  def cutImg = Action {
    Ok(views.html.stylist.cutImg(""))
  }
  
  def checkStylistIsExist(userId: String) = Action {
     val user = User.findOneByUserId(userId)
     user match {
       case Some(user) => {
         val stylist = Stylist.findOneById(user.id)
         stylist match {
           case Some(sty) => Ok("YES")
           case None => Ok("NO")
         }
       }
       case None => Ok("NO")
     }
     
    }
}