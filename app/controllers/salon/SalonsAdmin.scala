package controllers

import play.api.mvc._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date
import models.Salon
import models.ServiceType
import controllers._

object SalonsAdmin extends Controller {
  
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
 
  // Navigation Bar 
  //val nav0 = (Messages("index.mainPage"), routes.Application.index.toString())
  //val navBarList = nav0 :: Nil

  def mySalon(salonId: ObjectId) = Action {
    //val nav = (Messages("salonAdmin.mainPage"), routes.SalonsAdmin.mySalon(salonId))
    //navBarList ::= nav

    val salon: Salon = Salon.findById(salonId).get
    Ok(views.html.salon.admin.mySalonHome(salon = salon))
  }
  
  def myStylist(salonId: ObjectId) = Action {
	val stylist = Stylist.findBySalon(salonId)
	val salon = Salon.findById(salonId)
	salon match{
	  case Some(s) =>Ok(html.salon.admin.mySalonStylistAll(salon = s, stylist = stylist))
	  case None => NotFound
	}
  }
  
  def myReserv(salonId: ObjectId) = Action {
    Ok(views.html.salon.general.index(navBar = Nil, user = None))
  }
  
  def myComment(salonId: ObjectId) = Action {
	val commentList = Comment.findBySalon(salonId)
	val salon = Salon.findById(salonId)
	salon match{
	  case Some(s) =>Ok(html.salon.admin.mySalonCommentAll(salon = s, commentList = commentList))
	  case None => NotFound
	}
  }
  
  /**
   * 店铺服务后台管理
   */
  def myService(salonId: ObjectId) = Action {
    val salon = Salon.findById(salonId)
    val serviceList = Service.findBySalonId(salonId)
    val serviceTypeNameList = ServiceType.findAllServiceType
    val serviceTypeInserviceList = serviceList.map(service => service.serviceType)
    
    salon match{
        case Some(s) =>Ok(html.salon.admin.mySalonServiceAll(salon = s, serviceList = serviceList, serviceTypeNameList = serviceTypeNameList, serviceTypeInserviceList = serviceTypeInserviceList))
        case None => NotFound
    }
    
  }
  
  /**

   *  
   * 店铺优惠劵后台管理
   */
  def myCoupon(salonId: ObjectId) = Action {
    val salon = Salon.findById(salonId)
    val coupons = Coupon.findBySalon(salonId)
    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
    
    salon match {
      case Some(s) => Ok(html.salon.admin.mySalonCouponAll(s, Coupons.conditionForm.fill(couponServiceType), serviceTypes, coupons))
      case None => NotFound
    }
  }
  
