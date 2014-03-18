package controllers

import play.api._
import play.api.mvc._
import models.Style
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._

import models._
import views._


object Styles extends Controller {

  /**
   * 定义一个发型查询数据表单
   */
  val styleSearchForm:Form[Style] = Form(
	    mapping(
	        "styleImpression" -> list(text),
		    "serviceType" -> list(text),
		    "styleLength" -> text,
		    "styleColor" -> list(text),
		    "styleAmount" -> list(text),
		    "styleQuality" -> list(text),
		    "styleDiameter" -> list(text),
		    "faceType" -> list(text)
	        ){
	      (styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceType) =>
	          Style(new ObjectId,"",new ObjectId,List(""),
	           styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceType,"")
	    }
	    {
	      style=> Some((style.styleImpression,style.serviceType,
	          style.styleLength,style.styleColor,style.styleAmount,style.styleQuality,
	          style.styleDiameter,style.faceType))
	    }
  )
  
  def index = Action {
     val styles: Seq[Style] = Style.findAll()
     Ok(html.style.overview(styles))
  }
  
   /**
   * 
   */
  def findById(styleId: ObjectId) = Action {
    val style: Option[Style] = Style.findById(styleId)
    Ok(html.style.overview(style.toList))
  }
  
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    //此处由于豆平那技师和店铺关系的表还未确定，暂时固定写死，明日修改2014/03/18
    //val styles: Seq[Style] = Style.findBySalon(salonId)    
    val stylists = List("530d8010d7f2861457771bf8","530d8010d7f2861457771bf8")
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
    val style: Option[Style] = Style.findById(styleId)    
    Ok(html.salon.store.salonInfoStyle(salon = salon.get, style = style.get))

 }
  
  def styleSearchList = Action {
    implicit request => 
	  styleSearchForm.bindFromRequest.fold(
	      errors => BadRequest(html.index("")),
      {
          case(styleSearchForm) => {
            val styleSearchInfo = Style.findByPara(styleSearchForm)
            println("wwwwww："+styleSearchForm)
            println("册数："+styleSearchInfo)
            //此处由于豆平那技师和店铺关系的表还未确定，暂时固定写死，明日修改2014/03/18
            val salonId :ObjectId = new ObjectId("530d7288d7f2861457771bdd")
            val salon: Option[Salon] = Salon.findById(salonId)
            Ok(html.style.styleSearchList(styleSearchInfo,salon = salon.get))
          }
      }
    )
  }
  
  def styleSearch = Action {
    Ok(html.style.styleSearch(styleSearchForm))
  }

}
