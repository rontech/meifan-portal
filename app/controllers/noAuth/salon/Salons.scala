/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
package controllers.noAuth

import play.api.mvc._
import play.api.i18n.Messages
import jp.t2v.lab.play2.auth._
import java.util.Date
import java.util.Calendar
import controllers._
import models._
import utils._
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import com.mongodb.casbah.WriteConcern
import org.mindrot.jbcrypt.BCrypt
import controllers.auth.Coupons
import views.html
import utils.Const._
import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import models.portal.salon._
import models.portal.common._
import models.portal.salon.WorkTime
import models.portal.salon.SalonAccount
import models.portal.salon.BriefIntroduction
import models.portal.salon.Contact
import models.portal.common.OptContactMethod
import models.portal.common.Address
import models.portal.search.{SortByConditions, SeatNums, PriceRange, SearchParaForSalon}
import models.portal.industry.Industry
import models.portal.relation.SalonAndStylist
import models.portal.stylist.Stylist
import models.portal.style.Style
import models.portal.blog.Blog
import models.portal.coupon.{CouponServiceType, Coupon}
import models.portal.menu.Menu
import models.portal.service.{ServiceByType, Service, ServiceType}
import models.portal.nail.Nail

object Salons extends MeifanNetCustomerOptionalApplication {

  //沙龙注册Form
  val salonRegister: Form[Salon] = Form(
    mapping(
      "salonAccount" -> mapping(
        "accountId" -> text,
        "password" -> tuple(
          "main" -> text,
          "confirm" -> text)) {
          (accountId, password) => SalonAccount(accountId, BCrypt.hashpw(password._1, BCrypt.gensalt()))
        } {
          salonAccount => Some(salonAccount.accountId, (salonAccount.password, ""))
        },
      "salonName" -> text,
      "salonNameAbbr" -> optional(text),
      "salonIndustry" -> list(text),
      "homepage" -> optional(text),
      "salonAppeal" -> optional(text),
      "salonIntroduction" -> optional(mapping(
        "introHeader" -> text,
        "introContent" -> text,
        "introFooter" -> text)(BriefIntroduction.apply)(BriefIntroduction.unapply)),
      "contactMethod" -> mapping(
        "mainPhone" -> text,
        "contact" -> text,
        "email" -> text)(Contact.apply)(Contact.unapply),
      "optContactMethods" -> list(
        mapping(
          "contMethodType" -> text,
          "accounts" -> list(text))(OptContactMethod.apply)(OptContactMethod.unapply)),
      "establishDate" -> optional(date("yyyy-MM-dd")),
      "salonAddress" -> optional(mapping(
        "province" -> text,
        "city" -> optional(text),
        "region" -> optional(text),
        "town" -> optional(text),
        "addrDetail" -> text,
        "longitude" -> optional(bigDecimal),
        "latitude" -> optional(bigDecimal),
        "accessMethodDesc" -> text)(Address.apply)(Address.unapply)),
      "workTime" -> optional(mapping(
        "openTime" -> text,
        "closeTime" -> text)(WorkTime.apply)(WorkTime.unapply)),
      "restDays" -> optional(mapping(
        "restWay" -> text,
        "restDay1" -> list(text),
        "restDay2" -> list(text)) {
          (restWay, restDay1, restDay2) => Tools.getRestDays(restWay, restDay1, restDay2)
        } {
          restDay => Some(Tools.setRestDays(restDay))
        }),
      "seatNums" -> optional(number),
      "salonFacilities" -> optional(mapping(
        "canOnlineOrder" -> boolean,
        "canImmediatelyOrder" -> boolean,
        "canNominateOrder" -> boolean,
        "canCurntDayOrder" -> boolean,
        "canMaleUse" -> boolean,
        "isPointAvailable" -> boolean,
        "isPosAvailable" -> boolean,
        "isWifiAvailable" -> boolean,
        "hasParkingNearby" -> boolean,
        "parkingDesc" -> text)(SalonFacilities.apply)(SalonFacilities.unapply)),
      "salonPics" -> list(
        mapping(
          "fileObjId" -> text,
          "picUse" -> text,
          "showPriority" -> optional(number),
          "description" -> optional(text)) {
            (fileObjId, picUse, showPriority, description) => OnUsePicture(new ObjectId, picUse, Option(0), Option("none"))
          } {
            salonPics => Some(salonPics.fileObjId.toString(), salonPics.picUse, salonPics.showPriority, salonPics.description)
          }),
      "accept" -> checked("")) {
        (salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonAppeal, salonIntroduction, contactMethod, optContactMethods, establishDate, salonAddress,
        workTime, restDays, seatNums, salonFacilities, salonPics, _) =>
          Salon(new ObjectId, salonAccount, salonName, salonNameAbbr, salonIndustry, homepage, salonAppeal, salonIntroduction, contactMethod, optContactMethods, establishDate, salonAddress,
            workTime, restDays, seatNums, salonFacilities, salonPics, new Date(), new SalonStatus(1, true))
      } {
        salonRegister =>
          Some(salonRegister.salonAccount, salonRegister.salonName, salonRegister.salonNameAbbr, salonRegister.salonIndustry, salonRegister.homepage, salonRegister.salonAppeal, salonRegister.salonIntroduction, salonRegister.contactMethod,
            salonRegister.optContactMethods, salonRegister.establishDate, salonRegister.salonAddress,
            salonRegister.workTime, salonRegister.restDays, salonRegister.seatNums, salonRegister.salonFacilities, salonRegister.salonPics, false)
      })

