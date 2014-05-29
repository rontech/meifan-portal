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
package models

//import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import java.util.Date
import com.mongodb.casbah.query.Imports._
import com.meifannet.framework.db._
import java.text.SimpleDateFormat

/**
 * 预约内容，用于内嵌在预约表中
 * @param resvType 预约类型，如coupon: 优惠劵; menu: 菜单; service: 服务
 * @param mainResvObjId 预约类型id，如果类型为coupon，那么该属性为couponid
 * @param resvOrder 预约明细的主次序
 */
case class ResvItem(
  resvType: String,
  mainResvObjId: ObjectId,
  resvOrder: Int
)
  
/**
 * 预约内容list的class
 * 用于预约内容添加额外服务
 */
case class ResvGroup(
  resvItems: List[ResvItem]
)

/**
 * 用于店铺和技师日程表中最上层年/月显示数据
 * @param years 年/月
 * @param yearNum 统计对应年月的所占的天数
 */
case class YearsPart(
  years: String, // 年/月
  yearNum: Int // 不同月份所占的天数
)

/**
 * 用于店铺和技师日程表中日（周）的显示数据
 * @param years 年/月
 * @param day 天，格式为2位
 * @param weekDay 星期，这边为Calendar表中的数字形式
 */
case class DaysPart(
   years: String,
   day: String, // 格式为2位
   weekDay: Int // 星期，这边为Calendar表中的数字形式
)

/**
 * 用于店铺和技师日程表中每天的每个时间的预约情况的显示数据
 * @param resvDate 预约日程表中的时间，如yyyy-MM-dd HH:mm
 * @param isResvFlg 是否已预约
 */
case class ResvInfoItemPart(
  resvDate: Date,
  isResvFlg: Boolean)

/**
 * 根据每天将每个时间的预约情况，是否休息组合
 * @param resvDay 预约日期，天
 * @param isRestFlg 是否为休息日
 * @param resvInfoItemPart
 */
case class ResvInfoPart(
  resvDay: Int,
  isRestFlg: Boolean,
  resvInfoItemPart: List[ResvInfoItemPart])

/**
 * 预约日程表格式
 * 用于店铺和技师的日程表
 * @param yearsPart 年/月
 * @param daysPart 日（周）
 * @param timesPart 时间部分，如8:00,8:30...
 * @param resvInfoPart 预约情况
 */
case class ResvSchedule(
  yearsPart: List[YearsPart],
  daysPart: List[DaysPart],
  timesPart: List[String],
  resvInfoPart: List[ResvInfoPart])

/**
 * 预约表
 * @param id
 * @param userId 预约者
 * @param salonId 沙龙id
 * @param status 预约状态
 * @param expectedDate 预约期望的时间
 * @param serviceDuration 预约服务总时间
 * @param stylistId 预约技师，可为空
 * @param resvItems 预约内容
 * @param styleId 预约发型，可为空
 * @param userPhone 预约时所留号码
 * @param userLeaveMsg 预约留言
 * @param price 预约价格
 * @param usedPoint 预约时使用的积分（目前不用）
 * @param totalCost 预约使用积分后的价格（目前不用）
 * @param createDate 预约创建时间
 * @param processDate 预约状态修改时间
 */
case class Reservation(
  id: ObjectId = new ObjectId,
  userId: String,
  salonId: ObjectId,
  status: Int, // 0：已预约; 1：已消费; 2：已过期; -1：已取消
  expectedDate: Date, // 预约时间 + 预约日期
  serviceDuration: Int,
  stylistId: Option[ObjectId], // 技师表中的stylistId
  resvItems: List[ResvItem],
  styleId: Option[ObjectId],
  userPhone: String,
  userLeaveMsg: String,
  price: BigDecimal,
  usedPoint: Int,
  totalCost: BigDecimal,
  createDate: Date,
  processDate: Date) extends StyleIdUsed

object Reservation extends MeifanNetModelCompanion[Reservation] {

  val dao = new MeifanNetDAO[Reservation](collection = loadCollection()) {}

  /**
   * Get the best reserved Styles' reservations in a salon.
   *
   * @param salonId
   * @return
   */
  def findAllReservation(salonId: ObjectId): List[Reservation] = {
    dao.find($and(MongoDBObject("salonId" -> salonId), ("status" $in (0, 1)))).sort(MongoDBObject("expectedDate" -> -1)).toList
  }

  /**
   * Get the best reserved Styles' ObjectId.
   *     Ignore the data without styleId.
   *
   * @param topN
   * @return
   */
  def findBestReservedStyles(topN: Int = 0): List[ObjectId] = {
    val reservs = dao.find($and(("styleId" $exists true), ("status" $in (0, 1)))).sort(
      MongoDBObject("styleId" -> -1)).toList
    // styleId is exists absolutely.
    val topStyleIds = getBestRsvedStyleIds(reservs, topN)
    topStyleIds
  }

