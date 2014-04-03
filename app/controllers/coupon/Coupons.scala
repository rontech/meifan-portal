package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._

import java.util.Date
import java.util.Calendar
import models._
import views._
import play.api.i18n.Messages

object Coupons extends Controller {
    
  def couponForm: Form[Coupon] = Form {
    mapping(
        "couponName" -> nonEmptyText,
        "salonId" -> text,
        "serviceItems" -> list(
         mapping(
           "id" -> text
         ){(id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true)}
         {service => Some((service.id.toString()))}
         ).verifying(Messages("coupon.couponServiceRequired"), serviceItems => !serviceItems.isEmpty),
          "perferentialPrice" -> bigDecimal,
          "startDate" -> date,
          "endDate" -> date,
          "useConditions" -> nonEmptyText,
          "presentTime" -> nonEmptyText,
          "description" -> nonEmptyText
    ){
      (couponName, salonId, serviceItems, perferentialPrice, startDate, endDate, useConditions, presentTime, description) => Coupon(new ObjectId, "", couponName,
          new ObjectId(salonId), serviceItems, BigDecimal(0), perferentialPrice, 0, startDate, endDate, useConditions, presentTime, description, true)
    }
    {
      coupon => Some((coupon.couponName, coupon.salonId.toString(), coupon.serviceItems, coupon.perferentialPrice, coupon.startDate,
          coupon.endDate, coupon.useConditions, coupon.presentTime, coupon.description))
    }.verifying(
        Messages("coupon.couponNameRepeat"),
        coupon => !Coupon.checkCouponIsExit(coupon.couponName, coupon.salonId)   
    )
  }
  
  def couponUpdateForm: Form[Coupon] = Form {
    mapping(
        "couponName" -> nonEmptyText,
        "salonId" -> text,
        "serviceItems" -> list(
         mapping(
           "id" -> text
         ){(id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true)}
         {service => Some((service.id.toString()))}
         ).verifying(Messages("coupon.couponServiceRequired"), serviceItems => !serviceItems.isEmpty),
          "perferentialPrice" -> bigDecimal,
          "startDate" -> date,
          "endDate" -> date,
          "useConditions" -> nonEmptyText,
          "presentTime" -> nonEmptyText,
          "description" -> nonEmptyText
    ){
      (couponName, salonId, serviceItems, perferentialPrice, startDate, endDate, useConditions, presentTime, description) => Coupon(new ObjectId, "", couponName,
          new ObjectId(salonId), serviceItems, BigDecimal(0), perferentialPrice, 0, startDate, endDate, useConditions, presentTime, description, true)
    }
    {
      coupon => Some((coupon.couponName, coupon.salonId.toString(), coupon.serviceItems, coupon.perferentialPrice, coupon.startDate,
          coupon.endDate, coupon.useConditions, coupon.presentTime, coupon.description))
    }
  }
  
  def conditionForm: Form[CouponServiceType] = Form {
      mapping(
            "serviceTypes" -> list(
             mapping (
                 "serviceTypeName" -> text
             ){(serviceTypeName) => ServiceType(new ObjectId(), serviceTypeName, "")}
             {serviceType => Some((serviceType.serviceTypeName))}),
            "subMenuFlg" -> optional(text) 
      )(CouponServiceType.apply)(CouponServiceType.unapply)
  }
  
  def index = Action {
    val coupons:List[Coupon] = Coupon.findAll().toList
    
    // 获取当前时间的前7天的日期，用于判断是否为新券还是旧券
    var beforeSevernDate = Calendar.getInstance()
    beforeSevernDate.setTime(new Date())
    beforeSevernDate.add(Calendar.DAY_OF_YEAR, -7)
    
    Ok(views.html.coupon.couponOverview(coupons, beforeSevernDate.getTime()))
  }
  
  /**
   * 进入创建优惠劵画面
   */
  def couponMain(salonId: ObjectId) = Action{
	  val salon: Option[Salon] = Salon.findById(salonId)
	  var createCoupon: CreateCoupon = CreateCoupon(null, salon.get, Service.findBySalonId(salonId))
	  
	  salon match {
	    case Some(s) => Ok(html.salon.admin.createSalonCoupon(s, couponForm, Service.findBySalonId(salonId)))
        case None => NotFound
	  }
  }
  
