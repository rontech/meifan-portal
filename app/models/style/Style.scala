package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.Context
import mongoContext._
import com.mongodb.casbah.query.Imports._
import java.util.Date

case class Style(
    id: ObjectId = new ObjectId,
    styleName: String,
    stylistId: ObjectId,
    stylePic: List[String],
    styleImpression: List[String],
    serviceType: List[String],
    styleLength: List[String],
    styleColor: List[String],
    styleAmount: List[String],
    styleQuality: List[String],
    styleDiameter: List[String],
    faceShape: List[String],
    description: String,
    consumerAgeGroup: List[String],
    consumerSex: List[String],
    consumerSocialStatus: List[String],
    createDate: Date,
    isValid: Boolean
)

object Style extends StyleDAO

trait StyleDAO extends ModelCompanion[Style, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Style")

  val dao = new SalatDAO[Style, ObjectId](collection){}
  	
  def findByStylistId(stylistId: ObjectId): List[Style] = {
    	dao.find(DBObject("stylistId" -> stylistId)).toList
  }
    
  def findByPara(style: models.Style) : List[Style] = {
        dao.find($and("styleDiameter" $all style.styleDiameter ,"styleImpression" $in style.styleImpression,"faceShape" $in style.faceShape)).toList	
//        dao.find($and("styleDiameter" $all List("粗","细") ,"styleImpression" $in List("清新","舒适"),com.mongodb.casbah.commons.Imports.DBObject("styleLength" -> "短"))).toList
   }
   
   def updateTest(style: models.Style) = {
	   dao.update(MongoDBObject("_id" -> style.id), MongoDBObject("$set" -> (
			   MongoDBObject("styleName" -> "海贼王发型")++
			   MongoDBObject("styleLength" -> MongoDBList("中","长"))++
			   MongoDBObject("styleQuality" -> MongoDBList("硬"))
			   ))
	   )
   }
                   
   def findParaAll = {
	   val ParaStyleImpression : List[StyleImpression] = StyleImpression.findAll()
	   val ParaServiceType : List[ServiceType] = ServiceType.findAll().toList
	   val ParaStyleLength : List[StyleLength] = StyleLength.findAll()
	   val ParaStyleColor : List[StyleColor] = StyleColor.findAll()
	   val ParaStyleAmount : List[StyleAmount] = StyleAmount.findAll()
	   val ParaStyleQuality : List[StyleQuality] = StyleQuality.findAll()
	   val ParaStyleDiameter : List[StyleDiameter] = StyleDiameter.findAll()
	   val ParaFaceShape : List[FaceShape] = FaceShape.findAll()
	   val ParaConsumerAgeGroup : List[AgeGroup] = AgeGroup.findAll()
	   val ParaConsumerSex : List[Sex] = Sex.findAll()
	   val ParaConsumerSocialStatus : List[SocialStatus] = SocialStatus.findAll()
	   //val 
//	   val ParaAll: List[List[Object]] = List(ParaStyleImpression,ParaServiceType,ParaStyleLength,ParaStyleColor,ParaStyleAmount,ParaStyleQuality,ParaStyleDiameter,ParaFaceShape,ParaConsumerAgeGroup,ParaConsumerSex,ParaConsumerSocialStatus)
	   val ParaAll: List[List[Object]] = List(ParaStyleImpression,ParaServiceType,List("ParaStyleImpression","ParaServiceType"))
	 //  ParaAll.last.
	   print("www.wwww.wwww "+ParaAll.last)
//	   ParaAll.toList
	   //print("www.wwww.wwww "+ParaAll(2))
	   ParaAll
   }
}
