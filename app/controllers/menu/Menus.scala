package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import java.util.Date

import models._
import views._

object Menus extends Controller {
  
  def menuForm: Form[Menu] = Form {
    mapping(
        "menuName" -> nonEmptyText,
        "salonId" -> text,
        "serviceItems" -> seq(
         mapping(
           "id" -> text
         ){(id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true)}
         {service => Some((service.id.toString()))}),
          "description" -> text
    ){
      (menuName, salonId, serviceItems, description) => Menu(new ObjectId, menuName, description, new ObjectId(salonId), serviceItems, 0, BigDecimal(0), new Date(), None, true)
    }
    {
      menu => Some((menu.menuName, menu.salonId.toString(), menu.serviceItems, menu.description))
    }
  }
  
  def menuMain(salonId: ObjectId) = Action{
	  val salon: Option[Salon] = Salon.findById(salonId)
	  
	  salon match {
	    case Some(s) => Ok(html.salon.admin.createSalonMenu(s, menuForm, Service.findBySalonId(salonId)))
        case None => NotFound
	  }
  }
  
  /**
   * 创建菜单
   */
  def createMenu = Action {implicit request =>
    menuForm.bindFromRequest.fold(
        errors => BadRequest(views.html.error.errorMsg(errors)),
        {
          menu =>
            var services: List[Service] = Nil
            var originalPrice: BigDecimal = 0
            var serviceDuration: Int = 0
            
            for(serviceItem <- menu.serviceItems) {
              val service: Option[Service] = Service.findOneById(serviceItem.id)
              service match {
                case Some(s) => services = s::services
                				originalPrice = s.price + originalPrice
                				serviceDuration = s.duration + serviceDuration
                case None => NotFound
              }
            }
            val menuTemp = menu.copy(salonId = menu.salonId, serviceItems = services, originalPrice = originalPrice, serviceDuration = serviceDuration)
            Menu.save(menuTemp)
            
            val salon: Option[Salon] = Salon.findById(menu.salonId)
		    val menus: List[Menu] = Menu.findBySalon(menu.salonId)
		    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
		    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
		    
            salon match {
		      case Some(s) => Ok(html.salon.admin.mySalonMenuAll(s, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
		      case None => NotFound
		    }
        }
    )
  }
  
  /**
   * 进入修改菜单画面
   */
  def editMenuInfo(menuId: ObjectId) = Action {
    val menu = Menu.findOneById(menuId)
    
    menu match {
      case Some(s) => val salon = Salon.findById(s.salonId)
                      salon match {
                         case Some(k) => Ok(html.salon.admin.editSalonMenu(k, menuForm.fill(s), Service.findBySalonId(s.salonId)))
                         case None => NotFound
                      }
      case None => NotFound
    }
  }
  
  /**
   * 更新菜单信息
   */
  def updateMenu(menuId: ObjectId) = Action { implicit request =>
    menuForm.bindFromRequest.fold(
        errors => BadRequest(views.html.error.errorMsg(errors)),
        {
          menu =>
            val oldMenu: Option[Menu] = Menu.findOneById(menuId)
            var services: List[Service] = Nil
            var originalPrice: BigDecimal = 0
            var serviceDuration: Int = 0
            
            for(serviceItem <- menu.serviceItems) {
              val service: Option[Service] = Service.findOneById(serviceItem.id)
              service match {
                case Some(s) => services = s::services
                				originalPrice = s.price + originalPrice
                				serviceDuration = s.duration + serviceDuration
                case None => NotFound
              }
            }
            
            oldMenu match {
              case Some(s) => val newMenu = s.copy(description = menu.description, serviceItems = services, originalPrice = originalPrice, serviceDuration = serviceDuration)
            		  		  Menu.save(newMenu)
              case None => NotFound
            }
            
            val salon: Option[Salon] = Salon.findById(menu.salonId)
            val menus: List[Menu] = Menu.findBySalon(menu.salonId)
            val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
		    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
		    
            salon match {
		      case Some(s) => Ok(html.salon.admin.mySalonMenuAll(s, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
		      case None => NotFound
		    }
        }
    )
    
  }
  
  /**
   * 无效菜单
   */
  def invalidMenu(menuId: ObjectId) = Action {
    val menu: Option[Menu] = Menu.findOneById(menuId)
    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
    
    menu match {
      case Some(s) => val menuTemp = s.copy(expireDate = Some(new Date()), isValid = false)
                      Menu.save(menuTemp)
                      val salon: Option[Salon] = Salon.findById(s.salonId)
                      val menus: List[Menu] = Menu.findBySalon(s.salonId)
                      salon match {
                      	case Some(s) => Ok(html.salon.admin.mySalonMenuAll(s, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
                      	case None => NotFound
                      }
                      
      case None => NotFound
    }
  }
  
}
