/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
package controllers.noAuth

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date
import jp.t2v.lab.play2.auth._
import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import models.portal.style.{StyleWithAllInfo, Style}
import models.portal.common.OnUsePicture
import models.portal.salon.Salon

object Styles extends MeifanNetCustomerOptionalApplication {

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
      "consumerSocialScene" -> list(text)) {
        (stylistId, styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, consumerAgeGroup, consumerSex, consumerSocialScene) =>
          Style(new ObjectId, "", new ObjectId(stylistId), List(),
            styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, "", consumerAgeGroup, consumerSex, consumerSocialScene, new Date, true)
      } {
        style =>
          Some((style.stylistId.toString, style.styleImpression, style.serviceType,
            style.styleLength, style.styleColor, style.styleAmount, style.styleQuality,
            style.styleDiameter, style.faceShape, style.consumerAgeGroup, style.consumerSex, style.consumerSocialScene))
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
      "consumerSocialScene" -> list(text)) {
        (styleName, stylistId, stylePic, styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, description, consumerAgeGroup, consumerSex, consumerSocialScene) =>
          Style(new ObjectId, styleName, new ObjectId(stylistId), stylePic,
            styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, description, consumerAgeGroup, consumerSex, consumerSocialScene, new Date, true)
      } {
        style =>
          Some((style.styleName, style.stylistId.toString, style.stylePic, style.styleImpression, style.serviceType,
            style.styleLength, style.styleColor, style.styleAmount, style.styleQuality,
            style.styleDiameter, style.faceShape, style.description, style.consumerAgeGroup, style.consumerSex, style.consumerSocialScene))
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
      "consumerSocialScene" -> list(text)) {
        (id, styleName, stylistId, stylePic, styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, description, consumerAgeGroup, consumerSex, consumerSocialScene) =>
          Style(new ObjectId(id), styleName, new ObjectId(stylistId), stylePic,
            styleImpression, serviceType, styleLength, styleColor, styleAmount, styleQuality, styleDiameter, faceShape, description, consumerAgeGroup, consumerSex, consumerSocialScene, new Date, true)
      } {
        style =>
          Some((style.id.toString, style.styleName, style.stylistId.toString, style.stylePic, style.styleImpression, style.serviceType,
            style.styleLength, style.styleColor, style.styleAmount, style.styleQuality,
            style.styleDiameter, style.faceShape, style.description, style.consumerAgeGroup, style.consumerSex, style.consumerSocialScene))
      })

  /**
   * 前台店铺所有发型展示区域
   * 进入店铺内页发型模块时，检索出该店铺所有发型
   * @param salonId 店铺ID
   * @return
   */
  def findBySalon(salonId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    val styles = Salon.getAllStyles(salonId)
    salon match {
      case Some(salon) => {
        Ok(html.salon.store.salonInfoStyleAll(salon = salon, styles = styles, user = user))
      }
      case None => NotFound
    }
  }

  /**
   * 前台店铺所有发型展示区域
   * 店铺内页发型模块中，依据选择要显示的发型性别显示该店铺发型
   * @param salonId 店铺ID
   * @param sex 性别
   * @return
   */
  def findBySalonAndSex(salonId: ObjectId, sex: String) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    var styles = Style.findStylesBySalonAndSex(salonId, sex)
    salon match {
      case Some(salon) => {
        Ok(html.salon.store.salonInfoStyleAll(salon = salon, styles = styles, sex = sex, user = user))
      }
      case None => NotFound
    }
  }

  /**
   * 前台店铺内页单一发型展示区域
   * 用于获取某一发型的详细信息
   * @param salonId 店铺ID
   * @param styleId 发型ID
   * @return
   */
  def getStyleInfoOfSalon(salonId: ObjectId, styleId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    val style: Option[Style] = Style.findOneById(styleId)
    salon match {
      case Some(salon) => {
        style match {
          case Some(style) => {
            Ok(html.salon.store.salonInfoStyle(salon = salon, style = style, user = user))
          }
          case None => NotFound
        }
      }
      case None => NotFound
    }
  }

  /**
   * 前台发型检索主页面
   * @return
   */
  def index = StackAction { implicit request =>
    val user = loggedIn
    Ok(html.style.general.overview(styleSearchForm, Style.findParaAll, user))
  }

  /**
   * 通过发型长度和适合性别检索所有有效发型
   * @param styleLength 发型长度
   * @param consumerSex 发型适合性别
   * @return
   */
  def findByLength(styleLength: String, consumerSex: String) = StackAction { implicit request =>
    val user = loggedIn
    //以长度、性别，初始化一组发型数据，用于检索时缓存检索字段
    val styleSearchByLength: Style = Style(new ObjectId, "", new ObjectId, Nil, "", Nil, styleLength,
      Nil, Nil, Nil, Nil, Nil, "", Nil, consumerSex, Nil, new Date, true)
    val styleAllInfo: List[StyleWithAllInfo] = Style.findByLength(styleLength, consumerSex)

    Ok(html.style.general.styleSearchResultPage(styleSearchForm.fill(styleSearchByLength), styleAllInfo, Style.findParaAll, user))
  }

  /**
   * 通过发型风格和适合性别检索发型
   * @param styleImpression 发型风格
   * @param consumerSex 发型适合性别
   * @return
   */
  def findByImpression(styleImpression: String, consumerSex: String) = StackAction { implicit request =>
    val user = loggedIn
    //以风格、性别，初始化一组发型数据，用于检索时缓存检索字段
    val styleSearchByImpression: Style = Style(new ObjectId, "", new ObjectId, Nil, styleImpression,
      Nil, "", Nil, Nil, Nil, Nil, Nil, "", Nil, consumerSex, Nil, new Date, true)
    var styleAllInfo: List[StyleWithAllInfo] = Style.findByImpression(styleImpression, consumerSex)

    Ok(html.style.general.styleSearchResultPage(styleSearchForm.fill(styleSearchByImpression), styleAllInfo, Style.findParaAll, user))
  }

  /**
   * 前台发型检索结果展示
   * @return
   */
  def styleSearchList = StackAction { implicit request =>
    val user = loggedIn
    styleSearchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      {
        case (styleSearch) => {
          //检索所有符合条件的发型
          val styleAllInfo: List[StyleWithAllInfo] = Style.findByPara(styleSearch)
          Ok(html.style.general.styleSearchResultPage(styleSearchForm.fill(styleSearch), styleAllInfo, Style.findParaAll, user))
        }
      })
  }

  /**
   * 通过发型长度和适合性别检索Ranking排行符合要求的有效发型（通过预约次数）
   * @param styleLength 发型长度
   * @param consumerSex 发型适合性别
   * @return
   */
  def findByRanking(styleLength: String, consumerSex: String) = StackAction { implicit request =>
    val user = loggedIn
    var stlAllInfo: List[StyleWithAllInfo] = Nil
    //通过页面选择的发型长度字段的值，来进行不同的检索，all为不以长度为检索字段
    if (styleLength.equals("all")) {
      stlAllInfo = Style.findByRankingAndSex(consumerSex)
    } else {
      stlAllInfo = Style.findByRankingAndLenAndSex(styleLength, consumerSex)
    }
    Ok(html.style.general.styleRankingResultPage(styleSearchForm, stlAllInfo, Style.findParaAll, user))
  }

}
