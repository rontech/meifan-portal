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

  def delete(id: String) {	
        dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }
   
  def findByPara(style: models.Style) : List[Style] = {
        dao.find($and("styleDiameter" $in style.styleDiameter ,"styleImpression" $in style.styleImpression,"faceShape" $in style.faceShape)).toList	
  }
   
  def updateTest(style: models.Style) = {
	   dao.update(MongoDBObject("_id" -> style.id), MongoDBObject("$set" -> (
			   MongoDBObject("styleName" -> "海贼王发型")++
			   MongoDBObject("styleLength" -> MongoDBList("中","长"))++
			   MongoDBObject("styleQuality" -> MongoDBList("硬"))
			   ))
	   )
   }
   
   /**
    * 获取发型检索字段的主表信息 
   */                
   def findParaAll = {
	   val paraStyleImpression : List[StyleImpression] = StyleImpression.findAll()
	   var paraStyleImpressions: List[String] = Nil 
		   paraStyleImpression.map{para=>
		       paraStyleImpressions :::= List(para.styleImpression)
	   	   }
	   val paraServiceType : List[ServiceType] = ServiceType.findAll().toList
	   var paraServiceTypes: List[String] = Nil 
		   paraServiceType.map{para=>
		       paraServiceTypes :::= List(para.serviceTypeName)
	   	   }
	   val paraStyleLength : List[StyleLength] = StyleLength.findAll()
	   var paraStyleLengths: List[String] = Nil 
		   paraStyleLength.map{para=>
		       paraStyleLengths :::= List(para.styleLength)
	   	   }
	   val paraStyleColor : List[StyleColor] = StyleColor.findAll()
	   var paraStyleColors: List[String] = Nil 
		   paraStyleColor.map{para=>
		       paraStyleColors :::= List(para.styleColor)
	   	   }
	   val paraStyleAmount : List[StyleAmount] = StyleAmount.findAll()
	   var paraStyleAmounts: List[String] = Nil 
		   paraStyleAmount.map{para=>
		       paraStyleAmounts :::= List(para.styleAmount)
	   	   }
	   val paraStyleQuality : List[StyleQuality] = StyleQuality.findAll()
	   var paraStyleQualitys: List[String] = Nil 
		   paraStyleQuality.map{para=>
		       paraStyleQualitys :::= List(para.styleQuality)
	   	   }
	   val paraStyleDiameter : List[StyleDiameter] = StyleDiameter.findAll()
	   var paraStyleDiameters: List[String] = Nil 
		   paraStyleDiameter.map{para=>
		       paraStyleDiameters :::= List(para.styleDiameter)
	   	   }
	   val paraFaceShape : List[FaceShape] = FaceShape.findAll()
	   var paraFaceShapes: List[String] = Nil 
		   paraFaceShape.map{para=>
		   paraFaceShapes :::= List(para.faceShape)
	  }
	   val paraConsumerAgeGroup : List[AgeGroup] = AgeGroup.findAll()
	   var paraConsumerAgeGroups: List[String] = Nil 
		   paraConsumerAgeGroup.map{para=>
		       paraConsumerAgeGroups :::= List(para.ageGroup)
	   	   }
	   val paraConsumerSex : List[Sex] = Sex.findAll()
	   var paraConsumerSexs: List[String] = Nil 
		   paraConsumerSex.map{para=>
		       paraConsumerSexs :::= List(para.sex)
	   	   }
	   val paraConsumerSocialStatus : List[SocialStatus] = SocialStatus.findAll()
	   var paraConsumerSocialStatuss: List[String] = Nil 
		   paraConsumerSocialStatus.map{para=>
		       paraConsumerSocialStatuss :::= List(para.socialStatus)
	   	   }
	   val styleList = new StylePara(paraStyleImpressions,paraServiceTypes,paraStyleLengths,paraStyleColors,paraStyleAmounts,paraStyleQualitys,paraStyleDiameters,paraFaceShapes,paraConsumerAgeGroups,paraConsumerSexs,paraConsumerSocialStatuss)
	   styleList
   }
}

