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
case class Province(
    provinceId: ObjectId = new ObjectId,
    provinceName: String
)

object Province extends ModelCompanion[Province, ObjectId]{

  val dao = new SalatDAO[Province, ObjectId](collection = mongoCollection("Province")){}
  
}


/**
 * [Master Table]
 */
case class City(
    cityId: ObjectId = new ObjectId,
    cityName: String,
    provinceName: String                // Ref to the Master Table [Provice] on the [City] field.
)

object City extends ModelCompanion[City, ObjectId]{

  val dao = new SalatDAO[City, ObjectId](collection = mongoCollection("City")){}
  
}

/**
 * [Master Table]
 */
case class Region(
    regionId: ObjectId = new ObjectId,
    regionName: String,
    cityName: String                // Ref to the Master Table [Provice] on the [City] field.
)

object Region extends ModelCompanion[Region, ObjectId]{

  val dao = new SalatDAO[Region, ObjectId](collection = mongoCollection("Region")){}
  
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



