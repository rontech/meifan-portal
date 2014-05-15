/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
package models

import play.api.Play.current
import java.util.Date
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.PlayException
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import com.meifannet.framework.db._
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
  salonAccountId: String
)

object Stylist extends MeifanNetModelCompanion[Stylist]{
  val dao = new MeifanNetDAO[Stylist](collection = loadCollection()){}
  loadCollection().ensureIndex(DBObject("stylistId" -> 1), "stylistId", unique = true)
  
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
  
  /**
   *
   */ 
  def findUserName(stylistId: ObjectId): String = {
   findUser(stylistId).nickName
  }
  
  def findUser(stylistId: ObjectId): User = {
   // The stylist must as a basic user, so it absolutely exist.
   User.findOneById(stylistId).get
  } 
  
  /**
   * Find a Stylist by its user.id[ObjectId]
   */
  def findOneByStylistId(stylistId: ObjectId) = {
   dao.findOne(MongoDBObject("stylistId" -> stylistId))
  }

  /**
   * Find a Stylist by its user.id[ObjectId]
   */
  override
  def findOneById(stylistId: ObjectId) = {
   dao.findOne(MongoDBObject("stylistId" -> stylistId))
  }

  /**
   * Find a Stylist by its user.id[ObjectId]
   */
  def findOneByUserId(userId: String) = {
   val user = User.findOneByUserId(userId)
   user match {
     case None => None
     case Some(usr) => dao.findOne(MongoDBObject("stylistId" -> usr.id))
   }
  }



  /*--------------------------------
   * Methods for a full stylist info
   * with the basic user info, the stylist info, and 
   * the work relationship with a salon.
   ---------------------------------*/
  /**
   * get a stylist by its stylistId = the user Id.
   */
  def findStylistDtlByUserObjId(userObjId: ObjectId): Option[StylistDetailInfo] = {
  // first, check that if the stylist as a basic User is exist.
  val user = User.findOneById(userObjId)
  user match {
   case Some(u) => {
    // get the stylist info.
    val stylist = Stylist.findOneByStylistId(userObjId)

    // get the work info.(there is something we should pay attention to avoid errors.
    // NOTICE:  we should find the work info by Stylist table's real ObjectId not the publicId.
    val work = stylist match {
     case Some(st) => SalonAndStylist.findByStylistId(st.stylistId)    // st.stylistId = userObjId
     case None => None
    }
    
    // return 
    Some(StylistDetailInfo(u, stylist, work))
   }
   case None => None
  }
  }

  /**
   * If, we want to use the user.userId not the user.(objectId)id to find the 
   *     stylist detail info, use this method. 
   */ 
  def findStylistDtlByUserId(uid: String): Option[StylistDetailInfo] = {
  val user = User.findOneByUserId(uid)
  user match {
   case Some(u) => findStylistDtlByUserObjId(u.id)
   case None => None
  }
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
    val stylist = dao.findOne(DBObject("stylistId" -> app.stylistId))
    stylist match {
     case Some(sty) => stylists :::= List(sty)
     case _ => stylists
    }
   }
   stylists
  }
   
  /**
   * get the slaon info of a stylist base on the relationship betweeen
   *     the slaon and stylist.
   */
  def mySalon(stylistId: ObjectId): Salon = {
   val releation = SalonAndStylist.findByStylistId(stylistId)
   releation match {
    case Some(re) => {
     val salon = Salon.findOneById(re.salonId)
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
    Style.count(MongoDBObject("stylistId" -> stylistId, "isValid" -> true))
  }
  
  def isOwner(stylistId: ObjectId)(user:User) : Future[Boolean] = Future{User.findOneById(stylistId).map(_ == user).get}
  
  def findRecommendStylists:List[Stylist] = {
    var stylists:List[Stylist] = Nil
    SalonAndStylist.findAll.toList.map{releation =>
     if(releation.isValid){
       val stylist = Stylist.findOneByStylistId(releation.stylistId).get
       stylists :::= List(stylist)
       if(stylists.length == 4)
        return stylists
     }else Nil 
    }
    stylists
  }
}
