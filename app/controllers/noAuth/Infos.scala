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

//    /**
//     * Get All the infos.
//     */
//    def getAllInfos() = Action {
//        val infos: List[Info] = Info.findAll().toList
////        Ok(html.question.general.overview(quests))
//        Ok("")
//    }

}
