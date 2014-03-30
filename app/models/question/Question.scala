package models

import play.api.Play.current
import play.api.PlayException
import com.novus.salat.dao._
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date



case class Question(
    id: ObjectId = new ObjectId,   	
    questName: String,                  
    questContent: String,                  
    questedDate: Date = new Date,
    questedNum: Int = 1,
    isValid: Boolean = true
)


object Question extends ModelCompanion[Question, ObjectId] {

    def collection = MongoConnection()(
      current.configuration.getString("mongodb.default.db")
        .getOrElse(throw new PlayException(
          "Configuration error",
          "Could not find mongodb.default.db in settings")))("Question")
 
    val dao = new SalatDAO[Question, ObjectId](collection) {}
 
} 

