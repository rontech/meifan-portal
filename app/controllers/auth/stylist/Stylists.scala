package controllers.auth

import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat.Binders._
import jp.t2v.lab.play2.auth._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent._
import play.api.i18n.Messages
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.gridfs.Imports._
import com.mongodb.casbah.gridfs.GridFS
import play.api.libs.iteratee.Enumerator
import controllers._
import controllers.noAuth.Styles

object Stylists extends Controller with LoginLogout with AuthElement with UserAuthConfigImpl{
    
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
                    "showPriority" -> optional(number),
                    "description" -> optional(text)
                ){
                  (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriority, description)
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
  
	 
	  /**
	   *  同意salon邀请
	   */
	 def agreeSalonApply(salonId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) {implicit request =>
	      val user = loggedIn
	      
	      val record = SalonStylistApplyRecord.findOneSalonApRd(salonId, user.id)
	        record match {
	          case Some(re) => {
	            SalonStylistApplyRecord.agreeStylistApply(re)
	            val stylist = Stylist.findOneByStylistId(re.stylistId)
	            Stylist.becomeStylist(user.id)
	            SalonAndStylist.entrySalon(salonId, user.id)
	            Redirect(routes.Stylists.myHomePage)
	            //Redirect(noAuth.routes.Stylists.mySalon(stylistId))
	          }
	          case None => NotFound
	        }
	  }
	  
	  /**
	   *  拒绝salon邀请
	   */
	 def rejectSalonApply(salonId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) {implicit request =>
	   	 val user = loggedIn	
	     val record = SalonStylistApplyRecord.findOneSalonApRd(salonId, user.id)
	        record match {
	          case Some(re) => {
	            SalonStylistApplyRecord.agreeStylistApply(re)
	            val stylist = Stylist.findOneByStylistId(re.stylistId)
	            Redirect(routes.Stylists.myHomePage)
	          }
	          case None => NotFound
	        } 
	  }
	  
	 def applyFromSalon =  StackAction(AuthorityKey -> isLoggedIn _){implicit request =>
	    val user = loggedIn
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    val applySalons = SalonStylistApplyRecord.findApplingSalon(user.id)
	    val stylist = Stylist.findOneByStylistId(user.id)
	    stylist match {
	      case Some(sty) => {
	        Ok(views.html.stylist.management.stylistApplyingSalons(user, followInfo, user.id, true, applySalons, sty))
	      }
	      case None => NotFound
	    }
	  }
	  
	 def stylistInfo = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
	    val user = loggedIn
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    val goodAtStylePara = Stylist.findGoodAtStyle
	    val stylist = Stylist.findOneByStylistId(user.id)
	    
	    stylist match {
	      case Some(sty) => {
	        val stylistUpdate = stylistForm.fill(sty)
	        Ok(views.html.stylist.management.updateStylistInfo(user, followInfo, user.id, true, sty, stylistUpdate, goodAtStylePara))
	      }
	      case None => NotFound
	    }
	  }
	  
	 def updateStylistInfo(stylistId: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _) { implicit request =>
	    val user = loggedIn
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    stylistForm.bindFromRequest.fold(
	      errors => BadRequest(views.html.index("")),
	      {
	        case(stylist) => {
	          val newStylist = stylist.copy(id = stylistId)
	            Stylist.save(newStylist) //需修改图片更新
	            Redirect(auth.routes.Users.myPage())
	        }
	      })
	  }

	 def removeSalon(salonId: ObjectId) = StackAction(AuthorityKey -> isLoggedIn _) {implicit request =>
	    val user = loggedIn
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    SalonAndStylist.leaveSalon(salonId,user.id)
	    Redirect(auth.routes.Stylists.myHomePage)
	 }
	  
	
	 def updateStylistImage() = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
	    val user = loggedIn
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    val stylist = Stylist.findOneByStylistId(user.id).get
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
	     val stylist = Stylist.findOneByStylistId(user.id)
	     stylist match {
	        case Some(sty) => {
	        Stylist.updateImages(sty, imgId)
	        Redirect(routes.Stylists.myHomePage())
	       }
	      case None => NotFound
	    }
	    
	  }
  
  	/**
     *  跳转到技师发型更新页面
     */
    def styleUpdateByStylist(styleId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
        implicit request =>
        val styleOne: Option[Style] = Style.findOneById(styleId)
        val user = loggedIn
        val stylist = Stylist.findOneByStylistId(user.id)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        styleOne match {
            case Some(style) => Ok(views.html.stylist.management.updateStylistStyles(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, stylist = stylist.get, style = style, styleUpdateForm = Styles.styleUpdateForm.fill(style), styleParaAll = Style.findParaAll))
            case None => NotFound
        }
    }
    
    /**
     *  发型更新后台处理
     */
    def styleUpdateNewByStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
        implicit request =>
        val user = loggedIn
        val stylist = Stylist.findOneByStylistId(user.id)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
            Styles.styleUpdateForm.bindFromRequest.fold(
                errors => BadRequest(views.html.index("")),
                {
                    case (styleUpdateForm) => {
                        Style.save(styleUpdateForm.copy(id=styleUpdateForm.id), WriteConcern.Safe)
                        Redirect(noAuth.routes.Stylists.findStylesByStylist(user.id))
                    }
                })
    }

    
    /**
     * 后台发型删除，使之无效即可
     */
    def styleToInvalidByStylist(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) {implicit request =>
        val user = loggedIn
        val stylist = Stylist.findOneByStylistId(user.id)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        Style.styleToInvalid(id)
        Redirect(noAuth.routes.Stylists.findStylesByStylist(user.id))
    }

    /**
     *  跳转至技师发型新建页面
     */
    def styleAddByStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
        //此处为新发型登录
        implicit request =>
        val user = loggedIn
        val stylist = Stylist.findOneByStylistId(user.id)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        var stylists: List[Stylist] = Nil
        stylists :::= stylist.toList
        Ok(views.html.stylist.management.addStyleByStylist(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, styleAddForm = Styles.styleAddForm, styleParaAll = Style.findParaAll, stylists = stylists, isStylist = true))
    }
    
    /**
     *  技师新建发型后台处理
     */
    def newStyleAddByStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
        implicit request =>
            val user = loggedIn
            val stylist = Stylist.findOneByStylistId(user.id)
            val followInfo = MyFollow.getAllFollowInfo(user.id)
            Styles.styleAddForm.bindFromRequest.fold(
                errors => BadRequest(views.html.index("")),
                {
                    case (styleAddForm) => {
                        Style.save(styleAddForm)
                        Redirect(noAuth.routes.Stylists.findStylesByStylist(stylist.get.stylistId))
                    }
                })
    }
    

    def checkStylist(stylistId: ObjectId) = {
          
    }
    
    
    
  def findStylistApplying = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
	  val user = loggedIn
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      val record = SalonStylistApplyRecord.findOneStylistApRd(user.id)
      record.map{re=>
        val salon = Salon.findOneById(re.salonId)
        salon.map{ sa =>
        	Ok(views.html.stylist.management.stylistApplyingItem(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = sa))
        }getOrElse{
        	NotFound
        }
        
	  }getOrElse{
	    NotFound
	  }
      
  }
  
  def wantToApply = StackAction(AuthorityKey -> isLoggedIn _) { implicit request =>
      val user = loggedIn
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      val salon = Salon.findOneById(new ObjectId)
      Ok(views.html.stylist.management.stylistApplyPage(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = salon))
  }
  
  def myHomePage = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
	  val user = loggedIn
	  val followInfo = MyFollow.getAllFollowInfo(user.id)
	  val stylist = Stylist.findOneByStylistId(user.id)
	  stylist.map{sty=>
	      Ok(views.html.stylist.management.myHomePage(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, stylist = sty))
	  }getOrElse{
	      NotFound
	  }
      
  }
  
  def cancelMyApplying = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
      val user = loggedIn
      val record = SalonStylistApplyRecord.findOneStylistApRd(user.id)
      record.map{ re =>
        SalonStylistApplyRecord.removeById(re.id, WriteConcern.Safe)
        Redirect(routes.Stylists.myHomePage)
      }getOrElse{
        NotFound
      }
  }
  
  def toApplySalon(salonId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
      val user = loggedIn
      Salon.findOneById(salonId).map{ salon =>
    	  val applyRecord = new SalonStylistApplyRecord(new ObjectId, salonId, user.id, 1, new Date, 0, None)
    	  SalonStylistApplyRecord.save(applyRecord)
    	  Redirect(routes.Stylists.myHomePage)  
      }getOrElse{
    	  Redirect(routes.Stylists.myHomePage)
      }
      
  }
  
  def findSalonBySalonAccountId = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
      val salonAccountId = request.getQueryString("salonId").get
      val user = loggedIn
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      val salon = Salon.findByAccountId(salonAccountId)
      Ok(views.html.stylist.management.stylistApplyPage(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = salon))  
      
      
  }
  
}