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
      "storeId" -> nonEmptyText,
      "storeNm" -> text(minLength = 6),
      "homePage" -> text) {
        (id, storeId, storeNm, homePage) =>
          BasicInformation(id, storeId, storeNm, "none", homePage,new Date(), "none", "none", "none", "none", "none", "none", new Date(), "8", 000, 000, 000)
      }{
        basicInformation => Some((basicInformation.id, basicInformation.storeId, basicInformation.storeNm, basicInformation.homePage))
      })
        
  val basicInformation:Form[BasicInformation] = Form(
	    mapping(
	        "storeId" -> text,
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
	      (storeId, storeNm, storeTyp, homePage, establishDate, addProvince, addCity, addArea, addTown, add, email,
	       registerTime, image, qq, North, West) => BasicInformation(new ObjectId, storeId, storeNm, storeTyp, homePage, 
	       establishDate, addProvince, addCity, addArea, addTown, add, email, registerTime, image, qq, North, West)
	    }
	    {
	      basicInformation=> Some((basicInformation.storeId, basicInformation.storeNm, basicInformation.storeTyp,
	          basicInformation.homePage, basicInformation.establishDate, basicInformation.addProvince, basicInformation.addCity,
	          basicInformation.addArea,basicInformation.addTown,basicInformation.add, basicInformation.email, basicInformation.registerTime, 
	          basicInformation.image, basicInformation.qq, basicInformation.North, basicInformation.West))
	    }
	)
  
   val detailedInformation:Form[DetailedInformation] = Form(
	     mapping(
	        "storeId" -> text,
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
	      (storeId, tel, contact, trafficDescribe, openTime, closeTime, restDate, seatNum, onlineOrd, immediatelyOrd, appointOrd,
	       onDateOrd, pointOrd, maleOrd, posAvailibale, parking,wifi) => DetailedInformation(new ObjectId, storeId, tel, contact, trafficDescribe, openTime, 
	       closeTime, restDate, seatNum, onlineOrd, immediatelyOrd, appointOrd, onDateOrd, pointOrd, maleOrd, posAvailibale, parking, wifi)
	    }
	    {
	      detailedInformation=> Some((detailedInformation.storeId, detailedInformation.tel, detailedInformation.contact,
	          detailedInformation.trafficDescribe, detailedInformation.openTime, detailedInformation.closeTime, detailedInformation.restDate,
	          detailedInformation.seatNum,detailedInformation.onlineOrd,detailedInformation.immediatelyOrd, detailedInformation.appointOrd, detailedInformation.onDateOrd, 
	          detailedInformation.pointOrd, detailedInformation.maleOrd, detailedInformation.posAvailibale, detailedInformation.parking, detailedInformation.wifi))
	    }
	)
  /**
   * 店铺基本信息显示
   *
   */
//  def storeInfo = Action {
//    val storeId: String = "abc123"
//    val basicInformation = BasicInformation.findOneBystoreId(storeId).get 
//    val basic = BasicInformations.basicInformation.fill(basicInformation)
//    Ok(views.html.salon.basicInformation(basic))  
//  }

  def storeInfo(storeId: String) = Action {
    BasicInformation.findOneBystoreId(storeId).map { basicInformation =>
      val basic = BasicInformations.basicInformation.fill(basicInformation)
      Ok(views.html.salon.basicInformation(basic))
    } getOrElse {
      NotFound
    }
  }
  
  /**
   * 店铺详细信息显示
   */
//  def detailedInfo = Action {
//    val storeId: String = "abc123"
//    val detailedInformation = DetailedInformation.findOneBystoreId(storeId).get
//    val detailed = BasicInformations.detailedInformation.fill(detailedInformation)
//    Ok(views.html.salon.detailedInformation(detailed))
//  }

  def detailedInfo(storeId: String) = Action {
    DetailedInformation.findOneBystoreId(storeId).map { detailedInformation =>
      val detailed = BasicInformations.detailedInformation.fill(detailedInformation)
      Ok(views.html.salon.detailedInformation(detailed))
    } getOrElse {
      NotFound
    }
  }

 /**
  * 基本信息表更新
  */
  def storeUpd(id: ObjectId) = Action { implicit request =>
  	basicInformation.bindFromRequest.fold(
  		error => BadRequest(views.html.error.errorBasic(error)),
      {
        basicInformation =>
          BasicInformation.save(basicInformation.copy(id = id), WriteConcern.Safe)
          Redirect(routes.BasicInformations.detailedInfo(basicInformation.storeId))
      })
  }
  
  /**
   * 詳細信息表更新 
   */
  def storeUpdDet(id: ObjectId) = Action { implicit request =>
    detailedInformation.bindFromRequest.fold(
      error => BadRequest(views.html.error.errorDetailed(error)),
      {
        detailedInformation =>
          DetailedInformation.save(detailedInformation.copy(id = id), WriteConcern.Safe)
          Redirect(routes.BasicInformations.detailedInfo(detailedInformation.storeId))
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
        basicInformation =>
          BasicInformation.save(basicInformation, WriteConcern.Safe)
          Redirect(routes.BasicInformations.storeInfo(basicInformation.storeId))
      })
  }
}