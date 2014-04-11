package models

import play.api.Play.current
import play.api.PlayException

import com.novus.salat._
import com.novus.salat.dao._
import com.novus.salat.Context 

import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection


import mongoContext._

import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import java.util.Date



/**
 * [Master Table]
 */
case class Industry (
    id: ObjectId = new ObjectId,
    industryName: String
)

object Industry extends ModelCompanion[Industry, ObjectId]{
  val dao = new SalatDAO[Industry, ObjectId](collection = mongoCollection("Industry")){}
  
  def findById(id: ObjectId): Option[Industry] = dao.findOne(MongoDBObject("_id" -> id))
  
}

/**
 * [Master Table]
 */
case class PictureUse(
   id: ObjectId = new ObjectId,
   picUseName: String,
   division: Int
)

object PictureUse extends ModelCompanion[PictureUse, ObjectId]{

  val dao = new SalatDAO[PictureUse, ObjectId](collection = mongoCollection("PictureUse")) {}
  
}



