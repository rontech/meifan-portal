package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date

object SalonsAdmin extends Controller {
	def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
  
	def mySalon(salonId: ObjectId) = Action {
   val salon: Salon = new Salon(new ObjectId("530d7288d7f2861457771bdd"), "火影忍者吧", Some("火吧"),
       "美发", Some("www.sohu.com"), Some("本地最红的美发沙龙！"), "051268320328", "路飞", 
       Seq(OptContactMethod("QQ",List("99198121"))), 
       date("2014-03-12"), 
       Address("江苏", "苏州", "高新区", Some(""), "竹园路209号", Some(100.0), Some(110.0)),
       "地铁一号线汾湖路站1号出口向西步行500米可达", "9:00", "18:00", "Sat", 5, SalonFacilities(true, true, true, true, true, true, true, true, true, "附近有"), "pic", 
       date("2014-03-12") )
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
  
  def checkHoldApply(salonId: ObjectId) = Action {
    val stylist = ApplyRecord.findStylistApply(salonId)
    Ok(views.html.stylist.mySalonStylistGroup(stylist,1))
  }
  
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
  
  def rejectStylistApply(stylistId: ObjectId, salonId: ObjectId) = Action {
    val record = ApplyRecord.findStylistAyRd(salonId, stylistId)
        record match {
          case Some(re) => {
            val  rec = new ApplyRecord(re.id, re.stylistId, re.salonId, re.applyType,
                re.createTime,None ,Option(new Date), None, 2)
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
}