package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._

import models._
import views._

object Coupons extends Controller {
  
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
  
  /**
   * 根据店铺查找所有优惠劵
   */
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val coupons: Seq[Coupon] = Coupon.findBySalon(salonId)
    val menus: Seq[Menu] = Menu.findBySalon(salonId)
    val serviceTypes: Seq[ServiceType] = ServiceType.findAll().toList
    val serviceTypeNames: Seq[String] = Service.getServiceTypeList
    
    // TODO: process the salon not exist pattern.
    Ok(html.salon.store.salonInfoCouponAll(salon = salon.get, serviceTypes = serviceTypes, coupons = coupons, menus, serviceTypeNames))
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
          var coupons: Seq[Coupon] = None.toList
          var menus: Seq[Menu] = None.toList
          var serviceTypeNames: Seq[String] = Nil
          var conditions: List[ObjectId] = None.toList

          for(i <- 0 to serviceType.productArity-2) {
            serviceType.productElement(i) match {
              case Some(s) => val serviceTypeId = new ObjectId(s.toString)
              				  conditions = serviceTypeId::conditions
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
            } else {
              coupons = Coupon.findContainCondtions(conditions)
              menus = Menu.findContainCondtions(conditions)
              serviceTypeNames = Service.getTypeByCondition(conditions)
            }
          }
          val salon: Option[Salon] = Salon.findById(salonId)
          
          Ok(html.salon.store.salonInfoCouponAll(salon = salon.get, serviceTypes = serviceTypes, coupons = coupons, menus, serviceTypeNames))
      })
  }
  
  
}
