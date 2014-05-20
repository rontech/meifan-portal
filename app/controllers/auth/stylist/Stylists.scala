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
package controllers.auth

import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent._
import play.api.i18n.Messages
import com.mongodb.casbah.gridfs.Imports._
import com.mongodb.casbah.gridfs.GridFS
import play.api.libs.iteratee.Enumerator
import controllers._
import controllers.noAuth.Styles
import play.api.data.validation.Constraints._
import play.api.Routes
import routes.javascript._
import utils.Const._
import com.meifannet.framework.db._

object Stylists extends Controller with LoginLogout with AuthElement with UserAuthConfigImpl {

  val stylistForm: Form[Stylist] = Form(
    mapping(
      "workYears" -> number.verifying(min(1), max(100)),
      "position" -> list(
        mapping(
          "positionName" -> text,
          "industryName" -> text) {
            (positionName, industryName) => IndustryAndPosition(new ObjectId, positionName, industryName)
          } {
            industryAndPosition => Some(industryAndPosition.positionName, industryAndPosition.industryName)
          }),
      "goodAtImage" -> list(text),
      "goodAtStatus" -> list(text),
      "goodAtService" -> list(text),
      "goodAtUser" -> list(text),
      "goodAtAgeGroup" -> list(text),
      "myWords" -> text,
      "mySpecial" -> text,
      "myBoom" -> text,
      "myPR" -> text,
      "myPics" -> list(
        mapping(
          "fileObjId" -> text,
          "picUse" -> text,
          "showPriority" -> optional(number),
          "description" -> optional(text)) {
            (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriority, description)
          } {
            onUsePicture => Some((onUsePicture.fileObjId.toString, onUsePicture.picUse, onUsePicture.showPriority, onUsePicture.description))
          })) {
        (workYears, position, goodAtImage, goodAtStatus, goodAtService,
        goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR, myPics) =>
          Stylist(new ObjectId, new ObjectId(), workYears, position, goodAtImage, goodAtStatus,
            goodAtService, goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR,
            myPics, false, false)
      } {
        stylist =>
          Some(stylist.workYears, stylist.position,
            stylist.goodAtImage, stylist.goodAtStatus, stylist.goodAtService, stylist.goodAtUser,
            stylist.goodAtAgeGroup, stylist.myWords, stylist.mySpecial, stylist.myBoom, stylist.myPR, stylist.myPics)
      })

  /**
   * 技师同意salon邀请
   * @param applyRecordId - 邀请记录的id
   * @return 我的主页
   */
  def agreeSalonInvite(applyRecordId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val record = SalonStylistApplyRecord.findOneById(applyRecordId)
    record.map { re =>
      if(re.verifiedResult == 0) {
        //调用同意申请方法
        SalonStylistApplyRecord.agreeApply(re)
        //更新技师的有效状态，为有效技师
        Stylist.becomeStylist(user.id)
        //在店铺与技师关系表中绑定两者的关系
        SalonAndStylist.entrySalon(re.salonId, user.id)
        Redirect(routes.Stylists.myHomePage)
        //Redirect(noAuth.routes.Stylists.mySalon(stylistId))
      } else {
        NotFound
      }
    } getOrElse {
      NotFound
    }
  }

  /**
   * 技师拒绝salon邀请
   * @param applyRecordId - 邀请记录的id primary key
   * @return 重定向到我的主页
   */
  def rejectSalonInvite(applyRecordId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val record = SalonStylistApplyRecord.findOneById(applyRecordId)
    record.map{ re =>
      if(re.verifiedResult == 0) {
        //调用拒绝申请方法
        SalonStylistApplyRecord.rejectApply(re)
        Redirect(routes.Stylists.myHomePage)
      } else NotFound
    } getOrElse {
      NotFound
    }
  }

