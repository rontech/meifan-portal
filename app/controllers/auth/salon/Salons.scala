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

import play.api.mvc._
import views._
import java.util.Date
import controllers._
import controllers.noAuth._
import jp.t2v.lab.play2.auth._
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.gridfs.GridFS
import org.mindrot.jbcrypt.BCrypt
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import utils.Tools
import utils.Const._
import models.portal.salon._
import models.portal.common.{Address, OnUsePicture, OptContactMethod}
import models.portal.industry.Industry
import models.portal.review.Comment
import models.portal.stylist.Stylist
import models.portal.service.{ServiceType, Service}
import models.portal.coupon.{CouponServiceType, Coupon}
import models.portal.menu.Menu
import models.portal.relation.{SalonAndStylist, SalonStylistApplyRecord}
import models.portal.user.User
import models.portal.style.Style
import models.portal.reservation.{HandleReservation, ResvSreachCondition, Reservation}
import com.meifannet.portal.MeifanNetSalonApplication
import com.meifannet.framework.db.DBDelegate


object Salons extends MeifanNetSalonApplication {

  //沙龙登录Form
  val salonLoginForm = Form(mapping(
    "salonAccount" -> mapping(
      "accountId" -> nonEmptyText,
      "password" -> text)(SalonAccount.apply)(SalonAccount.unapply))(Salon.loginCheck)(_.map(s => (s.salonAccount))).verifying("Invalid userId or password", result => result.isDefined))

  //沙龙密码修改Form
  val changePassword = Form(
    mapping(
      "salonChange" -> mapping(
        "salonAccount" -> mapping(
          "accountId" -> text,
          "password" -> text)(SalonAccount.apply)(SalonAccount.unapply))(Salon.loginCheck)(_.map(s => (s.salonAccount))).verifying("Invalid OldPassword", result => result.isDefined),
      "newPassword" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[\w!@#$%&\+\"\:\?\^\&\*\(\)\.\,\;\-\_\[\]\=\`\~\<\>\/\{\}\|\\\'\s_]{6,18}+$""")),
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)) { (salonChange, newPassword) => (salonChange.get, BCrypt.hashpw(newPassword._1, BCrypt.gensalt())) } { salonChange => Some((Option(salonChange._1), ("", ""))) })

