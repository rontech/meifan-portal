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


case class BasicInformation(
				id: ObjectId = new ObjectId,
				storeId: String,
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
 
case class DetailedInformation(
				id: ObjectId = new ObjectId,
				storeId: String,
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

object BasicInformation extends ModelCompanion[BasicInformation, ObjectId]{
  
   val dao = new SalatDAO[BasicInformation, ObjectId](collection = mongoCollection("basicInformation")){}
     
   def findOneBystoreId(storeId: String): Option[BasicInformation] = dao.findOne(MongoDBObject("storeId" -> storeId))
   
   def createBasicInformation(basicInformation:BasicInformation) = dao.save(basicInformation, WriteConcern.Safe)
}

object DetailedInformation extends ModelCompanion[DetailedInformation, ObjectId]{
  
   val dao = new SalatDAO[DetailedInformation, ObjectId](collection = mongoCollection("detailedInformation")){}
     
   def findOneBystoreId(storeId: String): Option[DetailedInformation] = dao.findOne(MongoDBObject("storeId" -> storeId))
   
   def createDetailedInformation(detailedInformation:DetailedInformation) = dao.save(detailedInformation, WriteConcern.Safe)
}
