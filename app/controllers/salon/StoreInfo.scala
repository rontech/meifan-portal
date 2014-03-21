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
  val salonInfo:Form[Salon] = Form(
	    mapping(
	        "accountId" -> text,
	        "salonName" -> text,
	        "salonNameAbbr" -> optional(text),
	        "salonIndustry" -> list(text),
	        "homepage" -> optional(text),
	        "salonDescription" -> optional(text),
	        "mainPhone" -> text,
	        "contact" -> text,
	        "optContactMethod" -> list(
	            mapping(
	                "contMethmodType" -> text,
	                "account" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
	        "establishDate" -> date("yyyy-MM-dd"),
	        "salonAddress" -> mapping(
	        	"province" -> text,
	        	"city" -> text,
	        	"region" -> text,
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
	        "restDay" -> list(
	            mapping(
	                "restDayDivision" -> number,
	                "restDay" -> number
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
	      (accountId,salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, mainPhone, contact, optContactMethod, establishDate, salonAddress, accessMethodDesc,
	       workTime, restDay, seatNums, salonFacilities,salonPics,registerDate) => Salon(new ObjectId, accountId,salonName, salonNameAbbr, salonIndustry, homepage, salonDescription, mainPhone, contact, optContactMethod, establishDate, salonAddress, accessMethodDesc,
	       workTime, restDay, seatNums, salonFacilities,salonPics,registerDate)
	    }
	    {
	      salon=> Some((salon.accountId,salon.salonName, salon.salonNameAbbr, salon.salonIndustry, salon.homepage, salon.salonDescription, salon.mainPhone, salon.contact, salon.optContactMethod, salon.establishDate, salon.salonAddress, salon.accessMethodDesc,
	          salon.workTime, salon.restDay, salon.seatNums, salon.salonFacilities, salon.salonPics, salon.registerDate))
	    }
	)
	
  /**
   * 店铺基本信息显示
   *
   */
  def salonInfoBasic = Action {
    val id: ObjectId = new ObjectId("530d7288d7f2861457771bdd")
    val basic = Salon.findById(id).get
    val salon = SalonInfo.salonInfo.fill(basic)
    Ok(views.html.salon.salonInfo(salon))
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
          Ok("修改成功")
      })
  }
}
