package controllers.auth

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import se.radley.plugin.salat.Binders._
import java.util.Date
import models._
import views._
import play.api.i18n.Messages
import jp.t2v.lab.play2.auth._
import controllers._

object Menus extends Controller with AuthElement with SalonAuthConfigImpl{
  
  def menuForm: Form[Menu] = Form {
    mapping(
        "menuName" -> nonEmptyText,
        "salonId" -> text,
        "serviceItems" -> list(
         mapping(
           "id" -> text
         ){(id) => Service(new ObjectId(id), "", "", "", new ObjectId(), BigDecimal(0), 0, null, null, true)}
         {service => Some((service.id.toString))}
         ).verifying(Messages("menu.menuServiceRequired"), serviceItems => !serviceItems.isEmpty),
          "description" -> nonEmptyText(10, 100)
    ){
      (menuName, salonId, serviceItems, description) => Menu(new ObjectId, menuName, description, new ObjectId(salonId), serviceItems, 0, BigDecimal(0), new Date(), None, true)
    }
    {
      menu => Some((menu.menuName, menu.salonId.toString, menu.serviceItems, menu.description))
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
         {service => Some((service.id.toString))}
         ).verifying(Messages("menu.menuServiceRequired"), serviceItems => !serviceItems.isEmpty),
          "description" -> nonEmptyText(10, 100)
    ){
      (menuName, salonId, serviceItems, description) => Menu(new ObjectId, menuName, description, new ObjectId(salonId), serviceItems, 0, BigDecimal(0), new Date(), None, true)
    }
    {
      menu => Some((menu.menuName, menu.salonId.toString, menu.serviceItems, menu.description))
    }
  }

    /**
     * 进入创建画面
     */
  def menuMain = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
	  Ok(html.salon.admin.createSalonMenu(salon, menuForm, Service.findBySalonId(salon.id)))
  }
  
  /**
   * 创建菜单
   */
  def createMenu = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    menuForm.bindFromRequest.fold(
        errors => BadRequest(html.salon.admin.createSalonMenu(salon, errors, Service.findBySalonId(salon.id))),
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
            
		    val menus: List[Menu] = Menu.findBySalon(menu.salonId)
		    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
		    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
		    
            Ok(html.salon.admin.mySalonMenuAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
        }
    )
  }
  
  /**
   * 进入修改菜单画面
   */
  def editMenuInfo(menuId: ObjectId)= StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
    val salon = loggedIn
    val menu = Menu.findOneById(menuId)
    
    menu match {
      case Some(s) => Ok(html.salon.admin.editSalonMenu(salon, menuForm.fill(s), Service.findBySalonId(salon.id), s))
      case None => NotFound
    }
  }
  
  /**
   * 更新菜单信息
   */
  def updateMenu(menuId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
    menuUpdateForm.bindFromRequest.fold(
        errors => BadRequest(html.salon.admin.editSalonMenu(salon, errors, Service.findBySalonId(salon.id), Menu.findOneById(menuId).get)),
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
            
            val menus: List[Menu] = Menu.findBySalon(menu.salonId)
            val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
		    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
		    
            Ok(html.salon.admin.mySalonMenuAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
        }
    )
    
  }
  
  /**
   * 无效菜单
   */
  def invalidMenu(menuId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val salon = loggedIn
    val menu: Option[Menu] = Menu.findOneById(menuId)
    val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
    val couponServiceType: CouponServiceType = CouponServiceType(Nil, None)
    
    menu match {
      case Some(s) => val menuTemp = s.copy(expireDate = Some(new Date()), isValid = false)
                      Menu.save(menuTemp)
                      val menus: List[Menu] = Menu.findBySalon(s.salonId)
                      Ok(html.salon.admin.mySalonMenuAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
      case None => NotFound
    }
  }
    /**
     * 根据查找条件检索出符合的菜单
     */
    def findMenus = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
        val salon = loggedIn
        Coupons.conditionForm.bindFromRequest.fold(
        errors => BadRequest(views.html.error.errorMsg(errors)),
        {
            serviceType =>
                var menus: List[Menu] = Nil
                var conditions: List[String] = Nil
                var servicesByTypes: List[ServiceByType] = Nil
                var typeBySearches: List[ServiceType] = Nil
                var couponServiceType: CouponServiceType = CouponServiceType(Nil, serviceType.subMenuFlg)

                for(serviceTypeOne <- serviceType.serviceTypes) {
                    conditions = serviceTypeOne.serviceTypeName::conditions
                    val serviceType: Option[ServiceType] = ServiceType.findOneByTypeName(serviceTypeOne.serviceTypeName)
                    serviceType match {
                        case Some(s) => typeBySearches = s::typeBySearches
                        case None => NotFound
                    }
                }
                couponServiceType = couponServiceType.copy(serviceTypes = typeBySearches)

                val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
                if(serviceType.subMenuFlg == None) {
                    //coupons = Coupon.findContainCondtions(serviceTypes)
                } else {
                    if(serviceType.serviceTypes.isEmpty) {
                        menus = Menu.findBySalon(salon.id)
                    } else {
                        menus = Menu.findContainCondtions(conditions, salon.id)
                    }
                }
               Ok(html.salon.admin.mySalonMenuAll(salon, Coupons.conditionForm.fill(couponServiceType), serviceTypes, menus))
        })
    }

  
}
