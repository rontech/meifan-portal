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
        Salon(new ObjectId("530d7288d7f2861457771bdd"), SalonAccount("salon01", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "悦美月容吧", Some("悦容吧"), List("Hairdressing"), Some("www.sohu.com"), Some("本地最红的美发沙龙！"), Some(PicDescription("国家的最先进的从银座发型始发！你把它设置为[] AFLOATJAPAN如果头发爱莫特！","实行开放银座[AFLOAT日本]！超人气沙龙AFLOAT人才，艺人以及参加许多，聚集人气造型师谁冠美容行业★意义上的顶级制作“永远的成年女性”可爱“，”中应该让我一定会满意你的技术。它的舒适放松的质量好平静的空间，这也是私人房间也很高兴☆","欢迎光临！")), Contact("051268320328", "鸣人","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, false, false, true, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", Some(1), None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-01-12") ),
        Salon(new ObjectId("530d7292d7f2861457771bde"), SalonAccount("salon02", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "千美千寻吧", Some("美寻吧"), List("Hairdressing"), Some("www.163.com"), Some("本地第二红的美发沙龙！"),Some(PicDescription("国家的最先进的从银座发型始发！你把它设置为[] AFLOATJAPAN如果头发爱莫特！","实行开放银座[AFLOAT日本]！超人气沙龙AFLOAT人才，艺人以及参加许多，聚集人气造型师谁冠美容行业★意义上的顶级制作“永远的成年女性”可爱“，”中应该让我一定会满意你的技术。它的舒适放松的质量好平静的空间，这也是私人房间也很高兴☆","欢迎光临！")), Contact("051268320328", "鸣人","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, true, true, false, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", None, None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),None),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-03-12") ),
        Salon(new ObjectId("530d7288d7f2861457771bdf"), SalonAccount("salon03", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "忆荣吧", Some("忆荣吧"), List("Hairdressing"), Some("www.sohu.com"), Some("本地最红的美发沙龙！"), Some(PicDescription("国家的最先进的从银座发型始发！你把它设置为[] AFLOATJAPAN如果头发爱莫特！","实行开放银座[AFLOAT日本]！超人气沙龙AFLOAT人才，艺人以及参加许多，聚集人气造型师谁冠美容行业★意义上的顶级制作“永远的成年女性”可爱“，”中应该让我一定会满意你的技术。它的舒适放松的质量好平静的空间，这也是私人房间也很高兴☆","欢迎光临！")),Contact("051268320328", "鸣人","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, false, false, true, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", Some(1), None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),None),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-01-12") ),
        Salon(new ObjectId("530d7292d7f2861457771aaa"), SalonAccount("salon04", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "虞美人", Some("虞美人"), List("Hairdressing"), Some("www.163.com"), Some("本地第二红的美发沙龙！"),Some(PicDescription("国家的最先进的从银座发型始发！你把它设置为[] AFLOATJAPAN如果头发爱莫特！","实行开放银座[AFLOAT日本]！超人气沙龙AFLOAT人才，艺人以及参加许多，聚集人气造型师谁冠美容行业★意义上的顶级制作“永远的成年女性”可爱“，”中应该让我一定会满意你的技术。它的舒适放松的质量好平静的空间，这也是私人房间也很高兴☆","欢迎光临！")), Contact("051268320328", "鸣人","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, true, true, false, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", None, None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),None),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-03-12") ),
        Salon(new ObjectId("530d7292d7f2861457771bbb"), SalonAccount("salon05", "$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU."), "美吧", Some("美吧"), List("Hairdressing"), Some("www.163.com"), Some("本地第二红的美发沙龙！"),Some(PicDescription("国家的最先进的从银座发型始发！你把它设置为[] AFLOATJAPAN如果头发爱莫特！","实行开放银座[AFLOAT日本]！超人气沙龙AFLOAT人才，艺人以及参加许多，聚集人气造型师谁冠美容行业★意义上的顶级制作“永远的成年女性”可爱“，”中应该让我一定会满意你的技术。它的舒适放松的质量好平静的空间，这也是私人房间也很高兴☆","欢迎光临！")), Contact("051268320328", "鸣人","hz-han@rontech.co.jp"), List(OptContactMethod("QQ",List("99198121"))), Some(date("2014-03-12")), Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"地铁一号线汾湖路站1号出口向西步行500米可达")) , Some(WorkTime("9:00", "18:00")), Some(RestDay("Fixed",List("Monday"))), Some(25), Some(SalonFacilities(true, true, true, true, false, true, true, true, true, "附近有")), List(new OnUsePicture(new ObjectId, "LOGO", None, None),new OnUsePicture(new ObjectId,"Navigate",Some(1),None),new OnUsePicture(new ObjectId,"Navigate",Some(2),None),new OnUsePicture(new ObjectId,"Navigate",Some(3),None),new OnUsePicture(new ObjectId,"Atmosphere",Some(1),Some("清新怡人")),new OnUsePicture(new ObjectId,"Atmosphere",Some(2),Some("环境优雅！")),new OnUsePicture(new ObjectId,"Atmosphere",Some(3),Some("安静典雅！"))), date("2014-03-12") )
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
       User(new ObjectId("530d8010d7f2861457771bf8"),"demo01","维达沙宣","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"Administrator", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd3"),"demo02","苏小魂","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"Administrator", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd4"),"demo03","阿哲","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd9"),"demo04","李莫愁","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd8"),"demo05","西门吹雪","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd7"),"demo06","叶孤城","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd6"),"demo07","陆小风","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd5"),"demo08","花满楼","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"stylist","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffd0"),"demo09","独孤求败","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"normalUser","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true),
       User(new ObjectId("53202c29d4d5e3cd47efffe1"),"demo10","中原一点红","$2a$10$q0rl.qI.X9UTPZ6mDRbVhOvxYjk9S7RsrAmJ3aXaJaEcLV/3f/bU.","F", Some(date("1991-03-18")),Some(Address("江苏省", Option("苏州市"), Option("高新区"), None, "竹园路209号", Some(100.0), Some(110.0),"")),new ObjectId,Some("15269845698"),"123@123.com",Seq(OptContactMethod("QQ", List{"845654891"})),Some("程序员"),"normalUser","userLevel.0",20,0,date("2014-03-18"),"LoggedIn", true)
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
        Style(new ObjectId("530d828cd7f2861457771c0d"), "male生纹理烫发型", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple"),"short",List("black","chocolate","brown"),List("much","moderate","few"),List("greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a02ed"), "背头发型", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"long",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face","diamond-face"),"凌乱中带有一丝淡淡的忧伤！",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a02ee"), "皮卡路发型", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"long",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin","moderate"),List("long-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a021d"), "板寸发型", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"super-short",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","evening-wear"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a021e"), "male生烟花烫发型", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple","Perm"),"long",List("flax"),List("much","moderate","few"),List("silky","greasy","dry","general"),List("bold","thin","moderate"),List("long-face","round-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"female",List("star","street","others"),date("2014-03-12"),true),
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
        Style(new ObjectId("530d828cd7f2861457761c0d"), "male生纹理烫发型-男", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple"),"short",List("black","chocolate","brown"),List("much","moderate","few"),List("greasy","general"),List("bold","thin"),List("long-face","round-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a12ed"), "背头发型-男", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"long",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face","diamond-face"),"凌乱中带有一丝淡淡的忧伤！",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a12ee"), "皮卡路发型-男", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"fresh",List("Supple","Perm"),"long",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold","thin","moderate"),List("long-face","oval-face","diamond-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star","evening-wear","others"),date("2014-03-12"),true),
        Style(new ObjectId("533133f29aa6b4dfc54a121d"), "板寸发型-男", new ObjectId("53202c29d4d5e3cd47efffd3"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"sweet",List("Supple","Perm"),"super-short",List("flax","red","alternative"),List("much","moderate","few"),List("silky","greasy","general"),List("bold"),List("long-face","round-face","oval-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","evening-wear"),date("2014-03-12"),true),
        Style(new ObjectId("533134b69aa6b4dfc54a121e"), "male生烟花烫发型-男", new ObjectId("53202c29d4d5e3cd47efffd9"), List(new OnUsePicture(new ObjectId, "logo", Some(1), None)),"natural",List("Supple","Perm"),"long",List("flax"),List("much","moderate","few"),List("silky","greasy","dry","general"),List("bold","thin","moderate"),List("long-face","round-face"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生",List("one--fifteen","fifty-five--","one--twenty-five","twenty-five--thirty-five"),"male",List("star","street","others"),date("2014-03-12"),true),
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
        MyFollow(new ObjectId("532f9889d4d5f03ea49463e0"), new ObjectId("53202c29d4d5e3cd47efffd0"),new ObjectId("530d828cd7f2861457771c0c"),"style"),
        MyFollow(new ObjectId("532f9889d4d5f03ea49463ff"), new ObjectId("53202c29d4d5e3cd47efffe1"),new ObjectId("53202c29d4d5e3cd47efffd0"),"user"),
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
        Question(new ObjectId(), "预约后想取消", "请按以下步骤取消：......", new Date, 1, true)
      ).foreach(Question.save)

    }

    /*    
    val stfile = new File(play.Play.application().path() + "/public/images/style")
    val stfiles = Image.listFilesRecursively()(stfile)
    println("stfiles = " + stfiles)
    */

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
	       Reservation(new ObjectId, "demo07", new ObjectId("530d7288d7f2861457771bdd"), 0, dateTime("2014-04-11 10:00"), 90, Option(new ObjectId("53202c29d4d5e3cd47efffd3")), List(ResvItem("coupon", new ObjectId("5317c0d1d4d57997ce3e6d6a"), 1)), Option(new ObjectId("533133f29aa6b4dfc54a02ed")), "051268320328", "准时到", 100, 0, 100, date("2014-04-11"), date("2014-04-11")),
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
  }
}
