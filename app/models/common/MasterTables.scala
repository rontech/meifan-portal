package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.Context 
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import java.util.Date
import com.meifannet.framework.db._


/**
 * [Master Table]
 */
case class Industry (
    id: ObjectId = new ObjectId,
    industryName: String
)

object Industry extends MeifanNetModelCompanion[Industry]{
  val dao = new MeifanNetDAO[Industry](collection = loadCollection()){}
  
  def findById(id: ObjectId): Option[Industry] = dao.findOne(MongoDBObject("_id" -> id))
  
  def findAllIndustryName = dao.find(MongoDBObject.empty).toList.map {
		industry =>industry.industryName
  }
  
}

/**
 * [Master Table]
 */
case class PictureUse(
   id: ObjectId = new ObjectId,
   picUseName: String,
   division: Int
)

object PictureUse extends MeifanNetModelCompanion[PictureUse]{

  val dao = new MeifanNetDAO[PictureUse](collection = loadCollection()){}
  
}