  /**
   * 技师查看来自店铺的邀请
   * 根据本人的用户id，在店铺与技师申请履历表中查找到当前所有店铺
   * 对其邀请
   * @return
   */
  def applyFromSalon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val applySalons = SalonStylistApplyRecord.findApplingSalon(user.id)
    val stylist = Stylist.findOneByStylistId(user.id)
    stylist match {
      case Some(sty) => {
        Ok(views.html.stylist.management.stylistApplyingSalons(user, followInfo, user.id, true, applySalons, sty))
      }
      case None => NotFound
    }
  }

  /**
   * 跳转到更新技师信息页面
   * transfer param
   * user - 当前登录的用户
   * followInfo - 关注的信息
   * user.id - 当前所在页面用户的id
   * true - 登录flag
   * sty - 技师
   * stylistUpdate - 更新技师form
   * goodAtStylePara - 擅长发型属性的集合
   * @return 跳转到更新技师信息页面
   */
  def stylistInfo = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    //擅长发型属性的集合
    val goodAtStylePara = Stylist.findGoodAtStyle
    val stylist = Stylist.findOneByStylistId(user.id)
    stylist match {
      case Some(sty) => {
        val stylistUpdate = stylistForm.fill(sty)
        Ok(views.html.stylist.management.updateStylistInfo(user, followInfo, user.id, true, sty, stylistUpdate, goodAtStylePara))
      }
      case None => NotFound
    }
  }

  /**
   * 后台更新技师信息处理
   * @param sid - 技师id
   * @return 重定向到当前用户的主页
   */
  def updateStylistInfo(sid: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(sid) _) { implicit request =>
    val user = loggedIn
    val sty = Stylist.findOneByStylistId(user.id).get
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    stylistForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      {
        case (stylist) => {
          Stylist.save(stylist.copy(id = sty.id, stylistId = user.id)) //需修改图片更新
          Redirect(auth.routes.Users.myPage())
        }
      })
  }

  /**
   * 技师与店铺解除关系
   * @param salonId
   * @return 我的主页
   */
  def removeSalon(salonId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    SalonAndStylist.leaveSalon(salonId, user.id)
    Redirect(auth.routes.Stylists.myHomePage)
  }

  /**
   * 跳转至技师更换图片画面
   * @param roles
   * @return
   */
  def updateStylistImage(roles: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val stylist = Stylist.findOneByStylistId(user.id).get
    Ok(views.html.stylist.management.updateStylistImage(user = user, stylist = stylist, followInfo = followInfo, loginUserId = user.id, logged = true, roles = roles))
  }

  /**
   * 更换头像，图片后台存储
   * @param role - 更新头像的角色，stylist or user
   * @return
   */
  def toUpdateStylistImage(role: String) = Action(parse.multipartFormData) { request =>
    request.body.file("photo") match {
      case Some(photo) =>
        val db = DBDelegate.picDB
        val gridFs = GridFS(db)
        val uploadedFile = gridFs.createFile(photo.ref.file)
        uploadedFile.contentType = photo.contentType.orNull
        uploadedFile.save()
        //根据存储返回的图片objectId 与 role 调用saveStylistImg
        Redirect(routes.Stylists.saveStylistImg(uploadedFile._id.get, role))
      case None => BadRequest("no photo")
    }

  }

  /**
   * 根据图片的id，角色类型判断，
   * 如果普通用户，表示用户申请成为技师存储图片后跳转
   * 如果是技师就定向到技师主页
   * @param imgId - 图片存储后返回的ObjectId
   * @param role - 角色类型
   * @return
   */
  def saveStylistImg(imgId: ObjectId, role: String) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val stylist = Stylist.findOneByStylistId(user.id)
    val goodAtStylePara = Stylist.findGoodAtStyle
    stylist match {
      case Some(sty) => {
        Stylist.updateImages(sty, imgId)
        if (role.equals("user")) {
          Ok(views.html.user.applyStylist(controllers.auth.Users.stylistApplyForm, user, goodAtStylePara, followInfo, true))
        } else if (role.equals("stylist")) {
          Redirect(routes.Stylists.myHomePage)
        } else {
          NotFound
        }
      }
      case None => NotFound
    }

  }

  /**
   * 跳转技师后台发型更新页面
   * @param styleId - 发型id，主键
   *@return
   */
  def styleUpdateByStylist(styleId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
    implicit request =>
      val styleOne: Option[Style] = Style.findOneById(styleId)
      val user = loggedIn
      val stylist = Stylist.findOneByStylistId(user.id)
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      styleOne match {
        case Some(style) => Ok(views.html.stylist.management.updateStylistStyles(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, stylist = stylist.get, style = style, styleUpdateForm = Styles.styleUpdateForm.fill(style), styleParaAll = Style.findParaAll))
        case None => NotFound
      }
  }

  /**
   * 技师后台发型更新处理
   * @return
   */
  def styleUpdateNewByStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
    implicit request =>
      val user = loggedIn
      val stylist = Stylist.findOneByStylistId(user.id)
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      Styles.styleUpdateForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index()),
        {
          case (styleUpdateForm) => {
            Style.save(styleUpdateForm.copy(id = styleUpdateForm.id), WriteConcern.Safe)
            Redirect(noAuth.routes.Stylists.findStylesByStylist(user.id))
          }
        })
  }

  /**
   * 技师后台发型删除，使之无效即可
   * 根据传入的发型id，调用方法使之无效
   * @param id - 发型id
   * @return
   */
  def styleToInvalidByStylist(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val stylist = Stylist.findOneByStylistId(user.id)
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Style.styleToInvalid(id)
    Redirect(noAuth.routes.Stylists.findStylesByStylist(user.id))
  }

  /**
   * 跳转至技师发型新建页面
   * transfer param
   * user - 登录的用户
   * followInfo - 关注的内容
   * loginUserId - 登录用户的id
   * logged - 是否登录flag
   * styleAddForm - 添加发型form
   * styleParaAll - 发型属性数据
   * stylists - 当前技师，但要变成list
   * isStylist - 是否是技师flag
   * @return 技师后台添加发型
   */
  def styleAddByStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
    //此处为新发型登录
    implicit request =>
      val user = loggedIn
      val stylist = Stylist.findOneByStylistId(user.id)
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      var stylists: List[Stylist] = Nil
      stylists :::= stylist.toList
      Ok(views.html.stylist.management.addStyleByStylist(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, styleAddForm = Styles.styleAddForm, styleParaAll = Style.findParaAll, stylists = stylists, isStylist = true))
  }

  /**
   * 技师新建发型后台处理
   * @return 技师后台发型一览
   */
  def newStyleAddByStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
    implicit request =>
      val user = loggedIn
      val stylist = Stylist.findOneByStylistId(user.id)
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      Styles.styleAddForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index()),
        {
          case (styleAddForm) => {
            Style.save(styleAddForm)
            Redirect(noAuth.routes.Stylists.findStylesByStylist(stylist.get.stylistId))
          }
        })
  }

  /**
   * 查看技师本人的当前申请
   * 根据本人的用户id，到店铺与技师关系表中查找到申请记录
   * 根据记录中的店铺id找到所对应的salon
   * @return
   */
  def findStylistApplying = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val record = SalonStylistApplyRecord.findOneStylistApRd(user.id)
    record.map { re =>
      val salon = Salon.findOneById(re.salonId)
      salon.map { sa =>
        Ok(views.html.stylist.management.stylistApplyingItem(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = Tuple2(sa, re.id)))
      } getOrElse {
        NotFound
      }

    } getOrElse {
      NotFound
    }

  }

  /**
   * 技师要申请店铺
   * @return 跳转至技师申请店铺页面
   */
  def wantToApply = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val salon = Salon.findOneById(new ObjectId)
    Ok(views.html.stylist.management.stylistApplyPage(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = salon))
  }

  /**
   * 我的主页
   * 如果技师当前绑定店铺就跳转到预约画面
   * 如果关系无效，就跳转至作为普通用户的主页
   * @return
   */
  def myHomePage = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val stylist = Stylist.findOneByStylistId(user.id)
    stylist.map { sty =>
      SalonAndStylist.findByStylistId(sty.stylistId).map { re =>
        Ok(views.html.stylist.management.myHomePage(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, stylist = sty))
      } getOrElse {
        Ok(views.html.user.myPageRes(user, followInfo))
      }
    } getOrElse {
      Ok(views.html.user.myPageRes(user, followInfo))
    }

  }

  /**
   * 取消我的申请
   * @param applyRecordId - 申请店铺记录的id
   * @return
   */
  def cancelMyApplying(applyRecordId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val record = SalonStylistApplyRecord.findOneById(applyRecordId)
    record.map { re =>
      SalonStylistApplyRecord.removeById(re.id, WriteConcern.Safe)
      Redirect(routes.Stylists.myHomePage)
    } getOrElse {
      Ok(views.html.stylist.management.stylistApplyingItem(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = null))
    }
  }

  /**
   * 技师申请店铺，将申请记录插入到记录表中
   * @param salonId
   * @return 重定向到我的主页
   */
  def toApplySalon(salonId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    Salon.findOneById(salonId).map { salon =>
      val applyRecord = new SalonStylistApplyRecord(new ObjectId, salonId, user.id, 1, new Date, 0, None)
      SalonStylistApplyRecord.save(applyRecord)
      Redirect(routes.Stylists.myHomePage)
    } getOrElse {
      Redirect(routes.Stylists.myHomePage)
    }

  }

  /**
   * 从form请求中获得输入的店铺accountId，然后获得该店铺
   * 跳转到申请店铺页面
   * @return
   */
  def findSalonBySalonAccountId = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val salonAccountId = request.getQueryString("salonId").get
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val salon = Salon.findByAccountId(salonAccountId)
    Ok(views.html.stylist.management.stylistApplyPage(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = salon))

  }

  /**
   * ajax查看发型的唯一性
   * @param value - style id 或其他的主键
   * @param key - 匹配item类型的值
   * @return true or false toString
   */
  def itemIsExist(value: String, key: String) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val stylist = loggedIn
    key match {
      case ITEM_TYPE_STYLE =>
        Ok((Style.checkStyleIsExist(value, stylist.id)).toString)
    }
  }
}
