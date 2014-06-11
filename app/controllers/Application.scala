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
package controllers

import play.api.mvc._
import se.radley.plugin.salat.Binders._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.GridFS
import java.text.SimpleDateFormat
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import controllers.noAuth._
import java.util.Date
import routes.javascript._
import play.api.Routes
import java.io._
import jp.t2v.lab.play2.auth._
import play.api.templates.Html
import javax.imageio.ImageIO
import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import models._
import utils.Const._
import com.meifannet.portal.MeifanNetCustomerOptionalApplication
import com.meifannet.framework.db._
import play.api.Play.current
import models.portal.industry.Industry
import models.portal.common.{Image, ImgForCrop}
import models.portal.search.HotestKeyword


object Application extends MeifanNetCustomerOptionalApplication {

  /**
   * JSMessages是一个插件，主要可以实现在play2中向js文件中直接追加翻译messages
   * 可参考：https://github.com/julienrf/play-jsmessages
   */
  // new a JsMessages instance
  val messages = JsMessages.default
  
  // Generates a JavaScript function computing localized messages in the given implicit 'Lang'.
  val jsMessages = Action { implicit request =>
    Ok(messages(Some("window.Messages")))
  }

  /**
   * Add routes for ajax
   * @return
   */
  def javascriptRoutes = Action { implicit request =>
    Ok(Routes.javascriptRouter("jsRoutes")(
      auth.routes.javascript.MyFollows.addFollow,
      routes.javascript.Application.uploadWithAjax,
      auth.routes.javascript.Stylists.itemIsExist,
      auth.routes.javascript.Salons.itemIsExist,
      noAuth.routes.javascript.Users.checkIsExist,
      auth.routes.javascript.MyFollows.followedCoupon,
      routes.javascript.Reservations.addResvService)).as("text/javascript")
  }

  def index = StackAction { implicit request =>
    val user = loggedIn
    Ok(views.html.index(user))
  }

  def login() = Action { implicit request =>
    Ok(views.html.user.login(auth.Users.loginForm))
  }

  def register() = Action {
    Ok(views.html.user.register(Users.registerForm()))
  }

  def salonLogin() = Action {
    Ok(views.html.salon.salonManage.salonLogin(auth.Salons.salonLoginForm))
  }

  def salonRegister() = Action {
    val industry = Industry.findAll.toList
    Ok(views.html.salon.salonManage.salonRegister(Salons.salonRegister, industry))
  }