  /**
   * 定义一个店铺查询数据表单
   */
  val salonSearchForm: Form[SearchParaForSalon] = Form(
    mapping(
      "keyWord" -> optional(text),
      "city" -> text,
      "region" -> text,
      "salonName" -> list(text),
      "salonIndustry" -> text,
      "serviceType" -> list(text),
      "priceRange" -> mapping(
        "minPrice" -> bigDecimal,
        "maxPrice" -> bigDecimal) {
        (minPrice, MaxPrice) => PriceRange(new ObjectId, minPrice, MaxPrice, "")
      } {
        priceRange => Some(priceRange.minPrice, priceRange.maxPrice)
      },
      "seatNums" -> mapping(
        "minNum" -> number,
        "maxNum" -> number)(SeatNums.apply)(SeatNums.unapply),
      "salonFacilities" -> mapping(
        "canOnlineOrder" -> boolean,
        "canImmediatelyOrder" -> boolean,
        "canNominateOrder" -> boolean,
        "canCurntDayOrder" -> boolean,
        "canMaleUse" -> boolean,
        "isPointAvailable" -> boolean,
        "isPosAvailable" -> boolean,
        "isWifiAvailable" -> boolean,
        "hasParkingNearby" -> boolean) {
          (canOnlineOrder, canImmediatelyOrder, canNominateOrder, canCurntDayOrder, canMaleUse, isPointAvailable, isPosAvailable, isWifiAvailable,
          hasParkingNearby) =>
            SalonFacilities(canOnlineOrder, canImmediatelyOrder, canNominateOrder, canCurntDayOrder, canMaleUse, isPointAvailable, isPosAvailable, isWifiAvailable,
              hasParkingNearby, "")

        } {
          salonFacilities =>
            Some((salonFacilities.canOnlineOrder, salonFacilities.canImmediatelyOrder, salonFacilities.canNominateOrder, salonFacilities.canCurntDayOrder, salonFacilities.canMaleUse, salonFacilities.isPointAvailable, salonFacilities.isPosAvailable, salonFacilities.isWifiAvailable,
              salonFacilities.hasParkingNearby))
        },
      "sortByConditions" -> mapping(
        "selSortKey" -> text,
        "sortByPopuAsc" -> boolean,
        "sortByReviewAsc" -> boolean,
        "sortByPriceAsc" -> boolean)(SortByConditions.apply)(SortByConditions.unapply))(SearchParaForSalon.apply)(SearchParaForSalon.unapply))

  /**
   * 沙龙注册处理
   * @return
   */
  def register() = Action { implicit request =>
    val industry = Industry.findAll.toList
    salonRegister.bindFromRequest.fold(
      errors => BadRequest(views.html.salon.salonManage.salonRegister(errors, industry)),
      {
        salonRegister =>
          Salon.save(salonRegister, WriteConcern.Safe)
          Redirect(auth.routes.Salons.salonLogin)
      })
  }

