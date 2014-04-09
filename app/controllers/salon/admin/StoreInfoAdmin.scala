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
import play.api.i18n.Messages
import controllers._
import org.mindrot.jbcrypt.BCrypt
import utils._

object SalonInfo extends Controller with LoginLogout with AuthElement with SalonAuthConfigImpl{

  //店铺信息管理Form
  val salonInfo:Form[Salon] = Form(
	    mapping(
	    	"salonAccount" -> mapping(
	    		"accountId" -> text,
	    		"password" -> text
	    	)(SalonAccount.apply)(SalonAccount.unapply),
	        "salonName" -> text,
	        "salonNameAbbr" -> optional(text),
	        "salonIndustry" -> list(text),
	        "homepage" -> optional(text),
	        "salonDescription" -> optional(text),
	        "picDescription" -> mapping(
	        		"picTitle" -> text,
	        		"picContent" -> text,
	        		"picFoot" -> text
	        )(PicDescription.apply)(PicDescription.unapply),
	        "mainPhone" -> nonEmptyText,
	        "contact" -> nonEmptyText,
	        "optContactMethod" -> list(
	            mapping(
	                "contMethodType" -> text,
	                "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
	        "establishDate" -> date("yyyy-MM-dd"),
	        "salonAddress" -> mapping(
	        	"province" -> text,
	        	"city" -> optional(text),
	        	"region" -> optional(text),
	        	"town" -> optional(text),
	        	"addrDetail" ->text,
	        	"longitude" -> optional(bigDecimal),
	        	"latitude" -> optional(bigDecimal),
	        	"accessMethodDesc" -> text
	        	)
	        	(Address.apply)(Address.unapply),
	        "workTime" -> mapping(
	            "openTime" -> text ,
	            "closeTime" -> text
	            )
	            (WorkTime.apply)(WorkTime.unapply),
            "restDays" -> mapping(
                "restWay" -> text,
                "restDay1" -> list(text),
                "restDay2" -> list(text)
            ){
                (restWay, restDay1, restDay2) => Tools.getRestDays(restWay,restDay1,restDay2)
            }{
                restDay => Some(Tools.setRestDays(restDay))},
	        "seatNums" -> number,
	        "salonFacilities" -> mapping(
	            "canOnlineOrder" -> boolean,
	            "canImmediatelyOrder" -> boolean,
	            "canNominateOrder" -> boolean,
	            "canCurntDayOrder" -> boolean,
	            "canMaleUse" -> boolean,
	            "isPointAvailable" -> boolean,
	            "isPosAvailable" -> boolean,
	            "isWifiAvailable" -> boolean,
	            "hasParkingNearby" -> boolean,
	            "parkingDesc" -> text)
	            (SalonFacilities.apply)(SalonFacilities.unapply),
	        "salonPics" -> list(
	            mapping(
	                "fileObjId" -> text,
	                "picUse" -> text,
	                "showPriority"-> optional(number),
	                "description" -> optional(text)
	                ){
	              (fileObjId,picUse,showPriority,description) => OnUsePicture(new ObjectId(fileObjId),picUse,showPriority,description)
	              }{
	                salonPics=>Some(salonPics.fileObjId.toString(), salonPics.picUse,salonPics.showPriority,salonPics.description)
	              }),
	        "registerDate" -> date
	        ){
	      (salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, picDescription,mainPhone, contact, optContactMethod, establishDate, salonAddress,
	       workTime, restDay, seatNums, salonFacilities,salonPics,registerDate) => Salon(new ObjectId, salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, picDescription,mainPhone, contact, optContactMethod, establishDate, salonAddress,
	       workTime, restDay, seatNums, salonFacilities,salonPics,registerDate)
	    }
	    {
	      salon=> Some((salon.salonAccount, salon.salonName, salon.salonNameAbbr, salon.salonIndustry, salon.homepage, salon.salonDescription, salon.picDescription,salon.mainPhone, salon.contact, salon.optContactMethod, salon.establishDate, salon.salonAddress,
	          salon.workTime, salon.restDays, salon.seatNums, salon.salonFacilities, salon.salonPics, salon.registerDate))
	    }
	)

	//店铺注册Form
  val salonRegister:Form[Salon] = Form(
      mapping(
	    	"salonAccount" -> mapping(
	    		"accountId" -> nonEmptyText(6,16),
	    		"password" -> tuple(
	    			"main" ->  text.verifying(Messages("user.passwordError"), main => main.matches("""^[a-zA-Z]\w{5,17}$""")),
	    			"confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
            Messages("user.twicePasswordError"), password => password._1 == password._2)
	    			){
	    		(accountId,password) => SalonAccount(accountId,BCrypt.hashpw(password._1, BCrypt.gensalt()))
	    	}{
	    	  salonAccount=>Some(salonAccount.accountId,(salonAccount.password, ""))
	    	},
	        "salonName" -> text.verifying(Messages("salon.salonNameNotAvaible"), salonName => !Salon.findOneBySalonName(salonName).nonEmpty),
	        "salonNameAbbr" -> optional(text),
	        "salonIndustry" -> list(text),
	        "homepage" -> optional(text),
	        "salonDescription" -> optional(text),
	        "picDescription" -> mapping(
	        		"picTitle" -> text,
	        		"picContent" -> text,
	        		"picFoot" -> text
	        )(PicDescription.apply)(PicDescription.unapply),	        
	        "mainPhone" -> nonEmptyText,
	        "contact" -> nonEmptyText,
	        "optContactMethod" -> list(
	            mapping(
	                "contMethodType" -> text,
	                "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
	        "establishDate" -> date("yyyy-MM-dd"),
	        "salonAddress" -> mapping(
	        	"province" -> text,
	        	"city" -> optional(text),
	        	"region" -> optional(text),
	        	"town" -> optional(text),
	        	"addrDetail" ->text,
	        	"longitude" -> optional(bigDecimal),
	        	"latitude" -> optional(bigDecimal),
	        	"accessMethodDesc" -> text      	
	        	)
	        	(Address.apply)(Address.unapply),
	        "workTime" -> mapping(
	            "openTime" -> text ,
	            "closeTime" -> text
	            )
	            (WorkTime.apply)(WorkTime.unapply),
	        "restDays" -> mapping(
	                "restWay" -> text,
	                "restDay1" -> list(text),
                    "restDay2" -> list(text)
            ){
                (restWay, restDay1, restDay2) => Tools.getRestDays(restWay,restDay1,restDay2)
            }{
                restDay => Some(Tools.setRestDays(restDay))},
	        "seatNums" -> number,
	        "salonFacilities" -> mapping(
	            "canOnlineOrder" -> boolean,
	            "canImmediatelyOrder" -> boolean,
	            "canNominateOrder" -> boolean,
	            "canCurntDayOrder" -> boolean,
	            "canMaleUse" -> boolean,
	            "isPointAvailable" -> boolean,
	            "isPosAvailable" -> boolean,
	            "isWifiAvailable" -> boolean,
	            "hasParkingNearby" -> boolean,
	            "parkingDesc" -> text)
	            (SalonFacilities.apply)(SalonFacilities.unapply),
	        "salonPics" -> list(
	            mapping(
	                "fileObjId" -> text,
	                "picUse" -> text,
	                "showPriority"-> optional(number),
	                "description" -> optional(text)
	                ){
	              (fileObjId,picUse,showPriority,description) => OnUsePicture(new ObjectId,picUse,Option(0),Option("none"))
	              }{
	                salonPics=>Some(salonPics.fileObjId.toString(), salonPics.picUse,salonPics.showPriority,salonPics.description)
	              })
      ){
        (salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonDescription,picDescription, mainPhone, contact, optContactMethod, establishDate, salonAddress,
	       workTime, restDays, seatNums, salonFacilities,salonPics) => Salon(new ObjectId, salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, picDescription, mainPhone, contact, optContactMethod, establishDate, salonAddress,
	       workTime, restDays, seatNums, salonFacilities,salonPics,new Date())
      }{
        salonRegister=> Some(salonRegister.salonAccount, salonRegister.salonName, salonRegister.salonNameAbbr, salonRegister.salonIndustry, salonRegister.homepage, salonRegister.salonDescription, salonRegister.picDescription, salonRegister.mainPhone, 
        		salonRegister.contact, salonRegister.optContactMethod, salonRegister.establishDate, salonRegister.salonAddress,
        		salonRegister.workTime, salonRegister.restDays, salonRegister.seatNums, salonRegister.salonFacilities, salonRegister.salonPics)
      }.verifying(

       Messages("user.userIdNotAvailable"), salon => !Salon.findByAccountId(salon.salonAccount.accountId).nonEmpty)
	)

   //店铺登录Form
  val salonLogin = Form(mapping(
      "salonAccount" -> mapping(
          "accountId"-> nonEmptyText,
          "password" -> nonEmptyText)(SalonAccount.apply)(SalonAccount.unapply)
          )(Salon.loginCheck)(_.map(s => (s.salonAccount))).verifying("Invalid userId or password", result => result.isDefined))

  val changePassword = Form(
    mapping(
      "salonAccount" ->mapping(
         "accountId" -> text,
         "password" -> nonEmptyText)(Salon.authenticate)(_.map(s => (s.salonAccount.accountId,""))).verifying("Invalid userId or password", result => result.isDefined),        
      "newPassword" -> tuple(
        "main" -> text.verifying(Messages("user.passwordError"), main => main.matches("""^[a-zA-Z]\w{5,17}$""")),
        "confirm" -> text).verifying(
        // Add an additional constraint: both passwords must match
        Messages("user.twicePasswordError"), passwords => passwords._1 == passwords._2)
    ){(salonAccount, newPassword) => (salonAccount.get, BCrypt.hashpw(newPassword._1, BCrypt.gensalt()))}{salonAccount => Some((Option(salonAccount._1),("","")))}
  )
  
  /**
   * 店铺登录
   */
  def loginSalon = Action { implicit request =>
    salonLogin.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.salonLogin(errors)),
      {
        salonLogin =>
          val getId = Salon.findByAccountId(salonLogin.get.salonAccount.accountId)
          Redirect(routes.SalonInfo.salonInfoBasic(getId.get.id))
      })

  }
  
  /**
   * 店铺注册页面
   */
  def register = Action {
    val industry = Industry.findAll.toList
    Ok(views.html.salon.salonRegister(salonRegister,industry))
  }   
  
  /**
   * 店铺注册
   */
  def doRegister = Action { implicit request =>
    val industry = Industry.findAll.toList
    SalonInfo.salonRegister.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.salonRegister(errors,industry)),
      {
        salonRegister =>
          Salon.save(salonRegister, WriteConcern.Safe)
          Redirect(routes.Application.salonLogin())
      })
  }
  
