package controllers

import play.api._
import play.api.mvc._
import models.Stylist
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._

import models._
import views._


object Stylists extends Controller {


  def index = TODO
  
   /**
   * 
   */
  def findById(stylistId: ObjectId) = Action { 
    val stylist: Option[Stylist] = Stylist.findById(stylistId)
    val salonId =  stylist.get.salonId
    val salon: Option[Salon] = Salon.findById(salonId)
    Ok(html.salon.store.salonInfoStylist(salon.get, stylist.get))
  }
  
  def findBySalon(salonId: ObjectId) = Action {

    val salon: Option[Salon] = Salon.findById(salonId)
    val nav: String = "style"
    val stylistsOfSalon: Seq[Stylist] = Stylist.findBySalon(salonId)    
    Ok(html.stylist.overview(stylistsOfSalon))
    val style: Seq[Style] = Style.findBySalonId(salonId) 
     // TODO
    Ok(html.salon.store.salonInfoStylistAll(salon.get, stylistsOfSalon))

  }
  
  def findStylistById(id: ObjectId) = Action{
    val stylist = Stylist.findById(id)
    val salon = Salon.findById(stylist.get.salonId)
    val style = Style.findByStylistId(stylist.get.salonId, id)
    Ok(html.salon.store.salonInfoStylistInfo(salon = salon.get, stylist = stylist.get, style = style))
    
  }
  
   def findStylist = Action{
      Ok(views.html.stylist.findStylist("hello"))
   }
    
    def checkStylist = Action{
      Ok(views.html.stylist.checkStylist("hello"))
    }

}