  def getPhoto(file: ObjectId) = Action {

    import com.mongodb.casbah.Implicits._
    import ExecutionContext.Implicits.global

    val db = DBDelegate.picDB
    val gridFs = GridFS(db)
    gridFs.findOne(Map("_id" -> file)) match {
      case Some(f) => SimpleResult(
        ResponseHeader(OK, Map(
          CONTENT_LENGTH -> f.length.toString,
          CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
          DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate))),
        Enumerator.fromStream(f.inputStream))
      // TODO ? is this necessary ? Enumerator.eof  

      case None => {
        val fi = new File(play.Play.application().path() + "/public/images/user/dafaultLog/portrait.png")
        var in: FileInputStream = null
        if (fi.exists) {
          in = new FileInputStream(fi)
          try {
            val bytes = Image.fileToBytes(in)
            Ok(bytes)
          } finally {
            in.close
          }
        } else {
          Ok("")
        }
      }
    }
  }

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("photo") match {
      case Some(photo) =>
        val db = DBDelegate.picDB
        val gridFs = GridFS(db)
        val uploadedFile = gridFs.createFile(photo.ref.file)
        uploadedFile.contentType = photo.contentType.orNull
        uploadedFile.save()
        Redirect(auth.routes.Users.saveImg(uploadedFile._id.get))
      case None => BadRequest("no photo")
    }
  }

  /**
   * 根据表单中的尺寸对上传的图片文件剪切、保存至数据库，并将图片的ObjectId保存至用户信息表中
   * @return
   */
  def changeLogo = Action(parse.multipartFormData) { implicit request =>
    request.body.file("photo").map { photo =>
      imgForm.bindFromRequest.fold(
        errors => Ok(Html(errors.toString)),
        img => {
          val db = DBDelegate.picDB
          val gridFs = GridFS(db)
          val file = photo.ref.file
          val originImage = ImageIO.read(file)
          var newImage = originImage;
          if (img.w != 0) {
            //intValue,img.h.intValue-2  防止截取图片尺寸超过图片本身尺寸
            newImage = originImage.getSubimage(img.x1.intValue, img.y1.intValue, img.w.intValue - 2, img.h.intValue - 2)
          }
          var os: ByteArrayOutputStream = null
          var inputStream: ByteArrayInputStream = null
          try {
            os = new ByteArrayOutputStream();
            ImageIO.write(newImage, "jpeg", os);
            inputStream = new ByteArrayInputStream(os.toByteArray());

            val uploadedFile = gridFs.createFile(inputStream)
            uploadedFile.contentType = photo.contentType.orNull
            uploadedFile.save()
            Redirect(auth.routes.Users.saveImg(uploadedFile._id.get))
          } finally {
            os.close
            inputStream.close
          }
        })
    }.getOrElse(Ok(Html("无图片")))
  }

  /**
   * 根据出生年月得到相应日期的年龄
   */
  def getAge(birthday: Date): Long = {
    val currentTime = new Date().getTime()
    val birthdayTime = birthday.getTime()
    val time = currentTime - birthdayTime
    val age = time / 1000 / 3600 / 24 / 365
    age
  }

  /**
   * 根据nav找到所对应的行业名
   *
   * @param nav 前台的tab
   * @return
   */
  def getIndustryByNav(nav : String) = {
    nav match {
      case "HairSalon" =>  "Hairdressing"
      case "NailSalon" =>  "Manicures"
      case "RelaxSalon" =>  "Healthcare"
      case "EstheSalon" =>  "Cosmetic"
      case _ => ""
    }
  }

  /**
   *  ajax fileupload 输出图片id到页面对应区域
   */
  def fileUploadAction = Action(parse.multipartFormData) { implicit request =>
    request.body.file("Filedata") match {
      case Some(photo) => {
        val db = DBDelegate.picDB
        val gridFs = GridFS(db)
        val uploadedFile = gridFs.createFile(photo.ref.file)
        uploadedFile.contentType = photo.contentType.orNull
        uploadedFile.save()
        Ok(uploadedFile._id.get.toString)
      }
      case None => BadRequest("no photo")
    }

  }

  def uploadWithAjax = Action(parse.multipartFormData) { implicit request =>
    request.body.file("photo") match {
      case Some(photo) => {
        val db = DBDelegate.picDB
        val gridFs = GridFS(db)
        val uploadedFile = gridFs.createFile(photo.ref.file)
        uploadedFile.contentType = photo.contentType.orNull
        uploadedFile.save()
        Ok(uploadedFile._id.get.toString)
      }
      case None => BadRequest("no photo")
    }

  }

  //用于存储图片剪切的尺寸
  val imgForm: Form[ImgForCrop] = Form(
    mapping(
      "x1" -> bigDecimal,
      "y1" -> bigDecimal,
      "x2" -> bigDecimal,
      "y2" -> bigDecimal,
      "w" -> bigDecimal,
      "h" -> bigDecimal)(ImgForCrop.apply)(ImgForCrop.unapply))

  def getkeyWordsByajax(wordText: String) = Action {
    val hotkeys = HotestKeyword.findHotestKeywordsByKW(wordText)
    var responseTxt = ""
    hotkeys.map { key =>
      responseTxt += key + ","
    }
    Ok(responseTxt)
  }

  /**
   * 用户选择切换城市后要跳转到所有城市一览的页面
   * 将点击切换城市时所在的页面uri放置缓存中
   * @return
   */
  def getAllCitys = StackAction { implicit request =>
    val user = loggedIn
    val uri = request.headers.get("Referer").getOrElse("")
    Ok(views.html.common.getAllCitys(user)).withSession("currentUri" -> uri)
  }

  /**
   * 用户切换城市选择某个城市的处理
   * 从session中获取用户选择点击切换城市时页面的uri
   * 重定向到上述页面，并将选择的城市放入缓存中
   * @param city 城市名称
   * @return
   */
  def getOneCity(city: String) = Action { implicit request =>
    request.session.get("currentUri").map{ uri=>
      Redirect(uri).withSession("myCity" -> city)
    }getOrElse{
      Redirect(routes.Application.index).withSession("myCity" -> city)
    }

  }

  /**
   * 第一次次访问美范网站时，前台页面ajax 将城市传送过来
   * 然后把城市保存在session中
   * @param city -城市名
   * @return
   */
  def setCityInSession(city: String) = Action {implicit request =>
    request.session.get("myCity").map{ myCity =>
      Ok("ok")
    }getOrElse{
      Ok("ok").withSession("myCity" -> city)
    }
  }

}
