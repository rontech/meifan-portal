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
  
  def mySalon(stylistId: ObjectId) = Action {
    val salon = Stylist.mySalon(stylistId)
    val stylist = Stylist.findOneById(stylistId)
    stylist match {
      case Some(sty) => {
        val user = User.findOneById(sty.publicId)
        Ok(html.salon.store.salonInfoStylistInfo(salon = salon.get, stylist = stylist.get, style = style))
      }
      case None => NotFound
    }
    
    
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
  
  val stylistForm: Form[Stylist] = Form(
        mapping(
		        "workYears" -> number,
			    "position" -> list(
			    	mapping(
			    		"positionName" -> text,
			    		"industryName" -> text
			    	){
			    		(positionName, industryName) => IndustryAndPosition(new ObjectId, positionName, industryName)
			    	}{
			    		industryAndPosition => Some(industryAndPosition.positionName, industryAndPosition.indestryName)
			    	}	
			    ),
			    "goodAtImage" -> list(text),
			    "goodAtStatus" -> list(text),
			    "goodAtService" -> list(text),
			    "goodAtUser" -> list(text),
			    "goodAtAgeGroup" -> list(text),
			    "myWords" -> text,
			    "mySpecial" -> text,
			    "myBoom" -> text,
			    "myPR" -> text,
			    "myPics" -> optional(list(mapping (
			    	"picUse" -> text
			    ){
			      (picUse) => OnUsePicture(new ObjectId, picUse, Some(0), Some("技师头像"))
			    }{
			      onUsePicture => Some(onUsePicture.picUse)
			    }))
			){
		      (workYears, position, goodAtImage, goodAtStatus, goodAtService,
		          goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR, myPics)
		      => Stylist(new ObjectId, new ObjectId(), 0, position, goodAtImage, goodAtStatus,
		    	   goodAtService, goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR, 
		           myPics, false, false)
		    }{
		      stylist => Some(stylist.workYears, stylist.position, 
		          stylist.goodAtImage, stylist.goodAtStatus, stylist.goodAtService, stylist.goodAtUser,
		          stylist.goodAtAgeGroup, stylist.myWords, stylist.mySpecial, stylist.myBoom, stylist.myPR, stylist.myPics)
		    }
		)
  
  def updateStylistInfo(stylistId: ObjectId) = Action {
    val stylist = Stylist.findOneById(stylistId)
    stylist match {
      case Some(sty) => {
        val user = User.findOneById(sty.id)
        Ok(html.stylist.management.updateStylistInfo(user = user.get, stylist = stylist, stylistForm = stylistForm))
        
      }
      case None => NotFound
    }
  }
  
  def toUpdateStylistInfo = Action {implicit request =>
    
    stylistForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("")),
      {
        case(stylist) => {
        	Stylist.updateStylistInfo(stylist,imageId = new ObjectId) //需修改图片更新
        	Redirect(routes.Stylists.updateStylistInfo(stylist.id))
        }
      })
    
    
  }
  
  def updateStyleByStylist(styleId: ObjectId) = Action {
     
  }
  
  def deleteStyleByStylist(styleId: ObjectId) = Action {
    
  }
  
  def createStyleByStylist(stylistId: ObjectId) = Action {
    
  }
  
  
}
