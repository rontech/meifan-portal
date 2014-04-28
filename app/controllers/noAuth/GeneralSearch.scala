package controllers.noAuth

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import jp.t2v.lab.play2.auth._
import controllers._
import com.mongodb.casbah.commons.Imports._
import java.util.Date

object GeneralSearch extends Controller with OptionalAuthElement with UserAuthConfigImpl {

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
        generalSearchForm.bindFromRequest.fold(
            errors => BadRequest(views.html.index()),
            {
                case (generalSearch) => {
                    //对综合检索的数据进行分析,画面进行跳转
                    //all全部  hairSalon美发沙龙   hairCatalog时尚靓发  nailSalon美甲沙龙  nailCatalog时尚美甲  relaxSalon休闲保健  estheSalon整形美容 
                    generalSearch._1 match {
                        case "全部" => {
                            Ok(views.html.index())
                        }
                        case "美发沙龙" => {
                            val searchParaForSalon = new SearchParaForSalon(Option(generalSearch._2), "苏州", "all", List(), "Hairdressing", List(), PriceRange(0, 1000000), SeatNums(0, 10000), SalonFacilities(false, false, false, false, false, false, false, false, false, ""), "热度")
                            val salons = Salon.findSalonBySearchPara(searchParaForSalon)
                            Ok(views.html.salon.general.index(navBar = SalonNavigation.getSalonTopNavBar, user = user, searchParaForSalon = searchParaForSalon, salons = salons))
                        }
                        case "时尚靓发" => {
                            val styleSearch = new Style(new ObjectId, "", new ObjectId, Nil, "all", Nil, "all", Nil, Nil, Nil, Nil, Nil, "", Nil, "all", Nil, new Date, true)
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
                            Ok(views.html.style.general.styleSearchResultPage(Styles.styleSearchForm.fill(styleSearch), styleAndSalons, Style.findParaAll, user))
                        }
                        case "美甲沙龙" => {
                            Ok(views.html.index())
                        }
                        case "时尚美甲" => {
                            Ok(views.html.index())
                        }
                        case "休闲保健" => {
                            Ok(views.html.index())
                        }
                        case "整形美容 " => {
                            Ok(views.html.index())
                        }
                        case _ => Ok(views.html.index())
                    }
                }
            })
    }

}