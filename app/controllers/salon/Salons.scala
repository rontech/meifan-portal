package controllers


import play.api.mvc._
import play.api.i18n.Messages
import com.mongodb.casbah.commons.Imports._
import models._
import jp.t2v.lab.play2.auth._
import scala.concurrent.ExecutionContext.Implicits.global

object Salons extends Controller with OptionalAuthElement with AuthConfigImpl{

    /*-------------------------
     * The Main Page of All Salon 
     -------------------------*/
    def index = StackAction{ implicit request =>
        val user = loggedIn
        Ok(views.html.salon.general.index(navBar = SalonNavigation.getSalonTopNavBar, user = user))
    }


    /*-------------------------
     * Individual Salon Infomations.
     * Include the Styles, Stylists, Coupons, Blogs, Comments..... 
     *------------------------*/
    def getSalon(salonId: ObjectId) = Action {
        val salon: Option[Salon] = Salon.findById(salonId)
        salon match {
            case Some(sl) => Ok(views.html.salon.store.salonInfoBasic(sl, SalonNavigation.getSalonNavBar(salon)))
            case _ => NotFound
        }
    }
  
     /**
      * Get All stylists of a salon.
      */
     def getAllStylists(salonId: ObjectId) = Action {
        val salon: Option[Salon] = Salon.findById(salonId)
        salon match {
            case Some(sl) => {
                val stylists = SalonAndStylist.getSalonStylistsInfo(salonId)
                // navigation bar
                val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.stylists"), routes.Salons.getAllStylists(sl.id).toString()))
                // Jump to stylists page in salon. 
                Ok(views.html.salon.store.salonInfoStylistAll(salon = sl, stylists = stylists, navBar = navBar))
            }
            case None => NotFound
        }

    }
  
    /**
      * Get a specified stylist from a salon.
      */
    def getOneStylist(salonId: ObjectId, stylistId: ObjectId) = Action { 
        // first, check that if the salon is exist.
        val salon = Salon.findById(salonId)
        salon match {
            // when salon is exist
            case Some(sl) => {
                // navigation bar
                val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.stylists"), routes.Salons.getAllStylists(sl.id).toString()))

                val stylist: Option[Stylist] = Stylist.findOneByStylistId(stylistId)
                stylist match {
                    // when stylist is exist, jump to the stylist page in salon.
                    case Some(st) => {
                        val dtl = Stylist.findStylistDtlByUserObjId(st.stylistId)
                        // check if the stylist has a work ship with the salon?
                        dtl.get.workInfo match {
                            case Some(ship) => {
                                // get Styles of a stylist.
                                val styles = Style.findByStylistId(stylistId)

                                // get a latest blog of a stylist.
                                val blgs = Blog.getBlogByUserId(dtl.get.basicInfo.userId)
                                val blog = if(blgs.length > 0) Some(blgs.head) else None 

                                // navigation item
                                val lastNav = List((dtl.get.basicInfo.nickName, ""))
                                Ok(views.html.salon.store.salonInfoStylist(salon = sl, stylist = dtl, 
                                        styles = styles, latestBlog = blog, navBar = navBar ::: lastNav))
                               
                            }
                            case None => {
                                // if not a worker of a salon. show nothing, for now, Jump to stylists page in salon. 
                                Ok(views.html.salon.store.salonInfoStylistAll(salon = sl, navBar = navBar))
                            }
                        }
                    }
                    // TODO, when stylist not exist, show nothing in salon.  
                    case None => Ok(views.html.salon.store.salonInfoStylist(salon = sl, stylist = None, navBar = navBar))
                 }
            }
            case None => NotFound // TODO
        } 
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
                    var style = Style.findByStylistId(stls.stylistId)
                    styles :::= style
                }
                // navigation bar
                val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.styles"), routes.Salons.getAllStyles(sl.id).toString()))
                // Jump to stylists page in salon. 
                Ok(views.html.salon.store.salonInfoStyleAll(salon = sl, styles = styles, navBar = navBar))
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
                        val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.styles"), routes.Salons.getAllStyles(sl.id).toString())) :::
                                List((st.styleName.toString(), ""))
                        // Third, we need to check the relationship between slaon and stylist to check if the style is active.
                        if(SalonAndStylist.isStylistActive(salonId, st.stylistId)) {
                            // If style is active, jump to the style show page in salon.
                            Ok(views.html.salon.store.salonInfoStyle(salon = sl, style = st, navBar = navBar))
                        } else {
                            // If style is not active, show nothing but must in the salon's page.
                            Ok(views.html.salon.store.salonInfoStyleAll(salon = sl, styles = Nil, navBar = navBar))
                        }
                   }
                    // If style is not exist, show nothing but must in the salon's page.
                    case None => {
                        val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.styles"), routes.Salons.getAllStyles(sl.id).toString()))
                        // TODO should with some message to show to user.
                        Ok(views.html.salon.store.salonInfoStyleAll(salon = sl, styles = Nil, navBar = navBar))
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
                var navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.couponMenus"), ""))
                // Jump
                Ok(views.html.salon.store.salonInfoCouponAll(salon = sl, Coupons.conditionForm.fill(couponSchDefaultConds), serviceTypes = srvTypes, coupons = coupons, menus = menus,
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
                      var navBar = SalonNavigation.getSalonNavBar(Some(s)) ::: List((Messages("salon.couponMenus"), ""))
                      Ok(views.html.salon.store.salonInfoCouponAll(s, conditionForm.fill(couponServiceType), serviceTypes, coupons, menus, servicesByTypes, navBar))
                  } 
                  case None => NotFound
              }
          })
     }
}
