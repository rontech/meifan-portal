package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat._
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.Context
import mongoContext._
import java.util.Date
import models._


case class Stylist(
    id: ObjectId = new ObjectId,
    publicId: ObjectId,
    workYears: Int,
    position: List[IndustryAndPosition],
    goodAtImage: List[String],
    goodAtStatus: List[String],
    goodAtService: List[String],
    goodAtUser: List[String],
    goodAtAgeGroup: List[String],
    myWords: String,
    mySpecial: String,
    myBoom: String,
    myPR: String,
    myPics: List[OnUsePicture],
    isVarified: Boolean,
    isValid: Boolean
)

case class GoodAtStyle(
    position: List[String],
    industry: List[String],
	goodAtImage: List[String],
    goodAtStatus: List[String],
    goodAtService: List[String],
    goodAtUser: List[String],
    goodAtAgeGroup: List[String]	
)

case class StylistApply(
	stylist: Stylist,
	salonId: ObjectId
)

object Stylist extends StylistDAO

trait StylistDAO extends ModelCompanion[Stylist, ObjectId]{
  def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings"))
  )("Stylist")
  
  val dao = new SalatDAO[Stylist, ObjectId](collection){}
  
  def findGoodAtStyle: GoodAtStyle  = {
	   val position = Position.findAll().toList
	   var positions: List[String] = Nil 
		   position.map{para=>
		       positions :::= List(para.positionName)
	   	   }
	   
	   val industry = Industry.findAll.toList
	   var industrys: List[String] = Nil
	   industry.map{para=>
		       industrys :::= List(para.industryName)
	   	   }
    
       val paraStyleImpression = StyleImpression.findAll().toList
	   var paraStyleImpressions: List[String] = Nil 
		   paraStyleImpression.map{para=>
		       paraStyleImpressions :::= List(para.styleImpression)
	   	   }

       val paraConsumerSocialStatus = SocialStatus.findAll().toList
	   var paraConsumerSocialStatuss: List[String] = Nil 
		   paraConsumerSocialStatus.map{para=>
		       paraConsumerSocialStatuss :::= List(para.socialStatus)
	   	   }


       val paraConsumerSex = Sex.findAll().toList
	   var paraConsumerSexs: List[String] = Nil 
		   paraConsumerSex.map{para=>
		       paraConsumerSexs :::= List(para.sex)
	   	   }

       val paraConsumerAgeGroup = AgeGroup.findAll().toList
	   var paraConsumerAgeGroups: List[String] = Nil 
		   paraConsumerAgeGroup.map{para=>
		       paraConsumerAgeGroups :::= List(para.ageGroup)
	   	   }

 	   val paraServiceType = ServiceType.findAll().toList
	   var paraServiceTypes: List[String] = Nil 
		   paraServiceType.map{para=>
		       paraServiceTypes :::= List(para.serviceTypeName)
	   	   }
 	   val goodAtStyle = new GoodAtStyle(positions , industrys, paraStyleImpressions, paraConsumerSocialStatuss, 
 	       paraServiceTypes, paraConsumerSexs, paraConsumerAgeGroups)
    goodAtStyle
  }
  
  def findUserName(publicId: ObjectId): String = {
    val user = User.findOneById(publicId)
    user match {
      case Some(u) => u.nickName
      case None => ""
    }
  }
  
  def findByUserId(publicId: ObjectId) = {
    dao.findOne(MongoDBObject("publicId" -> publicId))
  }
  
  def findUser(publicId: ObjectId): User = {
    val user = User.findOneById(publicId)
    user match {
      case Some(u) => u
      case None => null
    }
  } 
  
  def updateImages(stylist: Stylist, imageId: ObjectId) = {
    dao.update(MongoDBObject("_id" -> stylist.id), MongoDBObject("$set" -> (MongoDBObject("myPics" ->  MongoDBList(imageId, "head", 1, None)))))
  }
  
  def updateStylistInfo(stylist: Stylist, imageId: ObjectId) = {
    dao.save(stylist.copy(id = stylist.id))
    
  }
 
  	/**
     *  根据salonId查找这个店铺所有技师
     */
    def findBySalon(salonId: ObjectId): List[Stylist] = {
      var stylists: List[Stylist] = Nil
      val applyRe = SalonAndStylist.findBySalonId(salonId)
      applyRe.map{app =>
        val stylist = dao.findOne(DBObject("_id" -> app.stylistId))
        stylist match {
          case Some(sty) => stylists :::= List(sty)
          case _ => stylists
        }
      }
      stylists
    }
  	
    def mySalon(stylistId: ObjectId): Salon = {
	    val releation = SalonAndStylist.findByStylistId(stylistId)
	    releation match {
	      case Some(re) => {
	        val salon = Salon.findById(re.salonId)
	        salon.get
	        
	      }
	      case None => null
	    }
  	}
    
    def becomeStylist(stylistId : ObjectId) =  {
    	dao.update(MongoDBObject("_id" -> stylistId), MongoDBObject("$set" -> (MongoDBObject("isVarified" -> true)++
                MongoDBObject("isValid" -> true))))   
    }
}

