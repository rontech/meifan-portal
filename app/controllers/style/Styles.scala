package controllers

import play.api._
import play.api.mvc._
import models.Style
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date

object Styles extends Controller {

  /**
   * 定义一个发型查询数据表单
   */
  val styleSearchForm:Form[Style] = Form(
	    mapping(
	        "styleImpression" -> list(text),
		    "serviceType" -> list(text),
		    "styleLength" -> list(text),
		    "styleColor" -> list(text),
		    "styleAmount" -> list(text),
		    "styleQuality" -> list(text),
		    "styleDiameter" -> list(text),
		    "faceShape" -> list(text)
	        ){
	      (styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape) =>
	          Style(new ObjectId,"",new ObjectId,List(""),
	           styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,"",List(""),List(""),List(""), new Date,true)
	    }
	    {
	      style=> Some((style.styleImpression,style.serviceType,
	          style.styleLength,style.styleColor,style.styleAmount,style.styleQuality,
	          style.styleDiameter,style.faceShape))
	    }
  )
  
  val styleLoginForm:Form[Style] = Form(
	    mapping(
	        "styleName" -> text,
	        "stylistId" -> text,
	        "stylePic" -> list(text),
	        "styleImpression" -> list(text),
		    "serviceType" -> list(text),
		    "styleLength" -> list(text),
		    "styleColor" -> list(text),
		    "styleAmount" -> list(text),
		    "styleQuality" -> list(text),
		    "styleDiameter" -> list(text),
		    "faceShape" -> list(text),
		    "description" -> text
	        ){
	      (styleName,stylistId,stylePic,styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,description) =>
	          Style(new ObjectId,styleName,new ObjectId(stylistId),stylePic,
	           styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,description,List(""),List(""),List(""), new Date,true)
	    }
	    {
	      style=> Some((style.styleName,style.stylistId.toString,style.stylePic,style.styleImpression,style.serviceType,
	          style.styleLength,style.styleColor,style.styleAmount,style.styleQuality,
	          style.styleDiameter,style.faceShape,style.description))
	    }
  )
  
  def index = Action {
//     val styles: Seq[Style] = Style.findAll()
//     Ok(html.style.overview(styles))
//    Style.findParaAll
    val ParaStyleColor : List[StyleColor] = StyleColor.findAll()
    Ok(html.style.styleSearch(styleSearchForm,ParaStyleColor))
  }
  
   /**
   * 
   */
  def findById(styleId: ObjectId) = Action {
    val style: Option[Style] = Style.findOneById(styleId)
    Ok(html.style.overview(style.toList))
  }
  
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    //此处由于豆平那技师和店铺关系的表还未确定，暂时固定写死，明日修改2014/03/18
    //val styles: Seq[Style] = Style.findBySalon(salonId)    
    val stylists = List("530d8010d7f2861457771bf8")
    var styles: List[Style] = Nil
    stylists.map{ sty =>
    	var style = Style.findByStylistId(new ObjectId(sty))
    	styles :::= style
    }
    // TODO: process the salon not exist pattern.
    Ok(html.salon.store.salonInfoStyleAll(salon = salon.get, styles = styles))
  }
  
  def getStyleInfoOfSalon(salonId: ObjectId, styleId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)    
    val style: Option[Style] = Style.findOneById(styleId)    
    Ok(html.salon.store.salonInfoStyle(salon = salon.get, style = style.get))

 }
  
  def styleSearchList = Action {
    implicit request => 
	  styleSearchForm.bindFromRequest.fold(
	      errors => BadRequest(html.index("")),
      {
          case(styleSearch) => {
            val styleSearchInfo = Style.findByPara(styleSearch)
            //此处由于豆平那技师和店铺关系的表还未确定，暂时固定写死，明日修改2014/03/18
            val salonId :ObjectId = new ObjectId("530d7288d7f2861457771bdd")
            val salon: Option[Salon] = Salon.findById(salonId)
            //尝试更新 st
//            styleSearchInfo.map{ sty =>
//		    	Style.updateTest(sty)
//		    }
            //尝试更新 end
            Ok(html.style.styleSearchList(styleSearchForm.fill(styleSearch),styleSearchInfo,salon = salon.get))
          }
      }
    )
  }
  
  def styleSearch = Action {
    Ok(html.style.styleLogin(styleSearchForm))
  }

  def styleLogin = Action {
    implicit request => 
    println("welcome to new world first")
	  styleLoginForm.bindFromRequest.fold(
	      errors => BadRequest(html.index("")),
      {
          case(styleLoginForm) => {
            Style.save(styleLoginForm)
            println("welcome to new world")
            println("welcome+11"+styleLoginForm)
            
           // val styleLoginForm: models.Style = Style.create(styleLoginForm)
            Ok(html.style.test(styleLoginForm))
          }
      }
    )
  }
}
