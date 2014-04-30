package models

import play.api.Play.current
import play.api.PlayException
import com.meifannet.framework.db._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date

/**
 *
 */
case class IndustryAndPosition(
  id: ObjectId,
  positionName: String,
  industryName: String)

object IndustryAndPosition extends MeifanNetModelCompanion[IndustryAndPosition]{
  val dao = new MeifanNetDAO[IndustryAndPosition](collection = loadCollection()){}
}



/**
 * 
 */
case class Position(
  id: ObjectId,
  positionName: String
 )

object Position extends MeifanNetModelCompanion[Position]{
   val dao = new MeifanNetDAO[Position](collection = loadCollection()){}
}