  /**
   * 店铺基本信息显示
   *
   */
  def salonInfoBasic(id: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findOneById(id)
    val industry = Industry.findAll.toList 
        salon match {
            case Some(sl) => Ok(views.html.salon.salonInfo("", sl , industry))
            case _ => NotFound
        }
  }
  
  /**
   * 店铺基本信息修改页面
   *
   */
  def salonInfoManage(id: ObjectId) = Action {
    val basic: Option[Salon] = Salon.findOneById(id)
        basic match {
            case Some(sl) => 
                  val salon = SalonInfo.salonInfo.fill(sl)
                  val industry = Industry.findAll.toList  
                  Ok(views.html.salon.admin.salonManage("",salon ,industry , sl))
            case _ => NotFound
        }    
 

  }

  /**
   * 店铺基本信息更新
   */
  def update(id: ObjectId) = Action { implicit request =>
    val salon = Salon.findOneById(id).get
    val industry = Industry.findAll.toList     
    salonInfo.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.admin.salonManage("",errors,industry,salon)),
      {       
        salon =>
          Salon.save(salon.copy(id = id), WriteConcern.Safe)
          Redirect(routes.SalonInfo.salonInfoBasic(id))
      })
  }

  /**
   * 密码修改页面
   */
  def password(id: ObjectId) = Action { implicit request =>
    val salon = Salon.findOneById(id).get   
    val salonForm = SalonInfo.salonInfo.fill(salon)    
    Ok(views.html.salon.admin.salonChangePassword("", salonForm, salon))
  }
  
