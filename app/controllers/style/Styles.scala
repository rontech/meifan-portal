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
  
  
/*  val styleForm:Form[StyleAndPara] = Form(
        mapping(
	        "style" ->mapping(
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
			          Style(new ObjectId,"",new ObjectId,List(""),styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,"",List(""),List(""),List(""), new Date,true)
			    }
			    {
			      style=> Some((style.styleImpression,style.serviceType,
			          style.styleLength,style.styleColor,style.styleAmount,style.styleQuality,
			          style.styleDiameter,style.faceShape))
			    },
		    "styleImpression" ->mapping(
		       "styleImpression" -> text,
		       "description" -> text
			    ){
			      (styleImpression,description) =>
			        StyleImpression(new ObjectId,styleImpression,description)
			    }
			    {
			    styleImpression=> Some((styleImpression.styleImpression,styleImpression.description))  
			    }
	    )(StyleAndPara.apply)(StyleAndPara.unapply)

  )*/

//  val styleList:Form[StylePara] = Form(
//        "styleList" ->mapping(
//	        "styleImpression" -> list(text),
//		    "serviceType" -> list(text),
//		    "styleLength" -> list(text),
//		    "styleColor" -> list(text),
//		    "styleAmount" -> list(text),
//		    "styleQuality" -> list(text),
//		    "styleDiameter" -> list(text),
//		    "faceShape" -> list(text)
//	        )(StyleAnd.apply)(StyleAnd.unapply)
//  )
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
		    "faceShape" -> list(text),
		    "consumerAgeGroup" -> list(text),
		    "consumerSex" -> list(text),
		    "consumerSocialStatus" -> list(text)
	        ){
	      (styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,consumerAgeGroup,consumerSex,consumerSocialStatus) =>
	          Style(new ObjectId,"",new ObjectId,List(""),
	           styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,"",consumerAgeGroup,consumerSex,consumerSocialStatus, new Date,true)
	    }
	    {
	      style=> Some((style.styleImpression,style.serviceType,
	          style.styleLength,style.styleColor,style.styleAmount,style.styleQuality,
	          style.styleDiameter,style.faceShape,style.consumerAgeGroup,style.consumerSex,style.consumerSocialStatus))
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
		    "description" -> text,
		    "consumerAgeGroup" -> list(text),
		    "consumerSex" -> list(text),
		    "consumerSocialStatus" -> list(text)
	        ){
	      (styleName,stylistId,stylePic,styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,description,consumerAgeGroup,consumerSex,consumerSocialStatus) =>
	          Style(new ObjectId,styleName,new ObjectId(stylistId),stylePic,
	           styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,description,consumerAgeGroup,consumerSex,consumerSocialStatus, new Date,true)
	    }
	    {
	      style=> Some((style.styleName,style.stylistId.toString,style.stylePic,style.styleImpression,style.serviceType,
	          style.styleLength,style.styleColor,style.styleAmount,style.styleQuality,
	          style.styleDiameter,style.faceShape,style.description,style.consumerAgeGroup,style.consumerSex,style.consumerSocialStatus))
	    }
  )
  
  def index = Action {
	Ok(html.style.styleSearch(styleSearchForm,Style.findParaAll))
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
            Ok(html.style.styleSearchList(styleSearchForm.fill(styleSearch),styleSearchInfo,salon = salon.get,Style.findParaAll))
          }
      }
    )
  }
  
  def styleSearch = Action {
    //此处为发型更新，将来会传一个styleId过来修改
    val styleId :ObjectId = new ObjectId("530d828cd7f2861457771c0b")
    val styleOne: Option[Style] = Style.findOneById(styleId)
    styleOne match {
	    case Some(style) => {
	    	Ok(html.style.styleLogin(styleLoginForm, Style.findParaAll,style))
	    }
	    case None => {
	    	NotFound
	    } 
    }
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
