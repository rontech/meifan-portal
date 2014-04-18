package controllers.noAuth

import play.api.mvc._
import com.mongodb.casbah.commons.Imports._
import models._
import views._


object Infos extends Controller {

    /**
     * Get the required info.
     */
     def getOneInfo(infoId: ObjectId) = Action {
        val info: List[Info] = Info.findOneById(infoId).toList
        Ok(html.info.general.overview(info))
    }
    
    /**
     * get the IdUsePolicy info
     */
    def getIdUsePolicyInfo = Action {
        val info: List[Info] = Info.findIdUsePolicyInfo
        Ok(html.info.general.idUsePolicy(info)) //html 名字重命名  TODO 重用
    }
    
    /**
     * get the usePolicy info
     */
    def getUsePolicyInfo = Action {
        val info: List[Info] = Info.findUsePolicyInfo
        Ok(html.info.general.idUsePolicy(info)) //html 名字重命名  TODO 重用
    }
    
    /**
     * get the PrivacyPolicy info
     */
    def getSecurityPolicyInfo = Action {
        val info: List[Info] = Info.findSecurityPolicyInfo
        Ok(html.info.general.idUsePolicy(info)) //html 名字重命名  TODO 重用
    }
    
//    /**
//     * get the Ad info
//     */
//    def getAdInfo = Action {
//        val info: List[Info] = Info.findSecurityPolicyInfo
//        Ok(html.info.general.idUsePolicy(info)) //html 名字重命名  TODO 重用
//    }

//    /**
//     * Get All the infos.
//     */
//    def getAllInfos() = Action {
//        val infos: List[Info] = Info.findAll().toList
////        Ok(html.question.general.overview(quests))
//        Ok("")
//    }

}
