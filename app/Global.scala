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
  def dateTime(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(str)
  
  /*---------------------------
   * Master Data For Initialization. 
   * 预先需要登录的主表数据
   *--------------------------*/
  def insertMaster() = {

    // For nowtime, we change the Table Defination frequently....
    // If you have some errors about the DB, try to drop the database [fashion-mongo] which store the site data, and 
    //     the [Picture] db which store all of the pictures the site using.

//   if(Province.findAll.isEmpty) {
//      Seq(
//          Province(new ObjectId("5317c0d1d4d57997ce3e6daa"), "江苏省"),
//          Province(new ObjectId("5317c0d1d4d57997ce3e6dab"), "Shandong")
//      ).foreach(Province.save)
//    }
//    
//    if(City.findAll.isEmpty) {
//      Seq(
//          City(new ObjectId("5317c0d1d4d57997ce3e6dca"), "Nanjing", "江苏省"),
//          City(new ObjectId("5317c0d1d4d57997ce3e6dcb"), "苏州市", "江苏省"),
//          City(new ObjectId("5317c0d1d4d57997ce3e6dcc"), "Wuxi", "江苏省"),
//          City(new ObjectId("5317c0d1d4d57997ce3e6dcd"), "Jinan", "Shandong"), 
//          City(new ObjectId("5317c0d1d4d57997ce3e6dce"), "Qingdao", "Shandong") 
//      ).foreach(City.save)
//    }
//    
//    if(Region.findAll.isEmpty) {
//      Seq(
//          Region(new ObjectId("5317c0d1d4d57997ce3e6dda"), "Xuanwu", "Shandong"),
//          Region(new ObjectId("5317c0d1d4d57997ce3e6ddb"), "Gulou", "Nanjing"),
//          Region(new ObjectId("5317c0d1d4d57997ce3e6ddc"), "高新区", "Jinan")
//      ).foreach(Region.save)
//    }    
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
       Position(new ObjectId("531964e0d4d57d0a43771412"),"首席技师"),
       Position(new ObjectId("531964e0d4d57d0a43771413"),"技师"),
       Position(new ObjectId("531964e0d4d57d0a43771414"),"高级助手"),
       Position(new ObjectId("531964e0d4d57d0a43771415"),"普通助手"),
       Position(new ObjectId("531964e0d4d57d0a43771416"),"助手")
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
          StyleColor(new ObjectId, "black", "黑色"),
          StyleColor(new ObjectId, "chocolate", "巧克力色"),
          StyleColor(new ObjectId, "brown", "棕色"),
          StyleColor(new ObjectId, "flax", "亚麻"),
          StyleColor(new ObjectId, "red", "红色"),
          StyleColor(new ObjectId, "alternative", "另类")
        ).foreach(StyleColor.save)
    }
    
    if(StyleLength.findAll.isEmpty) {
      Seq (
          StyleLength(new ObjectId, "super-short", "超短"),
          StyleLength(new ObjectId, "short", "短"),
          StyleLength(new ObjectId, "near-shoulder-length", "及肩"),
          StyleLength(new ObjectId, "shoulder-length", "齐肩"),
          StyleLength(new ObjectId, "mid-length", "中长"),
          StyleLength(new ObjectId, "long", "长")
        ).foreach(StyleLength.save)
    }
    
    if(StyleImpression.findAll.isEmpty) {
      Seq (
          StyleImpression(new ObjectId, "natural", "自然"),
          StyleImpression(new ObjectId, "intellectual", "知性"),
          StyleImpression(new ObjectId, "sweet", "甜美"),
          StyleImpression(new ObjectId, "fashion", "时尚"),
          StyleImpression(new ObjectId, "fresh", "清新"),
          StyleImpression(new ObjectId, "gorgeous", "华丽"),
          StyleImpression(new ObjectId, "personality", "个性")
        ).foreach(StyleImpression.save)
    }
    
    if(StyleAmount.findAll.isEmpty) {
      Seq (
          StyleAmount(new ObjectId, "much", "多"),
          StyleAmount(new ObjectId, "moderate", "适中"),
          StyleAmount(new ObjectId, "few", "少")
        ).foreach(StyleAmount.save)
    }
    
    if(StyleQuality.findAll.isEmpty) {
      Seq (
          StyleQuality(new ObjectId, "silky", "柔顺"),
          StyleQuality(new ObjectId, "greasy", "油性"),
          StyleQuality(new ObjectId, "dry", "干性"),
          StyleQuality(new ObjectId, "general", "一般")
        ).foreach(StyleQuality.save)
    }
    
    if(StyleDiameter.findAll.isEmpty) {
      Seq (
          StyleDiameter(new ObjectId, "bold", "偏粗"),
          StyleDiameter(new ObjectId, "moderate", "适中"),
          StyleDiameter(new ObjectId, "thin", "偏细")
        ).foreach(StyleDiameter.save)
    }
     
    if(FaceShape.findAll.isEmpty) {
      Seq (
          FaceShape(new ObjectId, "oval-face", "椭圆脸"),
          FaceShape(new ObjectId, "round-face", "圆脸"),
          FaceShape(new ObjectId, "long-face", "长脸"),
          FaceShape(new ObjectId, "square-face", "方脸"),
          FaceShape(new ObjectId, "pointed-face", "尖脸"),
          FaceShape(new ObjectId, "diamond-face", "菱形")
        ).foreach(FaceShape.save)
    }
    
    if(SocialStatus.findAll.isEmpty) {
      Seq (
          SocialStatus(new ObjectId, "evening-wear", "晚装"),
          SocialStatus(new ObjectId, "brief", "简约"),
          SocialStatus(new ObjectId, "star", "明星"),
          SocialStatus(new ObjectId, "street", "街拍"),
          SocialStatus(new ObjectId, "T-stage", "T台"),
          SocialStatus(new ObjectId, "others", "其它")
        ).foreach(SocialStatus.save)
    }
    
    if(Sex.findAll.isEmpty) {
      Seq (
          Sex(new ObjectId, "male"),
          Sex(new ObjectId, "female")
        ).foreach(Sex.save)
    }
    
    if(AgeGroup.findAll.isEmpty) {
      Seq (
          AgeGroup(new ObjectId, "one--fifteen", "1—15"),
          AgeGroup(new ObjectId, "one--twenty-five", "15—25"),
          AgeGroup(new ObjectId, "twenty-five--thirty-five", "25—35"),
          AgeGroup(new ObjectId, "thirty-five--forty-five", "35—45"),
          AgeGroup(new ObjectId, "forty-five--fifty-five", "45—55"),
          AgeGroup(new ObjectId, "fifty-five--", "55以上")
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

    if(ContMethodType.findAll.isEmpty){
      Seq (
        ContMethodType(new ObjectId, "QQ", "QQ"),
        ContMethodType(new ObjectId, "MSN", "MSN"),
        ContMethodType(new ObjectId, "we-chat", "微信"),
        ContMethodType(new ObjectId, "other", "其他联系方式")
      ).foreach(ContMethodType.save)
    }

    if(SearchByLengthForF.findAll.isEmpty){
      Seq (
        SearchByLengthForF(new ObjectId,"female","super-short",new ObjectId,"女-超短"),
        SearchByLengthForF(new ObjectId,"female","short",new ObjectId,"女-短"),
        SearchByLengthForF(new ObjectId,"female","near-shoulder-length",new ObjectId,"女-及肩"),
        SearchByLengthForF(new ObjectId,"female","shoulder-length",new ObjectId,"女-齐肩"),
        SearchByLengthForF(new ObjectId,"female","mid-length",new ObjectId,"女-中长"),
        SearchByLengthForF(new ObjectId,"female","long",new ObjectId,"女-长")
      ).foreach(SearchByLengthForF.save)
    }
    
    if(SearchByLengthForM.findAll.isEmpty){
      Seq (
        SearchByLengthForM(new ObjectId,"male","super-short",new ObjectId,"男-超短"),
        SearchByLengthForM(new ObjectId,"male","short",new ObjectId,"男-短"),
        SearchByLengthForM(new ObjectId,"male","near-shoulder-length",new ObjectId,"男-及肩"),
        SearchByLengthForM(new ObjectId,"male","shoulder-length",new ObjectId,"男-齐肩"),
        SearchByLengthForM(new ObjectId,"male","mid-length",new ObjectId,"男-中长"),
        SearchByLengthForM(new ObjectId,"male","long",new ObjectId,"男-长")
      ).foreach(SearchByLengthForM.save)
    }
   
    if(SearchByImpression.findAll.isEmpty){
      Seq (
        SearchByImpression(new ObjectId,"female","natural",new ObjectId,"女-自然"),
        SearchByImpression(new ObjectId,"female","intellectual",new ObjectId,"女-知性"),
        SearchByImpression(new ObjectId,"female","sweet",new ObjectId,"女-甜美"),
        SearchByImpression(new ObjectId,"female","fashion",new ObjectId,"女-时尚"),
        SearchByImpression(new ObjectId,"female","fresh",new ObjectId,"女-清新"),
        SearchByImpression(new ObjectId,"female","gorgeous",new ObjectId,"女-华丽"),
        SearchByImpression(new ObjectId,"female","personality",new ObjectId,"女-个性")
      ).foreach(SearchByImpression.save)
    }
    
    if(DefaultLog.findAll.isEmpty){
        Seq(
          DefaultLog(new ObjectId, new ObjectId)
        ).foreach(DefaultLog.save)
    }
  }
  /*---------------------------
   * Sample Data For Test. 
   * 测试数据
   *--------------------------*/
  def insertSampleData() {
    
    if(Salon.findAll.isEmpty) { 
      Seq(
        Salon(new ObjectId("530d7288d7f2861457771bdd"), SalonAccount("salon01", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "悦美月容吧", Some("悦容吧"), List("Hairdressing"), Some("www.yuerong.com"), Some("本地最红的美发沙龙！"), Some(PicDescription("以人为本，追求卓越","追求与合作者及广大消费者的“双赢”甚至“多赢”的结果。强调诚信为本、技术领先、优质服务，立足于美发、美容、美体、健康、体闲市场，高瞻远瞩，做强做大，随时为广大消费者提供最前端、最时尚的、最潮流和最周到的服务。","欢迎光临！")), Contact("051268320328", "王经理","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, false, false, true, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", Some(1), None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-01-12") ),
        Salon(new ObjectId("530d7292d7f2861457771bde"), SalonAccount("salon02", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "千美千寻吧", Some("美寻吧"), List("Hairdressing"), Some("www.meixun.com"), Some("选择我们，选择美丽！"),Some(PicDescription("真诚  真心  真爱","千寻精神即为对社会对行业的承诺，包涵对消费者真诚，用它作为基石；对顾客的真心，用真心来服务；对员工的真爱，用真爱去支持。它体现的是一份宽容，是一份理解，是一份友爱，是一份关怀，是一份和谐，是一份美丽。","欢迎光临！")), Contact("051268320328", "李经理","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("江苏省", Option("徐州市"), Option("邳州市"), None, "南京路9号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, true, true, false, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", None, None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),None),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-03-12") ),
        Salon(new ObjectId("530d7288d7f2861457771bdf"), SalonAccount("salon03", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "忆荣吧", Some("忆荣吧"), List("Hairdressing"), Some("www.yirong.com"), Some("追忆往昔容颜"), Some(PicDescription("精诚团结，热忱服务，高效运作"," 企业内部精诚团结，企业与合作者通力合作，企业经营行为规范化。通过亲密的配合，及规范化的经营，追求高效率地运作，以保证在市场竞争中立于不败之地。","欢迎光临！")),Contact("051268320328", "冯先生","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("云南省", Option("昆明市"), Option("盘龙区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, false, false, true, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", Some(1), None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),None),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-01-12") ),
        Salon(new ObjectId("530d7292d7f2861457771aaa"), SalonAccount("salon04", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "虞美人", Some("虞美人"), List("Hairdressing"), Some("www.yumeiren.com"), Some("做个美丽的人"),Some(PicDescription("诚信为本，优质创新，引领时尚","最新的潮流资讯、最顶尖的时尚发布、最贴心的造型设计、最合理的价格优势、是永琪品牌的核心之一。我们立志以最时尚的理念，打造最生活的状态，塑造最自信的你！","欢迎光临！")), Contact("051268320328", "马先生","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("湖南省", Option("长沙市"), Option("天心区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, true, true, false, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", None, None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),None),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-03-12") ),
        Salon(new ObjectId("530d7292d7f2861457771bbb"), SalonAccount("salon05", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "美吧", Some("美吧"), List("Hairdressing"), Some("www.meiba.com"), Some("滋润，护理，完美女人，从你做起！"),Some(PicDescription("专业、时尚、 亲和、 信赖","为了您更美好的生活，我们一直在关注，我们一直在努力！您的肯定是我们最大的收获，您的信赖更是我们最满足的奖赏！","欢迎光临！")), Contact("051268320328", "张女士","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, true, true, false, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", None, None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),None),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-03-12") )
     ).foreach(Salon.save)
    }
      
   if(Stylist.findAll.isEmpty) { 
      Seq(
         Stylist(new ObjectId, new ObjectId("530d8010d7f2861457771bf8"), 5, List(new IndustryAndPosition(new ObjectId,"高级美发师","店长")), List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"用思想设计发型，用语言解释发型。","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪", (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId, new ObjectId("53202c29d4d5e3cd47efffd3"), 5, List(new IndustryAndPosition(new ObjectId,"初级美发师","店长")), List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"先求稳，再求准，最后求快。","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪", (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId, new ObjectId("53202c29d4d5e3cd47efffd4"), 5, List(new IndustryAndPosition(new ObjectId,"顶级美发师","店长")), List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"先了解发型的形，在做发型的型。 先求稳，再求准，最后求快。 先看上下，再看左右，最后看手位。","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪", (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId, new ObjectId("53202c29d4d5e3cd47efffd9"), 5, List(new IndustryAndPosition(new ObjectId,"初级美发师","店长")), List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"☆剪发：宁长勿短，宁厚勿薄，宁低勿高。 ","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪", (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId, new ObjectId("53202c29d4d5e3cd47efffd8"), 5, List(new IndustryAndPosition(new ObjectId,"初级美发师","店长")), List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"☆烫发：卷度宁小勿大，卷芯选择/宁小勿大。","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪", (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId, new ObjectId("53202c29d4d5e3cd47efffd7"), 5, List(new IndustryAndPosition(new ObjectId,"中级美发师","店长")), List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"☆染发：色度/宁深一度，不浅半度。色调/宁暗勿亮。","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪", (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId, new ObjectId("53202c29d4d5e3cd47efffd6"), 5, List(new IndustryAndPosition(new ObjectId,"中级美发师","店长")), List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"☆美发师的成长经历 =模仿 + 吸收 +发挥。","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪", (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ),
         Stylist(new ObjectId, new ObjectId("53202c29d4d5e3cd47efffd5"), 5, List(new IndustryAndPosition(new ObjectId,"特级美发师","店长")), List("小清新"), List("少年"), List("烫发", "染发", "卷发"), List("男", "女"), List("1~10", "10~20", "20~30", "30~40"),"苦和甜来自于外界，而坚强则来自内心，来自于一个人坚持不懈的努力！","按照头皮通过样式建议☆脑袋矿泉根据（季节）的麻烦的建议，一个健康的状态样式建议☆日本四季可再生按照客户的后顾之忧，即使从后面的房子，改善☆（头按摩）","*（自主开发，本作的，运动员都特别喜欢）（印象系统，人类系统特别喜欢）阅读*电影DVD观看是一个（*易清洁，整齐*厨师，健康食物卡在）","建议适合女性的身材和喜好的麻烦，希望每一位客户，下一次不只是这一次，它建的故事，包括发型到下一个，但一☆砍你强​​调“易用性对待”，风格建议♪", (List(new OnUsePicture(new ObjectId, "logo", Some(1), None))), true, true ) 
      ).foreach(Stylist.save)
    }
    
   if(User.findAll.isEmpty){
      Seq(
       User(new ObjectId("530d8010d7f2861457771bf8"),"demo01","维达沙宣","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"wzw1991@126.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"Administrator", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd3"),"demo02","苏小魂","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"wzw19910109@163.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"Administrator", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd4"),"demo03","阿哲","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd9"),"demo04","李莫愁","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd8"),"demo05","西门吹雪","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd7"),"demo06","叶孤城","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd6"),"demo07","陆小风","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd5"),"demo08","花满楼","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd0"),"demo09","独孤求败","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"normalUser","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffe1"),"demo10","中原一点红","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"normalUser","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffe2"),"demo11","红叶","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"normalUser","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true)
      ).foreach(User.save)
    }
    
    if(SalonAndStylist.findAll.isEmpty){
      Seq(
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bf8"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("53202c29d4d5e3cd47efffd3"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("53202c29d4d5e3cd47efffd9"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("53202c29d4d5e3cd47efffd4"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("53202c29d4d5e3cd47efffd8"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7292d7f2861457771bde"), new ObjectId("53202c29d4d5e3cd47efffd7"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7292d7f2861457771bde"), new ObjectId("53202c29d4d5e3cd47efffd6"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, None, true),
      SalonAndStylist(new ObjectId, new ObjectId("530d7292d7f2861457771bde"), new ObjectId("53202c29d4d5e3cd47efffd5"), List(new IndustryAndPosition(new ObjectId,"美甲师","店长")), new Date, Option(new Date), false)
      ).foreach(SalonAndStylist.save)
    }
    
    
    if(Style.findAll.isEmpty) { 
      Seq(
        Style(new ObjectId("530d828cd7f2861457771c0b"), "毛寸发型", new ObjectId("530d8010d7f2861457771bf8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple"),"super-short",List("black","chocolate","brown","flax","red","alternative"),List("much","moderate","few"),List("greasy","general"),List("bold","thin"),List("round-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("530d828cd7f2861457772c0c"), "分头发型", new ObjectId("530d8010d7f2861457771bf8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"gorgeous",List("Supple"),"short",List("black","chocolate","brown"),List("much","moderate","few"),List("greasy","general"),List("moderate","thin"),List("long-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("530d828cd7f2861457771c0d"), "纹理烫发型", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple"),"short",List("black","chocolate","brown"),List("much","moderate","few"),List("greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a02ed"), "背头发型", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"long",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face","diamond-face"),"凌乱中带有一丝淡淡的忧伤！",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a02ee"), "皮卡路发型", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"long",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin","moderate"),List("long-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a021d"), "板寸发型", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"super-short",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","evening-wear"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a021e"), "烟花烫发型", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple","Perm"),"long",List("flax"),List("much","moderate","few"),List("silky","greasy","dry","general"),List("bold","thin","moderate"),List("long-face","round-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","others"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a023d"), "朋克发型", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"short",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","evening-wear"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a023e"), "蓬松定位烫发型", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"mid-length",List("black","chocolate"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin"),List("long-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","others"),date("2014-03-12"),true),
        Style(new ObjectId("533134db9aa6b4dfc54a02ef"), "青春短寸无敌", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple"),"short",List("black","chocolate"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0374"), "斜刘海披肩发型", new ObjectId("53202c29d4d5e3cd47efffd8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"super-short",List("black","chocolate"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","evening-wear"),date("2014-03-12"),true),
        Style(new ObjectId("533134db9aa6b4dfc54a022f"), "齐刘海内扣发型", new ObjectId("53202c29d4d5e3cd47efffd8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple","Perm"),"short",List("black","chocolate"),List("much","moderate","few"),List("silky","greasy","dry","general"),List("bold","thin","moderate"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0324"), "斜刘海中短发型", new ObjectId("53202c29d4d5e3cd47efffd8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"intellectual",List("Supple","Perm"),"short",List("red"),List("moderate","few"),List("silky","greasy","general"),List("bold","thin","moderate"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a022d"), "偏分刘海披肩发", new ObjectId("53202c29d4d5e3cd47efffd8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"intellectual",List("Supple","Perm"),"shoulder-length",List("red"),List("moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a022e"), "偏分刘海中长发", new ObjectId("53202c29d4d5e3cd47efffd7"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple"),"mid-length",List("red"),List("moderate","few"),List("silky","greasy","general"),List("bold","thin"),List("long-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0375"), "碎发", new ObjectId("53202c29d4d5e3cd47efffd7"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"intellectual",List("Supple"),"mid-length",List("brown","flax"),List("moderate","few"),List("greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","others"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0376"), "波波头", new ObjectId("53202c29d4d5e3cd47efffd7"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"personality",List("Supple","Perm"),"super-short",List("brown","flax"),List("much"),List("silky","greasy","general"),List("bold","thin","moderate"),List("round-face","oval-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0377"), "丸子头梳法", new ObjectId("53202c29d4d5e3cd47efffd5"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"short",List("chocolate","brown","flax","red"),List("much"),List("silky","greasy","dry","general"),List("bold","thin"),List("long-face","oval-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","others"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0378"), "清纯发型", new ObjectId("53202c29d4d5e3cd47efffd5"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"personality",List("Supple","Perm"),"short",List("chocolate","brown","flax","red"),List("much"),List("silky","greasy","general"),List("bold","thin","moderate"),List("round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","evening-wear"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0379"), "可爱梨花头", new ObjectId("53202c29d4d5e3cd47efffd5"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple"),"shoulder-length",List("chocolate","brown","flax","red"),List("much"),List("silky","greasy","general"),List("bold"),List("oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a0373"), "大卷发型", new ObjectId("53202c29d4d5e3cd47efffd5"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"personality",List("Supple","Perm"),"shoulder-length",List("chocolate","brown","flax","red"),List("much"),List("silky","greasy","general"),List("bold"),List("long-face","round-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("530d828cd7f2861457761c0b"), "毛寸发型-男", new ObjectId("530d8010d7f2861457771bf8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple"),"super-short",List("black","chocolate","brown","flax","red","alternative"),List("much","moderate","few"),List("greasy","general"),List("bold","thin"),List("round-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("530d828cd7f2861457762c0c"), "分头发型-男", new ObjectId("530d8010d7f2861457771bf8"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"gorgeous",List("Supple"),"short",List("black","chocolate","brown"),List("much","moderate","few"),List("greasy","general"),List("moderate","thin"),List("long-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("530d828cd7f2861457761c0d"), "纹理烫发型-男", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple"),"short",List("black","chocolate","brown"),List("much","moderate","few"),List("greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a12ed"), "背头发型-男", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"long",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face","diamond-face"),"凌乱中带有一丝淡淡的忧伤！",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a12ee"), "皮卡路发型-男", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"long",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin","moderate"),List("long-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a121d"), "板寸发型-男", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"super-short",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","evening-wear"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a121e"), "烟花烫发型-男", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple","Perm"),"long",List("flax"),List("much","moderate","few"),List("silky","greasy","dry","general"),List("bold","thin","moderate"),List("long-face","round-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","others"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a123d"), "朋克发型-男", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"short",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","evening-wear"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a123e"), "蓬松定位烫发型-男", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"mid-length",List("black","chocolate"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin"),List("long-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","others"),date("2014-03-12"),true),
        Style(new ObjectId("533134db9aa6b4dfc54a12ef"), "青春短寸无敌-男", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple"),"short",List("black","chocolate"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a1375"), "碎发-男", new ObjectId("53202c29d4d5e3cd47efffd7"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"intellectual",List("Supple"),"mid-length",List("brown","flax"),List("moderate","few"),List("greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","others"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a1376"), "波波头-男", new ObjectId("53202c29d4d5e3cd47efffd7"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"personality",List("Supple","Perm"),"super-short",List("brown","flax"),List("much"),List("silky","greasy","general"),List("bold","thin","moderate"),List("round-face","oval-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533151209aa6b4dfc54a1377"), "丸子头梳法-男", new ObjectId("53202c29d4d5e3cd47efffd5"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"short",List("chocolate","brown","flax","red"),List("much"),List("silky","greasy","dry","general"),List("bold","thin"),List("long-face","oval-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","others"),date("2014-03-12"),true)
      ).foreach(Style.save)
    }
    if(Coupon.findAll.isEmpty) {
      Seq(
         Coupon(new ObjectId("5317c0d1d4d57997ce3e6d6a"), "xjc01", "[兄弟公司☆宝石]小脸斩+香薰头部按摩5250日元允许西装", new ObjectId("530d7288d7f2861457771bdd"), List(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true), Service(new ObjectId("5316bb36d4d57997ce3e6d49"), "离子烫", "离子烫对头发保护性最强", "Perm", new ObjectId("530d7288d7f2861457771bdd"), 90, 100, date("2014-03-31"), null, true)), 110, 100, 90, date("2014-03-01"), date("2014-03-31"), "你看到的HOTPEPPER美容♪字", "在预订时间", "◆体验到自己可爱虽然是♪我们自然要授予风格，你想仔细咨询☆◆您可以选择洗发水是一种根据请求的皮肤和头发，定型剂", true), 
         Coupon(new ObjectId("5317c0d1d4d57997ce3e6d6b"), "xjc02", "[兄弟公司☆1号热门]斩+颜色+温和的芳香按摩头11340日元", new ObjectId("530d7288d7f2861457771bdd"), List(Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "简单清洗", "Wash", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true) , Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "对发梢进行染色或者挑染", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 90, 120, date("2014-03-31"), null, true), Service(new ObjectId("5316bb36d4d57997ce3e6d49"), "离子烫", "离子烫对头发保护性最强", "Perm", new ObjectId("530d7288d7f2861457771bdd"), 90, 100, date("2014-03-31"), null, true)), 230, 190, 120, date("2014-03-01"), date("2014-03-31"), "你看到的HOTPEPPER美容♪字", "在预订时间", "◆无♪◆长费咨询和那些敏感的皮肤☆◆最好的季节Tsuyakara成为各1号和皮肤的每一个颜色，眼睛的颜色，从时尚，更是对皮肤的刺激性是一个关注的晒后", true)
      ).foreach(Coupon.save)
    }
    if(Menu.findAll().isEmpty) {
      Seq(
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d61"), "菜单1", "刘海剪 + 离子烫", new ObjectId("530d7288d7f2861457771bdd"), List(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true), Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "对发梢进行染色或者挑染", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 90, 120, date("2014-03-31"), null, true)), 130, 100, date("2014-03-19"), null, true),
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d62"), "菜单2", "刘海剪 + 3D彩色", new ObjectId("530d7288d7f2861457771bdd"), List(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true), Service(new ObjectId("5316be8ed4d57997ce3e6d52"), "3D彩色", "染后能让头发更有立体感", "Dye", new ObjectId("530d7288d7f2861457771bdd"), 80, 150, date("2014-03-31"), null, true)), 90, 160, date("2014-03-19"), null, true),
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d63"), "菜单3", "刘海剪 + 简洗", new ObjectId("530d7288d7f2861457771bdd"), List(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "专门剪刘海", "Cut", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true), Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "简单清洗", "Wash", new ObjectId("530d7288d7f2861457771bdd"), 10, 10, date("2014-03-31"), null, true)), 20, 20, date("2014-03-19"), null, true)
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
    
    if(MyFollow.findAll.isEmpty){
      Seq(
        MyFollow(new ObjectId("532f9889d4d5f03ea49463fd"), new ObjectId("530d8010d7f2861457771bf8"),new ObjectId("530d7288d7f2861457771bdd"),"salon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463fe"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("53202c29d4d5e3cd47efffe1"),"user"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e1"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("530d7288d7f2861457771bdd"),"salon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e2"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("530d8010d7f2861457771bf8"),"stylist"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463a3"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("53202c29d4d5e3cd47efffd3"),"stylist"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e4"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("5317c0d1d4d57997ce3e6d6a"),"coupon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e5"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("5317c0d1d4d57997ce3e6d6b"),"coupon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e6"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("53195fb4a89e175858abce92"),"blog"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e7"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("532a8ef4a89ee221d679bdc1"),"blog"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e8"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("532a8ef4a89ee221d679bdc2"),"blog"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e9"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("530d828cd7f2861457771c0b"),"style"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e0"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("530d828cd7f2861457772c0c"),"style"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463ff"), new ObjectId("53202c29d4d5e3cd47efffe1"),new ObjectId("53202c29d4d5e3cd47efffd0"),"user"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463ed"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("5317c0d1d4d57997ce3e6d6a"),"coupon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e1"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("5317c0d1d4d57997ce3e6d6b"),"coupon"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e3"), new ObjectId("530d8010d7f2861457771bf8"),new ObjectId("53195fb4a89e175858abce90"),"blog"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e4"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("53195fb4a89e175858abce91"),"blog"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e5"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d828cd7f2861457771c0b"),"style"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e6"), new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d828cd7f2861457772c0c"),"style")
      ).foreach(MyFollow.save)
    }
    if(Blog.findAll.isEmpty){
      Seq(
        Blog(new ObjectId("53195fb4a89e175858abce92"),"美发店装修设计注意事项","美发店是每个人都要去的地方，街上各种美发店大多数人们会怎样选择呢，个人觉得大众人群会选择装修店面好一些但也不是很豪华的那种。不同的美发店在装修设计上是改针对不同的客户群体的，那么在美发店的整体设计中都应该怎样做呢?<br>首先在街面上的自然是店面和招牌，对于最显眼的招牌来说，它直接反应的是店面的形象和店面的风格，因此应该着重注意一下色彩带的搭配，现在大多的以深色的主体配合其他颜色，以引人注目为主要目的。<br>再有就是重要的店面风格，不管选择什么样的风格，重要的是表现出店面的文化和专业的气氛。还应当和周围的店面在风格上区分开来，以显示不一样的经营，装饰用的话也不要尽选用那些美女帅哥的发型图片，安放一些工艺品和名人字画也是能够突出店面的文化档次。店面内应当设施适当的休息区域能够让客人坐下来休息，最好还是能够看到工作人员的工作，了解店面的服务质量，店面内的装修应当明快清新，暖色调能够使人感到轻松温馨，并且具有着依赖感和安全感，当然了店面内的设计还是应该注意所针对的消费群体的档次，不能太低，也不能让人看到后忘而却步。<br>对于美发店来说，店面内重要的就是操作区，它占有者店面一半以上的面积，对于操作区的设计，这里是直接服务顾客的地方，因此应当宽松干净和舒适，还应当考虑到行走的方便和顺畅，而且区域内的家具等尽量选择精美一些的，外星应当独特而色调统一。还有就是一些功能性的服务设施，例如卫生间，虽然大多客人可能用不到，但是还是应该重视的，这也会给顾客留下深刻的印象，卫生间可以相对的豪华一些，不能有异味，在卫生间内还可以挂上自己店的经营理念等具有销售意义的字画。当然店面内的一些小的注意事项也是应当在意的，例如毛巾的摆放，收银台的布置，饮水的安放等。最后是灯光的设置，应当选用明亮的有各种色彩的节能灯或者太阳灯，数量应当根据实际需要来定，灯光的照射应当冬季明快，夏天温馨，春秋舒畅。<br>美发店的装修要向让顾客回头光顾，还应当有热情的服务，室内的细节也应当去除冰冷，给人感觉温暖的感觉。<br>", "demo02", date("2014-02-18"), date("2014-02-18"), "其他", None, List("111", "2222"),true,Option(true),false,true),
        Blog(new ObjectId("532a8ef4a89ee221d679bdc1"),"生命是一场聚散","生命是一场聚散，初涉人世的第一声啼哭就拉开了聚的序幕。于是以后的岁月里，花开花落，云卷云舒，就有了数不清的相遇、相识、相处、相爱、相恨，到最后的相离。", "demo02", date("2014-02-19"), new Date(), "其他", None, List("111", "2222"),true,Option(true),false,true),
        Blog(new ObjectId("532a8ef4a89ee221d679bdc2"),"白醋让你成为千年老妖","白醋 让你年轻10岁（我的天，可怕的妖怪就要产生了！）<br>经常见的白醋，其实蕴藏着十分深刻的美容护肤秘密。只巧妙利用，平凡普通的白醋，就可以让你容颜焕发，拥有漂亮肌肤。", "demo01", date("2014-03-18"), new Date(), "健康养生", None,List("2222"), true, Option(true),false,true),
        Blog(new ObjectId("532a8ef4a89ee221d679bdc3"),"妙方让女人年轻10岁","爱美的女性，谁不想使自己更年轻，并能留住一份健康的美？我们介绍的方法非常容易实现，只要你能够坚持。想要年轻10岁？没有想象中那么困难,但是也要持之以恒哦!", "demo05", date("2014-04-01"), new Date(), "健康养生", None,List("2222"), true, Option(true),false,true),
        Blog(new ObjectId("532a8ef4a89ee221d679bdc4"),"每天一杯酸奶","肌肤要“润”常用酸奶，酸奶中含有大量的乳酸，作用温和，而且安全可靠。酸奶面膜就是利用这些乳酸，发挥剥离性面膜的功效，每日使用，会使肌肤柔嫩、细腻。做法也很简单，举手之劳而已。", "demo06", date("2014-04-02"), new Date(), "健康养生", None,List("2222"), true, Option(true),false,true),
        Blog(new ObjectId("53195fb4a89e175858abce90"),"一杯豆浆","女人一到中年，体内的雌激素开始减退，这样就会加速体内的钙质流失，就会引起人体的各个功能的很快衰退，每天坚持喝一杯豆浆，能增加人体雌激素的及时补充，有不会因人为的用药调节而形成对身体器官的其他副作用。 ", "demo02", date("2014-03-31"), new Date(), "健康养生", None, List("111", "2222"),true,Option(true),false,true),
        Blog(new ObjectId("53195fb4a89e175858abce91"),"每日泡一次脚","<b>可以在早上（只需20分钟），也可在晚上（最好1小时），用40度以上的热水加几滴醋泡脚，可以起到健身安神之效。</b>", "demo01", date("2014-03-30"), new Date(), "健康养生", None,List("2222"), true, Option(true),false,true)
        ).foreach(Blog.save)
    }
    
    if(Comment.findAll.isEmpty){
      Seq (
       Comment(new ObjectId("53195fb4a89e175858abce85"), 2, new ObjectId("5317c0d1d4d57997ce3e6d6a"), "布置的很精致，温馨。价格很适中，好地方，强力推荐", 5,5,5,5,5,"demo07", date("2014-02-18"), true),
        Comment(new ObjectId("53195fb4a89e175858abce87"), 3, new ObjectId("53195fb4a89e175858abce85"), "谢谢惠顾本店，您的满意是我们最大的幸福", 0,0,0,0,0,"demo01", date("2014-02-19"), true),
        Comment(new ObjectId("53195fb4a89e175858abce86"), 2, new ObjectId("5317c0d1d4d57997ce3e6d6b"), "发型师能根据我的脸型，发质提出多种方案供选择。最后剪出的发型非常适合自己。真的不错，下次还会来的。", 5,5,5,5,5,"demo08", date("2014-03-19"), true),
        Comment(new ObjectId, 2, new ObjectId("5317c0d1d4d57997ce3e6d6b"), "环境优雅，服务态度好。最重要的是发型师的技术非常不错。非常满意，下次还会来的。", 5,5,5,5,5,"demo07", date("2014-03-31"), true),
        Comment(new ObjectId, 2, new ObjectId("5317c0d1d4d57997ce3e6d6b"), "这家店是我有史以来去过的最好的一家店，强力推荐大家一起去。", 5,5,5,5,5,"demo07", date("2014-04-01"), true)
      ).foreach(Comment.save)
    }
    if(Message.findAll.isEmpty){
      Seq(
       Message(new ObjectId("531964e0d4d57d0a43771810"),"欢迎加入美范网！","欢迎你！！！！！！",new Date),
       Message(new ObjectId("531964e0d4d57d0a43771811"),"您有新粉丝关注你了！！","您有新粉丝关注你了，快去查看吧！！！",new Date),
       Message(new ObjectId("531964e0d4d57d0a43771812"),"您有新的消息！","新消息！", new Date)
      ).foreach(Message.save)
    }

    if(Question.findAll.isEmpty) {
      Seq(
        Question(new ObjectId(), "发型师如何注册", "发型师注册请先申请成为网站一般用户，在申请身份中填入发型师。此时只是准发型师状态，需要在个人管理菜单中的【邀请我的店铺】中同意店铺邀请，才可以正式成为发型师，享有发型师的特殊权限，包括上传发型等等。另外，如果没有收到店铺邀请，您可以在个人管理菜单中的【申请加入店铺】中向输入自己所在店铺的ID检索并作出申请，经店铺批准后方可正式成为发型师身份。", new Date, 1, true),
        Question(new ObjectId(), "店铺用户如何注册", "店铺用户需要先在网站上的店铺注册处注册，成为准店铺用户状态，此时店铺可以完善自己店铺的地址，发型，发型师等各种信息。然后上传店铺营业执照或者组织机构代码证扫描件，经网站管理平台认证后即可开通。开通后店铺的所有信息可以被用户看到，并可以被检索，预约。", new Date, 1, true),
        Question(new ObjectId(), "如何根据详细条件检索", "请这样根据详细条件检索：......", new Date, 1, true),
        Question(new ObjectId(), "预约后想取消", "请按以下步骤取消：......", new Date, 1, true),
        Question(new ObjectId(), "如何获取店铺权限", "根据资料完善度，网站将开放您不同的权限等级，如您可以按照相应权限发布服务、发型以及接受顾客预约，只有所有资料全部完善，您才能享受网站所有服务", new Date, 1, true)
      ).foreach(Question.save)

    }

    if(Notice.findAll.isEmpty) {
       Seq(
         Notice(new ObjectId(), "网站公告", "网站公告板块主要显示本网站的新闻、公告、通知等信息。","admin01", new Date,true),
         Notice(new ObjectId(), "网站规划", "本站第一阶段于2014-04-20正式上线，本阶段网站建设的重点放在网站功能的完整性上，后期会再针对网站的预约功能等其他问题做进一步的完善。敬请期待~","admin01", new Date,true),
         Notice(new ObjectId(), "网站目标", "一站式信息服务平台，最前沿美容美发咨询的汇集地！","admin01", new Date,true)
          ).foreach(Notice.save)
      }
   
    if(Info.findAll.isEmpty) {
       Seq(
//         Info(new ObjectId(), "巴黎欧莱雅-新的市场新的挑战", "对于传统意义上的领导品牌，一个事实正摆在眼前，传统营销形式所积累的受众正在慢慢老去，最受数字媒体所影响的年轻一代正在成为主流消费群体，品牌营销该何去何从？", new ObjectId, new ObjectId, new Date, 1, true),
         Info(new ObjectId(), "巴黎欧莱雅-新的市场新的挑战", "对于传统意义上的领导品牌，一个事实正摆在眼前，传统营销形式所积累的受众正在慢慢老去，最受数字媒体所影响的年轻一代正在成为主流消费群体，品牌营销该何去何从？", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 1, true),
         Info(new ObjectId(), "雅诗兰黛'鲜亮焕采'", "'鲜亮焕采'系列的全新单品惊艳四方，给到场的媒体嘉宾带来独特的护肤体验。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 1, true),
         Info(new ObjectId(), "油状护发到底该怎么用", "随着女性对于头发护理的愈加重视，越来越多的新型护发产品出现在市面上，尤其是具有免洗功效的便携护发油。所谓护发油是给头发以充分保湿和营养的头发护理产品，并且能够有效防止头发白天收到外界紫外线和空气污染的侵害，但是你真的用对了吗？", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 1, true),
         Info(new ObjectId(), "复刻麻豆气质扎发", "不少女明星都有自己的一套扎发造型。今天小编就为大家挑选了三款发型扎发，一起复刻她们的扎发造型，展现出独有的俏丽风情。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 1, true),
         Info(new ObjectId(), "显瘦立体 清爽短发+侧编刘海", "忙碌工作了一周，到了周末当然要好好的放松一下。和闺蜜约个会、聚个餐想必是最能让自己身心愉悦的事。可即便是最休闲的周末，妆发上也将就不得！今天教给你小有讲究的清爽短发发型。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 1, true),
         Info(new ObjectId(), "简单SPA解决常见头皮问题", "头皮就像我们的皮肤一样需要精心的呵护，不管每天你都在忙碌的做任何事情，可以会忽略了对头发的护理，导致最后拥有一头看了就厌烦的头发，我们现在就介绍一种简单快捷，只需15分钟就能帮助你护发的方法，只要天天坚持给秀发SPA，秀发立即焕然一新哦！", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 1, true),
         Info(new ObjectId(), "不失庄重的随性编发", "职场中慢慢历练的你总想给人种成熟干练的感觉，除了个人工作能力外，妆发造型也是可以为你加分的一部分。小编为工作中的你介绍职场超match发型。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 1, true),
         Info(new ObjectId(), "非手术整容已成为整形美容新宠", "美丽永远是求美者永恒的追求，这种追求促进了整形美容行业的发展。如今，整形美容机构林立，整形观念层出不穷，整容的手段更是日新月异。面对那名目繁多的整形美容项目，许多求美者越来越聪明，选择非手术无创整形实现自己的美丽梦。非手术整容成为时下整形美容新宠。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 2, true),
         Info(new ObjectId(), "产后修复整形术成为未来几年流行趋势", "如果细心观察会发现，最近一两年孕妇特别多，而且绝大部分是80后的美丽孕妈咪。爱美的80后群体已经到了成家立业的阶段，生孩子也在计划之中。80后爱美女士们都希望自己成为“辣妈”，所以未来几年内，产后修复整形术会继续流行。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 2, true),
         Info(new ObjectId(), "化妆同时护肤的7个小技巧", "有很多人认为化妆品是毁容的产品，尤其是粉底液、BB霜一类的底妆产品，会致痘，堵塞毛孔，导致各种皮肤问题。但其实这样的说法并不是完全正确的，在了解自己肤质的情况下选对的产品使用，不但可以帮你修饰瑕疵，甚至对皮肤还会有保护的作用！", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 2, true),
         Info(new ObjectId(), "OL久坐不动 如何减掉背部肉肉", "为什么大腿的肉肉这么难减？用什么方法才能减小肚子？你也有这样的减肥烦恼吗？女人频道减肥沙龙特别为苦恼的姐妹们推出了“减肥帮帮团”栏目。来自减肥沙龙的减肥顾问，随时准备帮你回答关于减肥的所有问题哦。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 2, true),
         Info(new ObjectId(), "双眼皮修复手术 5种眼部问题的修复方法", "随着整形医疗科技的逐步成熟，以及人们对整形美容业心里认知度的逐步提高，如今“割双眼皮”在大家看来已是微不足道的外科小手术。虽然手术并不复杂，但由于患者个体差异以及主刀医生审美观念的不同，做出来的双眼皮效果并不能保证让每一个患者都感到完全称心实意。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 2, true),
         Info(new ObjectId(), "谷歌眼镜新应用 整容手术好帮手", "在纽约的一家美容医院工作的整容医生Dr. Ramtin Kassir表示在他的工作中，Google Glass在他的工作中能起到非常实用的帮助。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 2, true),
         Info(new ObjectId(), "医美四招去疤痕 再现无暇肌肤", "皮肤创伤、烧伤、溃疡等愈合后留下的痕迹被称为疤痕，有些疤痕深深地留在了皮肤上很难去除，这同时也给人们内心增加了一道伤痕。疤痕修复手术可以去除疤痕，常见的疤痕手术有植皮手术、磨削去疤痕、切除缝合术及皮肤扩张器四种手术，不同的疤痕情况要选择不同的手术方式。", new ObjectId, List(new OnUsePicture(new ObjectId, "logo", Some(1), None)), new Date, 2, true)
       ).foreach(Info.save)
      }

    /*
     * TODO TEST DATA: Pictures.
     */
    if(!Style.findAll.isEmpty){
        if(Image.fuzzyFindByName("style").isEmpty) {
            // save picture of style
            val stylefile = new File(play.Play.application().path() + "/public/images/style")
            val stylefiles = Image.listFilesInFolder(stylefile)
            for((styf, index) <- stylefiles.zipWithIndex){
                if(index < Style.findAll.toList.length) {
                    val styleImgId = Image.save(styf)
                    val style = Style.findAll.toList(index)
                    Style.updateStyleImage(style,styleImgId)
                }
            }
        }
    }
    
    if(!Info.findAll.isEmpty){
        if(Image.fuzzyFindByName("info").isEmpty) {
            // save picture of info
            val infofile = new File(play.Play.application().path() + "/public/images/info")
            val infofiles = Image.listFilesInFolder(infofile)
            for((infof, index) <- infofiles.zipWithIndex){
                if(index < Info.findAll.toList.length) {
                    val infoImgId = Image.save(infof)
                    val info = Info.findAll.toList(index)
                    Info.updateInfoImage(info,infoImgId)
                }
            }
        }
    }

    if(!Stylist.findAll.isEmpty){
        if(Image.fuzzyFindByName("stylist").isEmpty) {
            // save picture of stylist
            val stylistfile = new File(play.Play.application().path() + "/public/images/stylist")
            val stylistfiles = Image.listFilesInFolder(stylistfile)
            for((f, index) <- stylistfiles.zipWithIndex){
                if(index < Stylist.findAll.length) { 
                    val stylistImgId = Image.save(f)
                    val stylist = Stylist.findAll.toList(index)
                    Stylist.updateImages(stylist, stylistImgId)
                }
            }
        }
    }

    if(!Salon.findAll.isEmpty){
        if(Image.fuzzyFindByName("salon").isEmpty) {
          //save picture of salon
            val logofile = new File(play.Play.application().path() + "/public/images/store/logo")
            val logofiles = Image.listFilesInFolder(logofile)
            for((l, index) <- logofiles.zipWithIndex){
                if(index < Salon.findAll.length) {
                    val logoImgId = Image.save(l)
                    val logo = Salon.findAll.toList(index)
                    Salon.updateSalonLogo(logo, logoImgId)
                }
            }       
         }
    }
    
    if(!SearchByImpression.findAll.isEmpty){
        if(Image.fuzzyFindByName("impression-male").isEmpty) {
            // save picture of style
            val stylefile = new File(play.Play.application().path() + "/public/images/style/styleForImpression")
            val stylefiles = Image.listFilesInFolder(stylefile)
            for((styf, index) <- stylefiles.zipWithIndex){
                if(index < Style.findAll.toList.length) {
                    val styleImgId = Image.save(styf)
                    val searchByImpression = SearchByImpression.findAll.toList(index)
                    SearchByImpression.saveSearchByImpressionImage(searchByImpression,styleImgId)
                }
            }
        }
    }
    
    if(!SearchByLengthForF.findAll.isEmpty){
        if(Image.fuzzyFindByName("length-female").isEmpty) {
            // save picture of style
            val stylefile = new File(play.Play.application().path() + "/public/images/style/styleForFemale")
            val stylefiles = Image.listFilesInFolder(stylefile)
            for((styf, index) <- stylefiles.zipWithIndex){
                if(index < Style.findAll.toList.length) {
                    val styleImgId = Image.save(styf)
                    val searchByLengthForF = SearchByLengthForF.findAll.toList(index)
                    SearchByLengthForF.saveSearchByLengthForFImage(searchByLengthForF,styleImgId)
                }
            }
        }
    }
    
    if(!SearchByLengthForM.findAll.isEmpty){
        if(Image.fuzzyFindByName("length-male").isEmpty) {
            // save picture of style
            val stylefile = new File(play.Play.application().path() + "/public/images/style/styleForMale")
            val stylefiles = Image.listFilesInFolder(stylefile)
            for((styf, index) <- stylefiles.zipWithIndex){
                if(index < Style.findAll.toList.length) {
                    val styleImgId = Image.save(styf)
                    val searchByLengthForM = SearchByLengthForM.findAll.toList(index)
                    SearchByLengthForM.saveSearchByLengthForMImage(searchByLengthForM,styleImgId)
                }
            }
        }
    }

    if(!Salon.findAll.isEmpty){
        if(Image.fuzzyFindByName("showPic").isEmpty) {
          //save picture of salon
            val showfile = new File(play.Play.application().path() + "/public/images/store/showPic")
            val showfiles = Image.listFilesInFolder(showfile)
            var imgList: List[ObjectId] = Nil
            for((l, index) <- showfiles.zipWithIndex){
                if(index < Salon.findAll.length * 3) {
                    val showImgId = Image.save(l)
                    imgList :::= List(showImgId) 
                    if(index % 3 == 2 ) {
                            val salon = Salon.findAll.toList(index / 3)
                            Salon.updateSalonShow(salon, imgList)
                            imgList = Nil
                    }
                    
                }
            }       
        }
    }
    
    if(!Salon.findAll.isEmpty){
        if(Image.fuzzyFindByName("atomPic").isEmpty) {
          //save picture of salon
            val atomfile = new File(play.Play.application().path() + "/public/images/store/atomPic")
            val atomfiles = Image.listFilesInFolder(atomfile)
            var imgList: List[ObjectId] = Nil
            for((n, index) <- atomfiles.zipWithIndex){
                if(index < Salon.findAll.length * 3) {
                    val atomImgId = Image.save(n)
                    imgList :::= List(atomImgId) 
                    if(index % 3 == 2 ) {
                            val salon = Salon.findAll.toList(index / 3)
                            Salon.updateSalonAtom(salon, imgList)
                            imgList = Nil
                    }
                    
                }
            }       
        }
    }

    if(Reservation.findAll.isEmpty) {
        Seq(
           Reservation(new ObjectId, "demo06", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-11 10:00"), 90, None, List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), None, "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo07", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-11 10:00"), 90, None, List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), None, "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo08", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-11 10:00"), 90, None, List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), None, "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo09", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-11 10:00"), 90, None, List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), None, "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo10", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-11 10:00"), 90, None, List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), None, "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo09", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-12 10:00"), 90, None, List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), None, "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo10", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-12 10:00"), 90, None, List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), None, "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo10", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd3")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533133f29aa6b4dfc54a02ed")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo09", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-12 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd3")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533133f29aa6b4dfc54a02ed")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo10", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-12 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd3")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533133f29aa6b4dfc54a02ed")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo06", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd3")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533133f29aa6b4dfc54a02ed")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo07", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd3")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533151209aa6b4dfc54a0374")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo06", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("530d8010d7f2861457771bf8")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("530d828cd7f2861457771c0b")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo07", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("530d8010d7f2861457771bf8")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("530d828cd7f2861457771c0b")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo08", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("530d8010d7f2861457771bf8")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("530d828cd7f2861457771c0b")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo09", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("530d8010d7f2861457771bf8")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("530d828cd7f2861457771c0b")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo08", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd3")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533134b69aa6b4dfc54a02ee")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo09", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd3")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533134b69aa6b4dfc54a02ee")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo10", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd9")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533133f29aa6b4dfc54a023d")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo09", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-12 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd9")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533133f29aa6b4dfc54a023d")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
           Reservation(new ObjectId, "demo10", new ObjectId("530d7288d7f2861457771bdd"), 1, dateTime("2014-04-12 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd9")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533134db9aa6b4dfc54a02ef")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11"))
          ).foreach(Reservation.save)
    }
    
    if(!DefaultLog.findAll.isEmpty){
        if(Image.fuzzyFindByName("defaultLog").isEmpty){
            val defaultLogFile = new File(play.Play.application().path() + "/public/images/user/dafaultLog")
            val defaultLogFiles = Image.listFilesInFolder(defaultLogFile)
            for((defaultLog, index) <- defaultLogFiles.zipWithIndex){
                if(index < DefaultLog.findAll.toList.length){
                    val defaultLogImgId = Image.save(defaultLog)
                    val defaultLogImg = DefaultLog.findAll.toList(index)
                    DefaultLog.saveLogImg(defaultLogImg, defaultLogImgId)
                }
            }
        }
    }

    /*    
    // Test for picture search.
    val stfile = new File(play.Play.application().path() + "/public/images/style")
    val stfiles = Image.listFilesRecursively()(stfile)
    println("stfiles = " + stfiles)

    // Test for General fuzzy search.
    println("general salon search = " + Salon.findSalonByFuzzyConds("千美悦美"))
    println("general salon search = " + Salon.findSalonByFuzzyConds("千美 悦美"))
    println("general salon search = " + Salon.findSalonByFuzzyConds("千美　悦美"))

    println("hotest style search = " + Style.getBestRsvedStylesInSalon(new ObjectId("530d7288d7f2861457771bdd"), 2))

    */



  }
}
