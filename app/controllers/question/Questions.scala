package controllers

import play.api._
import play.api.mvc._
import models.Style
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date

// TODO
// should be moved to under the noAuth folder.

object Questions extends Controller {

    /**
     * Get the required question.
     */
     def getOneQuestion(qId: ObjectId) = Action {
        val quests: List[Question] = Question.findOneById(qId).toList
        Ok(html.question.general.overview(quests))
    }

    /**
     * Get All the questions.
     */
    def getAllQuestions() = Action {
        val quests: List[Question] = Question.findAll().toList
        Ok(html.question.general.overview(quests))
    }

}
