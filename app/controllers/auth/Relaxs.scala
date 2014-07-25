package controllers.auth

import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import play.api.data._
import play.api.data.Forms._
import views._
import java.util.Date
import mongoContext._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.commons.ValidBSONType.ObjectId
import se.radley.plugin.salat.Binders.ObjectId
import models.portal.common.OnUsePicture
import models.portal.relax._

object Relaxs extends MeifanNetCustomerOptionalApplication {

  /**
   * 休闲保健（Relax）项目新规form
   *
   * @param salonId salon表的Id
   * @param id relax表的主键
   * @return
   */
  def newRelaxPhotoForm(salonId: ObjectId, id: ObjectId = new ObjectId): Form[Relax] = Form(
    mapping(
      "id" -> ignored(id),
      "salonId" -> ignored(salonId),
      "styleName" -> text,
      "serviceType" -> list(text),
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
     (id, salonId, styleName, serviceType, stylePic, description)
     => Relax(id, salonId, styleName, serviceType, stylePic, description, new Date, true)
    } {
      relax => Some(relax.id, relax.salonId, relax.styleName, relax.serviceType, relax.stylePic, relax.description)
    }
  )

  /**
   * 休闲保健（Relax）项目更新form
   *
   * @param salonId salon表的Id
   * @param id relax表的主键
   * @return
   */
  def updateRelaxPhotoForm(salonId: ObjectId, id: ObjectId = new ObjectId): Form[Relax] = Form(
    mapping(
      "id" -> ignored(id),
      "salonId" -> ignored(salonId),
      "styleName" -> text,
      "serviceType" -> list(text),
      "stylePic" ->(list(mapping(
        "fileObjId" -> text,
        "picUse" -> text,
        "showPriority" -> optional(number),
        "description" -> optional(text)) {
        (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId(fileObjId), picUse, showPriority, description)
      } {
        onUsePicture => Some((onUsePicture.fileObjId.toString, onUsePicture.picUse, onUsePicture.showPriority, onUsePicture.description))
      })),
      "description" -> text,
      "createDate" -> date){
      (id, salonId, styleName, serviceType, stylePic, description, createDate)
      => Relax(id, salonId, styleName, serviceType, stylePic, description, createDate, true)
    } {
      relax => Some(relax.id, relax.salonId, relax.styleName, relax.serviceType, relax.stylePic, relax.description, relax.createDate)
    }
  )

}
