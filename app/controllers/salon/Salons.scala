package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import com.mongodb.casbah.commons.Imports._
import models._
import views._
import java.util.Date

object Salons extends Controller {

    /*-------------------------
     * The Main Page of All Salon 
     -------------------------*/
    def index = Action {
        Ok(views.html.salon.general.index(getSalonTopNavBar()))
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
      * Get All stylists of a salon.
      */
     def getAllStylists(salonId: ObjectId) = Action {
        val salon: Option[Salon] = Salon.findById(salonId)
        val stylistsOfSalon: List[Stylist] = Stylist.findBySalon(salonId)    
         // TODO
        Ok(html.salon.store.salonInfoStylistAll(salon.get, stylistsOfSalon))
    }
  
    /**
      * Get a specified stylist from a salon.
      */
    def getOneStylist(salonId: ObjectId, stylistId: ObjectId) = Action { 
        val stylist: Option[Stylist] = Stylist.findOneById(stylistId)
        val salonId =  SalonAndStylist.findByStylistId(stylistId).get.salonId
        val salon: Option[Salon] = Salon.findById(salonId)
        Ok(html.salon.store.salonInfoStylist(salon.get, stylist.get))
    }
  
 def findStylistById(id: ObjectId) = Action {
    val stylist = Stylist.findOneById(id)
    val salonId =  SalonAndStylist.findByStylistId(id).get.salonId
    val salon = Salon.findById(salonId)
    val style = Style.findByStylistId(id)
    val user = Stylist.findUser(stylist.get.publicId)
    val blog = Blog.getBlogByUserId(user.userId).last
    Ok(html.salon.store.salonInfoStylistInfo(salon = salon.get, stylist = stylist.get, styles = style, blog = blog))
  }
 
    /**
     * Get all styles of a salon.
     */ 
    def getAllStyles(salonId: ObjectId) = Action {
        val salon: Option[Salon] = Salon.findById(salonId)
        salon match {
            case Some(sl) => {
                // find styles of all stylists via the relationship between [salon] and [stylist]. 
                val stylists = SalonAndStylist.findBySalonId(sl.id)
                var styles: List[Style] = Nil
                stylists.map { stls =>
                    var style = Style.findByStylistId(stls.id)
                    styles :::= style
                }
                // 
                Ok(html.salon.store.salonInfoStyleAll(salon = sl, styles = styles, navBar = getSalonNavBar(salon)))
            }
            case None => NotFound 
        }
    }
    
    /**
     * Get a specified Style from a salon.
     */ 
    def getOneStyle(salonId: ObjectId, styleId: ObjectId) = Action {
        // First of all, check that if the salon is acitve.
        val salon: Option[Salon] = Salon.findById(salonId)
        salon match {
            case Some(sl) => {
                // Second, check if the style is exist.
                val style: Option[Style] = Style.findOneById(styleId)    
                style match {
                    case Some(st) => {
                        val navBar = getSalonNavBar(Some(sl)) ::: List((Messages("salon.styles"), routes.Salons.getAllStyles(sl.id).toString())) :::
                                List((st.styleName.toString(), ""))
                        // Third, we need to check the relationship between slaon and stylist to check if the style is active.
                        if(SalonAndStylist.isStylistActive(salonId, st.stylistId)) {
                            // If style is active, jump to the style show page in salon.
                            Ok(html.salon.store.salonInfoStyle(salon = sl, style = st, navBar = navBar))
                        } else {
                            // If style is not active, show nothing but must in the salon's page.
                            Ok(html.salon.store.salonInfoStyleAll(salon = sl, styles = Nil, navBar = navBar))
                        }
                    }
                    // If style is not exist, show nothing but must in the salon's page.
                    case None => {
                        val navBar = getSalonNavBar(Some(sl)) ::: List((Messages("salon.styles"), routes.Salons.getAllStyles(sl.id).toString()))
                        // TODO should with some message to show to user.
                        Ok(html.salon.store.salonInfoStyleAll(salon = sl, styles = Nil, navBar = navBar))
                    }
                } 
            }
            // TODO: If salon is not active
            case None => NotFound 
        }
    }
 
    /**
     * Find All the coupons, menus, and services of a salon.  
     */
    def getAllCoupons(salonId: ObjectId) = Action {
        val salon: Option[Salon] = Salon.findById(salonId)
        salon match {
            case Some(sl) => {
                val coupons: List[Coupon] = Coupon.findBySalon(sl.id)
                val menus: List[Menu] = Menu.findBySalon(sl.id)
                val srvTypes: List[ServiceType] = ServiceType.findAll().toList
                val serviceTypeNames: List[String] = Service.getServiceTypeList
                val couponSchDefaultConds: CouponServiceType = CouponServiceType(Nil, Some("1"))
    
                var servicesByTypes: List[ServiceByType] = Nil
                for(serviceType <- serviceTypeNames) {
                    var servicesByType: ServiceByType = ServiceByType("", Nil)
                    val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = Service.getTypeListBySalonId(sl.id, serviceType))
                    servicesByTypes = y::servicesByTypes
                }

                // Navigation Bar
                var navBar = getSalonNavBar(Some(sl)) ::: List((Messages("salon.couponMenus"), ""))
                // Jump
                Ok(html.salon.store.salonInfoCouponAll(salon = sl, Coupons.conditionForm.fill(couponSchDefaultConds), serviceTypes = srvTypes, coupons = coupons, menus = menus,
                    serviceByTypes = servicesByTypes, navBar = navBar))
            }
            case None => NotFound
       }
    }

