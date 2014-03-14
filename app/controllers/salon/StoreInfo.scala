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
	        "salonName" -> text,
	        "salonNameAbbr" -> optional(text),
	        "salonIndst" -> text,
	        "homepage" -> optional(text),
	        "salonDescription" -> optional(text),
	        "mainPhone" -> text,
	        "contact" -> text,
	        "optContactMethod" -> seq(
	            mapping(
	                "contMethmodType" -> text,
	                "account" -> text)(OptContactMethod.apply)(OptContactMethod.unapply)),
	        "establishDate" -> date("yyyy-MM-dd"),
	        "address" -> mapping(
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
	        "openTime" -> text ,
	        "closeTime" -> text,
	        "restDay" -> text,
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
	        "salonPictures" -> text,
	        "registerDate" -> date("yyyy-MM-dd")
	        ){
	      (salonName, salonNameAbbr, salonIndst, homepage, salonDescription, mainPhone, contact, optContactMethod, establishDate, address, accessMethodDesc,
	       openTime, closeTime, restDay, seatNums, salonFacilities,salonPictures,registerDate) => Salon(new ObjectId, salonName, salonNameAbbr, salonIndst, homepage, salonDescription, mainPhone, contact, optContactMethod, establishDate, address, accessMethodDesc,
	       openTime, closeTime, restDay, seatNums, salonFacilities,salonPictures,registerDate)
	    }
	    {
	      salon=> Some((salon.salonName, salon.salonNameAbbr, salon.salonIndst, salon.homepage, salon.salonDescription, salon.mainPhone, salon.contact, salon.optContactMethod, salon.establishDate, salon.address, salon.accessMethodDesc,
	          salon.openTime, salon.closeTime, salon.restDay, salon.seatNums, salon.salonFacilities, salon.salonPictures, salon.registerDate))
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
    Ok(views.html.salon.salonInfoBasic(salon))
  }
}