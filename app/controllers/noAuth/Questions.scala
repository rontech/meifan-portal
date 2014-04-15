package controllers.noAuth

import play.api.mvc._
import com.mongodb.casbah.commons.Imports._
import models._
import views._

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