  /**
   * 跳转沙龙前台检索页面
   * 从session中获得城市，前面步骤保证必然会存在
   * 如果找不到城市默认给苏州
   * @return
   */
  def index = StackAction { implicit request =>
    val user = loggedIn
    var myCity = request.session.get("myCity").map{ city => city } getOrElse { "苏州" }

    val searchParaForSalon = new SearchParaForSalon(None, myCity, "all", List(), "Hairdressing", List(),
      PriceRange(new ObjectId, 0, 1000000, "Hairdressing"), SeatNums(0, 10000),
      SalonFacilities(false, false, false, false, false, false, false, false, false, ""),
      SortByConditions("price", false, false, true))
    val salons = Salon.findSalonBySearchPara(searchParaForSalon)
    val nav = "HairSalon"
    Ok(views.html.salon.general.index(nav = nav, navBar = SalonNavigation.getSalonTopNavBar, user = user, searchParaForSalon = searchParaForSalon, salons = salons))
  }

  /**
   * 跳转沙龙前台检索页面
   * 从session中获得城市，前面步骤保证必然会存在
   * 如果找不到城市默认给苏州
   * @return
   */
  def indexNail = StackAction { implicit request =>
    val user = loggedIn
    var myCity = request.session.get("myCity").map{ city => city } getOrElse { "苏州" }

    val searchParaForSalon = new SearchParaForSalon(None, myCity, "all", List(), "Manicures", List(),
      PriceRange(new ObjectId, 0, 1000000, "Hairdressing"), SeatNums(0, 10000),
      SalonFacilities(false, false, false, false, false, false, false, false, false, ""),
      SortByConditions("price", false, false, true))
    val salons = Salon.findSalonBySearchPara(searchParaForSalon)
    val nav = "NailSalon"
    Ok(views.html.salon.general.index(nav = nav, navBar = SalonNavigation.getSalonTopNavBar, user = user, searchParaForSalon = searchParaForSalon, salons = salons))
  }

  /*-------------------------
   * Individual Salon Infomations.
   * Include the Styles, Stylists, Coupons, Blogs, Comments..... 
   *------------------------*/
  def getSalon(salonId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon match {
      case Some(sl) => Ok(views.html.salon.store.salonContent(sl, SalonNavigation.getSalonNavBar(salon), user))
      case _ => NotFound
    }

  }

  /**
   * Get All stylists of a salon.
   */
  def getAllStylists(salonId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon match {
      case Some(sl) => {
        val stylists = SalonAndStylist.getSalonStylistsInfo(salonId)
        // navigation bar
        val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.stylists"), noAuth.routes.Salons.getAllStylists(sl.id).toString()))
        // Jump to stylists page in salon. 
        Ok(views.html.salon.store.salonInfoStylistAll(salon = sl, stylists = stylists, navBar = navBar, user = user))
      }
      case None => NotFound
    }

  }

  /**
   * Get a specified stylist from a salon.
   */
  def getOneStylist(salonId: ObjectId, stylistId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    // first, check that if the salon is exist.
    val salon = Salon.findOneById(salonId)
    salon match {
      // when salon is exist
      case Some(sl) => {
        // navigation bar
        val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.stylists"), noAuth.routes.Salons.getAllStylists(sl.id).toString()))

        val stylist: Option[Stylist] = Stylist.findOneByStylistId(stylistId)
        stylist match {
          // when stylist is exist, jump to the stylist page in salon.
          case Some(st) => {
            val dtl = Stylist.findStylistDtlByUserObjId(st.stylistId)
            // check if the stylist has a work ship with the salon?
            dtl.get.workInfo match {
              case Some(ship) => {
                // get a latest blog of a stylist.
                val blgs = Blog.getBlogByUserId(dtl.get.basicInfo.userId)
                val blog = if (blgs.length > 0) Some(blgs.head) else None
                // navigation item
                val lastNav = List((dtl.get.basicInfo.nickName, ""))
                Salon.findIndustryBySalonId(salonId) match {
                  case "Hairdressing" => {
                    // get Styles of a stylist.
                    val styles = Style.findByStylistId(stylistId)
                    Ok(views.html.salon.store.salonInfoStylist(salon = sl, stylist = dtl,
                      styles = styles,nails = Nil, latestBlog = blog, navBar = navBar ::: lastNav, user = user))
                  }
                  case "Manicures" => {
                    val nails = Nail.findByStylistId(stylistId)
                    Ok(views.html.salon.store.salonInfoStylist(salon = sl, stylist = dtl,
                      styles = Nil, nails = nails, latestBlog = blog, navBar = navBar ::: lastNav, user = user))
                  }
                }
              }
              case None => {
                // if not a worker of a salon. show nothing, for now, Jump to stylists page in salon. 
                Ok(views.html.salon.store.salonInfoStylistAll(salon = sl, navBar = navBar, user = user))
              }
            }
          }
          // TODO, when stylist not exist, show nothing in salon.  
          case None => Ok(views.html.salon.store.salonInfoStylist(salon = sl, stylist = None, navBar = navBar, user = user))
        }
      }
      case None => NotFound // TODO
    }
  }

