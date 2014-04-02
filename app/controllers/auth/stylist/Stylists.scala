package controllers

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
  
  def mySalon(stylistId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(stylistId) _){implicit request =>
    val loginUser = loggedIn
    val user = User.findOneById(stylistId).get
    val followInfo = MyFollow.getAllFollowInfo(stylistId)
    val salon = Stylist.mySalon(stylistId)
    val stylist = Stylist.findOneByStylistId(stylistId)
    stylist match {
      case Some(sty) => {
        Ok(views.html.stylist.management.stylistMySalon("mySalonPage",user,followInfo,loginUser.id,true,sty,salon))
      }
      case None => NotFound
    }
  }
  
  /**
   *  同意salon邀请
   */
  def agreeSalonApply(stylistId: ObjectId, salonId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(stylistId) _) {implicit request =>
      val loginUser = loggedIn
      val user = User.findOneById(stylistId).get
      val record = SalonStylistApplyRecord.findOneSalonApRd(salonId, stylistId)
        record match {
          case Some(re) => {
            SalonStylistApplyRecord.agreeStylistApply(re)
            val stylist = Stylist.findOneByStylistId(re.stylistId)
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
  def rejectSalonApply(stylistId: ObjectId, salonId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(stylistId) _) {implicit request =>
     val record = SalonStylistApplyRecord.findOneSalonApRd(salonId, stylistId)
        record match {
          case Some(re) => {
            SalonStylistApplyRecord.agreeStylistApply(re)
            val stylist = Stylist.findOneByStylistId(re.stylistId)
            Redirect(routes.Stylists.mySalon(re.stylistId))
          }
          case None => NotFound
        } 
  }
  
  def findSalonApply(stylistId: ObjectId) =  StackAction(AuthorityKey -> User.isFriend(stylistId) _){implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val applySalons = SalonStylistApplyRecord.findApplingSalon(stylistId)
    val stylist = Stylist.findOneByStylistId(stylistId)
    stylist match {
      case Some(sty) => {
        val user = User.findOneById(sty.stylistId)
        Ok(views.html.stylist.management.stylistApplyingSalons(user = user.get, stylist = sty, salons = applySalons, followInfo = followInfo))
      }
      case None => NotFound
    }
  }
  
  def updateStylistInfo(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
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
  
  def toUpdateStylistInfo(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    stylistForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("")),
      {
        case(stylist) => {
          val newStylist = stylist.copy(id = stylistId)
            Stylist.save(newStylist) //需修改图片更新
            Redirect(controllers.routes.auth.Users.myPage())
        }
      })
    
    
  }
  
  def removeSalon(salonId: ObjectId, stylistId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(stylistId) _) {implicit request =>
    SalonAndStylist.leaveSalon(salonId,stylistId)
    Redirect(routes.Stylists.mySalon(stylistId))
    
  }
  

  def updateStylistImage(stylistId: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
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
        Ok(views.html.stylist.management.stylistHomePage(user = user, stylist = sty, followInfo = followInfo))
      }
      case None => NotFound
    }
    
  }
  
  def styleAddNewStyle = StackAction(AuthorityKey -> authorization(LoggedIn) _) { implicit request =>
    val user = loggedIn
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    val stylist = Stylist.findOneByStylistId(user.id)
    Ok(views.html.stylist.management.stylistAddStyle( styleAddForm = Styles.styleAddForm,stylePara = Style.findParaAll, style = null, false))
    
  }
  
  def styleToAddNewStyle(styleId: ObjectId) = Action(parse.multipartFormData) { implicit request =>
    request.body.file("photo") match {
            case Some(photo) =>{
                val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(photo.ref.file)
                uploadedFile.contentType = photo.contentType.orNull
                uploadedFile.save()
                println("img id .."+ uploadedFile._id.get)
                Styles.styleAddForm.bindFromRequest.fold(
              errors => BadRequest(views.html.index("")),
              {
                case(style) => {
                  val styleAddForm = Styles.styleAddForm.fill(style)
                  val newStyle = style
                  Style.save(newStyle.copy(id = styleId)) //需修改图片更新
                  
                  Style.saveStyleImage(newStyle.copy(id = styleId), uploadedFile._id.get)
                  val upStyle = Style.findOneById(styleId)
                  Ok(views.html.stylist.management.stylistAddStyle( Styles.styleAddForm.fill(upStyle.get) , Style.findParaAll, upStyle.get, true))
                }
              })
            }    
            case None => BadRequest("no photo")
        }
    
  }
  
  def styleToAddNewStyle(styleId: ObjectId) = Action(parse.multipartFormData) { implicit request =>
    request.body.file("photo") match {
            case Some(photo) =>{
                val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(photo.ref.file)
                uploadedFile.contentType = photo.contentType.orNull
                uploadedFile.save()
                println("img id .."+ uploadedFile._id.get)
                Styles.styleAddForm.bindFromRequest.fold(
              errors => BadRequest(views.html.index("")),
              {
                case(style) => {
                  val styleAddForm = Styles.styleAddForm.fill(style)
                  val newStyle = style
                  Style.save(newStyle.copy(id = styleId)) //需修改图片更新
                  
                  Style.saveStyleImage(newStyle.copy(id = styleId), uploadedFile._id.get)
                  val upStyle = Style.findOneById(styleId)
                  Ok(views.html.stylist.management.stylistAddStyle( Styles.styleAddForm.fill(upStyle.get) , Style.findParaAll, upStyle.get, true))
                }
              })
            }    
            case None => BadRequest("no photo")
        }
    
  }
  
  /**
   * 后台发型检索
   */
  def findMyStylesByStylist(stylistId: ObjectId) = StackAction(AuthorityKey -> User.isFriend(stylistId) _) { implicit request =>
    val loginUser = loggedIn
    val styles = Style.findByStylistId(stylistId)
    val user = loggedIn
    val stylist = Stylist.findOneByStylistId(stylistId)
    val followInfo = MyFollow.getAllFollowInfo(user.id)
    Ok(views.html.stylist.management.stylistStyles(user, followInfo,loginUser.id, true, stylist.get, styles, Styles.styleSearchForm, Style.findParaAll, true, true))
  }
  
  def findMyStylesBySerach = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
        implicit request =>
            val user = loggedIn
            val stylist = Stylist.findOneByStylistId(user.id)
            val followInfo = MyFollow.getAllFollowInfo(user.id)
            Styles.styleSearchForm.bindFromRequest.fold(
                errors => BadRequest(views.html.index("")),
                {
                    case (styleSearch) => {
                        val styles = Style.findStylesByStylistBack(styleSearch,stylist.get.stylistId)
                        Ok(views.html.stylist.management.stylistStyles(user, followInfo, true, stylist.get, styles, Styles.styleSearchForm, Style.findParaAll, true, true))
                    }
                })
    }
  
    /**
     * 后台发型更新
     */
    def styleUpdateByStylist(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
        implicit request =>
        val styleOne: Option[Style] = Style.findOneById(id)
        val user = loggedIn
        val stylist = Stylist.findOneByStylistId(user.id)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        styleOne match {
            case Some(style) => Ok(views.html.stylist.management.updateStylistStyles(user, followInfo, user.id, true, stylist.get, style, followInfo , Styles.styleUpdateForm.fill(style), Style.findParaAll))
            case None => NotFound
        }
    }
    
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
                        Redirect(routes.Stylists.findMyStylesByStylist(stylist.get.stylistId))
                    }
                })
    }

    
    /**
     * 后台发型删除，使之无效即可
     */
    def styleToInvalidByStylist(id: ObjectId) = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
        implicit request =>
        val user = loggedIn
        val stylist = Stylist.findOneByStylistId(user.id)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        Style.styleToInvalid(id)
        Redirect(routes.Stylists.findMyStylesByStylist(stylist.get.stylistId))
    }

    /**
     * 后台发型新建
     */
    def styleAddByStylist = StackAction(AuthorityKey -> authorization(LoggedIn) _) {
        //此处为新发型登录
        implicit request =>
        val user = loggedIn
        val stylist = Stylist.findOneByStylistId(user.id)
        val followInfo = MyFollow.getAllFollowInfo(user.id)
        var stylists: List[Stylist] = Nil
        stylists :::= stylist.toList
        Ok(views.html.stylist.management.addStyleByStylist(user = user, stylist = stylist.get, followInfo = followInfo, styleAddForm = Styles.styleAddForm, styleParaAll = Style.findParaAll, stylists = stylists, isStylist = true))
      
        
    }

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
                        Redirect(routes.Stylists.findMyStylesByStylist(stylist.get.stylistId))
                    }
                })
    }
    

    def checkStylist(stylistId: ObjectId) = {
          
        }
    
    def fileUploadAction = Action(parse.multipartFormData) { implicit request =>
    request.body.file("photo") match {
            case Some(photo) =>{
                val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(photo.ref.file)
                uploadedFile.contentType = photo.contentType.orNull
                uploadedFile.save()
                println("upload image id "+uploadedFile._id.get)
                Ok(uploadedFile._id.get.toString)
            }    
            case None => BadRequest("no photo")
        }
    
  }
    
}