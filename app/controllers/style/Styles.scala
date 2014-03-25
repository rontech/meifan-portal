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
  
  val styleUpdateForm:Form[Style] = Form(
	    mapping(
	        "id" -> text,
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
	      (id,styleName,stylistId,stylePic,styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,description,consumerAgeGroup,consumerSex,consumerSocialStatus) =>
	          Style(new ObjectId(id),styleName,new ObjectId(stylistId),stylePic,
	           styleImpression,serviceType,styleLength,styleColor,styleAmount,styleQuality,styleDiameter,faceShape,description,consumerAgeGroup,consumerSex,consumerSocialStatus,new Date,true)
	    }
	    {
	      style=> Some((style.id.toString,style.styleName,style.stylistId.toString,style.stylePic,style.styleImpression,style.serviceType,
	          style.styleLength,style.styleColor,style.styleAmount,style.styleQuality,
	          style.styleDiameter,style.faceShape,style.description,style.consumerAgeGroup,style.consumerSex,style.consumerSocialStatus))
	    }
  )
  
  def index = Action {
      // TODO
      // Should get Initial Ranked Data for ranking by popularity.  
      
      Ok(html.style.general.overview(Nil, styleSearchForm, Style.findParaAll))
  }
  
   /**
   * 
   */
  def findById(styleId: ObjectId) = Action {
    val style: Option[Style] = Style.findOneById(styleId)
    Ok(html.style.general.overview(style.toList, styleSearchForm, Style.findParaAll))
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
            Ok(html.style.general.styleSearchList(styleSearchForm.fill(styleSearch),styleSearchInfo,salon = salon.get,Style.findParaAll))
          }
      }
    )
  }
  
  def styleLogin = Action {
    //此处为发型更新，将来会传一个styleId过来修改
    Ok(html.style.styleLogin(styleLoginForm, Style.findParaAll))
  }

  def styleLoginNew = Action {
    implicit request => 
	  styleLoginForm.bindFromRequest.fold(
	      errors => BadRequest(html.index("")),
      {
          case(styleLoginForm) => {
            Style.save(styleLoginForm)
            Ok(html.style.test(styleLoginForm))
          }
      }
    )
  }
  
  def styleUpdate(id: ObjectId) = Action {
    //此处为发型更新，将来会传一个styleId过来修改
    val styleOne: Option[Style] = Style.findOneById(id)
    styleOne match {
	    case Some(style) => Ok(html.style.styleUpdate(styleLoginForm, Style.findParaAll,style))
	    case None => NotFound 
    }
  }
  
  def styleUpdateNew = Action {
    implicit request => 
	  styleUpdateForm.bindFromRequest.fold(
	      errors => BadRequest(html.index("")),
      {
          case(styleUpdateForm) => {
        	  Style.updateStyle(styleUpdateForm)
//            Ok(html.style.test(styleUpdateForm))
        	  Redirect(routes.Styles.styleUpdate(styleUpdateForm.id))
          }
      }
    )
  }
  
  def backstageStyleSearch = Action {
      //该处暂时固定写死发型师ID,查询其所有发型
      val stylistId = List("530d8010d7f2861457771bf8")
      var styles: List[Style] = Nil
      stylistId.map{ sty =>
    	var style = Style.findByStylistId(new ObjectId(sty))
    	styles :::= style
      }
      Ok(html.style.backstageStyleSearchList(styleSearchForm,styles,Style.findParaAll,true))
  }
  
  def backstageStyleSearchList = Action {
    implicit request => 
	  styleSearchForm.bindFromRequest.fold(
	      errors => BadRequest(html.index("")),
      {
          case(styleSearch) => {
            val styleSearchInfo = Style.findByPara(styleSearch)
            Ok(html.style.backstageStyleSearchList(styleSearchForm.fill(styleSearch),styleSearchInfo,Style.findParaAll,false))
          }
      }
    )
  }
  
  def styleDelete(id: ObjectId) = Action {
    Style.delete(id)
    Redirect(routes.Styles.backstageStyleSearch)
  }
}