  /**
   * Get all styles of a salon.
   */
  def getAllStyles(salonId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon match {
      case Some(sl) => {
        Salon.findIndustryBySalonId(salonId) match {
          case "Hairdressing" => {
            // find styles of all stylists via the relationship between [salon] and [stylist].
            val styles = Salon.getAllStyles(salonId)
            // navigation bar
            val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.styles"), noAuth.routes.Salons.getAllStyles(sl.id).toString()))
            // Jump to stylists page in salon.
            Ok(views.html.salon.store.salonInfoStyleAll(salon = sl, styles = styles, navBar = navBar, user = user))
          }
          case "Manicures" => {
            val nails = Salon.getAllNails(salonId)
            // navigation bar
            val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("nailSalon.styles"), noAuth.routes.Salons.getAllStyles(sl.id).toString()))
            Ok(views.html.salon.store.nailSalon.salonInfoNailAll(salon = sl, nails = nails, navBar = navBar, user = user))
          }
        }
      }
      case None => NotFound
    }
  }

  /**
   * Get a specified Style from a salon.
   */
  def getOneStyle(salonId: ObjectId, styleId: ObjectId) = StackAction { implicit request =>
    val user = loggedIn
    // First of all, check that if the salon is acitve.
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon match {
      case Some(sl) => {
        Salon.findIndustryBySalonId(salonId) match {
          case "Hairdressing" => {
            // Second, check if the style is exist.
            val style: Option[Style] = Style.findOneById(styleId)
            style match {
              case Some(st) => {
                val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.styles"), noAuth.routes.Salons.getAllStyles(sl.id).toString())) :::
                  List((st.styleName.toString(), ""))
                // Third, we need to check the relationship between slaon and stylist to check if the style is active.
                if (SalonAndStylist.isStylistActive(salonId, st.stylistId)) {
                  // If style is active, jump to the style show page in salon.
                  Ok(views.html.salon.store.salonInfoStyle(salon = sl, style = st, navBar = navBar, user = user))
                } else {
                  // If style is not active, show nothing but must in the salon's page.
                  Ok(views.html.salon.store.salonInfoStyleAll(salon = sl, styles = Nil, navBar = navBar, user = user))
                }
              }
              // If style is not exist, show nothing but must in the salon's page.
              case None => {
                val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.styles"), noAuth.routes.Salons.getAllStyles(sl.id).toString()))
                // TODO should with some message to show to user.
                Ok(views.html.salon.store.salonInfoStyleAll(salon = sl, styles = Nil, navBar = navBar, user = user))
              }
            }
          }
          case "Manicures" => {
            // Second, check if the nail is exist.
            val nail: Option[Nail] = Nail.findOneById(styleId)
            nail match {
              case Some(nail) => {
                val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("nailSalon.styles"), noAuth.routes.Salons.getAllStyles(sl.id).toString())) :::
                  List((nail.styleName.toString(), ""))
                // Third, we need to check the relationship between slaon and stylist to check if the style is active.
                if (SalonAndStylist.isStylistActive(salonId, nail.stylistId)) {
                  // If nail is active, jump to the nail show page in salon.
                  Ok(views.html.salon.store.nailSalon.salonInfoNail(salon = sl, nail = nail, navBar = navBar, user = user))
                } else {
                  // If nail is not active, show nothing but must in the salon's page.
                  Ok(views.html.salon.store.nailSalon.salonInfoNailAll(salon = sl, nails = Nil, navBar = navBar, user = user))
                }
              }
              // If nail is not exist, show nothing but must in the salon's page.
              case None => {
                val navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("nailSalon.styles"), noAuth.routes.Salons.getAllStyles(sl.id).toString()))
                Ok(views.html.salon.store.nailSalon.salonInfoNailAll(salon = sl, nails = Nil, navBar = navBar, user = user))
              }
            }
          }
        }

      }
      // TODO: If salon is not active
      case None => NotFound
    }
  }

  /**
   * Find All the coupons, menus, and services of a salon.
   * @param salonId 沙龙id
   * @param stylistId 技师id，为string形式，可为空
   * @param jumpType 跳转类型，优惠劵·菜单画面通用，如果类型为reservation，那么跳转到预约选择服务画面，否则为serviceOfSalon,跳转到优惠劵，菜单，服务显示画面
   * @return
   */
  def getAllCoupons(salonId: ObjectId, stylistId: String, styleId: String, jumpType: String) = StackAction { implicit request =>
    val user = loggedIn
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon match {
      case Some(sl) => {
        val coupons: List[Coupon] = Coupon.findValidCouponBySalon(sl.id)
        val menus: List[Menu] = Menu.findValidMenusBySalon(sl.id)
        val srvTypes: List[ServiceType] = ServiceType.findAllServiceTypes(sl.salonIndustry)
        val serviceTypeNames: List[String] = Service.getServiceTypeList
        val couponSchDefaultConds: CouponServiceType = CouponServiceType(Nil, Some("1"))

        var servicesByTypes: List[ServiceByType] = Nil
        for (serviceType <- serviceTypeNames) {
          var servicesByType: ServiceByType = ServiceByType("", Nil)
          // 如果根据服务名查找出来的服务为空，那么不添加到指定列表中
          var services: List[Service] = Service.getTypeListBySalonId(sl.id, serviceType)
          if (!services.isEmpty) {
            val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = services)
            servicesByTypes = y :: servicesByTypes
          } else {

          }
        }

        // 获取当前时间的前7天的日期，用于判断是否为新券还是旧券
        var beforeSevernDate = Calendar.getInstance()
        beforeSevernDate.setTime(new Date())
        beforeSevernDate.add(Calendar.DAY_OF_YEAR, -7)

        // Navigation Bar
        var navBar = SalonNavigation.getSalonNavBar(Some(sl)) ::: List((Messages("salon.couponMenus"), ""))
        // Jump
        if(jumpType == "serviceOfSalon") {
          Ok(views.html.salon.store.salonInfoCouponAll(salon = sl, Coupons.conditionForm.fill(couponSchDefaultConds), serviceTypes = srvTypes, coupons = coupons, menus = menus,
             serviceByTypes = servicesByTypes, beforeSevernDate.getTime(), navBar = navBar, user = user))
        } else {
          Ok(views.html.reservation.reservSelectService(sl, Coupons.conditionForm.fill(couponSchDefaultConds), serviceTypes = srvTypes, coupons = coupons, menus = menus,
            serviceByTypes = servicesByTypes, beforeSevernDate.getTime(), stylistId, styleId, user,  navBar = navBar))
        }
      }
      case None => NotFound
    }
  }

  /**
   * Find coupons & menus & services by conditions from a salon.
   */
  def getCouponsByCondition(salonId: ObjectId, stylistId: String, styleId: String) = StackAction { implicit request =>
    val user = loggedIn
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
          var serviceTypes: List[ServiceType] = Nil
          var couponServiceType: CouponServiceType = CouponServiceType(Nil, serviceType.subMenuFlg)

          for (serviceTypeOne <- serviceType.serviceTypes) {
            conditions = serviceTypeOne.serviceTypeName :: conditions
            val serviceType: Option[ServiceType] = ServiceType.findOneByTypeName(serviceTypeOne.serviceTypeName)
            serviceType match {
              case Some(s) => typebySearchs = s :: typebySearchs
              case None => NotFound
            }
          }

          couponServiceType = couponServiceType.copy(serviceTypes = typebySearchs)

          if (serviceType.subMenuFlg == None) {
            //coupons = Coupon.findContainConditions(serviceTypes)
          } else {
            if (serviceType.serviceTypes.isEmpty) {
              coupons = Coupon.findValidCouponBySalon(salonId)
              menus = Menu.findValidMenusBySalon(salonId)
              serviceTypeNames = Service.getServiceTypeList
              for (serviceType <- serviceTypeNames) {
                var servicesByType: ServiceByType = ServiceByType("", Nil)
                var services: List[Service] = Service.getTypeListBySalonId(salonId, serviceType)
                if (!services.isEmpty) {
                  val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = services)
                  servicesByTypes = y :: servicesByTypes
                } else {

                }
              }
            } else {
              coupons = Coupon.findValidCouponByConditions(conditions, salonId)
              menus = Menu.findValidMenusByConditions(conditions, salonId)
              for (serviceTypeOne <- serviceType.serviceTypes) {
                var servicesByType: ServiceByType = ServiceByType("", Nil)
                var services: List[Service] = Service.getTypeListBySalonId(salonId, serviceTypeOne.serviceTypeName)
                if (!services.isEmpty) {
                  val y = servicesByType.copy(serviceTypeName = serviceTypeOne.serviceTypeName, serviceItems = services)
                  servicesByTypes = y :: servicesByTypes
                } else {

                }
              }
            }
          }

          // 获取当前时间的前7天的日期，用于判断是否为新券还是旧券
          var beforeSevernDate = Calendar.getInstance()
          beforeSevernDate.setTime(new Date())
          beforeSevernDate.add(Calendar.DAY_OF_YEAR, -7)

          val salon: Option[Salon] = Salon.findOneById(salonId)
          salon match {
            case Some(s) => {
              // Navigation Bar
              var navBar = SalonNavigation.getSalonNavBar(Some(s)) ::: List((Messages("salon.couponMenus"), ""))
              serviceTypes = ServiceType.findAllServiceTypes(s.salonIndustry)
              // 如果stylistId为空，进入沙龙优惠劵·菜单画面，否则进入预约菜单选择画面
              if(stylistId.isEmpty()) {
                Ok(views.html.salon.store.salonInfoCouponAll(s, conditionForm.fill(couponServiceType), serviceTypes, coupons, menus, servicesByTypes, beforeSevernDate.getTime(), navBar, user))
              } else {
                Ok(views.html.reservation.reservSelectService(s, conditionForm.fill(couponServiceType), serviceTypes, coupons, menus, servicesByTypes, beforeSevernDate.getTime(), stylistId, styleId, user, navBar))
              }
            }
            case None => NotFound
          }
      })
  }

  /**
   * 显示店铺所有的优惠劵
   */
  def showCoupons(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findOneById(salonId)
    val coupons: List[Coupon] = Coupon.findBySalon(salonId)

    salon match {
      case Some(s) => Ok(html.coupon.showCouponGroup(s, coupons))
      case None => NotFound
    }
  }

  def getMap(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findOneById(salonId)
    salon match {
      case Some(s) =>
        val address = s.salonAddress.get.province + s.salonAddress.get.city.getOrElse("") + s.salonAddress.get.region.getOrElse("") +
          s.salonAddress.get.town.getOrElse("") + s.salonAddress.get.addrDetail
        Ok(html.salon.store.map(s, SalonNavigation.getSalonNavBar(salon), None, address))
      case None => NotFound
    }
  }

  /**
   * 店铺前台检索
   */
  def getSalonBySearch = StackAction { implicit request =>
    val user = loggedIn
    salonSearchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.error.errorMsg(errors)),
      {
        case (salonSearchForm) => {
          val salons = Salon.findSalonBySearchPara(salonSearchForm)
          var nav = ""
          if(salonSearchForm.salonIndustry.equals("Hairdressing")){
            nav = "HairSalon"
          }else if(salonSearchForm.salonIndustry.equals("Manicures")){
            nav = "NailSalon"
          }
          Ok(views.html.salon.general.index(nav = nav, navBar = SalonNavigation.getSalonTopNavBar, user = user, searchParaForSalon = salonSearchForm, salons = salons))
        }
      })
  }
}
