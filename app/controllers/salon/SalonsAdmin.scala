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
	val commentList = Comment.findBySalon(salonId)
	val salon = Salon.findById(salonId)
	salon match{
	  case Some(s) =>Ok(html.salon.admin.mySalonCommentAll(salon = s, commentList = commentList))
	  case None => NotFound
	}
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
            val stylist = Stylist.findById(stylistId)
            stylist match {
              case Some(sty) => {
                val slt = new Stylist(sty.id, sty.label, salonId, sty.userId, sty.workYears, sty.stylistStyle,
                    sty.imageId, sty.consumerId, sty.description, sty.pictureName, 1)
                Stylist.save(slt.copy(id = sty.id))
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
            val stylist = Stylist.findById(stylistId)
            stylist match {
              case Some(sty) => {
                val slt = new Stylist(sty.id, sty.label, salonId, sty.userId, sty.workYears, sty.stylistStyle,
                    sty.imageId, sty.consumerId, sty.description, sty.pictureName, 0)
                Stylist.save(slt.copy(id = sty.id))
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
    val stylistId = request.getQueryString("searchStylistById")
    val salonId = request.getQueryString("salonId").get
	val salon = Salon.findById(new ObjectId(salonId)).get
    stylistId match {
      case Some(styId) =>{ 
        val stylist = Stylist.findById(new ObjectId(styId))
	    stylist match {
	      case Some(sty) =>{
		    if(salonId == sty.salonId.toString && sty.status == 1) {
		      Ok(html.salon.admin.findStylistBySearch(stylist = sty, salon = salon, status = 1))
		    } else {
		      Ok(html.salon.admin.findStylistBySearch(stylist = sty, salon = salon, status = 2))
		    }
	      }
	      case None => Ok(html.salon.admin.findStylistBySearch(stylist = null, salon = salon, status = 1))
	    }
      }
      case None => Ok(html.salon.admin.findStylistBySearch(stylist = null, salon = salon, status = 1))
    }
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