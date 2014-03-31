package controllers


import play.api.mvc._
import play.api.i18n.Messages
import com.mongodb.casbah.commons.Imports._
import models._


/**
 * The Navigation bar shown on a salon plain page.
 */
object SalonNavigation extends Controller {
    
    /*-------------------------
     * Common Functions. 
     -------------------------*/
    /**
     * Get the Navigation Bar of the Salon Main Page.
     */
    def getSalonTopNavBar = {
        val nav0 = (Messages("index.mainPage"), routes.Application.index.toString)
        val nav1 = (Messages("salon.salonMainPage"), routes.Salons.index.toString)
        nav0 :: nav1 :: Nil 
    }

    /**
     * Get the Navigation Bar of the Individual Salon page.
     */
     def getSalonNavBar(salon: Option[Salon]) = {
         val navBar: List[(String, String)] = salon match {
             case Some(sl) => {
                 // Province is not Null
                 //val nav2 = (Messages("province.provinceName." + sl.salonAddress.province), "")
                 val nav2 = List((sl.salonAddress.province, ""))

                 // The City May be Null when it is a [municipalities] like Beijing, Shanghai, Tianjin, Chongqing.
                 val nav3 = sl.salonAddress.city match {
                     //case Some(city) => (Messages("city.cityName." + city), "")
                     case Some(city) => List((city, ""))
                     case None => Nil  
                 }
                 
                 // The region May be Null.
                 val nav4 = sl.salonAddress.region match {
                     //case Some(region) => (Messages("region.regionName." + region), "")
                     case Some(region) => List((region, ""))
                     case None => Nil 
                 }

                 // The town May be Null.
                 val nav5 = sl.salonAddress.town match {
                     //case Some(town) => (Messages("town.townName." + town), "")
                     case Some(town) => List((town, ""))
                     case None => Nil 
                 }

                 // At last, The salon Name.
                 //val nav6 = (Messages(sl.salonName), "")
                 // If the salon Abbr Name is inputed, give priority to show it then the full name.
                 val abbrName = sl.salonNameAbbr match {
                     case Some(abbr) => abbr.toString()
                     case None => sl.salonName.toString()
                 }
                 val nav6 = List((Messages(abbrName), routes.Salons.getSalon(sl.id).toString()))
                 //List(nav2) ::: List(nav3) ::: List(nav4) ::: List(nav5) ::: List(nav6)
                 nav2 ::: nav3 ::: nav4 ::: nav5 ::: nav6 
            }

            case None => Nil
        } 

        // print(navBar)
        getSalonTopNavBar ::: navBar
    } 
}
