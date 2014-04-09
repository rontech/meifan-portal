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

object Reservations extends Controller{
	/**
	 * 进入到具体的店铺里，某产品的预约或者是更多服务的选择
	 */
	def reservHairView(id: ObjectId) = Action {
	  Ok(views.html.reservation.reservHairView("hello"))
	}
	
	/**
	 * 选择预约的日程
	 */
	/*def reservSelectDate = Action {
	  Ok(views.html.reservation.reservSelectDate("h"))
	}*/
	
	/**
	 * 预约密码确认，输入密码预约才有效
	 */
	def reservConfirmPwd = Action {
	  Ok(views.html.reservation.reservConfirmPwd("h"))
	}
	
	/**
	 * 预约最后一步，跳出完成预约的画面
	 */
	def reservFinish = Action {
	  Ok(views.html.reservation.reservFinish("h"))
	}
	
	/**
	 * 预约时确认之前预约的信息
	 */
	def reservConfirmInfo = Action {
	  Ok(views.html.reservation.reservConfirmInfo("h"))
	}
	
	/**
	 * 预约时进入到指定技师页面
	 */
	def reservSelectStylist = Action {
	  Ok(views.html.reservation.reservSelectStylist(".."))
	}
	
	/**
	 * 查看店铺预约日程表
	 */
	def reservShowDate(salonId: ObjectId, week: Int) = Action {
	  val salon: Option[Salon] = Salon.findById(salonId)
	  
	  // 查找出该地店铺的所有预约
	  val reservations: List[Reservation] = Reservation.findAllReservation(salonId)
	  
	  // 查看该店铺中技师的个数
	  val stylistNum = SalonAndStylist.countStylistBySalon(salonId)
	  
	  // 初始化预约表
	  var resvSchedule: ResvSchedule = ResvSchedule(Nil, Nil, Nil, Nil)
	  var yearsPart: List[YearsPart] = Nil
	  var daysPart: List[DaysPart] = Nil
	  var timesPart: List[String] = Nil
	  var resvInfoPart: List[ResvInfoPart] = Nil
	  
	  // 取得店铺的营业开始时间和结束时间
	  var openTime: String = "8:30"
	  var closeTime: String = "20:30"  
	  salon match {
	    case Some(s) => {
	    	if(s.workTime != null) {
	    	  openTime = s.workTime.openTime
	    	  closeTime = s.workTime.closeTime
	    	}
	    }
	    case None => NotFound
	  }
	  
	  // 将当前时间转化为calendar形式以便计算和比较时间
	  var nowDate: Date = new Date()
	  var startDay: Calendar = Calendar.getInstance()
	  var endDay: Calendar = Calendar.getInstance()
	  startDay.setTime(nowDate)
	  startDay.add(Calendar.DAY_OF_YEAR, week*7)
	  endDay.setTime(startDay.getTime())
	  endDay.add(Calendar.DAY_OF_YEAR, 13)
	  
	  var i: Int = 1
	  var month: Int = 13
	  
	  // 将以week参数为基础的天数到后14天的数据做出放入数据结构中
	  while(startDay.before(endDay) || startDay.equals(endDay)) {
	    // 将营业时间转化为calendar形式以便计算和比较时间
	    var openDate:Date = new SimpleDateFormat("HH:mm").parse(openTime)
	    var open: Calendar = Calendar.getInstance()
	    open.setTime(openDate)
	  
	    var closeDate:Date = new SimpleDateFormat("HH:mm").parse(closeTime)
	    var close: Calendar = Calendar.getInstance()
	    close.setTime(closeDate)
	    
	    var resvInfoItemPart: List[ResvInfoItemPart] = Nil
	    
	    // 添加日期和星期数据
	    var resvDay: DaysPart = DaysPart(new SimpleDateFormat("yyyy/MM").format(startDay.getTime()), "", startDay.get(Calendar.DAY_OF_WEEK))
	    var day: String = startDay.get(Calendar.DAY_OF_MONTH).toString()
	    if(day.size == 1) {
	      day = "0" + day
	    }
	    resvDay = resvDay.copy(day = day)
	    daysPart = daysPart ::: List(resvDay)
	    
	    // 添加日期yyyy/MM数据
	    if((startDay.get(Calendar.MONTH)+1) != month) {
	      if(month != 13) {
	        val resvYear1: YearsPart = YearsPart((startDay.get(Calendar.YEAR)+"/"+month), i)
	        val resvYear2: YearsPart = YearsPart(new SimpleDateFormat("yyyy/MM").format(startDay.getTime()), (14-i))
	        yearsPart = (yearsPart ::: List(resvYear1)) ::: List(resvYear2)
	      }
	      
	      month = startDay.get(Calendar.MONTH)+1
	    }
	    
	    if(i == 14 && yearsPart.isEmpty) {
	      val resvYear: YearsPart = YearsPart(new SimpleDateFormat("yyyy/MM").format(startDay.getTime()), i)
	      yearsPart = yearsPart ::: List(resvYear)
	    }
	    
	    while(open.before(close) || open.equals(close)) {
	      // 添加时间数据
	      if(i == 1) {
	        var minute = open.get(Calendar.MINUTE).toString
	        if(minute.size == 1) {
	          minute = minute + "0"
	        }
	        val resvTime = open.get(Calendar.HOUR_OF_DAY) + ":" + minute
	        timesPart = timesPart ::: List(resvTime)
	      }
	      
	      // 添加预约中的数据
	      var resvDate: Calendar = Calendar.getInstance()
	      resvDate.setTime(startDay.getTime())
	      //print("open = " + open.get(Calendar.HOUR) + " ")
	      resvDate.set(Calendar.HOUR_OF_DAY, open.get(Calendar.HOUR_OF_DAY))
	      //println("resvDate = " + resvDate.getTime() + " ")
	      resvDate.set(Calendar.MINUTE, open.get(Calendar.MINUTE))
	      
	      // 得到营业结束前的前两个的时间段
	      var endTime: Calendar = Calendar.getInstance()
	      endTime.setTime(startDay.getTime())
	      endTime.set(Calendar.HOUR_OF_DAY, close.get(Calendar.HOUR_OF_DAY))
	      endTime.set(Calendar.MINUTE, close.get(Calendar.MINUTE))
	      endTime.add(Calendar.MINUTE, -30)
	      
	      var resvInfoItem: ResvInfoItemPart = ResvInfoItemPart(resvDate.getTime(), true)
	      if(resvDate.getTime().after(nowDate) && resvDate.getTime().before(endTime.getTime())) {
	        resvInfoItem = resvInfoItem.copy(isResvFlg = true)
	        //reservations.find(p)
	      } else {
	        resvInfoItem = resvInfoItem.copy(isResvFlg = false)
	      }
	      resvInfoItemPart = resvInfoItemPart ::: List(resvInfoItem) 
	      
	      open.add(Calendar.MINUTE, 30)
	    }
	    
	    val resvInfo: ResvInfoPart = ResvInfoPart(startDay.get(Calendar.DAY_OF_YEAR), resvInfoItemPart)
	    resvInfoPart = resvInfoPart ::: List(resvInfo)
	    
	    startDay.add(Calendar.DAY_OF_YEAR, 1)
	    
	    i = i + 1
	  }
	  
	  // 将几个数据赋值
	  resvSchedule = resvSchedule.copy(yearsPart = yearsPart, daysPart = daysPart, timesPart = timesPart, resvInfoPart = resvInfoPart)
	  
	  //print("resvSchedule = " + resvSchedule)
	  salon match {
	    case Some(s) => Ok(views.html.reservation.reservSelectDate(s, resvSchedule))
	    case None => NotFound
	  }
	}
}
