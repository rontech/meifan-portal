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

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import jp.t2v.lab.play2.auth._
import controllers._
import com.mongodb.casbah.commons.Imports._
import java.util.Date
import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import models.portal.search.{SortByConditions, SeatNums, PriceRange, SearchParaForSalon}
import models.portal.salon.{Salon, SalonFacilities}
import models.portal.style.{StyleWithAllInfo, Style}

object GeneralSearch extends MeifanNetCustomerOptionalApplication {

  /**
   * 为综合检索添加一个检索数据表单
   */
  val generalSearchForm = Form(
    tuple(
      "category" -> text,
      "keyword" -> text))

  /**
   * 综合检索跳转到相应模块
   */
  def getGeneralSearch = StackAction { implicit request =>
    val user = loggedIn
    val myCity = request.session.get("myCity").map{ city => city } getOrElse { "苏州" }

    generalSearchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      {
        case (generalSearch) => {
          //对综合检索的数据进行分析,画面进行跳转
          //Top全部 HairSalon美发沙龙   HairCatalog时尚靓发 NailSalon美甲沙龙 NailCatalog时尚美甲 RelaxSalon休闲保健 EstheSalon整形美容 
          generalSearch._1 match {
            case "Top" => {
              // TODO
              Ok(views.html.index())
            }
            case "HairSalon" => {
              val searchParaForSalon = new SearchParaForSalon(Option(generalSearch._2), myCity, "all", List(), "Hairdressing", List(),
                PriceRange(0, 1000000), SeatNums(0, 10000),
                SalonFacilities(false, false, false, false, false, false, false, false, false, ""),
                SortByConditions("price", false, false, true))
              val salons = Salon.findSalonBySearchPara(searchParaForSalon)
              val nav = "HairSalon"
              Ok(views.html.salon.general.index(nav = nav, navBar = SalonNavigation.getSalonTopNavBar, user = user, searchParaForSalon = searchParaForSalon, salons = salons))
            }
            case "HairCatalog" => {
              val styleSearch = new Style(new ObjectId, "", new ObjectId, Nil, "all", Nil, "all", Nil, Nil, Nil, Nil, Nil, "", Nil, "all", Nil, new Date, true)
              var styleAndSalons: List[StyleWithAllInfo] = Style.findByPara(styleSearch)
              Ok(views.html.style.general.styleSearchResultPage(Styles.styleSearchForm.fill(styleSearch), styleAndSalons, Style.findParaAll, user))
            }
            case "NailSalon" => {
              Ok(views.html.index())
            }
            case "NailCatalog" => {
              Ok(views.html.index())
            }
            case "RelaxSalon" => {
              Ok(views.html.index())
            }
            case "EstheSalon" => {
              Ok(views.html.index())
            }
            case _ => Ok(views.html.index())
          }
        }
      })
  }

}
