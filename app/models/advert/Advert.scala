package models

import play.api.Play.current
import java.util.Date
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import com.meifannet.framework.db._

case class Advert(
	 id: ObjectId = new ObjectId,	
         description: String,     // advert name.
         advertContents: String,  // advert contents.
         thumbnailImg: String,    // 
         filePath: String,        // if we save the advertisements as static files, we should use the path to show them.
         advertType: String,      // TODO issued by our site or by one salon?
         salonId: ObjectId,       // TODO with the salonId, U can do much more thing, say get the geo area of this advert... 
         isOnService: Boolean,    // is this advert in the status of service? say, if U pay the money....
         issueStartDate: Date,
         issueEndDate: Date       // the interval this advert be issue.
         )

object Advert extends AdvertDAO

trait AdvertDAO extends MeifanNetModelCompanion[Advert]{

  val dao = new MeifanNetDAO[Advert](collection = loadCollection()){}

}
