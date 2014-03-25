package controllers


import play.api.mvc._
import com.mongodb.casbah.commons.Imports._
import models._


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
    // TODO
    val salonId = new ObjectId("530d7288d7f2861457771bdd")
    val salon: Option[Salon] = Salon.findById(salonId)
    salon match {
      case Some(sl) => Ok(views.html.salon.admin.mySalonHome(salon = sl))
      case None => NotFound
    }
    
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
