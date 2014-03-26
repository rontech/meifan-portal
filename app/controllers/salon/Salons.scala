package controllers


import play.api.mvc._
import play.api.i18n.Messages
import com.mongodb.casbah.commons.Imports._
import models._

object Salons extends Controller {

    /*-------------------------
     * The Main Page of All Salon 
     -------------------------*/
    def index = Action {
        Ok(views.html.salon.general.index(getSalonTopNavBar))
    }


    /*-------------------------
     * Individual Salon Infomations.
     * Include the Styles, Stylists, Coupons, Blogs, Comments..... 
     *------------------------*/
    def getSalon(salonId: ObjectId) = Action {
   
        val salon: Option[Salon] = Salon.findById(salonId)
        salon match {
            case Some(sl) => Ok(views.html.salon.store.salonInfoBasic(sl, getSalonNavBar(salon)))
            case _ => NotFound
        }
    }
  
    /**
     * Get all styles of a salon.
     */ 
    def getAllStyles(salonId: ObjectId) = Action {
        val salon: Option[Salon] = Salon.findById(salonId)
        salon match {
            case Some(sl) => {
                val stylists = SalonAndStylist.findBySalonId(sl.id)
                var styles: List[Style] = Nil
                stylists.map { stls =>
                    var style = Style.findByStylistId(stls.id)
                    styles :::= style
                }
                // 
                Ok(views.html.salon.store.salonInfoStyleAll(salon = sl, styles = styles, navBar = getSalonNavBar(salon)))
            }
            case None => NotFound 
        }
    }
    
    /**
     * Get a specified Style from a salon.
     */ 
    def getOneStyle(salonId: ObjectId, styleId: ObjectId) = Action {
        val style: Option[Style] = Style.findOneById(styleId)    
        // TODO
        // Need to consider the relationship between slaon and stylist to check if the style is active.
        style match {
            case Some(st) => {
                val salon: Option[Salon] = Salon.findById(salonId)
                salon match {
                    case Some(sl) => {
                       val navBar = getSalonNavBar(Some(sl)) ::: List((st.styleName.toString, ""))
                        // Jump to the show page.
                        Ok(views.html.salon.store.salonInfoStyle(salon = sl, style = st, navBar = navBar))
                    }
                    case None => NotFound 
                }
            }
            case None => NotFound
        } 
     }
 



    //-------------------
    // TODO
    //------------------

    def mySalon(salonId: ObjectId) = Action {
        // TODO
        val salonId = new ObjectId("530d7288d7f2861457771bdd")
        val salon: Option[Salon] = Salon.findById(salonId)
        salon match {
            case Some(sl) => Ok(views.html.salon.admin.mySalonHome(salon = sl))
            case None => NotFound
        }
    
    }
  
    def myStylist(salonId: ObjectId) = Action {
        Ok(views.html.salon.general.index(Nil))
    }
  
    def myReserv(salonId: ObjectId) = Action {
        Ok(views.html.salon.general.index(Nil))
    }
  
    def myComment(salonId: ObjectId) = Action {
        Ok(views.html.salon.general.index(Nil))
    }

    /*-------------------------
     * Common Functions. 
     -------------------------*/
    /**
     * Get the Navigation Bar of the Salon Main Page.
     */
    def getSalonTopNavBar = {
        val nav0 = (Messages("index.mainPage"), routes.Application.index.url.toString)
        val nav1 = (Messages("salon.salonMainPage"), routes.Salons.index.url.toString)
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
                 val nav2 = List((Messages("province.provinceName." + sl.salonAddress.province), ""))

                 // The City May be Null when it is a [municipalities] like Beijing, Shanghai, Tianjin, Chongqing.
                 val nav3 = sl.salonAddress.city match {
                     //case Some(city) => (Messages("city.cityName." + city), "")
                     case Some(city) => List((Messages("city.cityName." + city), ""))
                     case None => Nil  
                 }
                 
                 // The region May be Null.
                 val nav4 = sl.salonAddress.region match {
                     //case Some(region) => (Messages("region.regionName." + region), "")
                     case Some(region) => List((Messages("region.regionName." + region), ""))
                     case None => Nil 
                 }

                 // The town May be Null.
                 val nav5 = sl.salonAddress.town match {
                     //case Some(town) => (Messages("town.townName." + town), "")
                     case Some(town) => List((Messages("town.townName." + town), ""))
                     case None => Nil 
                 }

                 // At last, The salon Name.
                 //val nav6 = (Messages(sl.salonName), "")
                 // If the salon Abbr Name is inputed, give priority to show it then the full name.
		 val abbrName = sl.salonNameAbbr match {
		     case Some(abbr) => abbr.toString
		     case None => sl.salonName.toString
		 }
	         val nav6 = List((Messages(abbrName), routes.Salons.getAllStyles(sl.id).toString()))

                 //List(nav2) ::: List(nav3) ::: List(nav4) ::: List(nav5) ::: List(nav6)
                 nav2 ::: nav3 ::: nav4 ::: nav5 ::: nav6 
            }

            case None => Nil
        } 

        // print(navBar)
        getSalonTopNavBar ::: navBar
    } 

}
