import play.api._
import models._
import anorm._
import com.mongodb.casbah.commons.Imports._
import org.bson.types.ObjectId
import java.util.Date
import java.io.File


object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    // Initial Master Data.
    InitialData.insertMaster()

    // Initial Test Data.
    InitialData.insertSampleData()
   }
  
}

/**
 * Initial set of data to be imported 
 * in the sample application.
 */
object InitialData {
  
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  /*---------------------------
   * Master Data For Initialization. 
   * 预先需要登录的主表数据
   *--------------------------*/
  def insertMaster() = {

    // For nowtime, we change the Table Defination frequently....
    // Please do the drop actions below in mongodb.
    // mongo
    // use fashion-mongo
    // db.Salon.drop()
    // db.Style.drop()
    // db.Stylist.drop()

   if(Province.findAll.isEmpty) {
      Seq(
          Province(new ObjectId("5317c0d1d4d57997ce3e6daa"), "Jiangsu"),
          Province(new ObjectId("5317c0d1d4d57997ce3e6dab"), "Shandong")
      ).foreach(Province.save)
    }
    
    if(City.findAll.isEmpty) {
      Seq(
          City(new ObjectId("5317c0d1d4d57997ce3e6dca"), "Nanjing", "Jiangsu"),
          City(new ObjectId("5317c0d1d4d57997ce3e6dcb"), "Suzhou", "Jiangsu"),
          City(new ObjectId("5317c0d1d4d57997ce3e6dcc"), "Wuxi", "Jiangsu"),
          City(new ObjectId("5317c0d1d4d57997ce3e6dcd"), "Jinan", "Shandong"), 
          City(new ObjectId("5317c0d1d4d57997ce3e6dce"), "Qingdao", "Shandong") 
      ).foreach(City.save)
    }
    
    if(Region.findAll.isEmpty) {
      Seq(
          Region(new ObjectId("5317c0d1d4d57997ce3e6dda"), "Xuanwu", "Shandong"),
          Region(new ObjectId("5317c0d1d4d57997ce3e6ddb"), "Gulou", "Nanjing"),
          Region(new ObjectId("5317c0d1d4d57997ce3e6ddc"), "Gaoxin", "Jinan")
      ).foreach(Region.save)
    }    

    if(Industry.findAll.isEmpty) {
      Seq(
          Industry(new ObjectId("5317c0d1d4d57997ce3e6ec1"), "Hairdressing"),    // 美发 Hair
          Industry(new ObjectId("5317c0d1d4d57997ce3e6ec2"), "Manicures"),       // 美甲 Nail
          Industry(new ObjectId("5317c0d1d4d57997ce3e6ec3"), "Healthcare"),      // 保健 Health Care 
          Industry(new ObjectId("5317c0d1d4d57997ce3e6ec4"), "Cosmetic")         // 整形 Face, plastic 
      ).foreach(Industry.save)
    }
     
    if(Position.findAll.isEmpty){
      Seq(
       Position(new ObjectId("531964e0d4d57d0a43771411"),"店长"),
       Position(new ObjectId("531964e0d4d57d0a43771412"),"技师"),
       Position(new ObjectId("531964e0d4d57d0a43771412"),"助手")
      ).foreach(Position.save)
    }

    if(ServiceType.findAll.isEmpty) {
        Seq (
          ServiceType(new ObjectId("5316798cd4d5cb7e816db34b"), "Cut", "剪"),
          ServiceType(new ObjectId("53167a91d4d5cb7e816db34d"), "Wash", "洗"),
          ServiceType(new ObjectId("53167abbd4d5cb7e816db34f"), "Blow", "吹"),
          ServiceType(new ObjectId("53167aced4d5cb7e816db351"), "Dye", "染"),
          ServiceType(new ObjectId("53167ad9d4d5cb7e816db353"), "Care", "护理"),
          ServiceType(new ObjectId("53167ae7d4d5cb7e816db355"), "Perm", "烫"),
          ServiceType(new ObjectId("53167b3cd4d5cb7e816db359"), "Supple", "柔顺"),
          ServiceType(new ObjectId("5316c443d4d57997ce3e6d68"), "Other", "其他")
        ).foreach(ServiceType.save)
    }

    if(StyleColor.findAll.isEmpty) {
      Seq (
          StyleColor(new ObjectId, "红", "红色"),
          StyleColor(new ObjectId, "黄", "黄色"),
          StyleColor(new ObjectId, "黑色", "黑色"),
          StyleColor(new ObjectId, "其他颜色", "其他颜色")
        ).foreach(StyleColor.save)
    }
    
    if(StyleLength.findAll.isEmpty) {
      Seq (
          StyleLength(new ObjectId, "长", ""),
          StyleLength(new ObjectId, "中", ""),
          StyleLength(new ObjectId, "短", "")
        ).foreach(StyleLength.save)
    }
    
    if(StyleImpression.findAll.isEmpty) {
      Seq (
          StyleImpression(new ObjectId, "清新", ""),
          StyleImpression(new ObjectId, "自然", ""),
          StyleImpression(new ObjectId, "淡雅", ""),
          StyleImpression(new ObjectId, "其他印象", "")
        ).foreach(StyleImpression.save)
    }
    
    if(StyleAmount.findAll.isEmpty) {
      Seq (
          StyleAmount(new ObjectId, "多", ""),
          StyleAmount(new ObjectId, "一般", ""),
          StyleAmount(new ObjectId, "少", "")
        ).foreach(StyleAmount.save)
    }
    
    if(StyleQuality.findAll.isEmpty) {
      Seq (
          StyleQuality(new ObjectId, "软", ""),
          StyleQuality(new ObjectId, "适中", ""),
          StyleQuality(new ObjectId, "硬", ""),
          StyleQuality(new ObjectId, "柔顺", "")
        ).foreach(StyleQuality.save)
    }
    
    if(StyleDiameter.findAll.isEmpty) {
      Seq (
          StyleDiameter(new ObjectId, "细", ""),
          StyleDiameter(new ObjectId, "适中", ""),
          StyleDiameter(new ObjectId, "粗", "")
        ).foreach(StyleDiameter.save)
    }
     
    if(FaceShape.findAll.isEmpty) {
      Seq (
          FaceShape(new ObjectId, "标准", ""),
          FaceShape(new ObjectId, "圆形", ""),
          FaceShape(new ObjectId, "鹅蛋脸", ""),
          FaceShape(new ObjectId, "四角", "")
        ).foreach(FaceShape.save)
    }
    
    if(SocialStatus.findAll.isEmpty) {
      Seq (
          SocialStatus(new ObjectId, "程序员", ""),
          SocialStatus(new ObjectId, "教师", ""),
          SocialStatus(new ObjectId, "茶艺", ""),
          SocialStatus(new ObjectId, "工程师", "")
        ).foreach(SocialStatus.save)
    }
    
    if(Sex.findAll.isEmpty) {
      Seq (
          Sex(new ObjectId, "男"),
          Sex(new ObjectId, "女")
        ).foreach(Sex.save)
    }
    
    if(AgeGroup.findAll.isEmpty) {
      Seq (
          AgeGroup(new ObjectId, "1-10", ""),
          AgeGroup(new ObjectId, "11-20", ""),
          AgeGroup(new ObjectId, "21-30", ""),
          AgeGroup(new ObjectId, "31-80", "")
        ).foreach(AgeGroup.save)
    }

    if(BlogCategory.findAll.isEmpty){
      Seq (
        BlogCategory(new ObjectId("53195fb4a89e175858abce82"),"美容资讯", true),
        BlogCategory(new ObjectId("53195fb4a89e175858abce83"),"健康养生", true),
        BlogCategory(new ObjectId("53195fb4a89e175858abce85"),"个人", true),
        BlogCategory(new ObjectId("53195fb4a89e175858abce86"),"工作", true),
        BlogCategory(new ObjectId("53195fb4a89e175858abce87"),"喜欢", true),
        BlogCategory(new ObjectId("53195fb4a89e175858abce84"),"其他", true)
      ).foreach(BlogCategory.save)
    }
    
   
  }


