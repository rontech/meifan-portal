package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.MongoConnection
import mongoContext._

/**
 * A All Info structs of stylist including belows
 *   1. basic info as a user.   
 *   2. info as a stylist.
 *   3. work info to a salon.
 */
case class StylistDetailInfo(basicInfo: User, stylistInfo: Option[Stylist], workInfo: Option[SalonAndStylist]) {
  def apply(basicinfo: User, stylist: Option[Stylist], work: Option[SalonAndStylist]) = new StylistDetailInfo(basicinfo, stylist, work) 
}

case class Stylist(
    id: ObjectId = new ObjectId,
    stylistId: ObjectId,
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
    isVerified: Boolean,
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
  
  collection.ensureIndex(DBObject("stylistId" -> 1), "userId", unique = true)
  
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
  
  def findUserName(stylistId: ObjectId): String = {
    val user = User.findOneById(stylistId)
    user match {
      case Some(u) => u.nickName
      case None => ""
    }
  }
  
  def findByUserId(stylistId: ObjectId) = {
    dao.findOne(MongoDBObject("stylistId" -> stylistId))
  }
  
  def findUser(stylistId: ObjectId): User = {
    val user = User.findOneById(stylistId)
    user match {
      case Some(u) => u
      case None => null
    }
  } 
  
  def findOneByStylistId(stylistId: ObjectId) = {
	  dao.findOne(MongoDBObject("stylistId" -> stylistId))
  }
  /**
   * get a stylist by its stylistId = the user Id.
   */
  def findStylistByPubId(pubId: ObjectId): Option[StylistDetailInfo] = {
    // first, check that if the stylist as a basic User is exist.
    val user = User.findOneById(pubId)
    user match {
      case Some(u) => {
        // get the stylist info.
        val stylist = findOneByUserPubId(pubId)

        // get the work info.(there is something we should pay attention to avoid errors.
        //    we should find the work info by Stylist table's real ObjectId not the stylistId.
        //    TODO, should be modified later.)
        val work = stylist match {
          case Some(st) => SalonAndStylist.findByStylistId(st.id)    // TODO?
          case None => None
        }
        
        // return 
        Some(StylistDetailInfo(u, stylist, work))
      }
      case None => None
    }
  }

  /**
   * get a stylist by its user id.
   */ 
  def findStylistByUserId(uid: String): Option[StylistDetailInfo] = {
    val user = User.findOneByUserId(uid)
    user match {
      case Some(u) => findStylistByPubId(u.id)
      case None => None
    }
  }

  /**
   *
   */
  def findOneByUserPubId(stylistId: ObjectId) :Option[Stylist] = {
    dao.findOne(DBObject("stylistId" -> stylistId))
  }

  
  
  def updateStylistInfo(stylist: Stylist, stylistId: ObjectId) = {
    dao.save(stylist.copy(stylistId = stylistId))
    
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
    	dao.update(MongoDBObject("stylistId" -> stylistId), MongoDBObject("$set" -> (MongoDBObject("isVarified" -> true)++
                MongoDBObject("isValid" -> true))))   
    }
    
    def updateImages(stylist: Stylist, imgId: ObjectId) = {
        dao.update(MongoDBObject("stylistId" -> stylist.stylistId, "myPics.picUse" -> "logo"), 
            MongoDBObject("$set" -> ( MongoDBObject("myPics.$.fileObjId" ->  imgId))),false,true)   
    }
    
    def countStyleByStylist(stylistId: ObjectId): Long = {
        Style.count(MongoDBObject("stylistId" -> stylistId))
    }
}