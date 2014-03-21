package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.Context
import mongoContext._
import java.util.Date
import models._


case class Stylist(
    id: ObjectId = new ObjectId,
    publicId: ObjectId,
    workYears: Int,
    position: Seq[IndustryAndPosition],
    goodAtImage: Seq[String],
    goodAtStatus: Seq[String],
    goodAtService: Seq[String],
    goodAtUser: Seq[String],
    goodAtAgeGroup: Seq[String],
    myWords: String,
    mySpecial: String,
    myBoom: String,
    myPR: String,
    myPics: Option[Seq[OnUsePicture]],
    isVarified: Boolean,
    isValid: Boolean
)

case class StylistApply(
	stylist: Stylist,
	salonId: ObjectId
)

object Stylist extends StylistDAO

trait StylistDAO extends ModelCompanion[Stylist, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Stylist")
  
  val dao = new SalatDAO[Stylist, ObjectId](collection){}
 
  	/**
     *  根据salonId查找这个店铺所有技师
     */
    def findBySalon(salonId: ObjectId): Seq[Stylist] = {
      var stylists: Seq[Stylist] = Nil
      val applyRe = SalonAndStylist.findBySalonId(salonId)
      applyRe.map{app =>
        val stylist = dao.findOne(DBObject("_id" -> app.stylistId))
        stylist match {
          case Some(sty) => stylists :: List(sty)
          case _ => stylists
        }
      }
      stylists
    }
    
}