  /**
   * Get the best reserved Styles' ObjectId in a salon.
   *     Ignore the data without styleId.
   *
   * @param salonId
   * @param topN
   * @return
   */

  def findBestReservedStylesInSalon(salonId: ObjectId, topN: Int = 0): List[ObjectId] = {
    val reservs = dao.find($and(MongoDBObject("salonId" -> salonId), ("styleId" $exists true), ("status" $in (0, 1)))).sort(
      MongoDBObject("styleId" -> -1)).toList
    // styleId is exists absolutely.
    val topStyleIds = getBestRsvedStyleIds(reservs, topN)
    topStyleIds
  }

  /**
   * Get the best reserved styles' ObjectId from the reservation data.
   * TODO: Can the styleId not None check do in type T or trait?
   *
   * @param rsvs
   * @param topN
   * @return
   */
  def getBestRsvedStyleIds(rsvs: List[Reservation], topN: Int = 0): List[ObjectId] = {
    // styleId is exists absolutely.
    val rsvedStyles = rsvs.filter(_.styleId != None).map { _.styleId.get }
    val styleWithCnt = rsvedStyles.groupBy(x => x).map(y => (y._1, y._2.length)).toList.sortWith(_._2 > _._2)
    // get all stylesId or only top N stylesId of a salon according the topN parameters.  
    val top = if (topN == 0) styleWithCnt.map { _._1 } else styleWithCnt.map(_._1).slice(0, topN)
    top
  }

  /**
   * 根据预定时间查找预约信息
   * 用于判断日程表中某一时间段预约(状态为已预约)的总条数
   * @param reservations 预约列表
   * @param expectedDate 预约时间
   * @return
   */
  def findReservationByDate(reservations: List[Reservation], expectedDate: Date): Long = {
    val formattedDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(expectedDate)
    reservations.filter(r => (r.expectedDate.equals(new Date(formattedDate)) && r.status.equals(0))).size.toLong
  }

  /**
   * 根据状态为1和发型非空检索出符合热门排名的所有预约券
   *
   * @return
   */
  def findByStatusAndStyleId: List[Reservation] = {
    dao.find(MongoDBObject("status" -> 1)).toList
  }
  

  /**
   * 根据预定时间查找符合条件的预约信息的个数
   * @param salonId 沙龙
   * @param expectedDate 预约的时间
   * @return
   */
  def findResvByDateAndSalon(salonId: ObjectId, expectedDate: Date): Long = {
    val formattedDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(expectedDate)
    val resvCount: List[Reservation] = dao.find(MongoDBObject("expectedDate" -> new Date(formattedDate), "salonId" -> salonId, "status" -> 0)).toList
    resvCount.size.toLong
  }
  
  /**
   * 根据技师和预约时间查找状态为已预约的信息是否存在
   * @param expectedDate 预约时间
   * @param userId 预约者
   */
  def findReservByDateAndStylist(expectedDate: Date, stylistId: ObjectId): Boolean = {
    val formattedDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(expectedDate)
    val isExist = dao.findOne(MongoDBObject("expectedDate" -> new Date(formattedDate), "stylistId" -> stylistId, "status" -> 0))
    isExist match {
      case Some(iss) => true
      case None => false
    }
  }

  /**
   * 根据预约者和预约时间查找状态为已预约的信息是否存在
   * @param expectedDate 预约时间
   * @param userId 预约者
   */
  def findReservByDateAndUserId(expectedDate: Date, userId: String): Boolean = {
    val isExist = dao.findOne(MongoDBObject("expectedDate" -> expectedDate, "userId" -> userId, "status" -> 0))
    isExist match {
      case Some(is) => true
      case None => false
    }
  }
  
  /**
   * 查找出用户所有预约信息
   * @param userId 预约者
   */
  def findReservByUserId(userId: String): List[Reservation] = dao.find(MongoDBObject("userId" -> userId)).toList
  
  /**
   * 根据传入的List[ResvItem]查找出类型为除优惠劵外的信息
   * @param resvItems 预约表中预约内容
   */
  def findResvItemsForType(resvItems: List[ResvItem]): List[ResvItem] = {
    resvItems.filter(resvItem => (resvItem.resvType.equals("menu") || resvItem.resvType.equals("service")))
  }
  
  /**
   * 取得预约内容中的服务id
   * 用于预约添加服务时检索服务
   * @param resvItems 预约表中预约内容
   */
  def getServiceIdFromResv(resvItems: List[ResvItem]): List[ObjectId] = resvItems.map {
    resvItem => resvItem.mainResvObjId
  }
  
  /**
   * 取得预约内容中的主服务（主次序为1）
   * 用于预约添加额外服务时服务列表选择中不显示主服务
   * @param resvItems 预约表中预约内容
   */
  def getMainResvItem(resvItems: List[ResvItem]): ResvItem = {
    resvItems.filter(resvItem => (resvItem.resvOrder == 1)).head
  }
  
}
