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

import java.util.Date
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.WriteConcern
import mongoContext._
import com.meifannet.framework.db._

/**
 * 通用检索区分: 全部|美发沙龙|时尚靓发|美甲沙龙|时尚靓甲|
 */
object GeneralSrchDiv extends Enumeration {
  type GeneralSrchDiv = Value
  val Top, HairSalon, HairCatalog, NailSalon, NailCatalog, RelaxSalon, EstheSalon = Value
}

/*---------------------------
 - 沙龙检索：检索字段 
 ----------------------------*/
/**
 * salon检索条件字段合成类
 *
 * @param keyWord
 * @param city
 * @param region
 * @param salonName
 * @param salonIndustry
 * @param serviceType
 * @param priceRange
 * @param seatNums
 * @param salonFacilities
 * @param sortByConditions
 */
case class SearchParaForSalon(
  keyWord: Option[String],
  city: String,
  region: String,
  salonName: List[String],
  salonIndustry: String,
  serviceType: List[String],
  priceRange: PriceRange,
  seatNums: SeatNums,
  salonFacilities: SalonFacilities,
  sortByConditions: SortByConditions)

/**
 * Assistant Class: sort by conditions
 *
 * @param selSortKey
 * @param sortByPopuAsc
 * @param sortByReviewAsc
 * @param sortByPriceAsc
 */
case class SortByConditions(
  selSortKey: String = "price", // 选择的优先排序
  sortByPopuAsc: Boolean = false, // 热度: by popularity
  sortByReviewAsc: Boolean = false, // 评价: by review
  sortByPriceAsc: Boolean = true // 价格: by price
  )

/**
 * Data struct for General salon search.
 * 用于保存检索条件的数据结构: 保存检索条件，以便实现热门关键字，热门城市等功能.
 * TODO: 暂时只实现: 热门关键字
 *
 * @param id
 * @param atomicKeyword
 * @param searchDiv
 * @param hitTimes
 * @param isValid
 */
case class HotestKeyword(
  id: ObjectId,
  atomicKeyword: String, // 原子关键字: 查询字段切分处理之后的关键字 
  searchDiv: String, // search division: search for "hairsalon", "hairstyle", "nailsalon", "nailstyle" ......
  hitTimes: Long,
  isValid: Boolean)
object HotestKeyword extends HotestKeywordDAO
trait HotestKeywordDAO extends MeifanNetModelCompanion[HotestKeyword] {
  val dao = new MeifanNetDAO[HotestKeyword](collection = loadCollection()) {}

  /**
   *
   * @param keyword
   * @return
   */
  def findHotestKeywordsByKW(keyword: String): List[String] = {
    var rst: List[String] = Nil
    // pre process for keyword: process the double byte blank to single byte blank.
    val kws = keyword.replace("　", " ")
    if (kws.replace(" ", "").length == 0) {
      // when keyword is not exist, return Nil.
      rst
    } else {
      // when keyword is exist, convert it to regular expression.
      val kwsAry = kws.split(" ").map { x => (".*" + x.trim + ".*|") }
      val kwsRegex = kwsAry.mkString.dropRight(1).r
      // fields which search from 
      val s = dao.find(MongoDBObject("atomicKeyword" -> kwsRegex)).toList.sortBy(_.hitTimes)
      if (s.length < 8) s else s.slice(0, 8)
      s.map { key =>
        rst :::= List(key.atomicKeyword)
      }

      rst.distinct
    }
  }

  /**
   * Get the hotest top N keywords.
   *
   * @param srchDiv
   * @param topN
   * @return
   */
  def findTopKeywordsOfDiv(srchDiv: String = "Top", topN: Int = 0): List[String] = {
    val cond = if (srchDiv == GeneralSrchDiv.Top.toString) MongoDBObject.empty else MongoDBObject("searchDiv" -> srchDiv)
    if (topN == 0) {
      dao.find(cond).sort(MongoDBObject("hitTimes" -> -1)).toList.map { _.atomicKeyword }
    } else {
      dao.find(cond).sort(MongoDBObject("hitTimes" -> -1)).limit(topN).toList.map { _.atomicKeyword }
    }

  }

  /**
   *
   * @param keyword
   * @return
   */
  def addHitTimesBykWs(keyword: String) = {
    val kws = keyword.replace("　", " ")
    if (kws.replace(" ", "").length == 0) {
      // when keyword is not exist, return Nil.

    } else {
      // when keyword is exist, convert it to regular expression.
      val kwsAry = kws.split(" ").map { x => (".*" + x.trim + ".*|") }
      val kwsRegex = kwsAry.mkString.dropRight(1).r
      // fields which search from 
      dao.update(MongoDBObject("atomicKeyword" -> kwsRegex),
        MongoDBObject("$inc" -> (MongoDBObject("hitTimes" -> 1))), false, true)
    }
  }

}

/**
 * Data struct for General salon search.
 * 沙龙通用检索结果的数据结构: 用于保存检索条件，实现热门关键字，热门城市等功能
 *
 * @param salonInfo
 * @param selectedStyles
 * @param selectedCoupons
 * @param priceForCut
 * @param reviewsStat
 * @param keywordsHitStrs
 */
case class SalonGeneralSrchRst(
  salonInfo: Salon, // 沙龙基本情报
  selectedStyles: List[Style], // 发型集合
  selectedCoupons: List[Coupon], // 优惠券集合
  priceForCut: Option[BigDecimal], // 最低剪发价格
  reviewsStat: ReviewsStat, // 店铺评价状况
  keywordsHitStrs: List[String] // 检索中和关键字匹配的内容拔粹
  )

/*------------------------------
 * 检索字段Master表
 *-----------------------------*/
/**
 * 检索条件: 价格区间
 *
 * @param minPrice
 * @param maxPrice
 */
case class PriceRange(minPrice: BigDecimal, maxPrice: BigDecimal)
object PriceRange extends PriceRangeDAO
trait PriceRangeDAO extends MeifanNetModelCompanion[PriceRange] {
  val dao = new MeifanNetDAO[PriceRange](collection = loadCollection()) {}
}

/**
 * 沙龙检索条件: 席位数区间(店铺规模)
 *
 * @param minNum
 * @param maxNum
 */
case class SeatNums(minNum: Int, maxNum: Int)
object SeatNums extends SeatNumsDAO
trait SeatNumsDAO extends MeifanNetModelCompanion[SeatNums] {
  val dao = new MeifanNetDAO[SeatNums](collection = loadCollection()) {}
}
