package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date
import models.Salon

object SalonsAdmin extends Controller {
	def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
  
	def mySalon(salonId: ObjectId) = Action {

	   val salon: Salon = Salon.findById(salonId).get
	    Ok(views.html.salon.admin.mySalonHome(salon = salon))
	  }

   
  
  
  def myStylist(salonId: ObjectId) = Action {
	val stylist = Stylist.findBySalon(salonId)
	val salon = Salon.findById(salonId)
	salon match{
	  case Some(s) =>Ok(html.salon.admin.mySalonStylistAll(salon = s, stylist = stylist))
	  case None => NotFound
	}
  }
  
  def myReserv(salonId: ObjectId) = Action {
    Ok(views.html.salon.general.index(""))
  }
  
  def myComment(salonId: ObjectId) = Action {
    Ok(views.html.salon.general.index(""))
  }
  
  def myService(salonId: ObjectId) = Action {
    Ok(views.html.salon.general.index(""))
  }
  
  /**
   * 店铺查看申请中的技师
   */
  def checkHoldApply(salonId: ObjectId) = Action {
    val stylists = ApplyRecord.findStylistApply(salonId)
    
    stylists.map{sty=>
      
    }
    val salon = Salon.findById(salonId)
    salon match {
      case Some(s) =>{
    	 
        Ok(views.html.salon.admin.mySalonApplyAll(stylist = stylists, salon = s))
        
      }
      case None => NotFound
    }
  }
  
  /**
   * 同意技师申请
   */
  def agreeStylistApply(stylistId: ObjectId, salonId: ObjectId) = Action {
	  val record = ApplyRecord.findStylistAyRd(salonId, stylistId)
        record match {
          case Some(re) => {
            val  rec = new ApplyRecord(re.id, re.stylistId, re.salonId, re.applyType,
                re.createTime, Option(new Date), None, None, 1)
            ApplyRecord.save(rec.copy(id = re.id))
            val stylist = Stylist.findOneById(stylistId)
            stylist match {
              case Some(sty) => {
               
                Stylist.save(sty.copy(id = sty.id, isVarified = true, isValid = true))
              }
              case None => NotFound
            }
            Redirect(routes.SalonsAdmin.myStylist(salonId))
          }
          case None => NotFound
        }
  }
  
  /**
   * 店铺拒绝技师申请
   */
  def rejectStylistApply(stylistId: ObjectId, salonId: ObjectId) = Action {
    val record = ApplyRecord.findStylistAyRd(salonId, stylistId)
        record match {
          case Some(re) => {
            val  rec = new ApplyRecord(re.id, re.stylistId, re.salonId, re.applyType,
                re.createTime, None, Option(new Date), None, 2)
            ApplyRecord.save(rec.copy(id = re.id))
            val stylist = Stylist.findOneById(stylistId)
            stylist match {
              case Some(sty) => {
                Stylist.save(sty.copy(id = sty.id, isVarified = false, isValid = true))
              }
              case None => NotFound
            }
            Redirect(routes.SalonsAdmin.myStylist(salonId))
          }
          case None => NotFound
        }
  }
  
  /**
   * 根据Id查找技师
   */
  def searchStylistById() = Action {implicit request =>
    val stylistId = request.getQueryString("searchStylistById").get
    val salonId = request.getQueryString("salonId").get
	val salon = Salon.findById(new ObjectId(salonId)).get
	val stylist = Stylist.findOneById(new ObjectId(stylistId))
	
    Ok(html.salon.admin.findStylistBySearch(stylist = stylist.get, salon = salon, status = 1))
  }
  
  /**
   *店铺邀请技师 
   */
  def inviteStylist(stylistId: ObjectId, salonId: ObjectId) = Action {
    val  record = new ApplyRecord(new ObjectId, stylistId, salonId, 0,
                new Date, None , None, None, 0)
    ApplyRecord.save(record)
    Redirect(routes.SalonsAdmin.myStylist(salonId))
  }
}