    /**
     * Find coupons & menus & services by conditions from a salon.
     */
    def getCouponsByCondition(salonId: ObjectId) = Action { implicit request =>
        import Coupons.conditionForm
        conditionForm.bindFromRequest.fold(
            errors => BadRequest(views.html.error.errorMsg(errors)),
        {
            serviceType =>
                var coupons: List[Coupon] = Nil
                var menus: List[Menu] = Nil
                var serviceTypeNames: List[String] = Nil
                var conditions: List[String] = Nil
                var servicesByTypes: List[ServiceByType] = Nil
                var typebySearchs: List[ServiceType] = Nil
                var couponServiceType: CouponServiceType = CouponServiceType(Nil, serviceType.subMenuFlg)

                for(serviceTypeOne <- serviceType.serviceTypes) {
                    conditions = serviceTypeOne.serviceTypeName::conditions
                    val serviceType: Option[ServiceType] = ServiceType.findOneByTypeName(serviceTypeOne.serviceTypeName)
                    serviceType match {
                        case Some(s) => typebySearchs = s::typebySearchs
                        case None => NotFound
                    }
                }
            
                couponServiceType = couponServiceType.copy(serviceTypes = typebySearchs)
            
                val serviceTypes: List[ServiceType] = ServiceType.findAll().toList
                if(serviceType.subMenuFlg == None) {
                  //coupons = Coupon.findContainCondtions(serviceTypes)
                } else {
                    if(serviceType.serviceTypes.isEmpty) {
                        coupons = Coupon.findBySalon(salonId)
                        menus = Menu.findBySalon(salonId)
                        serviceTypeNames = Service.getServiceTypeList
                        for(serviceType <- serviceTypeNames) {
                            var servicesByType: ServiceByType = ServiceByType("", Nil)
                            val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = Service.getTypeListBySalonId(salonId, serviceType))
                            servicesByTypes = y::servicesByTypes
                        }
                    } else {
                        coupons = Coupon.findContainCondtions(conditions)
                        menus = Menu.findContainCondtions(conditions)
                        for(serviceTypeOne <- serviceType.serviceTypes) {
                            var servicesByType: ServiceByType = ServiceByType("", Nil)
                            val y = servicesByType.copy(serviceTypeName = serviceTypeOne.serviceTypeName, serviceItems = Service.getTypeListBySalonId(salonId, serviceTypeOne.serviceTypeName))
                            servicesByTypes = y::servicesByTypes
                       }
                  }
              }

             val salon: Option[Salon] = Salon.findById(salonId)
              salon match {
                  case Some(s) => {
                      // Navigation Bar
                      var navBar = getSalonNavBar(Some(s)) ::: List((Messages("salon.couponMenus"), ""))
                      Ok(html.salon.store.salonInfoCouponAll(s, conditionForm.fill(couponServiceType), serviceTypes, coupons, menus, servicesByTypes, navBar))
                  } 
                  case None => NotFound
              }
          })
     }
     
    /*-------------------------
     * Common Functions. 
     -------------------------*/
    /**
     * Get the Navigation Bar of the Salon Main Page.
     */
    def getSalonTopNavBar() = {
        var nav0 = (Messages("index.mainPage"), routes.Application.index.toString())
        val nav1 = (Messages("salon.salonMainPage"), routes.Salons.index.toString())
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
        getSalonTopNavBar() ::: navBar 
    } 

}
