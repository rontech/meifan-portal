package models

/**
 * Created by YS-HAN on 14/04/16.
 */

import play.api.Play.current
import java.util.Date
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.meifannet.framework.db._

case class Notice(
                      id : ObjectId,
                      title: String,
                      content: String,
                      author: String,
                      createdTime: Date,
                      isValid: Boolean
                      )

object Notice extends MeifanNetModelCompanion[Notice] {

    val dao = new MeifanNetDAO[Notice](collection = loadCollection()){}
}
