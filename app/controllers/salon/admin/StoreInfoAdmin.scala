package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import org.bson.types.ObjectId
import models._
import controllers._
import views._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.WriteConcern
import play.api.templates._
import java.util.Date

object SalonInfo extends Controller{        
  
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
	        "mainPhone" -> text,
	        "contact" -> text,
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
	        	"latitude" -> optional(bigDecimal)
	        	)
	        	(Address.apply)(Address.unapply),
	        "accessMethodDesc" -> text,
	        "workTime" -> mapping(
	            "openTime" -> text ,
	            "closeTime" -> text
	            )
	            (WorkTime.apply)(WorkTime.unapply),
	        "restDays" -> list(
	            mapping(
	                "restDayDivision" -> number,
	                "restDay" -> list(number)
	                )
	                (RestDay.apply)(RestDay.unapply)
	            ),
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
	              (fileObjId,picUse,showPriority,description) => OnUsePicture(new ObjectId(fileObjId),"",Option(0),Option(""))
	              }{
	                salonPics=>Some(salonPics.fileObjId.toString(), salonPics.picUse,salonPics.showPriority,salonPics.description)
	              }),
	        "registerDate" -> date
	        ){
	      (salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, mainPhone, contact, optContactMethod, establishDate, salonAddress, accessMethodDesc,
	       workTime, restDay, seatNums, salonFacilities,salonPics,registerDate) => Salon(new ObjectId, salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, mainPhone, contact, optContactMethod, establishDate, salonAddress, accessMethodDesc,
	       workTime, restDay, seatNums, salonFacilities,salonPics,registerDate)
	    }
	    {
	      salon=> Some((salon.salonAccount, salon.salonName, salon.salonNameAbbr, salon.salonIndustry, salon.homepage, salon.salonDescription, salon.mainPhone, salon.contact, salon.optContactMethod, salon.establishDate, salon.salonAddress, salon.accessMethodDesc,
	          salon.workTime, salon.restDays, salon.seatNums, salon.salonFacilities, salon.salonPics, salon.registerDate))
	    }
	)

	//店铺注册Form
  val salonRegister:Form[Salon] = Form(
      mapping(
	    	"salonAccount" -> mapping(
	    		"accountId" -> nonEmptyText(6,16),
	    		"password" -> tuple(
	    			"main" ->  text(minLength = 6),
	    			"confirm" -> text).verifying(
	    					"Passwords don't match", passwords => passwords._1 == passwords._2)
	    			){
	    		(accountId,password) => SalonAccount(accountId,password._1)
	    	}{
	    	  salonAccount=>Some(salonAccount.accountId,(salonAccount.password, ""))
	    	},
	        "salonName" -> text,
	        "salonNameAbbr" -> optional(text),
	        "salonIndustry" -> list(text),
	        "homepage" -> optional(text),
	        "salonDescription" -> optional(text),
	        "mainPhone" -> text,
	        "contact" -> text,
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
	        	"latitude" -> optional(bigDecimal)
	        	)
	        	(Address.apply)(Address.unapply),
	        "accessMethodDesc" -> text,
	        "workTime" -> mapping(
	            "openTime" -> text ,
	            "closeTime" -> text
	            )
	            (WorkTime.apply)(WorkTime.unapply),
	        "restDays" -> list(
	            mapping(
	                "restDayDivision" -> number,
	                "restDay" -> list(number)
	                )
	                (RestDay.apply)(RestDay.unapply)
	            ),
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
	              (fileObjId,picUse,showPriority,description) => OnUsePicture(new ObjectId(fileObjId),"",Option(0),Option(""))
	              }{
	                salonPics=>Some(salonPics.fileObjId.toString(), salonPics.picUse,salonPics.showPriority,salonPics.description)
	              }),
	        "registerDate" -> date
      ){
        (salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, mainPhone, contact, optContactMethod, establishDate, salonAddress, accessMethodDesc,
	       workTime, restDays, seatNums, salonFacilities,salonPics,registerDate) => Salon(new ObjectId, salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, mainPhone, contact, optContactMethod, establishDate, salonAddress, accessMethodDesc,
	       workTime, restDays, seatNums, salonFacilities,salonPics,registerDate)
      }{
        salonRegister=> Some(salonRegister.salonAccount, salonRegister.salonName, salonRegister.salonNameAbbr, salonRegister.salonIndustry, salonRegister.homepage, salonRegister.salonDescription, salonRegister.mainPhone, 
        		salonRegister.contact, salonRegister.optContactMethod, salonRegister.establishDate, salonRegister.salonAddress, salonRegister.accessMethodDesc,
        		salonRegister.workTime, salonRegister.restDays, salonRegister.seatNums, salonRegister.salonFacilities, salonRegister.salonPics, salonRegister.registerDate)
      }.verifying(
        "This salonId is not available", salon => !Salon.findByAccountId(salon.salonAccount).nonEmpty)

   )

   //店铺登录Form
  val salonLogin = Form(mapping(
      "salonAccount" -> mapping(
          "accountId"-> nonEmptyText,
          "password" -> nonEmptyText)(SalonAccount.apply)(SalonAccount.unapply)
          )(Salon.loginCheck)(_.map(s => (s.salonAccount))).verifying("Invalid userId or password", result => result.isDefined))

  /**
   * 用户登录
   */
  def loginSalon = Action { implicit request =>
    salonLogin.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        salonLogin =>
          val getId = Salon.findByAccountId(salonLogin.get.salonAccount)
          Redirect(routes.SalonInfo.salonInfoBasic(getId.get.id))
      })

  }
  
  /**
   * 店铺注册页面
   */
  def register = Action {
    val industry = Industry.findAll.toList
    val provinces = Province.findAll.toList
    val cities = City.findAll.toList
    val regions = Region.findAll.toList
    Ok(views.html.salon.salonRegister(salonRegister,industry,provinces,cities,regions))
  }   
  
  /**
   * 店铺注册
   */
  def doRegister = Action { implicit request =>
    SalonInfo.salonRegister.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        salonRegister =>
          Salon.create(salonRegister)
          Redirect(routes.Application.salonLogin())
      })
  }
  
  /**
   * 店铺基本信息显示
   *
   */
  def salonInfoBasic(id: ObjectId) = Action {
    val basic = Salon.findById(id).get
    val salon = SalonInfo.salonInfo.fill(basic)
    val industry = Industry.findAll.toList
    val provinces = Province.findAll.toList
    val cities = City.findAll.toList
    val regions = Region.findAll.toList    
    Ok(views.html.salon.salonInfo(salon = salon,industry = industry,provinces = provinces,cities =cities,regions = regions))
  }

  /**
   * 店铺基本信息更新
   */
  def update(id: ObjectId) = Action { implicit request =>
    salonInfo.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        
        salon =>
          Salon.save(salon.copy(id = id))
          Redirect(routes.SalonInfo.salonInfoBasic(id))
      })
  }
}
