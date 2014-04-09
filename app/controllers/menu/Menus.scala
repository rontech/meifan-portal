package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import java.util.Date

import models._
import views._
import play.api.i18n.Messages

object Menus extends Controller {
  
  def menuForm: Form[Menu] = Form {
    mapping(
        "menuName" -> nonEmptyText,
        "salonId" -> text,
        "serviceItems" -> list(
         mapping(
           "id" -> text
         ){(id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true)}
         {service => Some((service.id.toString()))}
         ).verifying(Messages("menu.menuServiceRequired"), serviceItems => !serviceItems.isEmpty),
          "description" -> nonEmptyText(10, 100)
    ){
      (menuName, salonId, serviceItems, description) => Menu(new ObjectId, menuName, description, new ObjectId(salonId), serviceItems, 0, BigDecimal(0), new Date(), None, true)
    }
    {
      menu => Some((menu.menuName, menu.salonId.toString(), menu.serviceItems, menu.description))
    }.verifying(
        Messages("menu.menuNameRepeat"),
        menu => !Menu.checkMenuIsExit(menu.menuName, menu.salonId)   
    )
  }
  
  def menuUpdateForm: Form[Menu] = Form {
    mapping(
        "menuName" -> nonEmptyText,
        "salonId" -> text,
        "serviceItems" -> list(
         mapping(
           "id" -> text
         ){(id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true)}
         {service => Some((service.id.toString()))}
         ).verifying(Messages("menu.menuServiceRequired"), serviceItems => !serviceItems.isEmpty),
          "description" -> nonEmptyText(10, 100)
    ){
      (menuName, salonId, serviceItems, description) => Menu(new ObjectId, menuName, description, new ObjectId(salonId), serviceItems, 0, BigDecimal(0), new Date(), None, true)
    }
    {
      menu => Some((menu.menuName, menu.salonId.toString(), menu.serviceItems, menu.description))
    }
  }
  
  def menuMain(salonId: ObjectId) = Action{
	  val salon: Option[Salon] = Salon.findOneById(salonId)
	  
	  salon match {
	    case Some(s) => Ok(html.salon.admin.createSalonMenu(s, menuForm, Service.findBySalonId(salonId)))
        case None => NotFound
	  }
  }
  
  /**
   * 创建菜单
   */
  def createMenu(salonId: ObjectId) = Action {implicit request =>
    menuForm.bindFromRequest.fold(
        errors => BadRequest(html.salon.admin.createSalonMenu(Salon.findOneById(salonId).get, errors, Service.findBySalonId(salonId))),
        {
          menu =>
            var services: List[Service] = Nil
            var originalPrice: BigDecimal = 0
            var serviceDuration: Int = 0
            
            println("menu.serviceItems = " + menu.serviceItems)
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
            
            val salon: Option[Salon] = Salon.findOneById(menu.salonId)
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
      case Some(s) => val salon = Salon.findOneById(s.salonId)
                      salon match {
                         case Some(k) => Ok(html.salon.admin.editSalonMenu(k, menuForm.fill(s), Service.findBySalonId(s.salonId), s))
                         case None => NotFound
                      }
      case None => NotFound
    }
  }
  
  /**
   * 更新菜单信息
   */
  def updateMenu(menuId: ObjectId, salonId: ObjectId) = Action { implicit request =>
    menuUpdateForm.bindFromRequest.fold(
        errors => BadRequest(html.salon.admin.editSalonMenu(Salon.findOneById(salonId).get, errors, Service.findBySalonId(salonId), Menu.findOneById(menuId).get)),
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
            
            val salon: Option[Salon] = Salon.findOneById(menu.salonId)
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
                      val salon: Option[Salon] = Salon.findOneById(s.salonId)
                      val menus: List[Menu] = Menu.findBySalon(s.salonId)
                      salon match {
                      	case Some(s) => Ok(html.salon.admin.mySalonMenuAll(s, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
                      	case None => NotFound
                      }
                      
      case None => NotFound
    }
  }
  
}