  /*---------------------------
   * Sample Data For Test. 
   * 测试数据
   *--------------------------*/
  def insertSampleData() {

    


if(Salon.findAll == Nil) { 

      Seq(
        Salon(new ObjectId("530d7288d7f2861457771bdd"), SalonAccount("accountId", "123456"), "火影忍者吧", Some("火吧"), List("Hairdressing"), Some("www.sohu.com"), Some("本地最红的美发沙龙！"), "051268320328", "鸣人", List(OptContactMethod("QQ",List("99198121"))), date("2014-03-12"), Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)), "地铁一号线汾湖路站1号出口向西步行500米可达", WorkTime("9:00", "18:00"), List(RestDay(1, 7)), 25, SalonFacilities(true, true, true, true, true, true, true, true, true, "附近有"), List(OnUsePicture(new ObjectId("531efde7018cdb5d9e63d592"), "LOGO", Some(1), None)), date("2014-01-12") ),
        Salon(new ObjectId("530d7292d7f2861457771bde"), SalonAccount("accountId", "123456"), "海贼王吧", Some("海吧"), List("Hairdressing"), Some("www.163.com"), Some("本地第二红的美发沙龙！"), "051268320328", "路飞", List(OptContactMethod("QQ",List("99198121"))), date("2014-03-12"), Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)), "地铁一号线汾湖路站1号出口向西步行500米可达", WorkTime("9:00", "18:00"), List(RestDay(1, 6), RestDay(1, 7)), 9, SalonFacilities(true, true, true, true, true, true, true, true, true, "附近有"), List(OnUsePicture(new ObjectId("531efde7018cdb5d9e63d593"), "LOGO", None, None)), date("2014-03-12") )
      ).foreach(Salon.save)
    }
      
   if(Stylist.findAll.isEmpty) { 
      Seq(
         Stylist(new ObjectId("531964e0d4d57d0a43771401"), new ObjectId("530d8010d7f2861457771bf8"), 5, List(new IndustryAndPosition(new ObjectId,"美甲师","店长")),
            List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"有乐趣的环境能滋养创意，没有人工作只是为了好玩，但并不意味工作不能变得有趣。 ","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪",
            (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId("530d8010d7f2861457771bf9"), new ObjectId("53202c29d4d5e3cd47efffd3"), 5, List(new IndustryAndPosition(new ObjectId,"美甲师","店长")),
            List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"一切节省，归根到底都归结为时间的节省。","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪",
            (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId("530d8010d7f2861457771bf4"), new ObjectId("53202c29d4d5e3cd47efffd4"), 5, List(new IndustryAndPosition(new ObjectId,"美甲师","店长")),
            List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"细节源于态度，细节体现素质","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪",
            (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId("530d8010d7f2861457771bf5"), new ObjectId("53202c29d4d5e3cd47efffd9"), 5, List(new IndustryAndPosition(new ObjectId,"美甲师","店长")),
            List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"生命有如铁砧，愈被敲打，愈能发出火花。","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪",
            (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId("530d8010d7f2861457771bf6"), new ObjectId("53202c29d4d5e3cd47efffd8"), 5, List(new IndustryAndPosition(new ObjectId,"美甲师","店长")),
            List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"不积跬步，无以致千里","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪",
            (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId("530d8010d7f2861457771bf7"), new ObjectId("53202c29d4d5e3cd47efffd7"), 5, List(new IndustryAndPosition(new ObjectId,"美甲师","店长")),
            List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"成功并不能用一个人达到什么地位来衡量，而是依据他在迈向成功的过程中，到底克服了多少困难和障碍","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪",
           (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId("530d8010d7f2861457771bf2"), new ObjectId("53202c29d4d5e3cd47efffd6"), 5, List(new IndustryAndPosition(new ObjectId,"美甲师","店长")),
            List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"自信是向成功迈出的第一步","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪",
            (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId("530d8010d7f2861457771bf1"), new ObjectId("53202c29d4d5e3cd47efffd5"), 5, List(new IndustryAndPosition(new ObjectId,"美甲师","店长")),
            List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"兴趣是最好的老师","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪",
            (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ) 
      ).foreach(Stylist.save)
    }
    
    if(SalonAndStylist.findAll.isEmpty){
      Seq(
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("531964e0d4d57d0a43771401"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bf9"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bf4"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bf5"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bf6"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d8010d7f2861457771bf7"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d8010d7f2861457771bf2"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d8010d7f2861457771bf1"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, Option(new Date), false)
      ).foreach(SalonAndStylist.save)

    }
    
    
    if(Style.findAll.isEmpty) { 
      Seq(
        Style(new ObjectId("530d828cd7f2861457771c0b"), "四代火影发型式", new ObjectId("530d8010d7f2861457771bf6"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple"),"中",List("黄","红","黑色"),List("少","多","一般"),List("软","硬"),List("粗","细"),List("圆脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"男",List("程序员"),date("2014-03-12"),true),
        Style(new ObjectId("530d828cd7f2861457771c0c"), "六道仙人发型", new ObjectId("530d8010d7f2861457771bf6"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"自然",List("Supple"),"长",List("红","黑色"),List("少","多","一般"),List("软","硬"),List("适中","细"),List("标准","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"男",List("程序员"),date("2014-03-12"),true),
        Style(new ObjectId("530d828cd7f2861457771c0d"), "海贼王发型", new ObjectId("530d8010d7f2861457771bf6"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple"),"中",List("黄","红","黑色"),List("少","多","一般"),List("软","硬"),List("粗","细"),List("标准","圆脸","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("31-80","21-30","1-10","11-20"),"女",List("程序员"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a02ed"), "豆平的鸟窝", new ObjectId("530d8010d7f2861457771bf1"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple","Perm"),"短",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗"),List("标准","圆脸","鹅蛋脸","四角"),"凌乱中带有一丝淡淡的忧伤！",List("31-80","21-30","1-10","11-20"),"女",List("程序员","茶艺","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a02ee"), "齐白石的虾", new ObjectId("530d8010d7f2861457771bf1"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"自然",List("Supple","Perm"),"中",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗","细","适中"),List("标准","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("31-80","21-30","1-10","11-20"),"男",List("程序员","茶艺","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a021d"), "达芬奇的蛋", new ObjectId("530d8010d7f2861457771bf1"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple","Perm"),"短",List("黄","红"),List("少","多","一般"),List("柔顺","软","硬"),List("粗"),List("标准","圆脸","鹅蛋脸"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"女",List("程序员","工程师","茶艺"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a021e"), "路飞的草帽", new ObjectId("530d8010d7f2861457771bf1"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"淡雅",List("Supple","Perm"),"长",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","适中","硬"),List("粗","细","适中"),List("标准","圆脸"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("31-80","21-30","1-10","11-20"),"男",List("程序员","工程师","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a023d"), "乌索普的弹弓", new ObjectId("530d8010d7f2861457771bf1"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple","Perm"),"短",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗","细"),List("标准","圆脸","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("31-80","21-30","1-10","11-20"),"女",List("程序员","工程师","茶艺"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a023e"), "佐罗的三刀流", new ObjectId("530d8010d7f2861457771bf1"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple","Perm"),"长",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗","细"),List("标准"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"男",List("程序员","工程师","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533134db9aa6b4dfc54a02ef"), "青春短寸无敌", new ObjectId("530d8010d7f2861457771bf7"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple"),"中",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗","细"),List("标准","圆脸","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"女",List("程序员","工程师","茶艺","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0374"), "斜刘海披肩发型", new ObjectId("530d8010d7f2861457771bf8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"自然",List("Supple","Perm"),"短",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗"),List("标准","圆脸","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("31-80","21-30","1-10","11-20"),"男",List("程序员","工程师","茶艺"),date("2014-03-12"),true),
        Style(new ObjectId("533134db9aa6b4dfc54a022f"), "齐刘海内扣发型", new ObjectId("530d8010d7f2861457771bf8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple","Perm"),"长",List("黄","黑色"),List("少","多","一般"),List("柔顺","软","适中","硬"),List("粗","细","适中"),List("标准","圆脸","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"女",List("程序员"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0324"), "斜刘海中短发型", new ObjectId("530d8010d7f2861457771bf2"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"自然",List("Supple","Perm"),"短",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗","细","适中"),List("标准","圆脸","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"男",List("程序员"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a022d"), "偏分刘海披肩发", new ObjectId("530d8010d7f2861457771bf8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"淡雅",List("Supple","Perm"),"长",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗"),List("标准"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("31-80","21-30","1-10","11-20"),"女",List("程序员"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a022e"), "偏分刘海中长发", new ObjectId("530d8010d7f2861457771bf8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple"),"中",List("黄","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗","细"),List("标准","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("31-80","21-30","1-10","11-20"),"男",List("程序员"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0375"), "碎发", new ObjectId("530d8010d7f2861457771bf4"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple"),"中",List("黄","黑色"),List("少","多","一般"),List("软","硬"),List("粗","细"),List("标准","圆脸","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"男",List("程序员","工程师","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0376"), "波波头", new ObjectId("530d8010d7f2861457771bf4"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"自然",List("Supple","Perm"),"中",List("黄","红"),List("少","多","一般"),List("柔顺","软","硬"),List("粗","细","适中"),List("圆脸","鹅蛋脸"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"女",List("程序员","茶艺","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0377"), "丸子头梳法", new ObjectId("530d8010d7f2861457771bf1"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple","Perm"),"短",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","适中","硬"),List("粗","细"),List("标准","鹅蛋脸"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"男",List("程序员","工程师","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0378"), "清纯发型", new ObjectId("530d8010d7f2861457771bf4"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"淡雅",List("Supple","Perm"),"短",List("黄","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗","细","适中"),List("圆脸","鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"女",List("程序员","工程师","茶艺"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0379"), "可爱梨花头", new ObjectId("530d8010d7f2861457771bf4"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple"),"长",List("黄","红","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗"),List("鹅蛋脸","四角"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"男",List("程序员","茶艺","教师"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0373"), "大卷发型", new ObjectId("530d8010d7f2861457771bf4"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"清新",List("Supple","Perm"),"长",List("黄","黑色"),List("少","多","一般"),List("柔顺","软","硬"),List("粗"),List("标准","圆脸"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("1-10","11-20"),"女",List("程序员"),date("2014-03-12"),true)
      ).foreach(Style.save)
    }

    if(Coupon.findAll.isEmpty) {
      Seq(
         Coupon(new ObjectId("5317c0d1d4d57997ce3e6d6a"), "xjc01", "[兄弟公司☆宝石]小脸斩+香薰头部按摩5250日元允许西装", new ObjectId("530d7288d7f2861457771bdd"),
         Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true),
             Service(new ObjectId("5316bb36d4d57997ce3e6d49"), "离子烫", "离子烫对头发保护性最强", "Perm", new ObjectId("530d7288d7f2861457771bdd"), 90, 100, date("2014-03-31"), null, true)),
         110, 100, 90, date("2014-03-01"), date("2014-03-31"), "你看到的HOTPEPPER美容♪字", "在预订时间", "◆体验到自己可爱虽然是♪我们自然要授予风格，你想仔细咨询☆◆您可以选择洗发水是一种根据请求的皮肤和头发，定型剂", true), 
         Coupon(new ObjectId("5317c0d1d4d57997ce3e6d6b"), "xjc02", "[兄弟公司☆1号热门]斩+颜色+温和的芳香按摩头11340日元", new ObjectId("530d7288d7f2861457771bdd"),
         Seq(Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "简单清洗", "Wash", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true) ,
             Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "对发梢进行染色或者挑染", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 90, 120, date("2014-03-31"), null, true),
             Service(new ObjectId("5316bb36d4d57997ce3e6d49"), "离子烫", "离子烫对头发保护性最强", "Perm", new ObjectId("530d7288d7f2861457771bdd"), 90, 100, date("2014-03-31"), null, true)),
         230, 190, 120, date("2014-03-01"), date("2014-03-31"), "你看到的HOTPEPPER美容♪字", "在预订时间", "◆无♪◆长费咨询和那些敏感的皮肤☆◆最好的季节Tsuyakara成为各1号和皮肤的每一个颜色，眼睛的颜色，从时尚，更是对皮肤的刺激性是一个关注的晒后", true)
      ).foreach(Coupon.save)
    }

    if(Menu.findAll().isEmpty) {
      Seq(
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d61"), "菜单1", "刘海剪 + 离子烫", new ObjectId("530d7288d7f2861457771bdd"), 
              Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true),
                  Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "对发梢进行染色或者挑染", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 90, 120, date("2014-03-31"), null, true)),
              130, 100, date("2014-03-19"), null, true),
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d62"), "菜单2", "刘海剪 + 3D彩色", new ObjectId("530d7288d7f2861457771bdd"),
              Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true),
                  Service(new ObjectId("5316be8ed4d57997ce3e6d52"), "3D彩色", "染后能让头发更有立体感", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 80, 150, date("2014-03-31"), null, true)),
              90, 160, date("2014-03-19"), null, true),
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d63"), "菜单3", "刘海剪 + 简洗", new ObjectId("530d7288d7f2861457771bdd"),
              Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true),
                  Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "简单清洗", "Wash", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true)),
              20, 20, date("2014-03-19"), null, true)
      ).foreach(Menu.save)
    }
   
     if(Service.findAll.isEmpty) {
        Seq (
          Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true),
          Service(new ObjectId("53168b61d4d5cb7e816db35e"), "数码烫", "数码烫是一种新型的技术", "Perm", new ObjectId("530d7288d7f2861457771bdd"), 90, 100, date("2014-03-31"), null, true),
          Service(new ObjectId("5316bb36d4d57997ce3e6d49"), "离子烫", "离子烫对头发保护性最强", "Perm", new ObjectId("530d7288d7f2861457771bdd"), 90, 100, date("2014-03-31"), null, true),
          Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "对发梢进行染色或者挑染", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 90, 120, date("2014-03-31"), null, true),
          Service(new ObjectId("5316be49d4d57997ce3e6d50"), "小脸剪", "该发型剪完之后会有很好地修饰脸蛋的作用", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 20, 30, date("2014-03-31"), null, true) ,
          Service(new ObjectId("5316be8ed4d57997ce3e6d52"), "3D彩色", "染后能让头发更有立体感", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 80, 150, date("2014-03-31"), null, true),
          Service(new ObjectId("5316beb4d4d57997ce3e6d54"), "低色短", "底色短", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 80, 140, date("2014-03-31"), null, true),
          Service(new ObjectId("5316bed4d4d57997ce3e6d56"), "蒸汽烫", "高温水蒸气使得头发弯曲", "Perm", new ObjectId("530d7288d7f2861457771bdd"), 45, 80, date("2014-03-31"), null, true) ,
          Service(new ObjectId("5316bef6d4d57997ce3e6d58"), "纯护理", "对干枯分叉等问题修复", "Care", new ObjectId("530d7288d7f2861457771bdd"), 50, 66, date("2014-03-31"), null, true) ,
          Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "简单清洗", "Wash", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true) ,
          Service(new ObjectId("5316c05bd4d57997ce3e6d5c"), "干洗", "先用干洗专用清洗剂洗一下，然后用水冲洗", "Wash", new ObjectId("530d7288d7f2861457771bdd"), 15, 20, date("2014-03-31"), null, true) ,
          Service(new ObjectId("5316c07fd4d57997ce3e6d5e"), "简吹", "将湿的头发吹干", "Blow", new ObjectId("530d7288d7f2861457771bdd"), 10, 15, date("2014-03-31"), null, true) ,
          Service(new ObjectId("5316c0a2d4d57997ce3e6d60"), "豪华护理", "对严重干枯分叉等问题修复", "Care", new ObjectId("530d7288d7f2861457771bdd"), 70, 200, date("2014-03-31"), null, true),
          Service(new ObjectId("5316c0d1d4d57997ce3e6d62"), "拉直", "拉直头发", "Supple", new ObjectId("530d7288d7f2861457771bdd"), 30, 50, date("2014-03-31"), null, true) ,
          Service(new ObjectId("5316ec2fd4d57997ce3e6d97"), "盘发", "将长发盘起", "Other", new ObjectId("530d7288d7f2861457771bdd"), 50, 50, date("2014-03-31"), null, true) ,
          Service(new ObjectId("5316ecffd4d57997ce3e6d9d"), "盘发2", "将中长发盘起", "Other", new ObjectId("530d7288d7f2861457771bdd"), 100, 80, date("2014-03-31"), null, true) 
        ).foreach(Service.save)
    }

   if(User.findAll.isEmpty){
      Seq(
       User(new ObjectId("530d8010d7f2861457771bf8"),"zhenglu316","关雨1","123456","F", date("2014-03-18"),Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)),new ObjectId,"15269845698","123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),"程序员","normal","userLevel.0",20,date("2014-03-18"),"Administrator", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd3"),"zhenglu","关雨2","123456","F", date("2014-03-18"),Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)),new ObjectId,"15269845698","123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),"程序员","normal","userLevel.0",20,date("2014-03-18"),"Administrator", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd4"),"zhenglu3","关雨3","123456","F", date("2014-03-18"),Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)),new ObjectId,"15269845698","123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),"程序员","stylist","userLevel.0",20,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd9"),"zhenglu4","阿哲","123456","F", date("2014-03-18"),Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)),new ObjectId,"15269845698","123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),"程序员","normal","userLevel.0",20,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd8"),"zhenglu5","西门吹雪","123456","F", date("2014-03-18"),Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)),new ObjectId,"15269845698","123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),"程序员","normal","userLevel.0",20,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd7"),"zhenglu6","叶孤城","123456","F", date("2014-03-18"),Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)),new ObjectId,"15269845698","123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),"程序员","normal","userLevel.0",20,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd6"),"zhenglu7","陆小风","123456","F", date("2014-03-18"),Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)),new ObjectId,"15269845698","123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),"程序员","normal","userLevel.0",20,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd5"),"zhenglu8","花满楼","123456","F", date("2014-03-18"),Address("Jiangsu", Option("Suzhou"), Option("Gaoxin"), None, "竹园路209号", Some(100.0), Some(110.0)),new ObjectId,"15269845698","123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),"程序员","stylist","userLevel.0",20,date("2014-03-18"),"LoggedIn", true)
      ).foreach(User.save)
    }
    
    if(MyFollow.findAll.isEmpty){
      Seq(
        MyFollow(new ObjectId("532f9889d4d5f03ea49463fd"), new ObjectId("530d8010d7f2861457771bf8"),new ObjectId("530d7288d7f2861457771bdd"),"salon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463fe"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("53202c29d4d5e3cd47efffd4"),"user"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463ff"), new ObjectId("53202c29d4d5e3cd47efffd4"),new ObjectId("53202c29d4d5e3cd47efffd3"),"user"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463ed"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("5317c0d1d4d57997ce3e6d6a"),"coupon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e1"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("5317c0d1d4d57997ce3e6d6b"),"coupon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e3"), new ObjectId("530d8010d7f2861457771bf8"),new ObjectId("53195fb4a89e175858abce90"),"blog"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e4"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("53195fb4a89e175858abce91"),"blog"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e5"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d828cd7f2861457771c0b"),"style"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e6"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d828cd7f2861457771c0c"),"style")
      ).foreach(MyFollow.save)
    }

    if(Blog.findAll.isEmpty){
      Seq(
        Blog(new ObjectId("53195fb4a89e175858abce92"),"美发店装修设计注意事项","美发店是每个人都要去的地方，街上各种美发店大多数人们会怎样选择呢，个人觉得大众人群会选择装修店面好一些但也不是很豪华的那种。不同的美发店在装修设计上是改针对不同的客户群体的，那么在美发店的整体设计中都应该怎样做呢?<br>首先在街面上的自然是店面和招牌，对于最显眼的招牌来说，它直接反应的是店面的形象和店面的风格，因此应该着重注意一下色彩带的搭配，现在大多的以深色的主体配合其他颜色，以引人注目为主要目的。<br>再有就是重要的店面风格，不管选择什么样的风格，重要的是表现出店面的文化和专业的气氛。还应当和周围的店面在风格上区分开来，以显示不一样的经营，装饰用的话也不要尽选用那些美女帅哥的发型图片，安放一些工艺品和名人字画也是能够突出店面的文化档次。店面内应当设施适当的休息区域能够让客人坐下来休息，最好还是能够看到工作人员的工作，了解店面的服务质量，店面内的装修应当明快清新，暖色调能够使人感到轻松温馨，并且具有着依赖感和安全感，当然了店面内的设计还是应该注意所针对的消费群体的档次，不能太低，也不能让人看到后忘而却步。<br>对于美发店来说，店面内重要的就是操作区，它占有者店面一半以上的面积，对于操作区的设计，这里是直接服务顾客的地方，因此应当宽松干净和舒适，还应当考虑到行走的方便和顺畅，而且区域内的家具等尽量选择精美一些的，外星应当独特而色调统一。还有就是一些功能性的服务设施，例如卫生间，虽然大多客人可能用不到，但是还是应该重视的，这也会给顾客留下深刻的印象，卫生间可以相对的豪华一些，不能有异味，在卫生间内还可以挂上自己店的经营理念等具有销售意义的字画。当然店面内的一些小的注意事项也是应当在意的，例如毛巾的摆放，收银台的布置，饮水的安放等。最后是灯光的设置，应当选用明亮的有各种色彩的节能灯或者太阳灯，数量应当根据实际需要来定，灯光的照射应当冬季明快，夏天温馨，春秋舒畅。<br>美发店的装修要向让顾客回头光顾，还应当有热情的服务，室内的细节也应当去除冰冷，给人感觉温暖的感觉。<br>", "zhenglu", date("2014-02-18"), date("2014-02-18"), "其他", None, List("111", "2222"),true,None,false,true),
    	Blog(new ObjectId("532a8ef4a89ee221d679bdc1"),"生命是一场聚散","生命是一场聚散，初涉人世的第一声啼哭就拉开了聚的序幕。于是以后的岁月里，花开花落，<br>云卷云舒，就有了数不清的相遇、相识、相处、相爱、相恨，到最后的相离。", "zhenglu", new Date(), new Date(), "其他", None, List("111", "2222"),true,None,false,true),
        Blog(new ObjectId("532a8ef4a89ee221d679bdc2"),"白醋让你成为千年老妖","白醋 让你年轻10岁（我的天，可怕的妖怪就要产生了！）<br>经常见的白醋，其实蕴藏着十分深刻的美容护肤秘密。只巧妙利用，平凡普通的白醋，就可以让你容颜焕发，拥有<br>漂亮肌肤。", "zhenglu316", new Date(), new Date(), "健康养生", None,List("2222"), true, None,false,true),
        Blog(new ObjectId("532a8ef4a89ee221d679bdc3"),"妙方让女人年轻10岁","爱美的女性，谁不想使自己更年轻，并能留住一份健康的美？我们介绍的方法非常容易实现，只要你能够坚<br>持。想要年轻10岁？没有想象中那么困难,但是也要持之以恒哦!", "zhenglu5", new Date(), new Date(), "健康养生", None,List("2222"), true, None,false,true),
        Blog(new ObjectId("532a8ef4a89ee221d679bdc4"),"每天一杯酸奶","肌肤要“润”常用酸奶，酸奶中含有大量的乳酸，<br>作用温和，而且安全可靠。酸奶面膜就是利用这些乳酸，发挥剥离性面膜的功效，每日使用，会使肌肤柔嫩、细腻。做法也很简单，举手之劳而已。", "zhenglu6", new Date(), new Date(), "健康养生", None,List("2222"), true, None,false,true),
        Blog(new ObjectId("53195fb4a89e175858abce90"),"一杯豆浆","女人一到中年，体内的雌激素开始减退，这样就会加速体内的钙质流失，就会引起人体的各个功能的很快衰退，<br>每天坚持喝一杯豆浆，能增加人体雌激素的及时补充，有不会因人为的用药调节而形成对身体器官的其他副作用。 ", "zhenglu", new Date(), new Date(), "健康养生", None, List("111", "2222"),true,None,false,true),
        Blog(new ObjectId("53195fb4a89e175858abce91"),"每日泡一次脚","<b>可以在早上（只需20分钟），也可在晚上（最好1小时），用40度以上的热水加几滴醋泡脚，可以起到健身<br>安神之效。</b>", "zhenglu316", new Date(), new Date(), "健康养生", None,List("2222"), true, None,false,true)
    	).foreach(Blog.save)
    }
    

    if(Comment.findAll.isEmpty){
      Seq (
        Comment(new ObjectId("53195fb4a89e175858abce85"), 1, new ObjectId("5317c0d1d4d57997ce3e6d6a"), "布置的很精致，温馨。价格很适中，好地方，强力推荐", "zhenglu7", date("2014-02-18"), true),
        Comment(new ObjectId("53195fb4a89e175858abce87"), 3, new ObjectId("53195fb4a89e175858abce85"), "谢谢惠顾本店，您的满意是我们最大的幸福", "zhenglu316", date("2014-02-19"), true),
        Comment(new ObjectId("53195fb4a89e175858abce86"), 1, new ObjectId("5317c0d1d4d57997ce3e6d6b"), "真的的不错，下次还会来的。", "zhenglu8", date("2014-03-19"), true)
      ).foreach(Comment.save)
    }

    if(Message.findAll.isEmpty){
      Seq(
       Message(new ObjectId("531964e0d4d57d0a43771810"),"欢迎加入美范网！","欢迎你！！！！！！",new Date),
       Message(new ObjectId("531964e0d4d57d0a43771811"),"您有新粉丝关注你了！！","您有新粉丝关注你了，快去查看吧！！！",new Date),
       Message(new ObjectId("531964e0d4d57d0a43771812"),"看什么看，这是测试消息！","告诉你了是测试消息，你还打开，有病！！！！！！",new Date)
      ).foreach(Message.save)
    }
    
    if(!Stylist.findAll.isEmpty){
	    if(Image.findAll.isEmpty) {
	    	
	        // save picture of style
	        val stylefile = new File(play.Play.application().path() + "/public/images/style")
	        val stylefiles = Image.listAllFiles(stylefile)
	        for(styf <- stylefiles){
		        val styleImgId = Image.save(styf)
		        val style = Style.findAll.toList(stylefiles.indexOf(styf))
		        println(style.id)
		        Style.updateStyleImage(style,styleImgId)
	        }
	        
	        // save picture of stylist
	        val stylistfile = new File(play.Play.application().path() + "/public/images/stylist")
	        Image.files = Nil
	        val stylistfiles = Image.listAllFiles(stylistfile)
	        for(f <- stylistfiles){
		        val stylistImgId = Image.save(f)
		        val stylist = Stylist.findAll.toList(stylistfiles.indexOf(f))
		        Stylist.updateImages(stylist, stylistImgId)
	        }
	      
	     }
    }
  }
  
}
