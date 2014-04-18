package models

import play.api.Play.current
import java.util.Date
import com.novus.salat.dao._
import com.mongodb.casbah.query.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.mongodb.casbah.WriteConcern

case class GeneralSearch(
  id: ObjectId,
  keyword: String,
  searchDiv: Option[String],    // search division: search for "hairsalon", "hairstyle", "nailsalon", "nailstyle" ......
  hitTimes: Long,
  isValid: Boolean
)


object GeneralSearch extends ModelCompanion[GeneralSearch, ObjectId] {

  val dao = new SalatDAO[GeneralSearch, ObjectId](collection = mongoCollection("GeneralSearch")) {}

}

