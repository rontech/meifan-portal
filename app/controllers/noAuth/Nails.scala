package controllers.noAuth

import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import models.portal.nail.{Nail, SearchPara}
import play.api.data._
import play.api.data.Forms._
import views._
import java.util.Date
import mongoContext._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.commons.ValidBSONType.ObjectId
import se.radley.plugin.salat.Binders.ObjectId


/**
 * Created by fan on 14/06/09.
 */
object Nails extends MeifanNetCustomerOptionalApplication {

  /**
   * 定义一个美甲查询数据表单
   */
  val nailSearchForm: Form[SearchPara] = Form(
    mapping(
      "keyWord" -> optional(text),
      "city" -> text,
      "region" -> text,
      "stylistId" -> text,
      "serviceType" -> list(text),
      "styleColor" -> list(text),
      "styleMaterial" -> list(text),
      "styleBase" -> list(text),
      "styleImpression" -> list(text),
      "socialScene" -> list(text))(SearchPara.apply)(SearchPara.unapply)
  )

  /**
   * 美甲前台主检索页面
   * @return
   */
  def index = StackAction {
    implicit request =>
      val user = loggedIn
      var myCity = request.session.get("myCity").map {
        city => city
      } getOrElse {
        "苏州"
      }
      val searchParaForNail = new SearchPara(None, myCity, "all", "all", List(), List(), List(), List(), List(), List())
      Ok(views.html.nailCatalog.general.overview(nailSearchForm = searchParaForNail, nailPara = Nail.findParaAll("Manicures"), user = user))
  }

  /**
   * 美甲前台检索结果展示页面
   * @return
   */
  def getNailBySearch = StackAction {
    implicit request =>
      val user = loggedIn
      nailSearchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),{
        case (nailSearchForm) => {
          //检索出所有符合条件的美甲，及与之相关的部分店铺信息
          val nailWithAllInfos = Nail.findNailBySearchPara(nailSearchForm)(0)
          Ok(views.html.nailCatalog.general.nailSearchRstPage(nailSearchForm = nailSearchForm, nailPara = Nail.findParaAll("Manicures"), nailWithAllInfos = nailWithAllInfos, user = user))
        }
      })
  }

}
