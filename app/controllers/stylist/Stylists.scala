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
  
   /**
   * 
   */
  def findById(stylistId: ObjectId) = Action { 
    val stylist: Option[Stylist] = Stylist.findOneById(stylistId)
    val salonId =  SalonAndStylist.findByStylistId(stylistId).get.salonId
    val salon: Option[Salon] = Salon.findById(salonId)
    Ok(html.salon.store.salonInfoStylist(salon.get, stylist.get))
  }
  
  def findBySalon(salonId: ObjectId) = Action {

    val salon: Option[Salon] = Salon.findById(salonId)
    val nav: String = "style"
    val stylistsOfSalon: List[Stylist] = Stylist.findBySalon(salonId)    
     // TODO
    Ok(html.salon.store.salonInfoStylistAll(salon.get, stylistsOfSalon))

  }
  
  def findStylistById(id: ObjectId) = Action {
    val stylist = Stylist.findOneById(id)
    val salonId =  SalonAndStylist.findByStylistId(id).get.salonId
    val salon = Salon.findById(salonId)
    val style = Style.findByStylistId(id)
    Ok(html.salon.store.salonInfoStylistInfo(salon = salon.get, stylist = stylist.get, style = style))
  }
  
  def agreeSalonApply(stylistId: ObjectId, salonId: ObjectId) = Action {
    val record = ApplyRecord.findSalonAyRd(salonId, stylistId)
    var userId: ObjectId = new ObjectId
        record match {
          case Some(re) => {
            val  rec = new ApplyRecord(re.id, re.stylistId, re.salonId, re.applyType,
                re.createTime, Option(new Date), None, None, 1)
            ApplyRecord.save(rec.copy(id = re.id))
            val stylist = Stylist.findOneById(stylistId)
            stylist match {
              case Some(sty) => {
                userId = sty.publicId
                Stylist.save(sty.copy(id = sty.id, isVarified = true, isValid = true))
              }
              case None => NotFound
            }
            Redirect(routes.Users.myPage())
          }
          case None => NotFound
        }
  }
  
  def rejectSalonApply(stylistId: ObjectId, salonId: ObjectId) = Action {
    val record = ApplyRecord.findStylistAyRd(salonId, stylistId)
    var userId: ObjectId = new ObjectId
        record match {
          case Some(re) => {
            val  rec = new ApplyRecord(re.id, re.stylistId, re.salonId, re.applyType,
                re.createTime,None ,Option(new Date), None, 2)
            ApplyRecord.save(rec.copy(id = re.id))
            val stylist = Stylist.findOneById(stylistId)
            stylist match {
              case Some(sty) => {
                userId = sty.publicId
                Stylist.save(sty.copy(id = sty.id, isVarified = true, isValid = true))
              }
              case None => NotFound
            }
            Redirect(routes.Users.myPage())
          }
          case None => NotFound
        }
  }
  
}
