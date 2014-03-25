package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._

import models._
import views._

object Coupons extends Controller {
  
  def couponForm: Form[Coupon] = Form {
    mapping(
        "couponName" -> nonEmptyText,
        "salonId" -> text,
        "serviceItems" -> seq(
         mapping(
           "id" -> text
         ){(id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true)}
         {service => Some((service.id.toString()))}),
          "perferentialPrice" -> bigDecimal,
          "startDate" -> date,
          "endDate" -> date,
          "useConditions" -> text,
          "presentTime" -> text,
          "description" -> text
    ){
      (couponName, salonId, serviceItems, perferentialPrice, startDate, endDate, useConditions, presentTime, description) => Coupon(new ObjectId, "", couponName,
          new ObjectId(salonId), serviceItems, BigDecimal(0), perferentialPrice, 0, startDate, endDate, useConditions, presentTime, description, true)
    }
    {
      coupon => Some((coupon.couponName, coupon.salonId.toString(), coupon.serviceItems, coupon.perferentialPrice, coupon.startDate,
          coupon.endDate, coupon.useConditions, coupon.presentTime, coupon.description))
    }
  }
  
  def condtionForm: Form[CouponServiceType] = Form {
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
    Ok(views.html.coupon.couponOverview(coupons))
  }
  
  /**
   * 进入创建优惠劵画面
   */
  def couponMain(salonId: ObjectId) = Action{
	  val salon: Option[Salon] = Salon.findById(salonId)
	  
	  salon match {
	    case Some(s) => Ok(html.salon.admin.createSalonCoupon(s, couponForm, Service.findBySalonId(salonId)))
        case None => NotFound
	  }
  }
  
  /**
   * 创建优惠劵
   */
  def createCoupon = Action {implicit request =>
    couponForm.bindFromRequest.fold(
        errors => BadRequest(views.html.error.errorMsg(errors)),
        {
          coupon =>
            var services: List[Service] = Nil
            var originalPrice: BigDecimal = 0
            var serviceDuration: Int = 0
            
            for(serviceItem <- coupon.serviceItems) {
              val service: Option[Service] = Service.findOneByServiceId(serviceItem.id)
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
            salon match {
		      case Some(s) => Ok(html.salon.admin.mySalonCouponAll(s, coupons))
		      case None => NotFound
		    }
        }
    )
  }
  
  /**
   * 根据店铺查找所有优惠劵，菜单和服务
   */
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val coupons: List[Coupon] = Coupon.findBySalon(salonId)
    val menus: List[Menu] = Menu.findBySalon(salonId)
    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
    val serviceTypeNames: List[String] = Service.getServiceTypeList
    val couponServiceType: CouponServiceType = CouponServiceType(Nil, Some("1"))
    
    var servicesByTypes: List[ServiceByType] = Nil
    for(serviceType <- serviceTypeNames) {
      var servicesByType: ServiceByType = ServiceByType("", Nil)
      val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = Service.getTypeListBySalonId(salonId, serviceType))
      servicesByTypes = y::servicesByTypes
    }
    
    salon match {
        case Some(s) => Ok(html.salon.store.salonInfoCouponAll(s, condtionForm.fill(couponServiceType), serviceTypes, coupons, menus, servicesByTypes))
        case None => NotFound
    }
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
   * 根据查找条件检索出符合的优惠劵

   */
  def findByCondtion(salonId: ObjectId) = Action {implicit request =>
    condtionForm.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        serviceType =>
          var coupons: List[Coupon] = Nil
          var menus: List[Menu] = Nil
          var serviceTypeNames: List[String] = Nil
          var conditions: List[String] = Nil
          var servicesByTypes: List[ServiceByType] = Nil
          var typebySearchs: List[ServiceType] = Nil
          var couponServiceType: CouponServiceType = CouponServiceType(Nil, serviceType.subMenuFlg)

          for(serviceTypeOne <- serviceType.serviceTypes) {
              conditions = serviceTypeOne.serviceTypeName::conditions
              val serviceType: Option[ServiceType] = ServiceType.findOneByTypeName(serviceTypeOne.serviceTypeName)
              serviceType match {
                  case Some(s) => typebySearchs = s::typebySearchs
                  case None => NotFound
              }
          }
            
          couponServiceType = couponServiceType.copy(serviceTypes = typebySearchs)
            
          val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
          if(serviceType.subMenuFlg == None) {
            //coupons = Coupon.findContainCondtions(serviceTypes)
          } else {
            if(serviceType.serviceTypes.isEmpty) {
              coupons = Coupon.findBySalon(salonId)
              menus = Menu.findBySalon(salonId)
              serviceTypeNames = Service.getServiceTypeList
		      for(serviceType <- serviceTypeNames) {
		        var servicesByType: ServiceByType = ServiceByType("", Nil)
		        val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = Service.getTypeListBySalonId(salonId, serviceType))
		        servicesByTypes = y::servicesByTypes
		      }
            } else {
              coupons = Coupon.findContainCondtions(conditions)
              menus = Menu.findContainCondtions(conditions)
		      for(serviceTypeOne <- serviceType.serviceTypes) {
		        var servicesByType: ServiceByType = ServiceByType("", Nil)
		        val y = servicesByType.copy(serviceTypeName = serviceTypeOne.serviceTypeName, serviceItems = Service.getTypeListBySalonId(salonId, serviceTypeOne.serviceTypeName))
		        servicesByTypes = y::servicesByTypes
		      }
            }
          }
          val salon: Option[Salon] = Salon.findById(salonId)
          
          salon match {
	          case Some(s) => Ok(html.salon.store.salonInfoCouponAll(s, condtionForm.fill(couponServiceType), serviceTypes, coupons, menus, servicesByTypes))
	          case None => NotFound
	      }
      })
  }
  
  /**
   * 进入修改优惠劵画面
   */
  def editCouponInfo(couponId: ObjectId) = Action {
    val coupon = Coupon.findOneById(couponId)
    
    coupon match {
      case Some(s) => val salon = Salon.findById(s.salonId)
                      salon match {
                         case Some(k) => Ok(html.salon.admin.editSalonCoupon(k, couponForm.fill(s), Service.findBySalonId(s.salonId)))
                         case None => NotFound
                      }
      case None => NotFound
    }
  }
  
  /**
   * 更新优惠劵信息
   */
  def updateCoupon(couponId: ObjectId) = Action { implicit request =>
    couponForm.bindFromRequest.fold(
        errors => BadRequest(views.html.error.errorMsg(errors)),
        {
          coupon =>
            var services: List[Service] = Nil
            var originalPrice: BigDecimal = 0
            var serviceDuration: Int = 0
            
            for(serviceItem <- coupon.serviceItems) {
              val service: Option[Service] = Service.findOneByServiceId(serviceItem.id)
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
            salon match {
		      case Some(s) => Ok(html.salon.admin.mySalonCouponAll(s, coupons))
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
                      salon match {
                      	case Some(s) => Ok(html.salon.admin.mySalonCouponAll(s, coupons))
                      	case None => NotFound
                      }
                      
      case None => NotFound
    }
  }
  

}
