package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.data.Form
import play.api.data.Forms._


case class SalonInfoBasic(
				id: ObjectId = new ObjectId,
				salonId: String,
				storeNm: String,
				storeTyp: String,
				homePage: String,
				establishDate: Date,
				addProvince: String,
				addCity: String,
				addArea: String,
				addTown: String,
				add: String,
				email: String,
				registerTime: Date,
				image: String,
				qq: Int,
				North: Int,
				West: Int
                 )
 
case class SalonInfoDetail(
				id: ObjectId = new ObjectId,
				salonId: String,
				tel: String,
				contact: String,
				trafficDescribe: String,
				openTime: Date,
				closeTime: Date,
				restDate: Date,
				seatNum: Int,
				onlineOrd: Boolean,
				immediatelyOrd:Boolean,
				appointOrd:Boolean,
				onDateOrd: Boolean,
				pointOrd: Boolean,
				maleOrd: Boolean,
				posAvailibale: Boolean,
				parking: Boolean,
				wifi: Boolean
    )

object SalonInfoBasic extends ModelCompanion[SalonInfoBasic, ObjectId]{
  
   val dao = new SalatDAO[SalonInfoBasic, ObjectId](collection = mongoCollection("basicInformation")){}
     
   def findOneBysalonId(salonId: String): Option[SalonInfoBasic] = dao.findOne(MongoDBObject("salonId" -> salonId))
   
   def createBasicInformation(basicInformation:SalonInfoBasic) = dao.save(basicInformation, WriteConcern.Safe)
}

object SalonInfoDetail extends ModelCompanion[SalonInfoDetail, ObjectId] {

  val dao = new SalatDAO[SalonInfoDetail, ObjectId](collection = mongoCollection("detailedInformation")) {}

  def findById(id: ObjectId): Option[SalonInfoDetail] = { dao.findOne(MongoDBObject("_id" -> id)) }

  def findOneBysalonId(salonId: String): Option[SalonInfoDetail] = dao.findOne(MongoDBObject("salonId" -> salonId))

  def createDetailedInformation(detailedInformation: SalonInfoDetail) = dao.save(detailedInformation, WriteConcern.Safe)
}
