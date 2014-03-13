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

object BasicInformations extends Controller{

  def registerForm(id: ObjectId = new ObjectId) = Form(
    mapping(
      "id" -> ignored(id),
      "salonId" -> nonEmptyText,
      "storeNm" -> text(minLength = 6),
      "homePage" -> text) {
        (id, salonId, storeNm, homePage) =>
          SalonInfoBasic(id, salonId, storeNm, "none", homePage,new Date(), "none", "none", "none", "none", "none", "none", new Date(), "8", 000, 000, 000)
      }{
        salonInfoBasic => Some((salonInfoBasic.id, salonInfoBasic.salonId, salonInfoBasic.storeNm, salonInfoBasic.homePage))
      })
        
  val salonInfoBasic:Form[SalonInfoBasic] = Form(
	    mapping(
	        "salonId" -> text,
	        "storeNm" -> text(minLength = 6),
	        "storeTyp" ->text,
	        "homePage" -> text,
	        "establishDate" -> date("yyyy-MM-dd"),
	        "addProvince" -> text,
	        "addCity" -> text,
	        "addArea" -> text,
	        "addTown" -> text,
	        "add" -> text,
	        "email" -> text,
	        "registerTime" -> date("yyyy-MM-dd") ,
	        "image" -> text,
	        "qq" -> number,
	        "North" -> number,
	        "West" -> number
	        ){
	      (salonId, storeNm, storeTyp, homePage, establishDate, addProvince, addCity, addArea, addTown, add, email,
	       registerTime, image, qq, North, West) => SalonInfoBasic(new ObjectId, salonId, storeNm, storeTyp, homePage, 
	       establishDate, addProvince, addCity, addArea, addTown, add, email, registerTime, image, qq, North, West)
	    }
	    {
	      salonInfoBasic=> Some((salonInfoBasic.salonId, salonInfoBasic.storeNm, salonInfoBasic.storeTyp,
	          salonInfoBasic.homePage, salonInfoBasic.establishDate, salonInfoBasic.addProvince, salonInfoBasic.addCity,
	          salonInfoBasic.addArea,salonInfoBasic.addTown,salonInfoBasic.add, salonInfoBasic.email, salonInfoBasic.registerTime, 
	          salonInfoBasic.image, salonInfoBasic.qq, salonInfoBasic.North, salonInfoBasic.West))
	    }
	)
  
   val salonInfoDetail:Form[SalonInfoDetail] = Form(
	     mapping(
	        "salonId" -> text,
	        "tel" -> text,
	        "contact" ->text,
	        "trafficDescribe" -> text,
	        "openTime" -> date("hh:mm"),
	        "closeTime" -> date("hh:mm"),
	        "restDate" -> date("yyyy-MM-dd"),
	        "seatNum" -> number,
	        "onlineOrd" -> boolean,
	        "immediatelyOrd" -> boolean,
	        "appointOrd" -> boolean,
	        "onDateOrd" -> boolean ,
	        "pointOrd" -> boolean,
	        "maleOrd" -> boolean,
	        "posAvailibale" -> boolean,
	        "parking" -> boolean,
	        "wifi" -> boolean
	        ){
	      (salonId, tel, contact, trafficDescribe, openTime, closeTime, restDate, seatNum, onlineOrd, immediatelyOrd, appointOrd,
	       onDateOrd, pointOrd, maleOrd, posAvailibale, parking,wifi) => SalonInfoDetail(new ObjectId, salonId, tel, contact, trafficDescribe, openTime, 
	       closeTime, restDate, seatNum, onlineOrd, immediatelyOrd, appointOrd, onDateOrd, pointOrd, maleOrd, posAvailibale, parking, wifi)
	    }
	    {
	      salonInfoDetail=> Some((salonInfoDetail.salonId, salonInfoDetail.tel, salonInfoDetail.contact,
	          salonInfoDetail.trafficDescribe, salonInfoDetail.openTime, salonInfoDetail.closeTime, salonInfoDetail.restDate,
	          salonInfoDetail.seatNum,salonInfoDetail.onlineOrd,salonInfoDetail.immediatelyOrd, salonInfoDetail.appointOrd, salonInfoDetail.onDateOrd, 
	          salonInfoDetail.pointOrd, salonInfoDetail.maleOrd, salonInfoDetail.posAvailibale, salonInfoDetail.parking, salonInfoDetail.wifi))
	    }
	)
	
  /**
   * 店铺基本信息显示
   *
   */
  def storeInfo(salonId: String) = Action {
    SalonInfoBasic.findOneBysalonId(salonId).map { salonInfoBasic =>
      val basic = BasicInformations.salonInfoBasic.fill(salonInfoBasic)
      Ok(views.html.salon.salonInfoBasic(basic))
    } getOrElse {
      NotFound
    }
  }
  
  /**
   * 店铺详细信息显示
   */
  def detailedInfo(salonId: String) = Action {
    SalonInfoDetail.findOneBysalonId(salonId).map { salonInfoDetail =>
      val detailed = BasicInformations.salonInfoDetail.fill(salonInfoDetail)
      Ok(views.html.salon.salonInfoDetail(detailed))
    } getOrElse {
      NotFound
    }
  }

 /**
  * 基本信息表更新
  */
  def storeUpd(id: ObjectId) = Action { implicit request =>
  	salonInfoBasic.bindFromRequest.fold(
  		error => BadRequest(views.html.error.errorBasic(error)),
      {
        salonInfoBasic =>
          SalonInfoBasic.save(salonInfoBasic.copy(id = id), WriteConcern.Safe)
          Redirect(routes.BasicInformations.detailedInfo(salonInfoBasic.salonId))
      })
  }
  
  /**
   * 詳細信息表更新 
   */
  def storeUpdDet(id: ObjectId) = Action { implicit request =>
    salonInfoDetail.bindFromRequest.fold(
      error => BadRequest(views.html.error.errorDetailed(error)),
      {
        salonInfoDetail =>
          SalonInfoDetail.save(salonInfoDetail.copy(id = id), WriteConcern.Safe)
          Redirect(routes.BasicInformations.detailedInfo(salonInfoDetail.salonId))
      })
  }

    /**
   * 店铺注册页面
   */
  def store = Action {
    Ok(views.html.salon.addInformation("Your new application is ready."))
  }
  
  /**
   * 店铺基本信息注册
   */
  def storeRegister = Action { implicit request =>
    registerForm().bindFromRequest.fold(
  		error => BadRequest(views.html.error.errorBasic(error)),
      {
        salonInfoBasic =>
          SalonInfoBasic.save(salonInfoBasic, WriteConcern.Safe)
          Redirect(routes.BasicInformations.storeInfo(salonInfoBasic.salonId))
      })
  }
  
//  /**
//   * 沙龙情报
//   */
//    def getSalon(id: ObjectId) = Action {
//    val salonInfoDetail: Option[SalonInfoDetail] = SalonInfoDetail.findById(id)
//    Ok(views.html.salon.store.salonInfoBasic(salonInfoDetail = salonInfoDetail.get))
//  }
}