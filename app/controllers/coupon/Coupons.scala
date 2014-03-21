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
    }.verifying(
        "This name has been used!",
        coupon => !Coupon.checkCoupon(coupon.couponName)   
    )
  }
  
  /**
   * 定义一个搜索表单
   */
  val condtionForm = Form(tuple(
      "mc0" -> optional(text),
      "mc1" -> optional(text),
      "mc2" -> optional(text),
      "mc3" -> optional(text),
      "mc4" -> optional(text),
      "mc5" -> optional(text),
      "mc6" -> optional(text),
      "mc7" -> optional(text),
      "subMenuFlg" -> optional(text)
      )
  )
  
  def index = Action {
    val coupons:Seq[Coupon] = Coupon.findAll
    Ok(views.html.coupon.couponOverview(coupons))
  }
  
  def couponMain(salonId: ObjectId) = Action{
	  val salon: Option[Salon] = Salon.findById(salonId)
	  
	  salon match {
	    case Some(s) => Ok(html.salon.admin.createSalonCoupon(s, couponForm, Service.findBySalonId(salonId)))
        case None => NotFound
	  }
  }
  
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
    val coupons: Seq[Coupon] = Coupon.findBySalon(salonId)
    val menus: Seq[Menu] = Menu.findBySalon(salonId)
    val serviceTypes: Seq[ServiceType] = ServiceType.findAll().toList
    val serviceTypeNames: Seq[String] = Service.getServiceTypeList
    
    var servicesByTypes: List[ServiceByType] = Nil
    for(serviceType <- serviceTypeNames) {
      var servicesByType: ServiceByType = ServiceByType("", Nil)
      val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = Service.getTypeListBySalonId(salonId, serviceType))
      servicesByTypes = y::servicesByTypes
    }
    
    // TODO: process the salon not exist pattern.
    Ok(html.salon.store.salonInfoCouponAll(salon = salon.get, serviceTypes = serviceTypes, coupons = coupons, menus, servicesByTypes))
  }
  
  /**
     * 显示店铺所有的优惠劵
     */
    def showCoupons(salonId: ObjectId) = Action {
      val salon: Option[Salon] = Salon.findById(salonId)
      val coupons: List[Coupon] = Coupon.findBySalon(salonId)
      
      Ok(html.coupon.showCouponGroup(salon.get, coupons))
    }
  
  /**
   * 根据查找条件检索出符合的优惠劵

   */
  def findByCondtion(salonId: ObjectId) = Action {implicit request =>
    condtionForm.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        serviceType =>
          val subMenuFlg = serviceType.productElement(serviceType.productArity-1)
          var coupons: Seq[Coupon] = Nil
          var menus: Seq[Menu] = Nil
          var serviceTypeNames: Seq[String] = Nil
          var conditions: List[String] = Nil
          var servicesByTypes: List[ServiceByType] = Nil

          for(i <- 0 to serviceType.productArity-2) {
            serviceType.productElement(i) match {
              case Some(s) => conditions = (s.toString)::conditions
              case None => NotFound
            }
          }
          
          val serviceTypes: Seq[ServiceType] = ServiceType.findAll().toList
          if(subMenuFlg == None) {
            //coupons = Coupon.findContainCondtions(serviceTypes)
          } else {
            if(conditions.isEmpty) {
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
		      for(serviceType <- conditions) {
		        var servicesByType: ServiceByType = ServiceByType("", Nil)
		        val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = Service.getTypeListBySalonId(salonId, serviceType))
		        servicesByTypes = y::servicesByTypes
		      }
            }
          }
          val salon: Option[Salon] = Salon.findById(salonId)
          
          Ok(html.salon.store.salonInfoCouponAll(salon = salon.get, serviceTypes = serviceTypes, coupons = coupons, menus, servicesByTypes))
      })
  }

}