  //沙龙信息管理Form
  val salonInfoForm: Form[Salon] = Form(
    mapping(
      "salonAccount" -> mapping(
        "accountId" -> text,
        "password" -> text)(SalonAccount.apply)(SalonAccount.unapply),
      "salonName" -> text,
      "salonNameAbbr" -> optional(text),
      "salonIndustry" -> list(text),
      "homepage" -> optional(text),
      "salonAppeal" -> optional(text),
      "salonIntroduction" -> optional(mapping(
        "introHeader" -> text,
        "introContent" -> text,
        "introFooter" -> text)(BriefIntroduction.apply)(BriefIntroduction.unapply)),
      "contactMethod" -> mapping(
        "mainPhone" -> text,
        "contact" -> text,
        "email" -> text)(Contact.apply)(Contact.unapply),
      "optContactMethods" -> list(
        mapping(
          "contMethodType" -> text,
          "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
      "establishDate" -> optional(date("yyyy-MM-dd")),
      "salonAddress" -> optional(mapping(
        "province" -> text,
        "city" -> optional(text),
        "region" -> optional(text),
        "town" -> optional(text),
        "addrDetail" -> text,
        "longitude" -> optional(bigDecimal),
        "latitude" -> optional(bigDecimal),
        "accessMethodDesc" -> text)(Address.apply)(Address.unapply)),
      "workTime" -> optional(mapping(
        "openTime" -> text,
        "closeTime" -> text)(WorkTime.apply)(WorkTime.unapply)),
      "restDays" -> optional(mapping(
        "restWay" -> text,
        "restDay1" -> list(text),
        "restDay2" -> list(text)) {
          (restWay, restDay1, restDay2) => Tools.getRestDays(restWay, restDay1, restDay2)
        } {
          restDay => Some(Tools.setRestDays(restDay))
        }),
      "seatNums" -> optional(number),
      "salonFacilities" -> optional(mapping(
        "canOnlineOrder" -> boolean,
        "canImmediatelyOrder" -> boolean,
        "canNominateOrder" -> boolean,
        "canCurntDayOrder" -> boolean,
        "canMaleUse" -> boolean,
        "isPointAvailable" -> boolean,
        "isPosAvailable" -> boolean,
        "isWifiAvailable" -> boolean,
        "hasParkingNearby" -> boolean,
        "parkingDesc" -> text)(SalonFacilities.apply)(SalonFacilities.unapply)),
      "salonPics" -> list(
        mapping(
          "fileObjId" -> text,
          "picUse" -> text,
          "showPriority" -> optional(number),
          "description" -> optional(text)) {
            (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriority, description)
          } {
            salonPics => Some(salonPics.fileObjId.toString(), salonPics.picUse, salonPics.showPriority, salonPics.description)
          }),
      "registerDate" -> date) {
        (salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonAppeal, salonIntroduction, contactMethod, optContactMethods, establishDate, salonAddress,
        workTime, restDay, seatNums, salonFacilities, salonPics, registerDate) =>
          Salon(new ObjectId, salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonAppeal, salonIntroduction, contactMethod, optContactMethods, establishDate, salonAddress,
            workTime, restDay, seatNums, salonFacilities, salonPics, registerDate,new SalonStatus(1, true))
      } {
        salon =>
          Some((salon.salonAccount, salon.salonName, salon.salonNameAbbr, salon.salonIndustry, salon.homepage, salon.salonAppeal, salon.salonIntroduction, salon.contactMethod, salon.optContactMethods, salon.establishDate, salon.salonAddress,
            salon.workTime, salon.restDays, salon.seatNums, salon.salonFacilities, salon.salonPics, salon.registerDate))
      })

  //图片更新Form
  val salonPicsForm: Form[SalonPics] = Form(
    mapping(
      "salonPics" -> list(
        mapping(
          "fileObjId" -> text,
          "picUse" -> text,
          "showPriority" -> optional(number),
          "description" -> optional(text)) {
            (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriority, description)
          } {
            salonPictures => Some(salonPictures.fileObjId.toString(), salonPictures.picUse, salonPictures.showPriority, salonPictures.description)
          }))(SalonPics.apply)(SalonPics.unapply))

  /**
   * 沙龙后台预约（进行中）管理的检索Form
   */
  def ResvSreachForm: Form[ResvSreachCondition] = Form {
    mapping(
      "startExpectedDate" -> text,
      "endExpectedDate" -> text,
      "resvId" -> text,
      "nickName" -> text,
      "userTel" -> text)
    {
      (startExpectedDate, endExpectedDate, resvId, nickName, userTel) => ResvSreachCondition(startExpectedDate, endExpectedDate, resvId,
        nickName, userTel, List("0"))
    } {
      resvCondition => Some((resvCondition.startExpectedDate, resvCondition.endExpectedDate, resvCondition.resvId, resvCondition.nickName, resvCondition.userTel))
    }
  }

  /**
   * 沙龙后台预约（履历）管理的检索Form
   */
  def ResvRecordSreachForm: Form[ResvSreachCondition] = Form {
    mapping(
      "startExpectedDate" -> text,
      "endExpectedDate" -> text,
      "resvId" -> text,
      "nickName" -> text,
      "userTel" -> text,
      "resvStatus" -> list(text))(ResvSreachCondition.apply)(ResvSreachCondition.unapply)
  }

  /**
   * 沙龙登录
   * @return
   */
  def salonLogin = Action.async { implicit request =>
    salonLoginForm.bindFromRequest.fold(
      formWithErrors => { Future.successful(BadRequest(views.html.salon.salonManage.salonLogin(formWithErrors))) },
      salon => gotoLoginSucceeded(salon.get.salonAccount.accountId))
  }

  /**
   * 沙龙退出
   * @return
   */
  def salonLogout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"))
  }

  /**
   * 沙龙情报显示页面
   * @return
   */
  def salonMainInfo = StackAction(AuthorityKey -> authImproveInfo _) { implicit request =>
    val salon = loggedIn
    val industry = Industry.findAll.toList
    Ok(views.html.salon.salonManage.salonInfo("", salon, industry))
  }

  /**
   * 沙龙注册信息管理页面
   * @return
   */
  def salonRegister = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val salonInfo = salonInfoForm.fill(salon)
    val industry = Industry.findAll.toList
    Ok(views.html.salon.salonManage.salonRegisterMange(salonInfo, industry, salon))
  }

