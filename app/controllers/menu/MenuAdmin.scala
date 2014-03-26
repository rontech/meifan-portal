package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import java.util.Date

import models._
import views._

object MenuAdmin extends Controller {
    
   /**
   * 根据查找条件检索出符合的菜单
   */
  def findMenus(salonId: ObjectId) = Action {implicit request =>
    Coupons.condtionForm.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        serviceType =>
          var menus: List[Menu] = Nil
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
              menus = Menu.findBySalon(salonId)
            } else {
              menus = Menu.findContainCondtions(conditions)
            }
          }
          val salon: Option[Salon] = Salon.findById(salonId)

          salon match {
	        case Some(s) => Ok(html.salon.admin.mySalonMenuAll(s, Coupons.condtionForm.fill(couponServiceType), serviceTypes, menus))
	        case None => NotFound
	      }
      })
  }
	
}