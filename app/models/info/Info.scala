package models

import com.mongodb.casbah.commons.Imports._
import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date
import com.meifannet.framework.db._


// 资讯
case class Info(
    id: ObjectId = new ObjectId,
    title: String,
    content: String,
    authorId: ObjectId,
//    infoPics: ObjectId,
    infoPics: List[OnUsePicture],
    createTime: Date = new Date,
    infoType : Int, // 暂定 1：美范 ，2：美容整形，3：利用规约， 4： 使用须知 ，5： 隐私政策 //TODO 6: 广告
    isValid: Boolean = true
)


object Info extends MeifanNetModelCompanion[Info] {

    val dao = new MeifanNetDAO[Info](collection = loadCollection()){}

    /**
     * meifan资讯
     */
    def findInfoForHome(num : Int) : List[Info]= {
      val infoList= dao.find(MongoDBObject("isValid" -> true, "infoType" -> 1)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
      infoList.sortBy(info => info.createTime)
    }

    /**
     * 整形资讯
     */
    def findEstheInfo(num : Int) : List[Info]= {
      val infoList= dao.find(MongoDBObject("isValid" -> true, "infoType" -> 2)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
      infoList
    }

    /**
     * 取得ID利用规约
     */
    // TODO 网站footer信息的表结构可能会调整，暂定数据存在资讯中
    def findIdUsePolicyInfo : List[Info]= {
      val infoList= dao.find(MongoDBObject("isValid" -> true, "infoType" -> 3)).sort(MongoDBObject("createTime" -> -1)).toList
      infoList
    }

    /**
     * 取得ID利用规约
     */
    // TODO 网站footer信息的表结构可能会调整，暂定数据存在资讯中
    def findUsePolicyInfo : List[Info]= {
      val infoList= dao.find(MongoDBObject("isValid" -> true, "infoType" -> 4)).sort(MongoDBObject("createTime" -> -1)).toList
      infoList
    }

    /**
     * 取得ID利用规约
     */
    // TODO 网站footer信息的表结构可能会调整，暂定数据存在资讯中
    def findSecurityPolicyInfo : List[Info]= {
      val infoList= dao.find(MongoDBObject("isValid" -> true, "infoType" -> 5)).sort(MongoDBObject("createTime" -> -1)).toList
      infoList
    }

    /**
     * 取得ID利用规约
     */
    // TODO 网站footer信息的表结构可能会调整，暂定数据存在资讯中
    def findAdInfo(num : Int) : List[Info]= {
      val infoList= dao.find(MongoDBObject("isValid" -> true, "infoType" -> 6)).sort(MongoDBObject("createTime" -> -1)).limit(num).toList
      infoList
    }

    def updateInfoImage(info: Info, imgId: ObjectId) = {
      dao.update(MongoDBObject("_id" -> info.id, "infoPics.picUse" -> "logo"),
      MongoDBObject("$set" -> (MongoDBObject("infoPics.$.fileObjId" -> imgId))), false, true)
    }
}

