/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
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
import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import models.portal.user.{User, MyFollow}
import models.portal.stylist.Stylist
import models.portal.style.Style
import models.portal.salon.Salon
import models.portal.relation.SalonAndStylist

object Stylists extends MeifanNetCustomerOptionalApplication {

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
   * 查看技师所属店铺，根据传入的技师stylistId
   * 调用方法查找到所属的店铺
   * @param stylistId
   * @return
   */
  def mySalonFromStylist(stylistId: ObjectId) = StackAction { implicit request =>
    val user = User.findOneById(stylistId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val salon = Stylist.mySalon(user.id)
    val stylist = Stylist.findOneByStylistId(user.id)
    stylist match {
      case Some(sty) => {
        loggedIn.map { loginUser =>
          Ok(views.html.stylist.management.stylistMySalon(user, followInfo, loginUser.id, true, sty, salon))
        }.getOrElse(
          Ok(views.html.stylist.management.stylistMySalon(user, followInfo, new ObjectId, false, sty, salon)))
      }
      case None => NotFound
    }
  }

  /**
   * 查看技师的所有发型
   * @param stylistId - 技师stylistId
   * @return
   */
  def findStylesByStylist(stylistId: ObjectId) = StackAction { implicit request =>
    val styles = Style.findByStylistId(stylistId)
    val user = User.findOneById(stylistId).get
    val stylist = Stylist.findOneByStylistId(stylistId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    loggedIn.map { loginUser =>
      Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = loginUser.id, logged = true, stylist = stylist, styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll("Hairdressing"), isFirstSearch = true, isStylist = true))
    } getOrElse {
      Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = new ObjectId, logged = false, stylist = stylist, styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll("Hairdressing"), isFirstSearch = true, isStylist = true))
    }

  }

  /**
   * 技师后台发型检索
   * 根据传入后台的发型检索条件form，查找到满足的所有发型
   * @param stylistId - 技师stylistId primary key
   *@return
   */
  def findStylesBySerach(stylistId: ObjectId) = StackAction { implicit request =>
    val user = User.findOneById(stylistId).get
    val stylist = Stylist.findOneByStylistId(stylistId)
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Styles.styleSearchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      {
        case (styleSearch) => {
          val styles = Style.findStylesByStylistBack(styleSearch, stylist.get.stylistId)
          loggedIn.map { loginUser =>
            Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = loginUser.id, logged = true, stylist = stylist.get, styles = styles, styleSearchForm = Styles.styleSearchForm.fill(styleSearch), styleParaAll = Style.findParaAll("Hairdressing"), isFirstSearch = false, isStylist = true))
          } getOrElse {
            Ok(views.html.stylist.management.stylistStyles(user = user, followInfo = followInfo, loginUserId = new ObjectId, logged = false, stylist = stylist.get, styles = styles, styleSearchForm = Styles.styleSearchForm.fill(styleSearch), styleParaAll = Style.findParaAll("Hairdressing"), isFirstSearch = false, isStylist = true))
          }
        }
      })
  }

  /**
   * 查看后台的单个发型的详细信息
   * @param styleId - 发型id
   * @param stylistId - 技师stylistId
   * @return
   */
  def getbackstageStyleItem(styleId: ObjectId, stylistId: ObjectId) = StackAction { implicit request =>
    val user = User.findOneById(stylistId).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val style = Style.findOneById(styleId)
    style match {
      case Some(style) => {
        loggedIn.map { loginUser =>
          Ok(views.html.stylist.management.styleItem(user = user, followInfo = followInfo, loginUserId = loginUser.id, logged = true, style = style))
        } getOrElse {
          Ok(views.html.stylist.management.styleItem(user = user, followInfo = followInfo, loginUserId = new ObjectId, logged = false, style = style))
        }
      }
      case None => NotFound
    }

  }

  /**
   * 查看他人技师的主页，或者普通用户查看技师的主页
   * @param stylistId - 技师stylistId
   * @return
   */
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

  /**
   * 普通用户申请成为技师时，或者技师要申请绑定店铺时
   * ajax 验证输入店铺账号id，该店铺是否存在
   * @param salonAccountId
   * @return YES or NO
   */
  def checkSalonIsExit(salonAccountId: String) = Action {
    println("salonId " + salonAccountId)
    Salon.findOneByAccountId(salonAccountId).map { salon =>
      Ok("YES")
    } getOrElse {
      Ok("NO")
    }
  }

  /**
   * ajax check
   * 店铺搜索技师时
   * 验证技师时否存在
   * 验证要搜索的技师与其它店铺关系是否为绑定
   * @param userId
   * @param salonId
   * @return
   */
  def checkStylistIsExist(userId: String, salonId: ObjectId) = Action {
    val user = User.findOneByUserId(userId)
    user match {
      case Some(u) =>
        val stylist = Stylist.findOneByStylistId(u.id)
        stylist match {
          case Some(sty) => {
            val isValid = SalonAndStylist.checkSalonAndStylistValid(salonId, sty.stylistId)
            if (isValid) {
              Ok("YES")
            } else {
              if (SalonAndStylist.findByStylistId(sty.stylistId).isEmpty) {
                Ok("YES")
              } else {
                Ok("NO")
              }
            }
          }
          case None => Ok("NO")
        }
      case None => Ok("NO")
    }

  }

}
