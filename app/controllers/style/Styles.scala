package controllers

import play.api._
import play.api.mvc._
import models.Style
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._

import models._
import views._


object Styles extends Controller {


  def index = Action {
     val styles: Seq[Style] = Style.findAll()
     Ok(html.style.overview(styles))
  }
  
   /**
   * 
   */
  def findById(styleId: ObjectId) = Action {
    val style: Option[Style] = Style.findById(styleId)
    Ok(html.style.overview(style.toList))
  }
  
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)    
    val styles: Seq[Style] = Style.findBySalon(salonId)    

    // TODO: process the salon not exist pattern.
    Ok(html.salon.store.salonInfoStyleAll(salon = salon.get, styles = styles))
  }

  def getStyleInfoOfSalon(salonId: ObjectId, styleId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)    
    val style: Option[Style] = Style.findBySalon(salonId, styleId)    
    Ok(html.salon.store.salonInfoStyle(salon = salon.get, style = style.get))

 }
  



}
