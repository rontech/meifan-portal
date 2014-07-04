package controllers.noAuth

import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import models.portal.nail.{NailWithAllInfo, Nail, SearchPara}
import play.api.data._
import play.api.data.Forms._
import views._
import java.util.Date
import mongoContext._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.commons.ValidBSONType.ObjectId
import se.radley.plugin.salat.Binders.ObjectId
import models.portal.common.OnUsePicture


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
      "serviceType" -> text,
      "styleColor" -> list(text),
      "styleMaterial" -> list(text),
      "styleBase" -> list(text),
      "styleImpression" -> list(text),
      "socialScene" -> list(text))(SearchPara.apply)(SearchPara.unapply)
  )

  /**
   * 定义一个美甲注册数据表单
   */
  val nailInfoForm: Form[Nail] = Form(
    mapping(
      "id" -> text,
      "styleName" -> text,
      "stylistId" -> text,
      "serviceType" -> text,
      "styleColor" -> list(text),
      "styleMaterial" -> list(text),
      "styleBase" -> list(text),
      "styleImpression" -> text,
      "socialScene" -> list(text),
      "stylePic" ->(list(mapping(
        "fileObjId" -> text,
        "picUse" -> text,
        "showPriority" -> optional(number),
        "description" -> optional(text)) {
          (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriority, description)
        } {
          onUsePicture => Some((onUsePicture.fileObjId.toString, onUsePicture.picUse, onUsePicture.showPriority, onUsePicture.description))
        })),
       "description" -> text){
     (id, styleName, stylistId, serviceType, styleColor, styleMaterial, styleBase, styleImpression, socialScene, stylePic, description)
     => Nail(new ObjectId(id), styleName, new ObjectId(stylistId), serviceType, styleColor, styleMaterial, styleBase, styleImpression, socialScene, stylePic, description, new Date, true)
    } {
      nail => Some(nail.id.toString, nail.styleName, nail.stylistId.toString, nail.serviceType, nail.styleColor, nail.styleMaterial, nail.styleBase, nail.styleImpression, nail.socialScene, nail.stylePic, nail.description)
    }
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
      val searchParaForNail = new SearchPara(None, myCity, "all", "all", "Nail", List(), List(), List(), List(), List())
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
  def getNailsByRanking(styleImpression: String) = StackAction {
    implicit request =>
      val user = loggedIn
      var nailAllInfo: List[NailWithAllInfo] = Nil
      //通过页面选择的发型长度字段的值，来进行不同的检索，all为不以长度为检索字段
      if (styleImpression.equals("all")) {
        nailAllInfo = Nail.findByRanking()
      } else {
        nailAllInfo = Nail.findByRankingAndImpression(styleImpression)
      }
      Ok(html.nailCatalog.general.nailRankingResultPage(nailSearchForm, nailAllInfo, Nail.findParaAll("Manicures"), user))
  }

}
