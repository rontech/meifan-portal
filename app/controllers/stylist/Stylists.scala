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
    val stylist: Option[Stylist] = Stylist.findById(stylistId)
    val salonId =  stylist.get.salonId
    val salon: Option[Salon] = Salon.findById(salonId)
    Ok(html.salon.store.salonInfoStylist(salon.get, stylist.get))
  }
  
  def findBySalon(salonId: ObjectId) = Action {

    val salon: Option[Salon] = Salon.findById(salonId)
    val nav: String = "style"
    val stylistsOfSalon: Seq[Stylist] = Stylist.findBySalon(salonId)    
    val style: Seq[Style] = Style.findBySalonId(salonId) 
     // TODO
    Ok(html.salon.store.salonInfoStylistAll(salon.get, stylistsOfSalon))

  }
  
  def findStylistById(id: ObjectId) = Action {
    val stylist = Stylist.findById(id)
    val salon = Salon.findById(stylist.get.salonId)
    val style = Style.findByStylistId(stylist.get.salonId, id)
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
            val stylist = Stylist.findById(stylistId)
            stylist match {
              case Some(sty) => {
                userId = sty.userId
                val slt = new Stylist(sty.id, sty.label, salonId, sty.userId, sty.workYears, sty.stylistStyle,
                    sty.imageId, sty.consumerId, sty.description, sty.pictureName, 1)
                Stylist.save(slt.copy(id = sty.id))
              }
              case None => NotFound
            }
            Redirect(routes.Users.myPage(userId.toString))
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
            val stylist = Stylist.findById(stylistId)
            stylist match {
              case Some(sty) => {
                userId = sty.userId
                val slt = new Stylist(sty.id, sty.label, salonId, sty.userId, sty.workYears, sty.stylistStyle,
                    sty.imageId, sty.consumerId, sty.description, sty.pictureName, 0)
                Stylist.save(slt.copy(id = sty.id))
              }
              case None => NotFound
            }
            Redirect(routes.Users.myPage(userId.toString))
          }
          case None => NotFound
        }
  }
  
  def findStylist = Action{
      Ok(views.html.stylist.findStylist("hello"))
   }
    
    def checkStylist = Action{
      Ok(views.html.stylist.checkStylist("hello"))
    }

}
