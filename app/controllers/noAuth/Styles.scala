package controllers.noAuth

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date
import jp.t2v.lab.play2.auth._
import controllers.UserAuthConfigImpl

object Styles extends Controller with OptionalAuthElement with UserAuthConfigImpl {

    /**
     * 定义一个发型查询数据表单
     */
    val styleSearchForm: Form[Style] = Form(
        mapping(
            "stylistId" -> text,
            "styleImpression" -> text,
            "serviceType" -> list(text),
            "styleLength" -> text,
            "styleColor" -> list(text),
            "styleAmount" -> list(text),
            "styleQuality" -> list(text),
            "styleDiameter" -> list(text),
            "faceShape" -> list(text),
            "consumerAgeGroup" -> list(text),
            "consumerSex" -> text,
            "consumerSocialStatus" -> list(text)) {
                (stylistId,styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, consumerAgeGroup, consumerSex, consumerSocialStatus) =>
                    Style(new ObjectId, "", new ObjectId(stylistId), List(),
                        styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, "", consumerAgeGroup, consumerSex, consumerSocialStatus, new Date, true)
            } {
                style =>
                    Some((style.stylistId.toString, style.styleImpression, style.serviceType,
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
            "stylePic" -> (list(mapping(
                "fileObjId" -> text,
                "picUse" -> text,
                "showPriority" -> optional(number),
                "description" -> optional(text)) {
                    (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriority, description)
                } {
                    onUsePicture => Some((onUsePicture.fileObjId.toString, onUsePicture.picUse, onUsePicture.showPriority, onUsePicture.description))
                })),
            "styleImpression" -> text,
            "serviceType" -> list(text),
            "styleLength" -> text,
            "styleColor" -> list(text),
            "styleAmount" -> list(text),
            "styleQuality" -> list(text),
            "styleDiameter" -> list(text),
            "faceShape" -> list(text),
            "description" -> text,
            "consumerAgeGroup" -> list(text),
            "consumerSex" -> text,
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
            "stylePic" -> (list(mapping(
                "fileObjId" -> text,
                "picUse" -> text,
                "showPriority" -> optional(number),
                "description" -> optional(text)) {
                    (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriority, description)
                } {
                    onUsePicture => Some((onUsePicture.fileObjId.toString, onUsePicture.picUse, onUsePicture.showPriority, onUsePicture.description))
                })),
            "styleImpression" -> text,
            "serviceType" -> list(text),
            "styleLength" -> text,
            "styleColor" -> list(text),
            "styleAmount" -> list(text),
            "styleQuality" -> list(text),
            "styleDiameter" -> list(text),
            "faceShape" -> list(text),
            "description" -> text,
            "consumerAgeGroup" -> list(text),
            "consumerSex" -> text,
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

    /**
     * 前台店铺发型展示区域及发型详细信息
     */
    def findBySalon(salonId: ObjectId) = StackAction { implicit request =>
        val user = loggedIn
        val salon: Option[Salon] = Salon.findOneById(salonId)
        val stylists = Style.findStylistBySalonId(salonId)
        var styles: List[Style] = Nil
        stylists.map { sty =>
            var style = Style.findByStylistId(sty.stylistId)
            styles :::= style
        }
        salon match {
            case Some(salon) => {
                Ok(html.salon.store.salonInfoStyleAll(salon = salon, styles = styles, user = user))
            }
            case None => NotFound
        }
    }

    def findBySalonAndSex(salonId: ObjectId, sex: String) = StackAction { implicit request =>
        val user = loggedIn
        val salon: Option[Salon] = Salon.findOneById(salonId)
        val stylists = Style.findStylistBySalonId(salonId)
        var styles: List[Style] = Nil
        stylists.map { sty =>
            var style = Style.findByStylistIdAndSex(sty.stylistId, sex)
            styles :::= style
        }
        salon match {
            case Some(salon) => {
                Ok(html.salon.store.salonInfoStyleAll(salon = salon, styles = styles, sex = sex, user = user))
            }
            case None => NotFound
        }
    }
    
    
    def getStyleInfoOfSalon(salonId: ObjectId, styleId: ObjectId) = StackAction { implicit request =>
        val user = loggedIn
        val salon: Option[Salon] = Salon.findOneById(salonId)
        val style: Option[Style] = Style.findOneById(styleId)
        salon match {
            case Some(salon) => {
                style match {
                    case Some(style) => {
                        Ok(html.salon.store.salonInfoStyle(salon = salon, style = style, user= user))
                    }
                    case None => NotFound
                }
            }
            case None => NotFound
        }
    }

    /**
     * 前台发型检索
     */
    def index = StackAction { implicit request =>
        val user = loggedIn
        Ok(html.style.general.overview(styleSearchForm, Style.findParaAll, user))
    }

    def findByLength(styleLength: String, consumerSex: String) = StackAction { implicit request =>
        val user = loggedIn
        val styleSearchInfo = Style.findByLength(styleLength, consumerSex)
        val styleSearchByLength: Style = Style(new ObjectId, "", new ObjectId, Nil, "", Nil, styleLength, Nil, Nil, Nil, Nil, Nil, "", Nil, consumerSex, Nil, new Date, true)
        var styleAndSalons: List[StyleAndSalon] = Nil
        styleSearchInfo.map { styleInfo =>
            val salonOne = Style.findSalonByStyle(styleInfo.stylistId)
            salonOne match {
                case Some(salonOne) => {
                    val styleAndSalon = new StyleAndSalon(styleInfo, salonOne)
                    styleAndSalons :::= List(styleAndSalon)
                }
                case None => null
            }
        }
        Ok(html.style.general.styleSearchResultPage(styleSearchForm.fill(styleSearchByLength), styleAndSalons, Style.findParaAll, user))
    }

    def findByImpression(styleImpression: String, consumerSex: String) = StackAction { implicit request =>
        val user = loggedIn
        val styleSearchInfo = Style.findByImpression(styleImpression, consumerSex)
        val styleSearchByImpression: Style = Style(new ObjectId, "", new ObjectId, Nil, styleImpression, Nil, "", Nil, Nil, Nil, Nil, Nil, "", Nil, consumerSex, Nil, new Date, true)
        var styleAndSalons: List[StyleAndSalon] = Nil
        styleSearchInfo.map { styleInfo =>
            val salonOne = Style.findSalonByStyle(styleInfo.stylistId)
            salonOne match {
                case Some(salonOne) => {
                    val styleAndSalon = new StyleAndSalon(styleInfo, salonOne)
                    styleAndSalons :::= List(styleAndSalon)
                }
                case None => null
            }
        }
        Ok(html.style.general.styleSearchResultPage(styleSearchForm.fill(styleSearchByImpression), styleAndSalons, Style.findParaAll, user))
    }

    def styleSearchList = StackAction { implicit request =>
        val user = loggedIn
            styleSearchForm.bindFromRequest.fold(
                errors => BadRequest(views.html.index()),
                {
                    case (styleSearch) => {
                        val styleSearchInfo = Style.findByPara(styleSearch)
                        var styleAndSalons: List[StyleAndSalon] = Nil
                        styleSearchInfo.map { styleInfo =>
                            val salonOne = Style.findSalonByStyle(styleInfo.stylistId)
                            salonOne match {
                                case Some(salonOne) => {
                                    val styleAndSalon = new StyleAndSalon(styleInfo, salonOne)
                                    styleAndSalons :::= List(styleAndSalon)
                                }
                                case None => null
                            }
                        }
                        Ok(html.style.general.styleSearchResultPage(styleSearchForm.fill(styleSearch), styleAndSalons, Style.findParaAll, user))
                    }
                })
    }

}