  /**
   * 店铺菜单后台管理
   */
  def myMenu(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val menus: List[Menu] = Menu.findBySalon(salonId)
    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
    
    salon match {
      case Some(s) => Ok(html.salon.admin.mySalonMenuAll(s, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
      case None => NotFound
    }
  }
  
  /**
   * 店铺查看申请中的技师
   */
  def checkHoldApply(salonId: ObjectId) = Action {
    val records = SalonStylistApplyRecord.findApplyingStylist(salonId)
    var stylists: List[Stylist] = Nil
    records.map{re =>
      val stylist = Stylist.findOneByStylistId(re.stylistId)
      stylist match {
        case Some(sty) => stylists :::= List(sty)
        case None => None
      }
    }
    val salon = Salon.findById(salonId)
    salon match {
      case Some(s) => Ok(views.html.salon.admin.mySalonApplyAll(stylist = stylists, salon = s))
      case None => NotFound
    }
  }
  
  /**
   *  同意技师申请
   */
  def agreeStylistApply(stylistId: ObjectId, salonId: ObjectId) = Action {
	  val record = SalonStylistApplyRecord.findOneStylistApRd(stylistId)
        record match {
          case Some(re) => {
            SalonStylistApplyRecord.save(re.copy(id=re.id, applyDate = new Date, verifiedResult = 1))
            val stylist = Stylist.findOneByStylistId(re.stylistId)
            Stylist.becomeStylist(stylistId)
            SalonAndStylist.entrySalon(salonId, stylistId)
            Redirect(routes.SalonsAdmin.myStylist(salonId))
          }
          case None => NotFound
        }
  }
  
  /**
   *  店铺拒绝技师申请
   */
  def rejectStylistApply(stylistId: ObjectId, salonId: ObjectId) = Action {
    val record = SalonStylistApplyRecord.findOneStylistApRd(stylistId)
    record match {         
      case Some(re) => {
        SalonStylistApplyRecord.rejectStylistApply(re)
        Redirect(routes.SalonsAdmin.myStylist(salonId))
      }
      case None => NotFound
    }
  }
  
  /**
   *  根据Id查找技师
   */
  def searchStylistById() = Action {implicit request =>
    val stylistId = request.getQueryString("searchStylistById").get
    val salonId = request.getQueryString("salonId").get
	val salon = Salon.findById(new ObjectId(salonId)).get
	val stylist = Stylist.findOneByStylistId(new ObjectId(stylistId))
	stylist match {
      case Some(sty) => {
        val isValid = SalonAndStylist.checkSalonAndStylistValid(new ObjectId(salonId), sty.stylistId)
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
    
  }
  
  /**
   *店铺邀请技师 
   */
  def inviteStylist(stylistId: ObjectId, salonId: ObjectId) = Action {
	SalonStylistApplyRecord.save(new SalonStylistApplyRecord(new ObjectId, salonId, stylistId, 2, new Date, 0, None))
    Redirect(routes.SalonsAdmin.myStylist(salonId))
  }
  
  /**
   *  检查根据Id搜索技师是否有用
   */
  def checkStylistIsValid(salonId: String, stylistId: String) = Action {
    val styId = new ObjectId(stylistId)
    val salId = new ObjectId(salonId)
    val record = SalonAndStylist.findByStylistId(styId)
    record match {
      case Some(re) => {
        if(re.salonId == salonId) Ok("") else Ok("抱歉该技师已属于其它店铺")
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
  
  def removeStylist(salonId: ObjectId, stylistId: ObjectId) = Action {
    SalonAndStylist.leaveSalon(salonId,stylistId)
    Redirect(routes.SalonsAdmin.myStylist(salonId))
  }
  
  /**
   * 查看店铺所有发型
   */
  def getAllStylesBySalon(salonId: ObjectId) = Action {
        val salon: Option[Salon] = Salon.findById(salonId)
        val stylists = SalonAndStylist.getStylistsBySalon(salonId)
        var styles: List[Style] = Nil
        stylists.map { sty =>
            var style = Style.findByStylistId(sty.stylistId)
            styles :::= style
        }
        salon match {
            case Some(sa) => {
                Ok(html.salon.admin.mySalonStyles(salon = sa , styles = styles, styleSearchForm = Styles.styleSearchForm, styleParaAll = Style.findParaAll, isFirstSearch = true, isStylist = false))
            }
            case None => NotFound
        }
  }
  
  def getAllStylesListBySalon = Action {
        implicit request =>
            Styles.styleSearchForm.bindFromRequest.fold(
                errors => BadRequest(views.html.index("")),
                {
                    case (styleSearch) => {
                        val styles = Style.findByPara(styleSearch)
                        //权限控制时会获取salonID,暂写死
                        val salon = Salon.findById(new ObjectId("530d7288d7f2861457771bdd"))
                        Ok(html.salon.admin.mySalonStyles(salon = salon.get , styles = styles, styleSearchForm = Styles.styleSearchForm.fill(styleSearch), styleParaAll = Style.findParaAll, isFirstSearch = false, isStylist = false))
                    }
                })
    }
  
      /**
     * 后台发型更新
     */
    def styleUpdateBySalon(id: ObjectId) = Action {
        implicit request =>
        val styleOne: Option[Style] = Style.findOneById(id)
        //权限控制时会获取salonID,暂写死
        val salon = Salon.findById(new ObjectId("530d7288d7f2861457771bdd"))
        val stylists = SalonAndStylist.getStylistsBySalon(new ObjectId("530d7288d7f2861457771bdd"))
        styleOne match {
            case Some(style) => Ok(views.html.salon.admin.mySalonStyleUpdate(salon = salon.get, style = styleOne.get, stylists = stylists, styleUpdateForm = Styles.styleUpdateForm.fill(style), styleParaAll = Style.findParaAll))
            case None => NotFound
        }
    }
    
    def styleUpdateNewBySalon = Action {
        implicit request =>
            Styles.styleUpdateForm.bindFromRequest.fold(
                errors => BadRequest(views.html.test(errors)),
                {
                    case (styleUpdateForm) => {
                        Style.updateStyle(styleUpdateForm)
                        //权限控制时会获取salonID,暂写死
                        val salonId = new ObjectId("530d7288d7f2861457771bdd")
                        Redirect(routes.SalonsAdmin.styleUpdateBySalon(salonId))
                    }
                })
    }
    
    /**
     * 后台发型删除，使之无效即可
     */
    def styleToInvalidBySalon(id: ObjectId) = Action {
        implicit request =>
        Style.styleToInvalid(id)
        //权限控制时会获取salonID,暂写死
        val salonId = new ObjectId("530d7288d7f2861457771bdd")
        Redirect(routes.SalonsAdmin.styleUpdateBySalon(salonId))
    }
    
     /**
     * 后台发型新建
     */
    def styleAddBySalon(salonId: ObjectId) = Action {
        //此处为新发型登录
        implicit request =>
        val salon = Salon.findById(new ObjectId("530d7288d7f2861457771bdd"))
        val stylists = SalonAndStylist.getStylistsBySalon(salonId)
        Ok(views.html.salon.admin.mySalonStyleAdd(salon = salon.get, stylists = stylists, styleAddForm = Styles.styleAddForm, styleParaAll = Style.findParaAll, isStylist = false))
      
        
    }
    
    //    def styleAddNew = Action {
//        implicit request =>
//            styleAddForm.bindFromRequest.fold(
//                errors => BadRequest(html.index("")),
//                {
//                    case (styleAddForm) => {
//                        Style.save(styleAddForm)
//                                                Ok(html.style.test(styleAddForm))
////                        Ok(html.index(""))
//                    }
//                })
//    }
    
  
}
