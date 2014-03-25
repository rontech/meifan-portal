package controllers

import play.api._
import play.api.mvc._
import models.Stylist
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date


object Stylists extends Controller {


  def index = TODO
  
 
   def findStylistById(id: ObjectId) = Action {
        val stylist = Stylist.findOneById(id)
        val salonId =  SalonAndStylist.findByStylistId(id).get.salonId
        val salon = Salon.findById(salonId)
        val style = Style.findByStylistId(id)
        Ok(html.salon.store.salonInfoStylistInfo(salon = salon.get, stylist = stylist.get, style = style))
    }

  /**
   *  同意salon邀请
   */
  def agreeSalonApply(stylistId: ObjectId, salonId: ObjectId) = Action {
   val record = SalonStylistApplyRecord.findOneSalonApRd(salonId, stylistId)
        record match {
          case Some(re) => {
            SalonStylistApplyRecord.agreeStylistApply(re)
            val stylist = Stylist.findOneById(re.stylistId)
            Stylist.becomeStylist(stylistId)
            SalonAndStylist.entrySalon(salonId, stylistId)
            Redirect(routes.SalonsAdmin.myStylist(salonId))
          }
          case None => NotFound
        }
  }
  
  /**
   *  拒绝salon邀请
   */
  def rejectSalonApply(stylistId: ObjectId, salonId: ObjectId) = Action {
	 val record = SalonStylistApplyRecord.findOneSalonApRd(salonId, stylistId)
        record match {
          case Some(re) => {
            SalonStylistApplyRecord.agreeStylistApply(re)
            val stylist = Stylist.findOneById(re.stylistId)
            Redirect(routes.SalonsAdmin.myStylist(salonId))
          }
          case None => NotFound
        } 
  }
  
}