  /**
   * 创建优惠劵
   */
  def createCoupon(salonId: ObjectId) = Action {implicit request =>
    couponForm.bindFromRequest.fold(
          errors => BadRequest(html.salon.admin.createSalonCoupon(Salon.findById(salonId).get,errors,Service.findBySalonId(salonId))),
        {
          coupon =>
            var services: List[Service] = Nil
            var originalPrice: BigDecimal = 0
            var serviceDuration: Int = 0
            
            for(serviceItem <- coupon.serviceItems) {
              val service: Option[Service] = Service.findOneById(serviceItem.id)
              service match {
                case Some(s) => services = s::services
                				originalPrice = s.price + originalPrice
                				serviceDuration = s.duration + serviceDuration
                case None => NotFound
              }
            }
            val couponTemp = coupon.copy(couponId = coupon.id.toString(), salonId = coupon.salonId, serviceItems = services, originalPrice = originalPrice, serviceDuration = serviceDuration)
            Coupon.save(couponTemp)
            
            val salon: Option[Salon] = Salon.findById(coupon.salonId)
		    val coupons: List[Coupon] = Coupon.findBySalon(coupon.salonId)
		    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
            val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
            salon match {
		      case Some(s) => Ok(html.salon.admin.mySalonCouponAll(s, conditionForm.fill(couponServiceType), serviceTypes, coupons))
		      case None => NotFound
		    }
        }
    )
  }
  
  /**
     * 显示店铺所有的优惠劵
     */
    def showCoupons(salonId: ObjectId) = Action {
      val salon: Option[Salon] = Salon.findById(salonId)
      val coupons: List[Coupon] = Coupon.findBySalon(salonId)
      
      salon match {
        case Some(s) => Ok(html.coupon.showCouponGroup(s, coupons))
        case None => NotFound
      }
    }
  
  /**
   * 进入修改优惠劵画面
   */
  def editCouponInfo(couponId: ObjectId) = Action {
    val coupon = Coupon.findOneById(couponId)
    
    coupon match {
      case Some(s) => val salon = Salon.findById(s.salonId)
                      salon match {
                         case Some(k) => Ok(html.salon.admin.editSalonCoupon(k, couponForm.fill(s), Service.findBySalonId(s.salonId), s))
                         case None => NotFound
                      }
      case None => NotFound
    }
  }
  
  /**
   * 更新优惠劵信息
   */
  def updateCoupon(couponId: ObjectId, salonId: ObjectId) = Action { implicit request =>
    couponUpdateForm.bindFromRequest.fold(
        errors => BadRequest(html.salon.admin.editSalonCoupon(Salon.findById(salonId).get, errors, Service.findBySalonId(salonId), Coupon.findOneById(couponId).get)),
        {
          coupon =>
            var services: List[Service] = Nil
            var originalPrice: BigDecimal = 0
            var serviceDuration: Int = 0
			    
            for(serviceItem <- coupon.serviceItems) {
              val service: Option[Service] = Service.findOneById(serviceItem.id)
              service match {
                case Some(s) => services = s::services
                				originalPrice = s.price + originalPrice
                				serviceDuration = s.duration + serviceDuration
                case None => NotFound
              }
            }
            val couponTemp = coupon.copy(id = couponId, couponId = couponId.toString(), salonId = coupon.salonId, serviceItems = services, originalPrice = originalPrice, serviceDuration = serviceDuration)
            Coupon.save(couponTemp)
            
            val salon: Option[Salon] = Salon.findById(coupon.salonId)
		    val coupons: List[Coupon] = Coupon.findBySalon(coupon.salonId)
		    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
		    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
		    
            salon match {
		      case Some(s) => Ok(html.salon.admin.mySalonCouponAll(s, conditionForm.fill(couponServiceType), serviceTypes, coupons))
		      case None => NotFound
		    }
        }
    )
    
  }
  
  /**
   * 无效优惠劵
   */
  def invalidCoupon(couponId: ObjectId) = Action {
    val coupon: Option[Coupon] = Coupon.findOneById(couponId)
    
    coupon match {
      case Some(s) => val couponTemp = s.copy(isValid = false)
                      Coupon.save(couponTemp)
                      val salon: Option[Salon] = Salon.findById(s.salonId)
                      val coupons: List[Coupon] = Coupon.findBySalon(s.salonId)
                      val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
                      val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
                      salon match {
                      	case Some(s) => Ok(html.salon.admin.mySalonCouponAll(s, conditionForm.fill(couponServiceType), serviceTypes, coupons))
                      	case None => NotFound
                      }
                      
      case None => NotFound
    }
  }
  

}
