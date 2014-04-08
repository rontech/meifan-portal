package utils

import models._

object Tools{
    def getRestDays(restWay:String, restDay1:List[String], restDay2:List[String]) = {
        if(restWay.equals("Fixed")){
            RestDay(restWay, restDay1)
        }else{
            RestDay(restWay, restDay2)
        }
    }

    def setRestDays(restDays : RestDay) = {
        if(restDays.restWay.equals("Fixed")){
            (restDays.restWay, restDays.restDay, Nil)
        }else{
            (restDays.restWay, Nil, restDays.restDay)
        }
    }
}