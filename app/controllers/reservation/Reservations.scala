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
package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import java.util.Calendar
import java.util.Date
import com.mongodb.casbah.commons.Imports._
import models._
import java.text.SimpleDateFormat
import play.cache.Cache
import controllers.auth._

  
import com.meifannet.framework.MeifanNetApplication

object Reservations extends MeifanNetApplication {
  /**
   * 添加额外服务form
   */
  def addServicesForm: Form[ResvGroup] = Form {
    mapping(
      "resvItems" -> list(
        mapping(
          "id" -> text) { (id) => ResvItem("service", new ObjectId(id), 0) } { resvItem => Some((resvItem.mainResvObjId.toString())) })
    )(ResvGroup.apply)(ResvGroup.unapply)
  }
  

  def reservHairView(id: ObjectId) = Action {
    Ok(views.html.reservation.reservHairView("hello"))
  }
  
  /**
   * 进入添加额外服务的画面
   * @param salonId 沙龙id
   * @param resvType 预约内容类型
   * @param id 根据预约内容类型区分是什么id，如resvType为coupon,那么id为优惠劵id
   */
  def reservServicesView(salonId: ObjectId, resvType: String, id: ObjectId) = Action {
    var resvItems: List[ResvItem] = Nil
    var resvItem: ResvItem = ResvItem("", id, 1)
    val serviceTypeNames: List[String] = Service.getServiceTypeList
    var servicesByTypes: List[ServiceByType] = Nil
    val couponSchDefaultConds: CouponServiceType = CouponServiceType(Nil, Some("1"))
    
    if(resvType == "coupon") {
      resvItem = ResvItem("coupon", id, 1)
    } else {
      if(resvType == "menu") {
        resvItem = ResvItem("menu", id, 1)
      } else {
        resvItem = ResvItem("service", id, 1)
      }
    }
    resvItems = resvItems ::: List(resvItem)
    
    for (serviceType <- serviceTypeNames) {
      var servicesByType: ServiceByType = ServiceByType("", Nil)
      // 如果根据服务名查找出来的服务为空，那么不添加到指定列表中
      var services: List[Service] = Service.getTypeListBySalonId(salonId, serviceType)
      if (!services.isEmpty) {
        val y = servicesByType.copy(serviceTypeName = serviceType, serviceItems = services)
        servicesByTypes = y :: servicesByTypes
      } else {

      }
    }
    
    var reservation: Reservation = Reservation(new ObjectId, "", salonId, 0, new Date, 0, None, resvItems, None, "", "", BigDecimal(0), 0, BigDecimal(0), new Date, new Date)
    Cache.set("reservation", reservation)
    
    val salon: Option[Salon] = Salon.findOneById(reservation.salonId)
    
    salon match {
      case Some(s) => {
        val srvTypes: List[ServiceType] = ServiceType.findAllServiceTypes(s.salonIndustry)
        Ok(views.html.reservation.addExtraService(s, reservation, resvItem, Coupons.conditionForm.fill(couponSchDefaultConds), servicesByTypes, srvTypes))
      }
      case None => NotFound
    }
    
  }

