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
import controllers._


object Stylists extends Controller {


  def index = TODO
  
  
  val styleLoginForm:Form[Style] = Form(
	    mapping(
	        "styleName" -> text,
	        "stylistId" -> text,
	        "stylePic" -> list(text),
	        "styleImpression" -> list(text),
		    "serviceType" -> list(text),
		    "styleLength" -> list(text),
		    "styleColor" -> list(text),
		    "styleAmount" -> list(text),
		    "styleQuality" -> list(text),
		    "styleDiameter" -> list(text),
		    "faceShape" -> list(text),
		    "description" -> text,
		    "consumerAgeGroup" -> list(text),
		    "consumerSex" -> list(text),
		    "consumerSocialStatus" -> list(text)
	        ){
	      (styleName,stylistId,stylePic,styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,description,consumerAgeGroup,consumerSex,consumerSocialStatus) =>
	          Style(new ObjectId,styleName,new ObjectId(stylistId),stylePic,
	           styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,description,consumerAgeGroup,consumerSex,consumerSocialStatus, new Date,true)
	    }
	    {
	      style=> Some((style.styleName,style.stylistId.toString,style.stylePic,style.styleImpression,style.serviceType,
	          style.styleLength,style.styleColor,style.styleAmount,style.styleQuality,
	          style.styleDiameter,style.faceShape,style.description,style.consumerAgeGroup,style.consumerSex,style.consumerSocialStatus))
	    }
  )
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
  
  def myStyles(stylistId: ObjectId) = Action {
    val styles = Style.findByStylistId(stylistId)
    val stylist = Stylist.findOneById(stylistId)
    
    stylist match {
      case Some(sty) => {
        val user = User.findOneById(sty.publicId)
        user match {
          case Some(u) => Ok(html.stylist.management.stylistStyles(user = u, stylist = sty, styles = styles))
          case None => NotFound
        }
      }
      case None => NotFound
    }
  }
  
  def updateStyleByStylist(styleId: ObjectId) = Action {
    Ok(html.stylist.management) 
  }
  
  def deleteStyleByStylist(styleId: ObjectId) = Action {
    
  }
  
  def createStyleByStylist(stylistId: ObjectId) = Action {
    
  }
  
  
}
