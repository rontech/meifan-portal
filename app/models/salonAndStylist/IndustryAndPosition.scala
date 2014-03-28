package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
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

object IndustryAndPosition extends IndustryAndPositionDAO

trait IndustryAndPositionDAO extends ModelCompanion[IndustryAndPosition, ObjectId] {
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("IndustryAndPosition")

  val dao = new SalatDAO[IndustryAndPosition, ObjectId](collection) {}

  collection.ensureIndex(DBObject("_id" -> 1), "id", unique = true)

}



/**
 * 
 */
case class Position(
  id: ObjectId,
  positionName: String
 )

object Position extends PositionDAO

trait PositionDAO extends ModelCompanion[Position, ObjectId] {
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("Position")

  val dao = new SalatDAO[Position, ObjectId](collection) {}

}
