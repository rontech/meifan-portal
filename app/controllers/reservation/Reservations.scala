package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import java.util.Calendar
import java.util.Date
import com.mongodb.casbah.commons.Imports._

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
	def reservSelectDate = Action {
	  Ok(views.html.reservation.reservSelectDate("h"))
	}
	
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
}
