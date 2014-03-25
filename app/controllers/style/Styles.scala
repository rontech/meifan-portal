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
    val styleSearchForm: Form[Style] = Form(
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
            "consumerSocialStatus" -> list(text)) {
                (styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, consumerAgeGroup, consumerSex, consumerSocialStatus) =>
                    Style(new ObjectId, "", new ObjectId, List(""),
                        styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, "", consumerAgeGroup, consumerSex, consumerSocialStatus, new Date, true)
            } {
                style =>
                    Some((style.styleImpression, style.serviceType,
                        style.styleLength, style.styleColor, style.styleAmount, style.styleQuality,
                        style.styleDiameter, style.faceShape, style.consumerAgeGroup, style.consumerSex, style.consumerSocialStatus))
            })

    /**
     * 定义一个发型注册数据表单
     */
    val styleAddForm: Form[Style] = Form(
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
            "consumerSocialStatus" -> list(text)) {
                (styleName, stylistId, stylePic, styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, description, consumerAgeGroup, consumerSex, consumerSocialStatus) =>
                    Style(new ObjectId, styleName, new ObjectId(stylistId), stylePic,
                        styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, description, consumerAgeGroup, consumerSex, consumerSocialStatus, new Date, true)
            } {
                style =>
                    Some((style.styleName, style.stylistId.toString, style.stylePic, style.styleImpression, style.serviceType,
                        style.styleLength, style.styleColor, style.styleAmount, style.styleQuality,
                        style.styleDiameter, style.faceShape, style.description, style.consumerAgeGroup, style.consumerSex, style.consumerSocialStatus))
            })

    /**
     * 定义一个发型更新数据表单
     */
    val styleUpdateForm: Form[Style] = Form(
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
            "consumerSocialStatus" -> list(text)) {
                (id, styleName, stylistId, stylePic, styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, description, consumerAgeGroup, consumerSex, consumerSocialStatus) =>
                    Style(new ObjectId(id), styleName, new ObjectId(stylistId), stylePic,
                        styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, description, consumerAgeGroup, consumerSex, consumerSocialStatus, new Date, true)
            } {
                style =>
                    Some((style.id.toString, style.styleName, style.stylistId.toString, style.stylePic, style.styleImpression, style.serviceType,
                        style.styleLength, style.styleColor, style.styleAmount, style.styleQuality,
                        style.styleDiameter, style.faceShape, style.description, style.consumerAgeGroup, style.consumerSex, style.consumerSocialStatus))
            })

    def findById(styleId: ObjectId) = Action {
        val style: Option[Style] = Style.findOneById(styleId)
        Ok(html.style.general.overview(style.toList, styleSearchForm, Style.findParaAll))
    }

    /**
     * 前台店铺发型展示区域及发型详细信息
     */
    def findBySalon(salonId: ObjectId) = Action {
        val salon: Option[Salon] = Salon.findById(salonId)
        //此处由于豆平那技师和店铺关系的表yi确定，暂时固定写死，明日修改2014/03/18
        //val styles: Seq[Style] = Style.findBySalon(salonId)    
        val stylists = List("530d8010d7f2861457771bf8")
        var styles: List[Style] = Nil
        stylists.map { sty =>
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

    /**
     * 前台发型检索
     */
    def index = Action {
        // TODO
        Ok(html.style.general.overview(Nil, styleSearchForm, Style.findParaAll))
    }

    def styleSearchList = Action {
        implicit request =>
            styleSearchForm.bindFromRequest.fold(
                errors => BadRequest(html.index("")),
                {
                    case (styleSearch) => {
                        val styleSearchInfo = Style.findByPara(styleSearch)
                        //此处由于豆平那技师和店铺关系的表还未确定，暂时固定写死，明日修改2014/03/18
                        val salonId: ObjectId = new ObjectId("530d7288d7f2861457771bdd")
                        val salon: Option[Salon] = Salon.findById(salonId)
                        Ok(html.style.general.styleSearchList(styleSearchForm.fill(styleSearch), styleSearchInfo, salon = salon.get, Style.findParaAll))
                    }
                })
    }

    /**
     * 后台发型新建
     */
    def styleAdd = Action {
        //此处为新发型登录
        Ok(html.style.styleAdd(styleAddForm, Style.findParaAll))
    }

    def styleAddNew = Action {
        implicit request =>
            styleAddForm.bindFromRequest.fold(
                errors => BadRequest(html.index("")),
                {
                    case (styleAddForm) => {
                        Style.save(styleAddForm)
                        Ok(html.style.test(styleAddForm))
                    }
                })
    }

    /**
     * 后台发型更新
     */
    def styleUpdate(id: ObjectId) = Action {
        val styleOne: Option[Style] = Style.findOneById(id)
        styleOne match {
            case Some(style) => Ok(html.style.styleUpdate(styleAddForm, Style.findParaAll, style))
            case None => NotFound
        }
    }

    def styleUpdateNew = Action {
        implicit request =>
            styleUpdateForm.bindFromRequest.fold(
                errors => BadRequest(html.index("")),
                {
                    case (styleUpdateForm) => {
                        Style.updateStyle(styleUpdateForm)
                        //              Ok(html.style.test(styleUpdateForm))
                        Redirect(routes.Styles.styleUpdate(styleUpdateForm.id))
                    }
                })
    }

    /**
     * 后台发型检索
     */
    def backstageStyleSearch = Action {
        //该处暂时固定写死发型师ID,查询其所有发型
        val stylistId = List("530d8010d7f2861457771bf8")
        var styles: List[Style] = Nil
        stylistId.map { sty =>
            var style = Style.findByStylistId(new ObjectId(sty))
            styles :::= style
        }
        Ok(html.style.backstageStyleSearchList(styleSearchForm, styles, Style.findParaAll, true))
    }

    def backstageStyleSearchList = Action {
        implicit request =>
            styleSearchForm.bindFromRequest.fold(
                errors => BadRequest(html.index("")),
                {
                    case (styleSearch) => {
                        val styleSearchInfo = Style.findByPara(styleSearch)
                        Ok(html.style.backstageStyleSearchList(styleSearchForm.fill(styleSearch), styleSearchInfo, Style.findParaAll, false))
                    }
                })
    }

    /**
     * 后台发型删除，使之无效即可
     */
    def styleToInvalid(id: ObjectId, isValid: Boolean) = Action {
        Style.styleToInvalid(id, isValid)
        Redirect(routes.Styles.backstageStyleSearch)
    }
}
