package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date



case class Info(
    id: ObjectId = new ObjectId,   	
    title: String,                  
    content: String,                  
    authorId: ObjectId,
//    infoPics: ObjectId,
    infoPics: List[OnUsePicture],
    creteTime: Date = new Date,
    infoType : Int,
    isValid: Boolean = true
)


object Info extends ModelCompanion[Info, ObjectId] {

    def collection = MongoConnection()(
      current.configuration.getString("mongodb.default.db")
        .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings")))("Info")
 
    val dao = new SalatDAO[Info, ObjectId](collection) {}
    
    /**
     * meifan资讯
     */
    def findInfoForHome(num : Int) : List[Info]= {    
      val infoList= dao.find(MongoDBObject("isValid" -> true, "infoType" -> 1)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
      infoList
    }
    
    /**
     * 整形资讯
     */
    def findEstheInfo(num : Int) : List[Info]= {    
      val infoList= dao.find(MongoDBObject("isValid" -> true, "infoType" -> 2)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
      infoList
    }
    
    def updateInfoImage(info: Info, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> info.id, "infoPics.picUse" -> "logo"),
      MongoDBObject("$set" -> (MongoDBObject("infoPics.$.fileObjId" -> imgId))), false, true)
    }
} 