  /**
   * 从优惠劵·菜单画面选择进入预约日期选择
   * @param salonId 沙龙id
   * @param resvType 预约内容类型
   * @param id 根据预约内容类型区分是什么id，如resvType为coupon,那么id为优惠劵id
   * @param week 从今天开始的第几周显示，如果为0，那么从这周显示
   */
  def reservSelectDate(salonId: ObjectId, resvType: String, id: ObjectId, week: Int) = Action { implicit request =>
    // 将优惠劵的有关信息存入预约表中
    var resvItems: List[ResvItem] = Nil
    var resvItem: ResvItem = ResvItem("", id, 1)
    var serviceDuration: Int = 0
    var price: BigDecimal = BigDecimal(0)
    
    if(resvType == "coupon") {
      resvItem = ResvItem("coupon", id, 1)
      val coupon: Option[Coupon] = Coupon.findOneById(id)
      coupon match {
        case Some(c) => {
          serviceDuration = c.serviceDuration
          price = c.perferentialPrice
        }
        case None => NotFound
      }
    } else {
      if(resvType == "menu") {
        resvItem = ResvItem("menu", id, 1)
        val menu: Option[Menu] = Menu.findOneById(id)
        menu match {
          case Some(m) => {
            serviceDuration = m.serviceDuration
            price = m.originalPrice
          }
          case None => NotFound
        }
      } else {
        resvItem = ResvItem("service", id, 1)
        val service: Option[Service] = Service.findOneById(id)
        service match {
          case Some(s) => {
            serviceDuration = s.duration
            price = s.price
          }
          case None => NotFound
        }        
      }
    }
    
    resvItems = resvItems ::: List(resvItem)

    var reservation: Reservation = Reservation(new ObjectId, "", salonId, 0, new Date, serviceDuration, None, resvItems, None, "", "", price, 0, price, new Date, new Date)
   
    Cache.set("reservation", reservation);
    Redirect(routes.Reservations.reservShowDate(salonId, week))
  }

  /**
   * 预约时进入到指定技师页面
   * @param resvDate 预约的日时
   */
  def reservSelectStylist(resvDate: String) = Action {
    var reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)
    
    // 将String类型的格式转化为Date
    var expectedDate: Date = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(resvDate)
    reservation = reservation.copy(expectedDate = expectedDate)
    
    Cache.set("reservation", reservation)
    
    // 找出店铺所有技师，将在该时间段已经预约和未预约的区分开
    val salonAndStylists: List[StylistDetailInfo] = SalonAndStylist.getSalonStylistsInfo(reservation.salonId)
    
    var unReservStylists: List[StylistDetailInfo] = Nil
    var reservStylists: List[StylistDetailInfo] = Nil
    salonAndStylists.map {salonAndStylist =>
      salonAndStylist.workInfo match {
        case Some(wk) => {
          val isExist: Boolean = Reservation.findReservByDateAndStylist(expectedDate, wk.stylistId)
	      if(isExist) {
	        reservStylists = reservStylists ::: List(salonAndStylist)
	      } else {
	        unReservStylists = unReservStylists ::: List(salonAndStylist)
	      }
        }
      }
    }
    
    val salon: Option[Salon] = Salon.findOneById(reservation.salonId)
    
