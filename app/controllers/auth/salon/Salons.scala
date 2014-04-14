package controllers.auth

import play.api.mvc._
import models._
import views._
import java.util.Date
import controllers._
import jp.t2v.lab.play2.auth._
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import com.mongodb.casbah.{MongoConnection, WriteConcern}
import com.mongodb.casbah.gridfs.GridFS
import org.mindrot.jbcrypt.BCrypt
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.Imports._
import models.SalonAccount
import scala.Some
import models.CouponServiceType
import se.radley.plugin.salat.Binders.ObjectId

object Salons extends Controller with LoginLogout with AuthElement with SalonAuthConfigImpl{
  
  //店铺登录Form
    val salonLoginForm = Form(mapping(
      "salonAccount" -> mapping(
          "accountId"-> nonEmptyText,
          "password" -> nonEmptyText)(SalonAccount.apply)(SalonAccount.unapply)
  )(Salon.loginCheck)(_.map(s => (s.salonAccount))).verifying("Invalid userId or password", result => result.isDefined))

   //密码修改
  val changePassword = Form(
    mapping(
      "salonChange" ->mapping(
          "salonAccount" -> mapping(
            "accountId" -> text,
            "password" -> nonEmptyText
              )(SalonAccount.apply)(SalonAccount.unapply))(Salon.loginCheck)(_.map(s => (s.salonAccount))).verifying("Invalid userId or password", result => result.isDefined),        
      "newPassword" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[A-Za-z0-9]+$""")),
        "confirm" -> text).verifying(
        // Add an additional constraint: both passwords must match
        Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)
    ){(salonChange, newPassword) => (salonChange.get, newPassword._1)}{salonChange => Some((Option(salonChange._1),("","")))}
  )

    /**
     * 店铺登录
     */
    def salonLogin = Action.async { implicit request =>
        salonLoginForm.bindFromRequest.fold(
        formWithErrors => { Future.successful(BadRequest(views.html.salon.salonLogin(formWithErrors))) },
        salon => gotoLoginSucceeded(salon.get.salonAccount.accountId))
    }

    /**
     * 退出登录
     */
    def salonLogout = Action.async { implicit request =>
        gotoLogoutSucceeded.map(_.flashing(
            "success" -> "You've been logged out"
        ))
    }
    
    /**
     * 基本信息完善页面
     */
    def salonBasic = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
      val salonRegister = noAuth.Salons.salonRegister.fill(salon)
      val industry = Industry.findAll.toList
      Ok(views.html.salon.salonBasic(salonRegister ,industry , salon))
    }

    /**
     * 店铺基本信息修改页面
     *
     */
    def salonInfoManage = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
        val salon = loggedIn
        val salonInfo = noAuth.Salons.salonInfo.fill(salon)
        val industry = Industry.findAll.toList
        Ok(views.html.salon.admin.salonManage("",salonInfo ,industry , salon))
    }

    /**
     * 店铺基本信息更新
     */
    def update(id: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
        val salon = loggedIn
        val industry = Industry.findAll.toList
        noAuth.Salons.salonInfo.bindFromRequest.fold(
        errors => BadRequest(views.html.salon.admin.salonManage("",errors,industry,salon)),
        {
            salon =>
                Salon.save(salon.copy(id = id), WriteConcern.Safe)
                Redirect(routes.Salons.salonInfoManage)
        })
    }

    /**
     * 密码修改页面
     */
    def password = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
        val salon = loggedIn
        val changeForm = Salons.changePassword.fill(salon,"")   
        Ok(views.html.salon.admin.salonChangePassword("", changeForm, salon))
    }

    //  /**
    //   * 密码修改
    //   */
    //  def salonChangePassword(id :ObjectId) = Action { implicit request =>
    //    val salon = Salon.findOneById(id).get
    //    changePassword.bindFromRequest.fold(
    //      errors => BadRequest(views.html.error.errorMsg(errors)),
    //      {
    //        case (salonAccount, main) =>
    //          Salon.save(salonAccount.copy(password = main), WriteConcern.Safe)
    //           Redirect(noAuth.routes.Salons.salonInfoBasic(id))
    //    })
    //  }

    /**
     * 店铺Logo更新页面
     */
    def addImage = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
        val salon = loggedIn
        Ok(views.html.salon.salonImage(salon))
    }

    /**
     * 店铺图片保存
     */
    def saveSalonImg(imgId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
        val salon = loggedIn
        Salon.updateSalonLogo(salon, imgId)
        Redirect(routes.Salons.salonInfoManage)
    }

    /**
     * 店铺图片上传
     */
    def imageUpload = StackAction(parse.multipartFormData, AuthorityKey -> isLoggedIn _){implicit request =>
        val salon = loggedIn
        request.body.file("logo") match {
            case Some(logo) =>
                val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(logo.ref.file)
                uploadedFile.contentType = logo.contentType.orNull
                uploadedFile.save()
                Redirect(routes.Salons.saveSalonImg(uploadedFile._id.get))
            case None => BadRequest("no photo")
        }
    }

    /**
     * 我的技师
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
    Ok(views.html.salon.general.index(navBar = Nil, user = None))
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
      val serviceTypeNameList = ServiceType.findAllServiceType
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
      val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
      val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
      Ok(html.salon.admin.mySalonCouponAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, coupons))
  }
  
  /**
   * 店铺菜单后台管理
   */
  def myMenu = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
      val menus: List[Menu] = Menu.findBySalon(salon.id)
      val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
      val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
      Ok(html.salon.admin.mySalonMenuAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
  }
  
  /**
   * 店铺查看申请中的技师
   */
  def checkHoldApply = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
      val records = SalonStylistApplyRecord.findApplyingStylist(salon.id)
      var stylists: List[Stylist] = Nil
    records.map{re =>
      val stylist = Stylist.findOneByStylistId(re.stylistId)
      stylist match {
        case Some(sty) => stylists :::= List(sty)
        case None => None
      }
    }
    Ok(views.html.salon.admin.mySalonApplyAll(stylist = stylists, salon = salon))
  }
  
  /**
   *  同意技师申请
   */
  //TODO 传StylistID 不好，应该传申请记录的Id
  def agreeStylistApply(stylistId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
	  val record = SalonStylistApplyRecord.findOneStylistApRd(stylistId)
        record match {
          case Some(re) => {
            SalonStylistApplyRecord.save(re.copy(id=re.id, applyDate = new Date, verifiedResult = 1))
            val stylist = Stylist.findOneByStylistId(re.stylistId)
            Stylist.becomeStylist(stylistId)
            val user = User.findOneById(stylistId).get
            User.save(user.copy(id=stylistId,userTyp="stylist"))
            SalonAndStylist.entrySalon(salon.id, stylistId)
            Redirect(routes.Salons.myStylist)
          }
          case None => Ok(views.html.salon.admin.applyResultPage(salon))
        }
  }
  
  /**
   *  店铺拒绝技师申请
   */
  //TODO 同上
  def rejectStylistApply(stylistId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
      val record = SalonStylistApplyRecord.findOneStylistApRd(stylistId)
      record match {
      case Some(re) => {
        SalonStylistApplyRecord.rejectStylistApply(re)
        Redirect(routes.Salons.myStylist)
      }
      case None => Ok(views.html.salon.admin.applyResultPage(salon))
    }
  }
  
  /**
   *  根据Id查找技师
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
	        if(isValid) {
	          Ok(html.salon.admin.findStylistBySearch(stylist = sty, salon = salon, status = 1))
	        } else {
	          if(SalonAndStylist.findByStylistId(sty.stylistId).isEmpty) {
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
   *店铺邀请技师 
   */
  def inviteStylist(stylistId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
	SalonStylistApplyRecord.save(new SalonStylistApplyRecord(new ObjectId, salon.id, stylistId, 2, new Date, 0, None))
    Redirect(routes.Salons.myStylist)
  }

  /**
   *  检查根据Id搜索技师是否有用
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
          case Some(sty) => if(!sty.isValid) Ok("暂无该技师") else Ok("ID 有效")
          case None => Ok("暂无该技师")
        }
      }
    }
  }
  
  def removeStylist(stylistId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    SalonAndStylist.leaveSalon(salon.id,stylistId)
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
        Ok(html.salon.admin.mySalonStyles(salon = salon , styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll, isFirstSearch = true, isStylist = false, stylists = stylists))
  }

  //TODO 命名同上 难以区分区别
  def getAllStylesListBySalon = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
            Styles.styleSearchForm.bindFromRequest.fold(
                errors => BadRequest(views.html.index("")),
                {
                    case (styleSearch) => {
                        val stylists = Style.findStylistBySalonId(salon.id)
                        val styles = Style.findStylesBySalonBack(styleSearch,salon.id)
                        Ok(html.salon.admin.mySalonStyles(salon = salon, styles = styles, styleSearchForm = Styles.styleSearchForm.fill(styleSearch), styleParaAll = Style.findParaAll, isFirstSearch = false, isStylist = false, stylists = stylists))
                    }
                })
    }
  
     /**
     * 后台发型更新
     */
    def styleUpdateBySalon(styleId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) {implicit request =>
        val salon = loggedIn
        val styleOne: Option[Style] = Style.findOneById(styleId)
        val stylists = SalonAndStylist.getStylistsBySalon(salon.id)
        styleOne match {
            case Some(style) => Ok(views.html.salon.admin.mySalonStyleUpdate(salon = salon, style = styleOne.get, stylists = stylists, styleUpdateForm = Styles.styleUpdateForm.fill(style), styleParaAll = Style.findParaAll))
            case None => NotFound
        }
    }
    
    def styleUpdateNewBySalon = StackAction(AuthorityKey -> isLoggedIn _) {implicit request =>
        val salon = loggedIn
            Styles.styleUpdateForm.bindFromRequest.fold(
                errors => BadRequest(views.html.index("")),
                {
                    case (styleUpdateForm) => {
                        Style.updateStyle(styleUpdateForm)
                        Redirect(routes.Salons.getAllStylesBySalon)
                    }
                })
    }
    
    /**
     * 后台发型删除，使之无效即可
     */
    def styleToInvalidBySalon(styleId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) {implicit request =>
        val salon = loggedIn
        Style.styleToInvalid(styleId)
        Redirect(routes.Salons.getAllStylesBySalon)
    }
    
     /**
     * 后台发型新建
     */
    def styleAddBySalon = StackAction(AuthorityKey -> isLoggedIn _) {implicit request =>
         val salon = loggedIn
        val stylists = SalonAndStylist.getStylistsBySalon(salon.id)
        Ok(views.html.salon.admin.mySalonStyleAdd(salon = salon, stylists = stylists, styleAddForm = Styles.styleAddForm, styleParaAll = Style.findParaAll, isStylist = false))
      
        
    }
    
    def newStyleAddBySalon = StackAction(AuthorityKey -> isLoggedIn _) {implicit request =>
        val salon = loggedIn
        Styles.styleAddForm.bindFromRequest.fold(
            errors => BadRequest(html.index("")),
            {
                case (styleAddForm) => {
                    Style.save(styleAddForm)
                    Redirect(routes.Salons.getAllStylesBySalon)
                }
            })
    }

    /**
     * 店铺回复消费者的评论，后台逻辑
     */
    def replyBySalon(commentObjId : ObjectId, commentObjType : Int) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
        val salon = loggedIn
        auth.Comments.formHuifuComment.bindFromRequest.fold(
        //处理错误
        errors => BadRequest(views.html.comment.errorMsg("")),
        {
            case (content) =>
                Comment.reply(salon.salonAccount.accountId, content, commentObjId, commentObjType)
                Redirect(auth.routes.Salons.myComment)
        }
        )
    }
    
    def checkInfoState = StackAction(AuthorityKey -> isLoggedIn _){implicit request =>
        val salon = loggedIn
    	Ok(views.html.salon.checkInfostate(""))
    }
    
    def salonShowPics = Action{
      val salon = Salon.findByAccountId("salon01").get
      val salonInfo = noAuth.Salons.salonInfo.fill(salon)
      Ok(views.html.salon.salonShowPictures("",salonInfo))
    }
    
}