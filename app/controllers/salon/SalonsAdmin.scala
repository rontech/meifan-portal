package controllers

import play.api.mvc._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date
import models.Salon

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
    Ok(views.html.salon.general.index(Nil))
  }
  
  def myComment(salonId: ObjectId) = Action {
	val commentList = Comment.findBySalon(salonId)
	val salon = Salon.findById(salonId)
	salon match{
	  case Some(s) =>Ok(html.salon.admin.mySalonCommentAll(salon = s, commentList = commentList))
	  case None => NotFound
	}
  }
  
  def myService(salonId: ObjectId) = Action {
    Ok(views.html.salon.general.index(Nil))
  }
  
  /**

   *  
   * 店铺优惠劵后台管理
   */
  def myCoupon(salonId: ObjectId) = Action {
    val salon = Salon.findById(salonId)
    val coupons = Coupon.findBySalon(salonId)
    
    salon match {
      case Some(s) => Ok(html.salon.admin.mySalonCouponAll(s, coupons))
      case None => NotFound
    }
  }
  
  /**
   * 店铺菜单后台管理
   */
  def myMenu(salonId: ObjectId) = Action {
    val salon = Salon.findById(salonId)
    val menus = Menu.findBySalon(salonId)
    
    salon match {
      case Some(s) => Ok(html.salon.admin.mySalonMenuAll(s, menus))
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
      val stylist = Stylist.findOneById(re.stylistId)
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
            val stylist = Stylist.findOneById(re.stylistId)
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
	val stylist = Stylist.findOneById(new ObjectId(stylistId))
	stylist match {
      case Some(sty) => {
        val isValid = SalonAndStylist.checkSalonAndStylistValid(new ObjectId(salonId), sty.id)
        if(isValid) {
          Ok(html.salon.admin.findStylistBySearch(stylist = sty, salon = salon, status = 1))
        } else {
          if(SalonAndStylist.findByStylistId(sty.id).isEmpty) {
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
        val stylist = Stylist.findOneById(styId)
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
}
