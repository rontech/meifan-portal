package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date


object Salons extends Controller {
	
  def index = Action {
    val salons: Seq[Salon] = Salon.findAll()
//    Ok(views.html.salon.overview(salons))
      Ok(views.html.salon.general.index(""))
  }

  def getSalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    Ok(views.html.salon.store.salonInfoBasic(salon = salon.get))
  }
   
  def mySalon(salonId: ObjectId) = Action {
   val salon: Salon = new Salon(new ObjectId("530d7288d7f2861457771bdd"), "火影忍者吧", Some("火吧"),
       "美发", Some("www.sohu.com"), Some("本地最红的美发沙龙！"), "051268320328", "路飞", 
       Seq(OptContactMethod("QQ",List("99198121"))), new Date("2014-03-12"), Address("江苏", "苏州", "高新区", Some(""), "竹园路209号", Some(100.0), Some(110.0)),
       "地铁一号线汾湖路站1号出口向西步行500米可达", "9:00", "18:00", "Sat", 5, SalonFacilities(true, true, true, true, true, true, true, true, true, "附近有"), "pic", 
       new Date("2014-03-12") )
    Ok(views.html.salon.admin.mySalonHome(salon = salon))
  }
  
  def myStylist(salonId: ObjectId) = Action {
    Ok(views.html.salon.general.index(""))
  }
  
  def myReserv(salonId: ObjectId) = Action {
    Ok(views.html.salon.general.index(""))
  }
  
  def myComment(salonId: ObjectId) = Action {
    Ok(views.html.salon.general.index(""))
  }
}