    salon match {
      case Some(s) => Ok(views.html.reservation.reservSelectStylistMain(s, reservation, unReservStylists, reservStylists))
      case None => NotFound
    }
  }
  
  /**
   * 根据指定的技师进入相关发型的选择
   * @param stylistId 指定技师
   */
  def reservSelectStyle(stylistId: ObjectId) = Action {
    var reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)
    
    reservation = reservation.copy(stylistId = Some(stylistId))
    
    Cache.set("reservation", reservation)
    
    // 得到该技师的所有发型
    val styles: List[Style] = Style.findByStylistId(stylistId)
    
    val salon: Option[Salon] = Salon.findOneById(reservation.salonId)
    
    salon match {
      case Some(s) => Ok(views.html.reservation.reservSelectStyleMain(s, reservation, styles))
      case None => NotFound
    }
  }

  /**
   * 查看店铺预约日程表
   * @param salonId 店铺Id
   * @param week 从今天开始的第几周显示，如果为0，那么从这周显示
   */
  def reservShowDate(salonId: ObjectId, week: Int) = Action {
    val salon: Option[Salon] = Salon.findOneById(salonId)
    
    val reservation: Reservation = Cache.getOrElse[Reservation]("reservation", null, 0)
    
    // 查找出该地店铺的所有预约
    val reservations: List[Reservation] = Reservation.findAllReservation(salonId)

    // 查看该店铺中技师的个数
    val stylistNum: Long = SalonAndStylist.countStylistBySalon(salonId)

    // 初始化预约表
    var resvSchedule: ResvSchedule = ResvSchedule(Nil, Nil, Nil, Nil)
    var yearsPart: List[YearsPart] = Nil
    var daysPart: List[DaysPart] = Nil
    var timesPart: List[String] = Nil
    var resvInfoPart: List[ResvInfoPart] = Nil

    // 取得店铺的营业开始时间和结束时间, 取得店铺的休息日期（目前值取得固定休息类型，即每周几休息）
    var openTime: String = "8:30"
    var closeTime: String = "20:30"
    var salonRests: List[String] = Nil
    salon match {
      case Some(s) => {
        if (s.workTime != null) {
          openTime = s.workTime.map { workTime => workTime.openTime }.getOrElse("")
          closeTime = s.workTime.map { workTime => workTime.closeTime }.getOrElse("")
        }

        if (s.restDays.map { rest => rest.restWay }.getOrElse("") == "Fixed") {
          salonRests = s.restDays.map { rest => rest.restDay }.getOrElse(Nil)
        }
      }
      case None => NotFound
    }
    
    // 将monday形式转化为日历中日期数据形式（如星期日为1...）
    var salonRestWeeks: List[Int] = Nil
    for (salonRest <- salonRests) {
      salonRest match {
        case "Sunday" => salonRestWeeks = salonRestWeeks ::: List(1)
        case "Monday" => salonRestWeeks = salonRestWeeks ::: List(2)
        case "Tuesday" => salonRestWeeks = salonRestWeeks ::: List(3)
        case "Wednesday" => salonRestWeeks = salonRestWeeks ::: List(4)
        case "Thursday" => salonRestWeeks = salonRestWeeks ::: List(5)
        case "Friday" => salonRestWeeks = salonRestWeeks ::: List(6)
        case "Saturday" => salonRestWeeks = salonRestWeeks ::: List(7)
      }
    }
    
    // 只能显示3周预约表，如果传入的week为2那么将显示超过3周的数据，那么将传入的参数返回为0
    var weekIndex: Int = week
    if (week > 1) {
      weekIndex = 0
    }

    // 将当前时间转化为calendar形式以便计算和比较时间
    var nowDate: Date = new Date()
    var startDay: Calendar = Calendar.getInstance()
    var endDay: Calendar = Calendar.getInstance()
    startDay.setTime(nowDate)
    startDay.add(Calendar.DAY_OF_YEAR, weekIndex * 7)
    endDay.setTime(startDay.getTime())
    endDay.add(Calendar.DAY_OF_YEAR, 13)

    var i: Int = 1
    var month: Int = 13
    // 用于时间（时：分）只需添加一次的判断
    var k: Int = 1

    // 将以week参数为基础的天数到后14天的数据做出放入数据结构中
    while (startDay.before(endDay) || startDay.equals(endDay)) {
      // 将营业时间转化为calendar形式以便计算和比较时间
      var openDate: Date = new SimpleDateFormat("HH:mm").parse(openTime)
      var open: Calendar = Calendar.getInstance()
      open.setTime(openDate)

      var closeDate: Date = new SimpleDateFormat("HH:mm").parse(closeTime)
      var close: Calendar = Calendar.getInstance()
      close.setTime(closeDate)

      var resvInfoItemPart: List[ResvInfoItemPart] = Nil

      // 添加日期和星期数据
      var resvDay: DaysPart = DaysPart(new SimpleDateFormat("yyyy/MM").format(startDay.getTime()), "", startDay.get(Calendar.DAY_OF_WEEK))
      var day: String = startDay.get(Calendar.DAY_OF_MONTH).toString()
      if (day.size == 1) {
        day = "0" + day
      }
      resvDay = resvDay.copy(day = day)
      daysPart = daysPart ::: List(resvDay)

      // 添加日期yyyy/MM数据
      if ((startDay.get(Calendar.MONTH) + 1) != month) {
        if (month != 13) {
          val resvYear1: YearsPart = YearsPart((startDay.get(Calendar.YEAR) + "/" + month), i-1)
          val resvYear2: YearsPart = YearsPart(new SimpleDateFormat("yyyy/MM").format(startDay.getTime()), (15 - i))
          yearsPart = (yearsPart ::: List(resvYear1)) ::: List(resvYear2)
        }

        month = startDay.get(Calendar.MONTH) + 1
      }

      if (i == 14 && yearsPart.isEmpty) {
        val resvYear: YearsPart = YearsPart(new SimpleDateFormat("yyyy/MM").format(startDay.getTime()), i)
        yearsPart = yearsPart ::: List(resvYear)
      }

      // 判断该天是否为休息天
      var resvInfo: ResvInfoPart = ResvInfoPart(startDay.get(Calendar.DAY_OF_YEAR), false, resvInfoItemPart)
      if (salonRestWeeks.contains(startDay.get(Calendar.DAY_OF_WEEK))) {
        resvInfo = resvInfo.copy(isRestFlg = true)
      } else {
        while (open.before(close) || open.equals(close)) {
          // 添加时间数据,只需添加一次
          if (k == 1) {
            var minute = open.get(Calendar.MINUTE).toString
            if (minute.size == 1) {
              minute = minute + "0"
            }
            val resvTime = open.get(Calendar.HOUR_OF_DAY) + ":" + minute
            timesPart = timesPart ::: List(resvTime)
          }

          // 添加预约中的数据
          var resvDate: Calendar = Calendar.getInstance()
          resvDate.setTime(startDay.getTime())
          resvDate.set(Calendar.HOUR_OF_DAY, open.get(Calendar.HOUR_OF_DAY))
          resvDate.set(Calendar.MINUTE, open.get(Calendar.MINUTE))

          // 得到营业结束前的前两个的时间段
          var endTime: Calendar = Calendar.getInstance()
          endTime.setTime(startDay.getTime())
          endTime.set(Calendar.HOUR_OF_DAY, close.get(Calendar.HOUR_OF_DAY))
          endTime.set(Calendar.MINUTE, close.get(Calendar.MINUTE))
          endTime.add(Calendar.MINUTE, -30)

          // 得到当前时间年月日 时分的最大时间和最小时间（根据秒）
          var expectedDateStart: Calendar = Calendar.getInstance()
          expectedDateStart.setTime(resvDate.getTime())
          expectedDateStart.add(Calendar.MINUTE, -1)
          var expectedDateEnd: Calendar = Calendar.getInstance()
          expectedDateEnd.setTime(resvDate.getTime())
          expectedDateEnd.add(Calendar.MINUTE, 1)

          var resvInfoItem: ResvInfoItemPart = ResvInfoItemPart(resvDate.getTime(), true)
          if (resvDate.getTime().after(nowDate) && resvDate.getTime().before(endTime.getTime())) {
            // 判断如果该时间内店铺技术是否已预约满
            if (Reservation.findReservationByDate(reservations, expectedDateStart.getTime(), expectedDateEnd.getTime()) < stylistNum) {
              resvInfoItem = resvInfoItem.copy(isResvFlg = true)
            } else {
              resvInfoItem = resvInfoItem.copy(isResvFlg = false)
            }
          } else {
            resvInfoItem = resvInfoItem.copy(isResvFlg = false)
          }
          resvInfoItemPart = resvInfoItemPart ::: List(resvInfoItem)

          open.add(Calendar.MINUTE, 30)
        }
        
        k = k + 1

        resvInfo = resvInfo.copy(resvInfoItemPart = resvInfoItemPart)
      }

      resvInfoPart = resvInfoPart ::: List(resvInfo)

      startDay.add(Calendar.DAY_OF_YEAR, 1)

      i = i + 1
    }

    // 将几个数据赋值
    resvSchedule = resvSchedule.copy(yearsPart = yearsPart, daysPart = daysPart, timesPart = timesPart, resvInfoPart = resvInfoPart)
    salon match {
      case Some(s) => Ok(views.html.reservation.reservationInfo(s, resvSchedule, reservation, weekIndex))
      case None => NotFound
    }
  }
}