//  /**
//   * 密码修改
//   */
//  def salonChangePassword(id :ObjectId) = Action { implicit request =>
//    val salon = Salon.findOneById(id).get    
//    changePassword.bindFromRequest.fold(
//      errors => BadRequest(views.html.error.errorMsg(errors)),
//      {
//        case (salonAccount, main) =>
//          Salon.save(salonAccount.copy(password = main), WriteConcern.Safe)
//           Redirect(routes.SalonInfo.salonInfoBasic(id))
//    })
//  }
  
  /**
   * 店铺Logo更新页面

   */
  def addImage(id: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findOneById(id)
        salon match {
            case Some(sl) => 
                  Ok(views.html.salon.salonImage(sl))
            case _ => NotFound
        }

  }
  
  /**
   * 店铺图片保存
   */
  def saveSalonImg(id: ObjectId, imgId: ObjectId) = Action{implicit request =>
    val salon: Option[Salon] = Salon.findOneById(id)
       salon match {
       case Some(sl) => 
             Salon.updateSalonLogo(sl, imgId)
             Redirect(routes.SalonInfo.salonInfoBasic(id))
       case _ => NotFound
        }   
  }
  
  /**
   * 店铺图片上传
   */
  def imageUpload(id: ObjectId) = Action(parse.multipartFormData) { request =>
        request.body.file("logo") match {
            case Some(logo) =>
                val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(logo.ref.file)
                uploadedFile.contentType = logo.contentType.orNull
                uploadedFile.save()
                Redirect(routes.SalonInfo.saveSalonImg(id,uploadedFile._id.get))
            case None => BadRequest("no photo")
        }
    }
}
