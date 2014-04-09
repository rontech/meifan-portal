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
import controllers.AuthConfigImpl
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.gridfs.Imports._
import com.mongodb.casbah.gridfs.GridFS
import play.api.libs.iteratee.Enumerator
import controllers._
import models._

object Stylists extends Controller with LoginLogout with AuthElement with AuthConfigImpl{
    
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
	 def agreeSalonApply(stylistId: ObjectId, salonId: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _) {implicit request =>
	      val user = loggedIn
	      
	      val record = SalonStylistApplyRecord.findOneSalonApRd(salonId, stylistId)
	        record match {
	          case Some(re) => {
	            SalonStylistApplyRecord.agreeStylistApply(re)
	            val stylist = Stylist.findOneByStylistId(re.stylistId)
	            Stylist.becomeStylist(stylistId)
	            SalonAndStylist.entrySalon(salonId, stylistId)
	            Redirect(noAuth.routes.Stylists.mySalon(stylistId))
	          }
	          case None => NotFound
	        }
	  }
	  
	  /**
	   *  拒绝salon邀请
	   */
	 def rejectSalonApply(stylistId: ObjectId, salonId: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _) {implicit request =>
	     val record = SalonStylistApplyRecord.findOneSalonApRd(salonId, stylistId)
	        record match {
	          case Some(re) => {
	            SalonStylistApplyRecord.agreeStylistApply(re)
	            val stylist = Stylist.findOneByStylistId(re.stylistId)
	            Redirect(noAuth.routes.Stylists.mySalon(stylistId))
	          }
	          case None => NotFound
	        } 
	  }
	  
	 def findSalonApply(stylistId: ObjectId) =  StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _){implicit request =>
	    val user = loggedIn
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    val applySalons = SalonStylistApplyRecord.findApplingSalon(user.id)
	    val stylist = Stylist.findOneByStylistId(stylistId)
	    stylist match {
	      case Some(sty) => {
	        Ok(views.html.stylist.management.stylistApplyingSalons(user, followInfo, loggedIn.id, true, applySalons, sty))
	      }
	      case None => NotFound
	    }
	  }
	  
	 def updateStylistInfo(stylistId: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _) { implicit request =>
	    val loginUser = loggedIn
	    val user = User.findOneById(stylistId).get
	    val followInfo = MyFollow.getAllFollowInfo(stylistId)
	    val goodAtStylePara = Stylist.findGoodAtStyle
	    val stylist = Stylist.findOneByStylistId(stylistId)
	    
	    stylist match {
	      case Some(sty) => {
	        val stylistUpdate = stylistForm.fill(sty)
	        Ok(views.html.stylist.management.updateStylistInfo(user, followInfo, loginUser.id, true, sty, stylistUpdate, goodAtStylePara))
	      }
	      case None => NotFound
	    }
	  }
	  
	 def toUpdateStylistInfo(stylistId: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _) { implicit request =>
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
	  
	 def removeSalon(salonId: ObjectId, stylistId: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _) {implicit request =>
	    val user = loggedIn
	    val followInfo = MyFollow.getAllFollowInfo(user.id)
	    SalonAndStylist.leaveSalon(salonId,stylistId)
	    Redirect(noAuth.routes.Stylists.mySalon(stylistId))
	    
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
                        Style.updateStyle(styleUpdateForm)
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
        Ok(views.html.stylist.management.addStyleByStylist(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, stylist = stylist.get, styleAddForm = Styles.styleAddForm, styleParaAll = Style.findParaAll, stylists = stylists, isStylist = true))
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
    
    /**
     *  ajax fileupload 输出图片id到页面对应区域
     */
    def fileUploadAction = Action(parse.multipartFormData) { implicit request =>
    request.body.file("Filedata") match {
            case Some(photo) =>{
            	val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(photo.ref.file)
                uploadedFile.contentType = photo.contentType.orNull
                uploadedFile.save()
                Ok(uploadedFile._id.get.toString)
            }    
            case None => BadRequest("no photo")
        }
    
  }
    
  def findStylistApplying(stylistId: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _) { implicit request =>
	  val user = loggedIn
      val stylist = Stylist.findOneByStylistId(user.id)
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      val record = SalonStylistApplyRecord.findOneStylistApRd(user.id)
      record.map{re=>
        val salon = Salon.findById(re.salonId)
        salon.map{ sa =>
        	Ok(views.html.stylist.management.stylistApplyingItem(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = sa))
        }getOrElse{
        	NotFound
        }
        
	  }getOrElse{
	    NotFound
	  }
      
  }
  
  def wantToApply(stylistId: ObjectId) = StackAction(AuthorityKey -> Stylist.isOwner(stylistId) _) { implicit request =>
      val user = loggedIn
      val followInfo = MyFollow.getAllFollowInfo(user.id)
      val salon = Salon.findById(new ObjectId)
      Ok(views.html.stylist.management.stylistApplyPage(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, salon = salon))
  }
  
  def myHomePage = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
	  val user = loggedIn
	  val followInfo = MyFollow.getAllFollowInfo(user.id)
	  val stylist = Stylist.findOneByStylistId(user.id)
	  val blgs = Blog.getBlogByUserId(user.userId)
      val blog = if(blgs.length > 0) Some(blgs.head) else None
	  stylist.map{sty=>
	      Ok(views.html.stylist.management.myPageHome(user = user, followInfo = followInfo, loginUserId = user.id, logged = true, stylist = sty, lastBlog = blog))
	  }getOrElse{
	      NotFound
	  }
      
  }
  
  def cancelMyApplying = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
      val user = loggedIn
      val followInfo = MyFollow.getAllFollowInfo(user.id)
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
      Salon.findById(salonId).map{ salon =>
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