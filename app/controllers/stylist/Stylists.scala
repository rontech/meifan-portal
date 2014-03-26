package controllers

import java.util.Date
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.commons.Imports._
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.Future
import play.api.templates._
import se.radley.plugin.salat._
import com.mongodb.casbah.gridfs.Imports._
import com.mongodb.casbah.gridfs.GridFS
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import com.mongodb.casbah.MongoConnection


object Stylists extends Controller with LoginLogout with AuthElement with AuthConfigImpl {


  def index = TODO
  
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
			    		industryAndPosition => Some(industryAndPosition.positionName, industryAndPosition.industryName)
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
			    "myPics" -> (list(mapping (
			    	"picUse" -> text
			    ){
			      (picUse) => OnUsePicture(new ObjectId, picUse, Some(0), Some("技师头像"))
			    }{
			      onUsePicture => Some(onUsePicture.picUse)
			    }))
			){
		      (workYears, position, goodAtImage, goodAtStatus, goodAtService,
		          goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR, myPics)
		      => Stylist(new ObjectId, new ObjectId(), workYears, position, goodAtImage, goodAtStatus,
		    	   goodAtService, goodAtUser, goodAtAgeGroup, myWords, mySpecial, myBoom, myPR, 
		           myPics, false, false)
		    }{
		      stylist => Some(stylist.workYears, stylist.position, 
		          stylist.goodAtImage, stylist.goodAtStatus, stylist.goodAtService, stylist.goodAtUser,
		          stylist.goodAtAgeGroup, stylist.myWords, stylist.mySpecial, stylist.myBoom, stylist.myPR, stylist.myPics)
		    }
		)
  
  def mySalon(stylistId: ObjectId) = Action {
    val salon = Stylist.mySalon(stylistId)
    val stylist = Stylist.findOneById(stylistId)
    stylist match {
      case Some(sty) => {
        val user = User.findOneById(sty.publicId)
        Ok(views.html.stylist.management.stylistMySalon(user = user.get, stylist = sty, salon = salon))
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
            Redirect(routes.Stylists.mySalon(re.stylistId))
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
            Redirect(routes.Stylists.mySalon(re.stylistId))
          }
          case None => NotFound
        } 
  }
  
  def findSalonApply(stylistId: ObjectId) =  Action {
    val applySalons = SalonStylistApplyRecord.findApplingSalon(stylistId)
    println("applySalons "+applySalons)
    val stylist = Stylist.findOneById(stylistId)
    stylist match {
      case Some(sty) => {
        val user = User.findOneById(sty.publicId)
        Ok(views.html.stylist.management.stylistApplyingSalons(user = user.get, stylist = sty, salons = applySalons))
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
          case Some(u) => Ok(views.html.stylist.management.stylistStyles(user = u, stylist = sty, styles = styles))
          case None => NotFound
        }
      }
      case None => NotFound
    }
  }
  
  
  
  def updateStylistInfo(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val goodAtStylePara = Stylist.findGoodAtStyle
    val stylist = Stylist.findOneById(stylistId)
    
    stylist match {
      case Some(sty) => {
        val stylistUpdate = stylistForm.fill(sty)
        println("stylist  ......"+sty)
        Ok(views.html.stylist.management.updateStylistInfo(user = user, stylist = sty, stylistForm = stylistUpdate, goodAtStylePara = goodAtStylePara))
        
      }
      case None => NotFound
    }
  }
  
  def toUpdateStylistInfo = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    stylistForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("")),
      {
        case(stylist) => {
          println("stylist ..."+stylist)
        	Stylist.updateStylistInfo(stylist,imageId = new ObjectId) //需修改图片更新
        	Ok(views.html.stylist.management.stylistHomePage(user = user, stylist = stylist))
        }
      })
    
    
  }
  
  def removeSalon(salonId: ObjectId, stylistId: ObjectId) = Action {
    SalonAndStylist.leaveSalon(salonId,stylistId)
    Redirect(routes.Stylists.mySalon(stylistId))
    
  }
  
}