  /**
   *基本信息管理页面
   * @return
   */
  def salonBasic = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val salonInfo = salonInfoForm.fill(salon)
    val industry = Industry.findAll.toList
    Ok(views.html.salon.salonManage.salonBasic(salonInfo, industry, salon))
  }

  /**
   * 详细信息管理页面
   * @return
   */
  def salonDetail = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val salonInfo = salonInfoForm.fill(salon)
    val industry = Industry.findAll.toList
    Ok(views.html.salon.salonManage.salonDetail(salonInfo, industry, salon))
  }

  /**
   * 沙龙详细信息更新
   * @param id 登录的沙龙id
   * @return
   */
  def salonDetailUpdate(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val industry = Industry.findAll.toList
    salonInfoForm.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.salonManage.salonDetail(errors, industry, salon)),
      {
        salon =>
          Salon.save(salon.copy(id = id), WriteConcern.Safe)
          Redirect(routes.Salons.checkInfoState)
      })
  }

  /**
   * 沙龙基本信息更新
   * @param id 登录的沙龙id
   * @return
   */
  def salonBasicUpdate(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val industry = Industry.findAll.toList
    salonInfoForm.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.salonManage.salonBasic(errors, industry, salon)),
      {
        salon =>
          Salon.save(salon.copy(id = id), WriteConcern.Safe)
          Redirect(routes.Salons.checkInfoState)
      })
  }

  /**
   * 沙龙注册信息更新
   * @param id 登录的沙龙id
   * @return
   */
  def salonRegisterUpdate(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val industry = Industry.findAll.toList
    salonInfoForm.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.salonManage.salonRegisterMange(errors, industry, salon)),
      {
        salon =>
          Salon.save(salon.copy(id = id), WriteConcern.Safe)
          Redirect(routes.Salons.checkInfoState)
      })
  }

  /**
   * 沙龙密码修改页面
   * @return
   */
  def password = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val changeForm = Salons.changePassword.fill(salon, "")
    Ok(views.html.salon.admin.salonChangePassword("", changeForm, salon))
  }

  /**
   * 沙龙密码修改处理
   * @param accountId 登录的沙龙账号
   * @return
   */
  def salonChangePassword(accountId: String) = StackAction(AuthorityKey -> Salon.isOwner(accountId) _) { implicit request =>
    val salon = loggedIn
    changePassword.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.admin.salonChangePassword("", errors, salon)),
      {
        case (salon, main) =>
          Salon.save(salon.copy(salonAccount = new SalonAccount(accountId, main)), WriteConcern.Safe)
          Redirect(routes.Salons.salonLogout)
      })
  }

  /**
   * 沙龙头像(LOGO)更新页面
   * @return
   */
  def addImage = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Ok(views.html.salon.salonManage.salonImage(salon))
  }

  /**
   * 沙龙头像(LOGO)更新
   * @param imgId 图片上传id(对应mongodb中的图片id)
   * @return
   */
  def saveSalonImg(imgId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Salon.updateSalonLogo(salon, imgId)
    Redirect(routes.Salons.checkInfoState)
  }

  /**
   * 沙龙头像上传
   * @return
   */
  def imageUpload = StackAction(parse.multipartFormData, AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    request.body.file("logo") match {
      case Some(logo) =>
        val db = DBDelegate.picDB
        val gridFs = GridFS(db)
        val uploadedFile = gridFs.createFile(logo.ref.file)
        uploadedFile.contentType = logo.contentType.orNull
        uploadedFile.save()
        Redirect(routes.Salons.saveSalonImg(uploadedFile._id.get))
      case None => BadRequest("no photo")
    }
  }

  /**
   * 店铺后台我的技师一览
   * @return
   */
  def myStylist = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val stylist = Stylist.findBySalon(salon.id)
    Ok(html.salon.admin.mySalonStylistAll(salon = salon, stylist = stylist))
  }

  def myReserv = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    //TODO
    //这里原来的指向有问题，暂随便制定一个链接(复制了下面myComment方法的跳转)
    val commentList = Comment.findBySalon(salon.id)
    Ok(html.salon.admin.mySalonCommentAll(salon = salon, commentList = commentList))
    //    Ok(views.html.salon.general.index(navBar = Nil, user = None))

  }

  def myComment = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val commentList = Comment.findBySalon(salon.id)
    Ok(html.salon.admin.mySalonCommentAll(salon = salon, commentList = commentList))
  }

  /**
   * 店铺服务后台管理
   */
  def myService = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val serviceList = Service.findBySalonId(salon.id)
    var serviceTypeNameList: List[String] = Nil
    salon.salonIndustry.map { industryName =>
      serviceTypeNameList :::= ServiceType.findAllServiceType(industryName)
    }
    val serviceTypeInserviceList = serviceList.map(service => service.serviceType)
    Ok(html.salon.admin.mySalonServiceAll(salon = salon, serviceList = serviceList, serviceTypeNameList = serviceTypeNameList, serviceTypeInserviceList = serviceTypeInserviceList))
  }

  /**
   *
   * 店铺优惠劵后台管理
   */
  def myCoupon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val coupons = Coupon.findBySalon(salon.id)
    val serviceTypes: List[ServiceType] = ServiceType.findAllServiceTypes(salon.salonIndustry)
    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
    Ok(html.salon.admin.mySalonCouponAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, coupons))
  }

  /**
   * 店铺菜单后台管理
   */
  def myMenu = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val menus: List[Menu] = Menu.findBySalon(salon.id)
    val serviceTypes: List[ServiceType] = ServiceType.findAllServiceTypes(salon.salonIndustry)
    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
    Ok(html.salon.admin.mySalonMenuAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
  }

  /**
   * 店铺查看申请中的技师
   * @return 跳转到我的店铺所有申请的技师
   */
  def checkHoldApply = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    //查找有关该店铺的所有申请中技师的申请记录
    val records = SalonStylistApplyRecord.findApplyingStylist(salon.id)
    var applies: List[(Stylist, ObjectId)] = Nil
    records.map { re =>
      //根据申请记录的stylistId查找到该技师
      val stylist = Stylist.findOneByStylistId(re.stylistId)
      stylist match {
        case Some(sty) => applies :::= List((sty, re.id))
        case None => None
      }
    }
    Ok(views.html.salon.admin.mySalonApplyAll(applies = applies, mySalon = salon))
  }

  /**
   * 店铺查看自己正在邀请中的技师
   * 先根据店铺的ID，查找到该店铺正在邀请中所有记录，迭代记录，根据记录中的技师id
   * 获得该技师，并且将对应记录的id一同放入该list集合中
   * @return 跳转至我邀请中的技师页面
   */
  def myInvite = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    //inviteds 为tuple（存放技师与对应的申请id）类型的List集合
    var inviteds: List[(Stylist, ObjectId)] = Nil
    //查找店铺申请中的记录
    SalonStylistApplyRecord.findSalonInvited(salon.id).map { r =>
      Stylist.findOneByStylistId(r.stylistId).map { stylist =>
        inviteds :::= List((stylist, r.id))
      }
    }
    Ok(views.html.salon.admin.mySalonInvitedStylists(inviteds = inviteds, mySalon = salon))
  }

  /**
   * 店铺取消对某个技师的邀请
   *
   * 根据申请记录ID将改条记录状态修改为取消
   * @param applyRecordId - 申请记录的ID
   * @return 重定向到 myInvite
   */
  def cancelInviteStylist(applyRecordId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    SalonStylistApplyRecord.cancelSalonInvited(applyRecordId)
    Redirect(routes.Salons.myInvite)
  }

  /**
   * 店铺同意技师申请
   * @param applyRecordId - 申请记录的Id
   * @return 根据条件判断 重定向到我的技师一览 或 跳转至申请信息提示画面
   */
  def agreeStylistApply(applyRecordId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val record = SalonStylistApplyRecord.findOneById(applyRecordId)
    record.map { re =>
      //当前记录的验证结果为‘0’申请中时
      if (re.verifiedResult == 0) {
        //调用同意申请的方法
        SalonStylistApplyRecord.agreeApply(re)
        //调用该方法，使技师成为有效合法的技师
        Stylist.becomeStylist(re.stylistId)
        val user = User.findOneById(re.stylistId).get
        //将普通会员的类型修改为‘stylist’
        User.save(user.copy(id = re.stylistId, userTyp = "stylist"))
        //将店铺与技师当前握手状态在店铺与技师关系表中记录下来
        SalonAndStylist.entrySalon(salon.id, re.stylistId)
        Redirect(routes.Salons.myStylist)
      } else {
        //当前记录申请结果已经被他人修改后，跳转到申请信息提示画面
        Ok(views.html.salon.admin.applyResultPage(salon))
      }
    } getOrElse {
      //申请记录不存在时跳转到申请信息提示画面
      Ok(views.html.salon.admin.applyResultPage(salon))
    }
  }

  /**
   * 店铺拒绝技师申请
   * @param applyRecordId - 申请履历的id
   * @return
   */
  def rejectStylistApply(applyRecordId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val record = SalonStylistApplyRecord.findOneById(applyRecordId)
    record.map { re =>
      //当前申请记录的验证结果为‘0’，申请中时
      if(re.verifiedResult == 0) {
        SalonStylistApplyRecord.rejectApply(re)
        Redirect(routes.Salons.myStylist)
      } else {
        Ok(views.html.salon.admin.applyResultPage(salon))
      }
    } getOrElse {
      Ok(views.html.salon.admin.applyResultPage(salon))
    }
  }

  /**
   * 根据店铺输入的用户userId查找对应的技师
   * 店铺搜索的用户userId查找到该用户
   * 根据用户的id查找对应的技师
   * 判断技师与店铺关系是否有效
   * 有效给予status为1
   * 无效给予status为2
   * NotFound 逻辑不会走到这步，画面的ajax有做check
   *
   */
  def searchStylistById = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val userId = request.getQueryString("searchStylistById").get
    val user = User.findOneByUserId(userId)
    user match {
      case Some(u) =>
        val stylist = Stylist.findOneByStylistId(u.id)
        stylist match {
          case Some(sty) => {
            val isValid = SalonAndStylist.checkSalonAndStylistValid(salon.id, sty.stylistId)
            if (isValid) {
              Ok(html.salon.admin.findStylistBySearch(stylist = sty, salon = salon, status = 1))
            } else {
              if (SalonAndStylist.findByStylistId(sty.stylistId).isEmpty) {
                Ok(html.salon.admin.findStylistBySearch(stylist = sty, salon = salon, status = 2))
              } else {
                NotFound
              }
            }

          }
          case None => NotFound
        }
      case None => NotFound
    }

  }

  /**
   * 店铺邀请技师，生成一条申请记录
   * @param stylistId - stylist ObjectId primary key
   * @return redirect myStylist of Salon
   */
  def inviteStylist(stylistId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    SalonStylistApplyRecord.save(new SalonStylistApplyRecord(new ObjectId, salon.id, stylistId, 2, new Date, 0, None))
    Redirect(routes.Salons.myStylist)
  }

  /**
   * 检查根据Id搜索技师是否有用，店铺后台搜索技师 ajax check
   * @param stylistId - stylist ObjectId primary key
   */
  def checkStylistIsValid(stylistId: String) = Action {
    val styId = new ObjectId(stylistId)
    val record = SalonAndStylist.findByStylistId(styId)
    record match {
      case Some(re) => {
        Ok("抱歉该技师已属于其它店铺")
      }
      case None => {
        val stylist = Stylist.findOneByStylistId(styId)
        stylist match {
          case Some(sty) => if (!sty.isValid) Ok("暂无该技师") else Ok("ID 有效")
          case None => Ok("暂无该技师")
        }
      }
    }
  }

  /**
   * 从本店铺中移除技师，解约
   * @param stylistId - stylist ObjectId primary key
   * @return 重定向到后台我的技师一览
   */
  def removeStylist(stylistId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    SalonAndStylist.leaveSalon(salon.id, stylistId)
    Redirect(routes.Salons.myStylist)
  }

  /**
   * 查看店铺所有发型
   */
  def getAllStylesBySalon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val stylists = SalonAndStylist.getStylistsBySalon(salon.id)
    var styles: List[Style] = Nil
    stylists.map { sty =>
      styles :::= Style.findByStylistId(sty.stylistId)
    }
    styles.sortBy(_.createDate).reverse
    Ok(html.salon.admin.mySalonStyles(salon = salon, styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll("Hairdressing"), isFirstSearch = true, isStylist = false, stylists = stylists))
  }

  //TODO 命名同上 难以区分区别
  def getAllStylesListBySalon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Styles.styleSearchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      {
        case (styleSearch) => {
          val stylists = Style.findStylistBySalonId(salon.id)
          val styles = Style.findStylesBySalonBack(styleSearch, salon.id)
          Ok(html.salon.admin.mySalonStyles(salon = salon, styles = styles, styleSearchForm = Styles.styleSearchForm.fill(styleSearch), styleParaAll = Style.findParaAll("Hairdressing"), isFirstSearch = false, isStylist = false, stylists = stylists))
        }
      })
  }

  /**
   * 后台发型更新
   */
  def styleUpdateBySalon(styleId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val styleOne: Option[Style] = Style.findOneById(styleId)
    val stylists = SalonAndStylist.getStylistsBySalon(salon.id)
    styleOne match {
      case Some(style) => Ok(views.html.salon.admin.mySalonStyleUpdate(salon = salon, style = styleOne.get, stylists = stylists, styleUpdateForm = Styles.styleUpdateForm.fill(style), styleParaAll = Style.findParaAll("Hairdressing")))
      case None => NotFound
    }
  }

  def styleUpdateNewBySalon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Styles.styleUpdateForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      {
        case (styleUpdateForm) => {
          Style.save(styleUpdateForm.copy(id = styleUpdateForm.id), WriteConcern.Safe)
          Redirect(routes.Salons.getAllStylesBySalon)
        }
      })
  }

  /**
   * 后台发型删除，使之无效即可
   */
  def styleToInvalidBySalon(styleId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Style.styleToInvalid(styleId)
    Redirect(routes.Salons.getAllStylesBySalon)
  }

  /**
   * 后台发型新建
   */
  def styleAddBySalon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val stylists = SalonAndStylist.getStylistsBySalon(salon.id)
    Ok(views.html.salon.admin.mySalonStyleAdd(salon = salon, stylists = stylists, styleAddForm = Styles.styleAddForm, styleParaAll = Style.findParaAll("Hairdressing"), isStylist = false))

  }

  def newStyleAddBySalon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Styles.styleAddForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      {
        case (styleAddForm) => {
          Style.save(styleAddForm)
          Redirect(routes.Salons.getAllStylesBySalon)
        }
      })
  }

  /**
   * 后台发型基本信息查看
   */
  def getbackstageStyleItem(styleId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val style = Style.findOneById(styleId)
    style match {
      case Some(style) => Ok(views.html.salon.admin.mySalonStyleItem(salon = salon, style = style))
      case None => NotFound
    }
  }

  /**
   * 店铺回复消费者的评论，后台逻辑
   */
  def replyBySalon(commentObjId: ObjectId, commentObjType: Int) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    auth.Comments.formReplyComment.bindFromRequest.fold(
      //处理错误
      errors => BadRequest(views.html.comment.errorMsg("")),
      {
        case (content) =>
          Comment.reply(salon.salonAccount.accountId, content, commentObjId, commentObjType)
          Redirect(auth.routes.Salons.myComment)
      })
  }

  /**
   * 沙龙信息是否完善检查页面
   * @return
   */
  def checkInfoState = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    var counts: Int = 0
    if (!Salon.checkBasicInfoIsFill(salon)) counts += 1
    if (!Salon.checkDetailIsFill(salon)) counts += 1
    if (!Salon.checkImgIsExist(salon)) counts += 1
    Ok(views.html.salon.admin.checkInfostate(salon, counts))
  }

  /**
   * 信息不完善跳转页面
   * @return
   */
  def checkAuth = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Ok(views.html.salon.salonManage.checkAuth(salon))
  }

  /**
   * 沙龙头像上传页面
   * @return
   */
  def salonLogoPicture = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val salonInfo = salonInfoForm.fill(salon)
    Ok(views.html.salon.admin.salonLogoPicture(salon, salonInfo))
  }

  /**
   * 沙龙头像上传
   * @return
   */
  def updateLogoPicture = StackAction(parse.multipartFormData, AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    request.body.file("logo") match {
      case Some(logo) =>
        val db = DBDelegate.picDB
        val gridFs = GridFS(db)
        val uploadedFile = gridFs.createFile(logo.ref.file)
        uploadedFile.contentType = logo.contentType.orNull
        uploadedFile.save()
        Salon.updateSalonLogo(salon, uploadedFile._id.get)
        Redirect(auth.routes.Salons.salonLogoPicture)
      case None => BadRequest("no photo")
    }
  }


  /**
   * 沙龙展示图片上传页面
   * @return
   */
  def salonShowPicture = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val pictures = new SalonPics(salon.salonPics)
    val salonPics = salonPicsForm.fill(pictures)
    Ok(views.html.salon.admin.salonShowPicture(salon, salonPics))
  }

  /**
   * 沙龙展示图片更新
   * @return
   */
  def updateShowPicture = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val industry = Industry.findAll.toList
    salonPicsForm.bindFromRequest.fold(
    errors => BadRequest(views.html.salon.salonManage.salonBasic(salonInfoForm.fill(salon), industry, salon)),
    {
      salonpictures =>
        Salon.save(salon.copy(salonPics = salonpictures.salonPics), WriteConcern.Safe)
        Redirect(auth.routes.Salons.salonShowPicture)
    })
  }


  /**
   * 沙龙环境图片上传页面
   * @return
   */
  def salonAtmoPicture = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val pictures = new SalonPics(salon.salonPics)
    val salonPics = salonPicsForm.fill(pictures)
    Ok(views.html.salon.admin.salonAtmoPicture(salon, salonPics))
  }

  /**
   * 沙龙环境图片更新
   * @return
   */
  def updateAtmoPicture = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val industry = Industry.findAll.toList
    salonPicsForm.bindFromRequest.fold(
    errors => BadRequest(views.html.salon.salonManage.salonBasic(salonInfoForm.fill(salon), industry, salon)),
    {
      salonpictures =>
        Salon.save(salon.copy(salonPics = salonpictures.salonPics), WriteConcern.Safe)
        Redirect(auth.routes.Salons.salonAtmoPicture)
    })
  }

  /**
   * 沙龙营业执照图片上传页面
   * @return
   */
  def salonCheckPicture = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val pictures = new SalonPics(salon.salonPics)
    val salonPics = salonPicsForm.fill(pictures)
    Ok(views.html.salon.admin.salonCheckPicture(salon, salonPics))
  }

  /**
   * 沙龙营业执照更新
   * @return
   */
  def updateCheckPicture  = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val industry = Industry.findAll.toList
    salonPicsForm.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.salonManage.salonBasic(salonInfoForm.fill(salon), industry, salon)),
      {
        salonpictures =>
          Salon.save(salon.copy(salonPics = salonpictures.salonPics), WriteConcern.Safe)
          Redirect(auth.routes.Salons.salonCheckPicture)
      })
  }

  /**
   * checks for menu，service, coupon name
   */

  def itemIsExist(value: String, key: String) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    key match {
      case ITEM_TYPE_COUPON =>
        Ok(Coupon.checkCouponIsExist(value, salon.id).toString)
      case ITEM_TYPE_MENU =>
        Ok(Menu.checkMenuIsExist(value, salon.id).toString)
      case ITEM_TYPE_SERVICE =>
        Ok(models.portal.service.Service.checkServiceIsExist(value, salon.id).toString)
      case ITEM_TYPE_NAME_ABBR =>
        Ok((User.isExist(value, User.findOneByNickNm) ||
          !Salon.isValid(value, salon, Salon.findOneBySalonName) ||
          !Salon.isValid(value, salon, Salon.findOneBySalonNameAbbr)).toString)
    }
  }

  /**
   * 进入沙龙预约处理中的画面
   */
  def getAllResvsInProcessing = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn

    val resvStatusList: List[String] = List("-1", "0", "1", "2")

    val processingResvs: List[Reservation] = Reservation.findProcessingResvBySalon(salon.id).sortBy(_.expectedDate)

    Ok(views.html.salon.admin.myResvManage("my-resvInProcessing", salon, ResvSreachForm, resvStatusList, processingResvs))

  }

  /**
   * 进入沙龙预约履历的画面
   */
  def getAllResvsRecord = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn

    val resvStatusList: List[String] = List("-1", "0", "1", "2", "3")
    val defaultSreachCondition: ResvSreachCondition = ResvSreachCondition("", "", "", "", "", List("-1", "0", "1", "2", "3"))

    val resvRecords: List[Reservation] = Reservation.findAllResvBySalon(salon.id).sortBy(_.expectedDate)

    Ok(views.html.salon.admin.myResvManage("my-resvRecord", salon, ResvRecordSreachForm.fill(defaultSreachCondition), resvStatusList, resvRecords))

  }

  /**
   * 根据检索条件检索出符合条件的预约信息(处理中预约)
   * @return
   */
  def findResvs = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    ResvSreachForm.bindFromRequest.fold(
    errors => BadRequest(views.html.error.errorMsg(errors)), {
      resvCondition =>
        val resvs: List[Reservation] = Reservation.findResvFromCondition(salon.id, resvCondition).sortBy(_.expectedDate)

        val resvStatusList: List[String] = List("-1", "0", "1", "2")
        Ok(views.html.salon.admin.myResvManage("my-resvInProcessing", salon, ResvSreachForm.fill(resvCondition), resvStatusList, resvs))
    }
    )
  }

  /**
   * 根据检索条件检索出符合条件的预约信息(预约履历)
   * @return
   */
  def findResvsRecord = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    ResvRecordSreachForm.bindFromRequest.fold(
    errors => BadRequest(views.html.error.errorMsg(errors)), {
      resvCondition =>
        val resvs: List[Reservation] = Reservation.findResvFromCondition(salon.id, resvCondition).sortBy(_.expectedDate)

        val resvStatusList: List[String] = List("-1", "0", "1", "2", "3")
        Ok(views.html.salon.admin.myResvManage("my-resvRecord", salon, ResvSreachForm.fill(resvCondition), resvStatusList, resvs))
    }
    )
  }

  /**
   * 根据传入的handleType对其做取消，完成，过期等修改
   * @return
   */
  def handleResv = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Reservations.HandleResvFrom.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)), {
       handleResv =>
         for(resv <- handleResv.reservs) {
           Reservation.handleResv(handleResv.handleType, resv.id)
         }

        Redirect(auth.routes.Salons.getAllResvsInProcessing)

    }
    )
  }

  /**
   * 进入某个预约详细信息页面
   * @param resvId 预约id
   * @param pageType 从哪个页面跳转过来的
   * @return
   */
  def showResvDetail(resvId: ObjectId, pageType: String) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val reservation: Reservation = Reservation.findOneById(resvId).get
    println("reservation6/11 = " + reservation)

    Ok(views.html.salon.admin.resvItemDetail(salon, reservation, pageType))
  }

  /**
   * 根据预约id进行取消，完成，过期预约等操作
   * 用于沙龙后台进入某个详细预约对其进行操作
   * @return
   */
  def handleOneResv(handleType: String, resvId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    Reservation.handleResv(handleType, resvId)

    Redirect(auth.routes.Salons.getAllResvsInProcessing)

  }
}

