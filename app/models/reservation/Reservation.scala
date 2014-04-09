package models

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date
import play.api.PlayException

case class ResvItem (
		resvType: String, //coupon: 优惠劵; menu: 菜单; service: 服务
		mainResvObjId: ObjectId,
		resvOrder: Int  // 预约明细的主次序
)

case class Reservation (
        id: ObjectId = new ObjectId,
        userId: String,
        storeId: ObjectId,
        status: Int, // 0：已预约; 1：已消费; 2：已过期; -1：已取消
        expectedDate: Date, // 预约时间 + 预约日期
        serviceDuration: Int,
        originalPrice: BigDecimal,
        expectedStylist: String, // 技师表中的stylistId
        resvItems: List[ResvItem],
        rsvStyle: ObjectId,
        userPhone: String,
        userLeaveMsg: String,
        price: BigDecimal,
        usedPoint: Int,
        totalCost: BigDecimal,
        createDate: Date,
        processDate: Date
)

case class YearsPart (
		years: String, // 年/月
		yearNum: Int // 不同月份所占的天数
)

case class DaysPart (
		years: String,
		day: String, // 格式为2位
		weekDay: Int // 星期，这边为Calendar表中的数字形式
)

case class ResvInfoItemPart (
		resvDate: Date,
		isResvFlg: Boolean
)

case class ResvInfoPart (
        resvDay: Int,
        resvInfoItemPart: List[ResvInfoItemPart]
)

// 预约表格式
case class ResvSchedule (
		yearsPart: List[YearsPart],
		daysPart: List[DaysPart],
		timesPart: List[String],
		resvInfoPart: List[ResvInfoPart]
)


object Reservation extends ModelCompanion[Reservation, ObjectId]{
    
    def collection = MongoConnection()(
    current.configuration.getString("mongodb.default.db")
      .getOrElse(throw new PlayException(
        "Configuration error",
        "Could not find mongodb.default.db in settings")))("Reservation")

    val dao = new SalatDAO[Reservation, ObjectId](collection){}
    
    def findAllReservation(storeId: ObjectId):List[Reservation] = dao.find(MongoDBObject("storeId" -> storeId)).sort(MongoDBObject("expectedDate" -> -1)).toList
   
    /**
	 * 根据预定时间查找预约信息
	 */
	def findReservationByDate(reservations: List[Reservation], expectedDate: Date): List[Reservation] = {
		reservations.filter(r => r.expectedDate == expectedDate)
	}
}
