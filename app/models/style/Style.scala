package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.Context
import mongoContext._
import models.Style._
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


object StyleDAO extends SalatDAO[Style, ObjectId](
  collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Style"))


object Style {

    def findAll(): List[Style] = {
        StyleDAO.find(MongoDBObject.empty).toList
    }

    def findById(id: ObjectId): Option[Style] = {
        StyleDAO.findOne(MongoDBObject("_id" -> id))
    }

    def findByStylistId(stylistId: ObjectId): List[Style] = {
      StyleDAO.find(DBObject("stylistId" -> stylistId)).toList
    }
    
    def create(style: Style): Option[ObjectId] = {
        StyleDAO.insert(
            Style(
                styleName = style.styleName,
                stylistId = style.stylistId,
                stylePic = style.stylePic,
                styleImpression = style.styleImpression,
			    serviceType = style.serviceType,
			    styleLength = style.styleLength,
			    styleColor = style.styleColor,
			    styleAmount = style.styleAmount,
			    styleQuality = style.styleQuality,
			    styleDiameter = style.styleDiameter,
			    faceShape = style.faceShape,
			    description = style.description,
			    consumerAgeGroup = style.consumerAgeGroup,
			    consumerSex = style.consumerSex,
			    consumerSocialStatus = style.consumerSocialStatus,
			    createDate = style.createDate,
			    isValid = style.isValid
            )
        )
    }

    def save(style: Style) = {
        StyleDAO.save(
            Style(
		id = style.id,
                styleName = style.styleName,
                stylistId = style.stylistId,
                stylePic = style.stylePic,
                styleImpression = style.styleImpression,
			    serviceType = style.serviceType,
			    styleLength = style.styleLength,
			    styleColor = style.styleColor,
			    styleAmount = style.styleAmount,
			    styleQuality = style.styleQuality,
			    styleDiameter = style.styleDiameter,
			    faceShape = style.faceShape,
			    description = style.description,
			    consumerAgeGroup = style.consumerAgeGroup,
			    consumerSex = style.consumerSex,
			    consumerSocialStatus = style.consumerSocialStatus,
			    createDate = style.createDate,
			    isValid = style.isValid
            )
        )
    }

   def delete(id: String) {	
        StyleDAO.remove(MongoDBObject("_id" -> new ObjectId(id)))
    }
   
   def findByPara(style: models.Style) : List[Style] = {
        StyleDAO.find($and("styleDiameter" $all style.styleDiameter ,"styleImpression" $in style.styleImpression,"faceShape" $in style.faceShape)).toList	
//        StyleDAO.find($and("styleDiameter" $all List("粗","细") ,"styleImpression" $in List("清新","舒适"),com.mongodb.casbah.commons.Imports.DBObject("styleLength" -> "短"))).toList
   }
   
   def updateTest(style: models.Style) = {
	   StyleDAO.update(MongoDBObject("_id" -> style.id), MongoDBObject("$set" -> (
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
