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
import controllers._

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
  
  /**
   *  查看技师所属店铺
   */
  def mySalon(stylistId: ObjectId) = StackAction{implicit request =>
	    val user = User.findOneById(stylistId).get
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    val salon = Stylist.mySalon(stylistId)
	    val stylist = Stylist.findOneByStylistId(user.id)
	    stylist match {
	      case Some(sty) => {
	       loggedIn.map{loginUser => 
	         Ok(views.html.stylist.management.stylistMySalon(user, followInfo, loginUser.id, true, sty, salon))
	       }getOrElse{
	         Ok(views.html.stylist.management.stylistMySalon(user, followInfo, new ObjectId, false, sty, salon))
	       }
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
                errors => BadRequest(views.html.index("")),
                {
                    case (styleSearch) => {
                        val styles = Style.findStylesByStylistBack(styleSearch,stylist.get.stylistId)
                        loggedIn.map{loginUser =>
                            Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = loginUser.id , logged = true, stylist = stylist.get, styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll, isFirstSearch = false, isStylist = true))
                        }getOrElse{
                            Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = new ObjectId , logged = false, stylist = stylist.get, styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll, isFirstSearch = false, isStylist = true))
                        }
                    }
                })
    }
  
  def otherHomePage(stylistId: ObjectId) = StackAction { implicit request =>
     val user = User.findOneById(stylistId).get
     val followInfo = MyFollow.getAllFollowInfo(user.id)
     val stylist = Stylist.findOneByStylistId(stylistId).get
     val styles = Style.findByStylistId(stylistId)
     
     val blgs = Blog.getBlogByUserId(user.userId)
     val blog = if(blgs.length > 0) Some(blgs.head) else None
     loggedIn.map{loginUser =>
       Ok(views.html.stylist.management.OtherHomePage(user = user, followInfo = followInfo, loginUserId = loginUser.id, logged = true,  latestBlog = blog, stylist = stylist, styles = styles ))
     }getOrElse{
       Ok(views.html.stylist.management.OtherHomePage(user = user, followInfo = followInfo, loginUserId = new ObjectId, logged = false,  latestBlog = blog, stylist = stylist, styles = styles ))
     }
     
     
  }
  
  def checkSalonIsexitBySalonAccountId(salonAccountId: String) = Action {
      println("ajax check ")
      Salon.findByAccountId(salonAccountId).map{ salon =>
          Ok("yes")
      }getOrElse{
          Ok("no")
      }
  }
  
}