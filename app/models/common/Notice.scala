package models

/**
 * Created by YS-HAN on 14/04/16.
 */

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._

case class Notice(
                      id : ObjectId,
                      title: String,
                      content: String,
                      author: String,
                      createdTime: Date,
                      isValid: Boolean
                      )

object Notice extends ModelCompanion[Notice, ObjectId] {

    val dao = new SalatDAO[Notice, ObjectId](collection = mongoCollection("Notice")) {}
}
