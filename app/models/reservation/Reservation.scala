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

case class ResvItem(
  resvType: String, //coupon: 优惠劵; menu: 菜单; service: 服务
  mainResvObjId: ObjectId,
  resvOrder: Int // 预约明细的主次序
)
  
/**
 * 预约内容list的class
 * 用于预约内容添加额外服务
 */
case class ResvGroup(
  resvItems: List[ResvItem]
)

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

case class YearsPart(
  years: String, // 年/月
  yearNum: Int // 不同月份所占的天数
  )

case class DaysPart(
  years: String,
  day: String, // 格式为2位
  weekDay: Int // 星期，这边为Calendar表中的数字形式
  )

case class ResvInfoItemPart(
  resvDate: Date,
  isResvFlg: Boolean)

case class ResvInfoPart(
  resvDay: Int,
  isRestFlg: Boolean,
  resvInfoItemPart: List[ResvInfoItemPart])

/**
 *  预约表格式
 */ 
case class ResvSchedule(
  yearsPart: List[YearsPart],
  daysPart: List[DaysPart],
  timesPart: List[String],
  resvInfoPart: List[ResvInfoPart])

object Reservation extends MeifanNetModelCompanion[Reservation] {

  val dao = new MeifanNetDAO[Reservation](collection = loadCollection()) {}

  /**
   * Get the best reserved Styles' reservations in a salon.
   */
  def findAllReservation(salonId: ObjectId): List[Reservation] = {
    dao.find($and(MongoDBObject("salonId" -> salonId), ("status" $in (0, 1)))).sort(MongoDBObject("expectedDate" -> -1)).toList
  }

  /**
   * Get the best reserved Styles' ObjectId.
   *     Ignore the data without styleId.
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
   * 根据预定时间查找符合条件的预约信息的个数
   */
  def findReservationByDate(reservations: List[Reservation], expectedDateStart: Date, expectedDateEnd: Date): Long = {
    reservations.filter(r => (r.expectedDate.before(expectedDateEnd) && r.expectedDate.after(expectedDateStart))).size.toLong
  }

  /**
   * 根据状态为1和发型非空检索出符合热门排名的所有预约券
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
    val resvCount: List[Reservation] = dao.find(MongoDBObject("expectedDate" -> expectedDate, "salonId" -> salonId, "status" -> 0)).toList
    resvCount.size.toLong
  }
  
  /**
   * 根据技师和预约时间查找状态为已预约的信息是否存在
   * @param expectedDate 预约时间
   * @param userId 预约者
   */
  def findReservByDateAndStylist(expectedDate: Date, stylistId: ObjectId): Boolean = {
    val isExist = dao.findOne(MongoDBObject("expectedDate" -> expectedDate, "stylistId" -> stylistId, "status" -> 0))
    isExist match {
      case Some(is) => true
      case None => false
    }
  }

  /**
   * 根据预约者和预约时间查找状态为已预约的信息是否存在
   * @param expectedDate 预约时间
   * @param userId 预约者
   */
  def findReservByDateAndUserId(expectedDate: Date, userId: String): Boolean = {
    println("expectedDate = " + expectedDate + " userId = " + userId)
    val isExist = dao.findOne(MongoDBObject("expectedDate" -> expectedDate, "userId" -> userId, "status" -> 0))
    println("isExist = " + isExist)
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
    println("resvItems = " + resvItems)
    resvItems.filter(resvItem => (resvItem.resvType.equals("menu") || resvItem.resvType.equals("service")))
  }
  
}
