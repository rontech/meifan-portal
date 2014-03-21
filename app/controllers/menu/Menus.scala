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
  
  def createMenu = Action {implicit request =>
    menuForm.bindFromRequest.fold(
        errors => BadRequest(views.html.error.errorMsg(errors)),
        {
          menu =>
            var services: List[Service] = Nil
            var originalPrice: BigDecimal = 0
            var serviceDuration: Int = 0
            
            for(serviceItem <- menu.serviceItems) {
              val service: Option[Service] = Service.findOneByServiceId(serviceItem.id)
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
            salon match {
		      case Some(s) => Ok(html.salon.admin.mySalonMenuAll(s, menus))
		      case None => NotFound
		    }
        }
    )
  }
  
}
