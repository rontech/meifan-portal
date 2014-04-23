package controllers.noAuth

import play.api.mvc._
import com.mongodb.casbah.commons.Imports._
import models._
import views._


object Notices extends Controller {

    /**
     * Get the required notice.
     */
     def getOneNotice(noticeId: ObjectId) = Action {
        val notice: List[Notice] = Notice.findOneById(noticeId).toList
        Ok(html.notice.general.overview(notice))
    }
    

}
