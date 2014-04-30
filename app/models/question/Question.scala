package models

import mongoContext._
import se.radley.plugin.salat.Binders._
import java.util.Date
import com.meifannet.framework.db._



case class Question(
    id: ObjectId = new ObjectId,   	
    questName: String,                  
    questContent: String,                  
    questedDate: Date = new Date,
    questedNum: Int = 1,
    isValid: Boolean = true
)


object Question extends MeifanNetModelCompanion[Question] {

    val dao = new MeifanNetDAO[Question](collection = loadCollection()){}
 
} 

