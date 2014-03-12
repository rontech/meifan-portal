package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.mongodb.casbah.commons.Imports._

import models._
import views._

object Coupons extends Controller {
	
  def index = Action {
    val coupons:Seq[Coupon] = Coupon.findAll
    Ok(views.html.coupon.couponOverview(coupons))
  }
  
  def findBySalon(salonId: ObjectId) = Action {
    val salon: Option[Salon] = Salon.findById(salonId)
    val coupons: Seq[Coupon] = Coupon.findBySalon(salonId)
    
    // TODO: process the salon not exist pattern.
    Ok(html.salon.store.salonInfoCouponAll(salon = salon.get, coupons = coupons))
  }
  
  
}