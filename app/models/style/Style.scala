package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import com.mongodb.casbah.query.Imports._
import java.util.Date

case class StylePara(
	styleImpression: List[String],
	serviceType: List[String],
	styleLength: List[String],
	styleColor: List[String],
	styleAmount: List[String],
	styleQuality: List[String],
	styleDiameter: List[String],
	faceShape: List[String],
	consumerAgeGroup: List[String],
	consumerSex: List[String],
	consumerSocialStatus: List[String]
) 
	
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

  def delete(id: ObjectId) {	
        dao.remove(MongoDBObject("_id" -> id))
  }
   
  def findByPara(style: Style) : List[Style] = {
        dao.find($and("styleDiameter" $in style.styleDiameter ,"styleImpression" $in style.styleImpression,"faceShape" $in style.faceShape)).toList	
  }
   
  def updateStyle(style: Style) = {
	   dao.update(MongoDBObject("_id" -> style.id), MongoDBObject("$set" -> (
			   MongoDBObject("styleName" -> style.styleName)++
			   MongoDBObject("stylePic" -> style.stylePic)++
			   MongoDBObject("styleLength" -> style.styleLength)++
			   MongoDBObject("styleColor" -> style.styleColor)++
			   MongoDBObject("styleImpression" -> style.styleImpression)++
			   MongoDBObject("serviceType" -> style.serviceType)++
			   MongoDBObject("styleAmount" -> style.styleAmount)++
			   MongoDBObject("styleQuality" -> style.styleQuality)++
			   MongoDBObject("styleDiameter" -> style.styleDiameter)++
			   MongoDBObject("faceShape" -> style.faceShape)++
			   MongoDBObject("consumerSocialStatus" -> style.consumerSocialStatus)++
			   MongoDBObject("consumerSex" -> style.consumerSex)++
			   MongoDBObject("consumerAgeGroup" -> style.consumerAgeGroup)++
			   MongoDBObject("description" -> style.description)
			   ))
	   )
   }
   
   /**
    * 获取发型检索字段的主表信息 
   */                
   def findParaAll = {
	   val paraStyleImpression = StyleImpression.findAll().toList
	   var paraStyleImpressions: List[String] = Nil 
		   paraStyleImpression.map{para=>
		       paraStyleImpressions :::= List(para.styleImpression)
	   	   }
	   val paraServiceType = ServiceType.findAll().toList
	   var paraServiceTypes: List[String] = Nil 
		   paraServiceType.map{para=>
		       paraServiceTypes :::= List(para.serviceTypeName)
	   	   }
	   val paraStyleLength = StyleLength.findAll().toList
	   var paraStyleLengths: List[String] = Nil 
		   paraStyleLength.map{para=>
		       paraStyleLengths :::= List(para.styleLength)
	   	   }
	   val paraStyleColor = StyleColor.findAll().toList
	   var paraStyleColors: List[String] = Nil 
		   paraStyleColor.map{para=>
		       paraStyleColors :::= List(para.styleColor)
	   	   }
	   val paraStyleAmount = StyleAmount.findAll().toList
	   var paraStyleAmounts: List[String] = Nil 
		   paraStyleAmount.map{para=>
		       paraStyleAmounts :::= List(para.styleAmount)
	   	   }
	   val paraStyleQuality = StyleQuality.findAll().toList
	   var paraStyleQualitys: List[String] = Nil 
		   paraStyleQuality.map{para=>
		       paraStyleQualitys :::= List(para.styleQuality)
	   	   }
	   val paraStyleDiameter = StyleDiameter.findAll().toList
	   var paraStyleDiameters: List[String] = Nil 
		   paraStyleDiameter.map{para=>
		       paraStyleDiameters :::= List(para.styleDiameter)
	   	   }
	   val paraFaceShape = FaceShape.findAll().toList
	   var paraFaceShapes: List[String] = Nil 
		   paraFaceShape.map{para=>
		   paraFaceShapes :::= List(para.faceShape)
	  }
	   val paraConsumerAgeGroup = AgeGroup.findAll().toList
	   var paraConsumerAgeGroups: List[String] = Nil 
		   paraConsumerAgeGroup.map{para=>
		       paraConsumerAgeGroups :::= List(para.ageGroup)
	   	   }
	   val paraConsumerSex = Sex.findAll().toList
	   var paraConsumerSexs: List[String] = Nil 
		   paraConsumerSex.map{para=>
		       paraConsumerSexs :::= List(para.sex)
	   	   }
	   val paraConsumerSocialStatus = SocialStatus.findAll().toList
	   var paraConsumerSocialStatuss: List[String] = Nil 
		   paraConsumerSocialStatus.map{para=>
		       paraConsumerSocialStatuss :::= List(para.socialStatus)
	   	   }
	   val styleList = new StylePara(paraStyleImpressions,paraServiceTypes,paraStyleLengths,paraStyleColors,paraStyleAmounts,paraStyleQualitys,paraStyleDiameters,paraFaceShapes,paraConsumerAgeGroups,paraConsumerSexs,paraConsumerSocialStatuss)
	   styleList
   }
}

