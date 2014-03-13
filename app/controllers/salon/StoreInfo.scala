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

//  def registerForm(id: ObjectId = new ObjectId) = Form(
//    mapping(
//      "id" -> ignored(id),
//      "salonId" -> nonEmptyText,
//      "storeNm" -> text(minLength = 6),
//      "homePage" -> text) {
//        (id, salonId, storeNm, homePage) =>
//          SalonInfoBasic(id, salonId, storeNm, "none", homePage,new Date(), "none", "none", "none", "none", "none", "none", new Date(), "8", 000, 000, 000)
//      }{
//        salonInfoBasic => Some((salonInfoBasic.id, salonInfoBasic.salonId, salonInfoBasic.storeNm, salonInfoBasic.homePage))
//      })
        
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
//  def storeInfo(id: ObjectId) = Action {
//    Salon.findById(id).map { salon =>
//      val basic = SalonInfo.salonInfo.fill(salon)
//      Ok(views.html.salon.salonInfoBasic(basic))
//    } getOrElse {
//      NotFound
//    }
//  }

  def storeInfo = Action {
    val id: ObjectId = new ObjectId("530d7288d7f2861457771bdd")
    val basic = Salon.findById(id).get
    val salon = SalonInfo.salonInfo.fill(basic)
    Ok(views.html.salon.salonInfoBasic(salon))
  }
  
//  def findById(salonId: ObjectId) = Action { 
//    val salon: Option[Salon] = Salon.findById(salonId)
//    Ok(views.html.salon.salonInfoBasic(salon))
//  }
//  /**
//   * 店铺详细信息显示
//   */
//  def detailedInfo(salonId: String) = Action {
//    SalonInfoDetail.findOneBysalonId(salonId).map { salonInfoDetail =>
//      val detailed = BasicInformations.salonInfoDetail.fill(salonInfoDetail)
//      Ok(views.html.salon.salonInfoDetail(detailed))
//    } getOrElse {
//      NotFound
//    }
//  }

// /**
//  * 基本信息表更新
//  */
//  def storeUpd(id: ObjectId) = Action { implicit request =>
//  	salonInfo.bindFromRequest.fold(
//  		error => BadRequest(views.html.error.errorMsg(error)),
//      {
//        salonInfo =>
//          Salon.save(salonInfo.copy(id = id), WriteConcern.Safe)
//          Redirect(routes.BasicInformations.detailedInfo(salonInfoBasic.salonId))
//      })
//  }
  
//  /**
//   * 詳細信息表更新 
//   */
//  def storeUpdDet(id: ObjectId) = Action { implicit request =>
//    salonInfoDetail.bindFromRequest.fold(
//      error => BadRequest(views.html.error.errorDetailed(error)),
//      {
//        salonInfoDetail =>
//          SalonInfoDetail.save(salonInfoDetail.copy(id = id), WriteConcern.Safe)
//          Redirect(routes.BasicInformations.detailedInfo(salonInfoDetail.salonId))
//      })
//  }

//    /**
//   * 店铺注册页面
//   */
//  def store = Action {
//    Ok(views.html.salon.addInformation("Your new application is ready."))
//  }
//  
//  /**
//   * 店铺基本信息注册
//   */
//  def storeRegister = Action { implicit request =>
//    registerForm().bindFromRequest.fold(
//  		error => BadRequest(views.html.error.errorBasic(error)),
//      {
//        salonInfoBasic =>
//          SalonInfoBasic.save(salonInfoBasic, WriteConcern.Safe)
//          Redirect(routes.BasicInformations.storeInfo(salonInfoBasic.salonId))
//      })
//  }
}