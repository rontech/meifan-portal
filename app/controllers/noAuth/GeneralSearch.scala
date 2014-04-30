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
                    //Top全部 HairSalon美发沙龙   HairCatalog时尚靓发 NailSalon美甲沙龙 NailCatalog时尚美甲 RelaxSalon休闲保健 EstheSalon整形美容 
                    generalSearch._1 match {
                        case "Top" => {
                            Ok(views.html.index())
                        }
                        case "HairSalon" => {
                            val searchParaForSalon = new SearchParaForSalon(Option(generalSearch._2), "苏州", "all", List(), "Hairdressing", List(), 
                                    PriceRange(0, 1000000), SeatNums(0, 10000),
                                    SalonFacilities(false, false, false, false, false, false, false, false, false, ""),
                                    SortByConditions("price", false, false, true))
                            val salons = Salon.findSalonBySearchPara(searchParaForSalon)
                            Ok(views.html.salon.general.index(navBar = SalonNavigation.getSalonTopNavBar, user = user, searchParaForSalon = searchParaForSalon, salons = salons))
                        }
                        case "HairCatalog" => {
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