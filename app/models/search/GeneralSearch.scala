package models

import play.api.Play.current
import java.util.Date
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.WriteConcern
import mongoContext._


/**
 * 通用检索区分: 全部|美发沙龙|时尚靓发|美甲沙龙|时尚靓甲| 
 */
object GeneralSrchDiv extends Enumeration {
  type GeneralSrchDiv= Value
  val HairSalon, HairStyle, NailSalon, NailStyle, HealthSalon, CosmeSalon = Value 
}



/*---------------------------
 - 沙龙检索：检索字段 
 ----------------------------*/

/**
 * salon检索条件字段合成类
 */
case class SearchParaForSalon(
    keyWord : Option[String],
    city : String,
    region: String,
    salonName: List[String],
    salonIndustry: String,
    serviceType: List[String],
    priceRange: PriceRange, 
    seatNums: SeatNums,
    salonFacilities: SalonFacilities,
    sortByCondition: String
)


/**
 * Data struct for General salon search.
 * 用于保存检索条件的数据结构: 保存检索条件，以便实现热门关键字，热门城市等功能.
 * TODO: 暂时只实现: 热门关键字
 */
case class HotestKeyword(
  id: ObjectId,
  atomicKeyword: String,    // 原子关键字: 查询字段切分处理之后的关键字 
  searchDiv: String,        // search division: search for "hairsalon", "hairstyle", "nailsalon", "nailstyle" ......
  hitTimes: Long,
  isValid: Boolean
)
object HotestKeywordDAO extends ModelCompanion[HotestKeyword, ObjectId] {
  val dao = new SalatDAO[HotestKeyword, ObjectId](collection = mongoCollection("HotestKeyword")) {}
}


/**
 * Data struct for General salon search.
 * 沙龙通用检索结果的数据结构: 用于保存检索条件，实现热门关键字，热门城市等功能
 */
case class SalonGeneralSrchRst(
  salonInfo: Salon,               // 沙龙基本情报
  selectedStyles: List[Style],    // 发型集合
  selectedCoupons: List[Coupon],  // 优惠券集合
  reviewsStat: ReviewsStat,       // 店铺评价状况
  keywordsHitStrs: List[String]   // 检索中和关键字匹配的内容拔粹
)







/*------------------------------
 * 检索字段Master表
 *-----------------------------*/
/**
 * 检索条件: 价格区间
 */
case class PriceRange( minPrice: BigDecimal, maxPrice: BigDecimal)
object PriceRange extends PriceRangeDAO
trait PriceRangeDAO extends ModelCompanion[PriceRange, ObjectId] {
  val dao = new SalatDAO[PriceRange, ObjectId](collection = mongoCollection("PriceRange")) {}
}


/**
 * 沙龙检索条件: 席位数区间(店铺规模)
 */
case class SeatNums( minNum: Int, maxNum: Int)
object SeatNums extends SeatNumsDAO
trait SeatNumsDAO extends ModelCompanion[SeatNums, ObjectId] {
  val dao = new SalatDAO[SeatNums, ObjectId](collection = mongoCollection("SeatNums")) {}
}



