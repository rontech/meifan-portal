import play.api._
import models._
import anorm._
import com.mongodb.casbah.commons.Imports._
import org.bson.types.ObjectId
import java.util.Date
import java.io.File


object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    // TODO initial test Data.
    InitialData.insert()
  }
  
}

/**
 * Initial set of data to be imported 
 * in the sample application.
 */
object InitialData {
  
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() = {

    // For nowtime, we change the Table Defination frequently....
    // Please do the drop actions below in mongodb.
    // mongo
    // use fashion-mongo
    // db.Salon.drop()
    // db.Style.drop()
    // db.Stylist.drop()

    if(Salon.findAll == Nil) { 
      Seq(
        Salon(new ObjectId("530d7288d7f2861457771bdd"), "火影忍者吧", Some("火吧"), "美发", Some("www.sohu.com"), Some("本地最红的美发沙龙！"), "051268320328", "路飞", Seq(OptContactMethod("QQ",List("99198121"))), date("2014-03-12"), Address("江苏", "苏州", "高新区", Some(""), "竹园路209号", Some(100.0), Some(110.0)), "地铁一号线汾湖路站1号出口向西步行500米可达", "9:00", "18:00", "Sat", 5, SalonFacilities(true, true, true, true, true, true, true, true, true, "附近有"), "pic", date("2014-03-12") ),
        Salon(new ObjectId("530d7292d7f2861457771bde"), "海贼王吧", Some("海吧"), "美发", Some("www.sohu.com"), Some("本地最红的美发沙龙！"), "051268320328", "路飞", Seq(OptContactMethod("QQ",List("99198121"))), date("2014-03-12"), Address("江苏", "苏州", "高新区", Some(""), "竹园路209号", Some(100.0), Some(110.0)), "地铁一号线汾湖路站1号出口向西步行500米可达", "9:00", "18:00", "Sat", 5, SalonFacilities(true, true, true, true, true, true, true, true, true, "附近有"), "pic", date("2014-03-12") )
      ).foreach(Salon.save)
      
      Seq(
        Stylist(new ObjectId("530d8010d7f2861457771bf8"), "漩涡鸣人", new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d7288d7f2861457771bdd"), "5", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "中国顶尖理发师", "B004138935_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771bf9"), "宇智波佐助", new  ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d7288d7f2861457771bdd"), "4", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "美国顶尖理发师", "B004670057_164-219.jpg", 1 ),
        Stylist(new ObjectId("530d8010d7f2861457771bfa"), "宇智波带土", new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d7288d7f2861457771bdd"), "3", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "埃塞俄比亚顶尖理发师", "B004554657_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771bfb"), "宇智波斑", new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d7288d7f2861457771bdd"), "2", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "朝鲜顶尖理发师", "B003961937_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771bfc"), "千手柱间", new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d7288d7f2861457771bdd"), "5", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "越南顶尖理发师", "B003921724_164-219.jpg", 1),

        Stylist(new ObjectId("530d8010d7f2861457771bfd"), "路飞", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "4", List("美发师","美甲师"),List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "老挝顶尖理发师", "B002740532_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771bfe"), "索隆", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "7", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "缅甸顶尖理发师", "B004138935_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771bff"), "香吉士", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "9", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "刚果顶尖理发师", "B004689277_164-219", 1),
        Stylist(new ObjectId("530d8010d7f2861457771c00"), "娜美", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "8", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "利比亚顶尖理发师", "B004452138_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771c01"), "乔巴", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "4", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "印度顶尖理发师", "B004554657_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771c02"), "罗宾", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "7", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "哈萨克斯坦顶尖理发师", "B004670057_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771c03"), "乌索普", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "5", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "日本顶尖理发师", "B004537535_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771c04"), "弗兰奇", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "4", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "美国顶尖理发师", "B004538417_164-219.jpg", 1),
        Stylist(new ObjectId("530d8010d7f2861457771c05"), "布鲁克", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d7288d7f2861457771bdd"), "2", List("美发师","美甲师"), List("53168b61d4d5cb7e816db35e"), List("53168b38d4d5cb7e816db35c"), "泰国顶尖理发师", "B004689277_164-219.jpg", 1)
      ).foreach(Stylist.save)
      
      Seq(
        Style(new ObjectId("530d828cd7f2861457771c0b"), "四代火影发型式", new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bf8"), List("B002740532_164-219.jpg","B004689277_164-219.jpg","B004538417_164-219.jpg"),"清爽、舒适","烫发","中",List("黄色","绿色","紫色"),List("很少","很多","较少"),List("软","硬"),List("粗","细"),List("国字","方形","圆形","尖"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生"),
        Style(new ObjectId("530d828cd7f2861457771c0c"), "六道仙人发型", new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bf8"), List("B004670057_164-219.jpg","B004670057_164-219.jpg","B004554657_164-219.jpg"),"舒适","染发","长",List("绿色","紫色"),List("很少","很多","较少"),List("软","硬"),List("适中","细"),List("国字","方形","圆形","尖"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生"),
        Style(new ObjectId("530d828cd7f2861457771c0d"), "海贼王发型", new ObjectId("530d7292d7f2861457771bde"), new ObjectId("530d8010d7f2861457771bfd"), List("B004554657_164-219.jpg"),"清爽、舒适","烫发","中",List("黄色","绿色","紫色"),List("很少","很多","较少"),List("软","硬"),List("粗","细"),List("国字","方形","圆形","尖"),"此种发型清爽怡人，迎面而过，回眸一笑百媚生")
      ).foreach(Style.save)

    }
    
    if(StyleColor.findAll.isEmpty) {
      Seq (
          StyleColor(new ObjectId, "红"),
          StyleColor(new ObjectId, "黄"),
          StyleColor(new ObjectId, "黑色"),
          StyleColor(new ObjectId, "其他颜色")
        ).foreach(StyleColor.save)
    }
    
    if(StyleLength.findAll.isEmpty) {
      Seq (
          StyleLength(new ObjectId, "长"),
          StyleLength(new ObjectId, "中"),
          StyleLength(new ObjectId, "短")
        ).foreach(StyleLength.save)
    }
    
    if(Impression.findAll.isEmpty) {
      Seq (
          Impression(new ObjectId, "清新"),
          Impression(new ObjectId, "自然"),
          Impression(new ObjectId, "淡雅"),
          Impression(new ObjectId, "其他印象")
        ).foreach(Impression.save)
    }
    
    if(StyleAmount.findAll.isEmpty) {
      Seq (
          StyleAmount(new ObjectId, "多"),
          StyleAmount(new ObjectId, "一般"),
          StyleAmount(new ObjectId, "少")
        ).foreach(StyleAmount.save)
    }
    
    if(StyleQuality.findAll.isEmpty) {
      Seq (
          StyleQuality(new ObjectId, "软"),
          StyleQuality(new ObjectId, "适中"),
          StyleQuality(new ObjectId, "硬"),
          StyleQuality(new ObjectId, "柔顺")
        ).foreach(StyleQuality.save)
    }
    
    if(StyleDiameter.findAll.isEmpty) {
      Seq (
          StyleDiameter(new ObjectId, "细"),
          StyleDiameter(new ObjectId, "适中"),
          StyleDiameter(new ObjectId, "粗")
        ).foreach(StyleDiameter.save)
    }
    
    if(FaceType.findAll.isEmpty) {
      Seq (
          FaceType(new ObjectId, "标准"),
          FaceType(new ObjectId, "圆形"),
          FaceType(new ObjectId, "鹅蛋脸"),
          FaceType(new ObjectId, "四角")
        ).foreach(FaceType.save)
    }
    
    if(ServiceType.findAll.isEmpty) {
        Seq (
          ServiceType(new ObjectId("5316798cd4d5cb7e816db34b"), "剪"),
          ServiceType(new ObjectId("53167a91d4d5cb7e816db34d"), "洗"),
          ServiceType(new ObjectId("53167abbd4d5cb7e816db34f"), "吹"),
          ServiceType(new ObjectId("53167aced4d5cb7e816db351"), "染"),
          ServiceType(new ObjectId("53167ad9d4d5cb7e816db353"), "护理"),
          ServiceType(new ObjectId("53167ae7d4d5cb7e816db355"), "烫"),
          ServiceType(new ObjectId("53167b3cd4d5cb7e816db359"), "柔顺"),
          ServiceType(new ObjectId("5316c443d4d57997ce3e6d68"), "其他")
        ).foreach(ServiceType.save)

        Seq (
          Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "剪", 10, 10) ,
          Service(new ObjectId("53168b61d4d5cb7e816db35e"), "数码烫", "烫", 90, 100),
          Service(new ObjectId("5316bb36d4d57997ce3e6d49"), "离子烫", "烫", 90, 100),
          Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "染", 90, 120),
          Service(new ObjectId("5316be49d4d57997ce3e6d50"), "小脸剪", "剪", 20, 30) ,
          Service(new ObjectId("5316be8ed4d57997ce3e6d52"), "3D彩色", "染", 80, 150),
          Service(new ObjectId("5316beb4d4d57997ce3e6d54"), "低色短", "染", 80, 140),
          Service(new ObjectId("5316bed4d4d57997ce3e6d56"), "蒸汽烫", "染", 45, 80) ,
          Service(new ObjectId("5316bef6d4d57997ce3e6d58"), "纯护理", "护理", 50, 66) ,
          Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "洗", 10, 10) ,
          Service(new ObjectId("5316c05bd4d57997ce3e6d5c"), "干洗", "洗", 15, 20) ,
          Service(new ObjectId("5316c07fd4d57997ce3e6d5e"), "简吹", "吹", 10, 15) ,
          Service(new ObjectId("5316c0a2d4d57997ce3e6d60"), "豪华护理", "护理", 70, 200),
          Service(new ObjectId("5316c0d1d4d57997ce3e6d62"), "拉直", "柔顺", 30, 50) ,
          Service(new ObjectId("5316ec2fd4d57997ce3e6d97"), "盘发", "其他", 50, 50) ,
          Service(new ObjectId("5316ecffd4d57997ce3e6d9d"), "盘发2", "其他", 100, 80) 
        ).foreach(Service.save)

    }

    if(Coupon.findAll.isEmpty) {
      Seq(
         Coupon(new ObjectId("5317c0d1d4d57997ce3e6d6a"), "xjc01", "[兄弟公司☆宝石]小脸斩+香薰头部按摩5250日元允许西装", new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bfd"),
         Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "剪", 10, 10), Service(new ObjectId("5316bb36d4d57997ce3e6d49"), "离子烫", "烫", 90, 100)),
         Seq(ServiceType(new  ObjectId("5316798cd4d5cb7e816db34b"), "剪"), ServiceType(new ObjectId("53167ae7d4d5cb7e816db355"), "烫")),
         110, 100, 90, date("2014-03-01"), date("2014-03-31"), "你看到的HOTPEPPER美容♪字", "在预订时间", "◆体验到自己可爱虽然是♪我们自然要授予风格，你想仔细咨询☆◆您可以选择洗发水是一种根据请求的皮肤和头发，定型剂", "0"), 
         Coupon(new ObjectId("5317c0d1d4d57997ce3e6d6b"), "xjc02", "[兄弟公司☆1号热门]斩+颜色+温和的芳香按摩头11340日元", new ObjectId("530d7288d7f2861457771bdd"), new ObjectId("530d8010d7f2861457771bfc"),
         Seq(Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "洗", 10, 10), Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "染", 90, 120), Service(new ObjectId("5316bb36d4d57997ce3e6d49"), "离子烫", "烫", 90, 100)),
         Seq(ServiceType(new ObjectId("53167a91d4d5cb7e816db34d"), "洗"), ServiceType(new ObjectId("53167aced4d5cb7e816db351"), "染"), ServiceType(new ObjectId("53167ae7d4d5cb7e816db355"), "烫")),
         230, 190, 120, date("2014-03-01"), date("2014-03-31"), "你看到的HOTPEPPER美容♪字", "在预订时间", "◆无♪◆长费咨询和那些敏感的皮肤☆◆最好的季节Tsuyakara成为各1号和皮肤的每一个颜色，眼睛的颜色，从时尚，更是对皮肤的刺激性是一个关注的晒后", "0")
      ).foreach(Coupon.save)
    }

    if(Menu.findAll().isEmpty) {
      Seq(
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d61"), new ObjectId("530d7288d7f2861457771bdd"), Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "剪", 10, 10), Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "染", 90, 120)),
              Seq(ServiceType(new  ObjectId("5316798cd4d5cb7e816db34b"), "剪"), ServiceType(new ObjectId("53167aced4d5cb7e816db351"), "染")), 130, "0"),
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d62"), new ObjectId("530d7288d7f2861457771bdd"), Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "剪", 10, 10), Service(new ObjectId("5316be8ed4d57997ce3e6d52"), "3D彩色", "染", 80, 150)),
              Seq(ServiceType(new  ObjectId("5316798cd4d5cb7e816db34b"), "剪"), ServiceType(new ObjectId("53167aced4d5cb7e816db351"), "染")), 160, "0"),
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d63"), new ObjectId("530d7288d7f2861457771bdd"), Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "剪", 10, 10), Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "洗", 10, 10)),
              Seq(ServiceType(new  ObjectId("5316798cd4d5cb7e816db34b"), "剪"), ServiceType(new ObjectId("53167a91d4d5cb7e816db34d"), "洗")), 20, "0")
      ).foreach(Menu.save)
    }

    if(Menu.findAll().isEmpty) {
      Seq(
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d61"), new ObjectId("530d7288d7f2861457771bdd"), Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "剪", 10, 10), Service(new ObjectId("5316bb51d4d57997ce3e6d4b"), "局部染", "染", 90, 120)),
              Seq(ServiceType(new  ObjectId("5316798cd4d5cb7e816db34b"), "剪"), ServiceType(new ObjectId("53167aced4d5cb7e816db351"), "染")), 130, "0"),
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d62"), new ObjectId("530d7288d7f2861457771bdd"), Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "剪", 10, 10), Service(new ObjectId("5316be8ed4d57997ce3e6d52"), "3D彩色", "染", 80, 150)),
              Seq(ServiceType(new  ObjectId("5316798cd4d5cb7e816db34b"), "剪"), ServiceType(new ObjectId("53167aced4d5cb7e816db351"), "染")), 160, "0"),
          Menu(new ObjectId("5317c0d1d4d57337ce3e6d63"), new ObjectId("530d7288d7f2861457771bdd"), Seq(Service(new ObjectId("53168b38d4d5cb7e816db35c"), "剪刘海", "剪", 10, 10), Service(new ObjectId("5316c048d4d57997ce3e6d5a"), "简洗", "洗", 10, 10)),
              Seq(ServiceType(new  ObjectId("5316798cd4d5cb7e816db34b"), "剪"), ServiceType(new ObjectId("53167a91d4d5cb7e816db34d"), "洗")), 20, "0")
      ).foreach(Menu.save)
    }
    
    if(BlogCatagory.getCommonCatagory.isEmpty){
      Seq (
        BlogCatagory(new ObjectId("53195fb4a89e175858abce82"), "", List("选择分类", "私密博文"), 0, 0)       
      ).foreach(BlogCatagory.save)
    }

    if(Image.findAll.isEmpty) {
      // val file = new File(controllers.routes.Assets.at("/images").toString())
      val file = new File(play.Play.application().path() + "/public/images")
      val files = Image.listAllFiles(file)
        files.foreach(f=>Image.save(f)) 
    }

    if(RelationType.findAll.isEmpty){
      Seq(
       RelationType(new ObjectId("53217c2ed4d5c027e48dd978"), "关注店铺", 1),
       RelationType(new ObjectId("53217c35d4d5c027e48dd97a"), "关注技师", 2),
       RelationType(new ObjectId("53217c4cd4d5c027e48dd97c"), "收藏风格", 3),
       RelationType(new ObjectId("53217c62d4d5c027e48dd97e"), "收藏优惠劵", 4),
       RelationType(new ObjectId("53217c6fd4d5c027e48dd980"), "收藏博客", 5),
       RelationType(new ObjectId("53217c6fd4d5c027e48dd981"), "关注用户", 6)
      ).foreach(RelationType.save)
    } 
    
    if(FollowCollect.findAll.isEmpty){
      Seq(
       FollowCollect(new ObjectId("531563e2d4d5b6a812c359a8"),new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d7288d7f2861457771bdd"),1,true),
       FollowCollect(new ObjectId("531563e2d4d5b6a812c359a9"),new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d7292d7f2861457771bde"),1,true),
       FollowCollect(new ObjectId("531563e2d4d5b6a812c359a0"),new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d8010d7f2861457771bfd"),2,true),
       FollowCollect(new ObjectId("531563e2d4d5b6a812c359a1"),new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d8010d7f2861457771c01"),2,true),
       FollowCollect(new ObjectId("531563e2d4d5b6a812c359a2"),new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("530d8010d7f2861457771c05"),2,true),
       FollowCollect(new ObjectId("531563e2d4d5b6a812c359a3"),new ObjectId("53202c29d4d5e3cd47efffd3"),new ObjectId("531964e0d4d57d0a43771411"),6,true),
       FollowCollect(new ObjectId("531563e2d4d5b6a812c359a4"),new ObjectId("531964e0d4d57d0a43771411"),new ObjectId("53202c29d4d5e3cd47efffd3"),6,true)
      ).foreach(FollowCollect.save)
    }
    
  }
  
}
