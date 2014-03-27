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
			    "myPics" -> list(
			        mapping(
			    	"fileObjId" -> text,
			    	"picUse" -> text,
			    	"showPriotiry" -> optional(number),
			    	"description" -> optional(text)
			    ){
			      (fileObjId, picUse, showPriotiry, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriotiry, description)
			    }{
			      onUsePicture => Some((onUsePicture.fileObjId.toString, onUsePicture.picUse, onUsePicture.showPriority, onUsePicture.description))
			    })
			   
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
  
  def mySalon(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _){implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val salon = Stylist.mySalon(stylistId)
    val stylist = Stylist.findOneById(stylistId)
    stylist match {
      case Some(sty) => {
        Ok(views.html.stylist.management.stylistMySalon(user = user, stylist = sty, salon = salon, followInfo = followInfo))
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
  
  def findSalonApply(stylistId: ObjectId) =  StackAction(AuthorityKey -> authorization(LoggedIn) _){implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val applySalons = SalonStylistApplyRecord.findApplingSalon(stylistId)
    val stylist = Stylist.findOneById(stylistId)
    stylist match {
      case Some(sty) => {
        val user = User.findOneById(sty.publicId)
        Ok(views.html.stylist.management.stylistApplyingSalons(user = user.get, stylist = sty, salons = applySalons, followInfo = followInfo))
      }
      case None => NotFound
    }
  }
  
  def myStyles(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _){implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val styles = Style.findByStylistId(stylistId)
    val stylist = Stylist.findOneById(stylistId)
    
    stylist match {
      case Some(sty) => {
        val user = User.findOneById(sty.publicId)
        user match {
          case Some(u) => Ok(views.html.stylist.management.stylistStyles(user = u, stylist = sty, styles = styles, followInfo = followInfo))
          case None => NotFound
        }
      }
      case None => NotFound
    }
  }
  
  
  
  def updateStylistInfo(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val goodAtStylePara = Stylist.findGoodAtStyle
    val stylist = Stylist.findOneById(stylistId)
    
    stylist match {
      case Some(sty) => {
        val stylistUpdate = stylistForm.fill(sty)
        println("stylist  ......"+sty)
        Ok(views.html.stylist.management.updateStylistInfo(user = user, stylist = sty, stylistForm = stylistUpdate, goodAtStylePara = goodAtStylePara, followInfo = followInfo))
        
      }
      case None => NotFound
    }
  }
  
  def toUpdateStylistInfo(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    stylistForm.bindFromRequest.fold(
      errors => BadRequest(views.html.fortest(errors)),
      {
        case(stylist) => {
          println("stylist ..."+stylist)
        	Stylist.updateStylistInfo(stylist, stylistId) //需修改图片更新
        	Ok(views.html.stylist.management.stylistHomePage(user = user, stylist = stylist, followInfo = followInfo))
        }
      })
    
    
  }
  
  def removeSalon(salonId: ObjectId, stylistId: ObjectId) = Action {
    SalonAndStylist.leaveSalon(salonId,stylistId)
    Redirect(routes.Stylists.mySalon(stylistId))
    
  }
  
  def updateStylistImage(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val stylist = Stylist.findByUserId(user.id).get
    Ok(views.html.stylist.management.updateStylistImage(user = user, stylist = stylist, followInfo = followInfo))
  }
  
  def toUpdateStylistImage = Action(parse.multipartFormData) { request =>
        request.body.file("photo") match {
            case Some(photo) =>
                val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(photo.ref.file)
                uploadedFile.contentType = photo.contentType.orNull
                uploadedFile.save()
                Redirect(routes.Stylists.saveStylistImg(uploadedFile._id.get))
            case None => BadRequest("no photo")
        }
    
  }
  
  def saveStylistImg(imgId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _){implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val stylist = Stylist.findByUserId(user.id)
    stylist match {
      case Some(sty) => {
        Stylist.updateImages(sty, imgId)
        Ok(views.html.stylist.management.stylistHomePage(user = user, stylist = sty, followInfo = followInfo))
      }
      case None => NotFound
    }
    
  }
  /*def updateStyleByStylist(styleId: ObjectId) = Action {
     
  }
  
  def deleteStyleByStylist(styleId: ObjectId) = Action {
    
  }
  
  def createStyleByStylist(stylistId: ObjectId) = Action {
    
  }*/
  
  
}
