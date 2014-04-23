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
import com.mongodb.casbah.MongoConnection
import javax.imageio.ImageIO
import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import models._
import utils.Const._

object Application extends Controller with OptionalAuthElement with UserAuthConfigImpl{

    /**
     * for ajax
     * @return
     */
    def javascriptRoutes = Action { implicit request =>
        Ok(Routes.javascriptRouter("jsRoutes")(
            auth.routes.javascript.MyFollows.addFollow,
            routes.javascript.Application.uploadWithAjax,
            routes.javascript.Application.itemIsExist
        )).as("text/javascript")
    }

    def index = StackAction{ implicit request =>
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
        Ok(views.html.salon.salonManage.salonRegister(Salons.salonRegister,industry))
    }

    def itemIsExist(value:String, key:String) = Action {
        key match{
            case ITEM_TYPE_ID =>
                Ok((User.isExist(value, User.findOneByUserId)||Salon.isExist(value, Salon.findByAccountId)).toString)
            case ITEM_TYPE_NAME =>
                Ok((User.isExist(value, User.findOneByNickNm)||Salon.isExist(value, Salon.findOneBySalonName)||Salon.isExist(value,Salon.findOneBySalonNameAbbr)).toString)
            case ITEM_TYPE_EMAIL =>
                Ok((User.isExist(value, User.findOneByEmail)||Salon.isExist(value, Salon.findOneByEmail)).toString)
            case ITEM_TYPE_TEL =>
                Ok((User.isExist(value, User.findOneByTel)||Salon.isExist(value, Salon.findOneByMainPhone)).toString)
        }
    }
    
    def getPhoto(file: ObjectId) = Action {

        import com.mongodb.casbah.Implicits._
        import ExecutionContext.Implicits.global

        val db = MongoConnection()("Picture")
        val gridFs = GridFS(db)
        gridFs.findOne(Map("_id" -> file)) match {
            case Some(f) => SimpleResult(
                ResponseHeader(OK, Map(
                    CONTENT_LENGTH -> f.length.toString,
                    CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
                    DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate))),
                Enumerator.fromStream(f.inputStream))

            case None => {
              val fi = new File(play.Play.application().path() + "/public/images/user/dafaultLog/portrait.png")
              var in = new FileInputStream(fi)
              var bytes = Image.fileToBytes(in)
              Ok(bytes)
            }
        }
    }
    
    def getPhotoByString(sfile: String) = Action {

        import com.mongodb.casbah.Implicits._
        import ExecutionContext.Implicits.global
        
        val file = new ObjectId(sfile)

        val db = MongoConnection()("Picture")
        val gridFs = GridFS(db)
        //println("get photo id "+ file)
        gridFs.findOne(Map("_id" -> file)) match {
            case Some(f) => SimpleResult(
                ResponseHeader(OK, Map(
                    CONTENT_LENGTH -> f.length.toString,
                    CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
                    DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate))),
                Enumerator.fromStream(f.inputStream))

            case None => {
              val fi = new File(play.Play.application().path() + "/public/images/user/dafaultLog/portrait.png")
              var in = new FileInputStream(fi)
              var bytes = Image.fileToBytes(in)
              Ok(bytes)
            }
        }
    }

    def upload = Action(parse.multipartFormData) { request =>
        request.body.file("photo") match {
            case Some(photo) =>
                val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(photo.ref.file)
                uploadedFile.contentType = photo.contentType.orNull
                uploadedFile.save()
                Redirect(auth.routes.Users.saveImg(uploadedFile._id.get))
            case None => BadRequest("no photo")
        }
    }

    def changeLogo = Action(parse.multipartFormData) {implicit request =>
        request.body.file("photo").map{ photo =>
            imgForm.bindFromRequest.fold(
                errors =>Ok(Html(errors.toString)),
                img =>{
                    val db = MongoConnection()("Picture")
                    val gridFs = GridFS(db)
                    val file = photo.ref.file
                    val originImage =  ImageIO.read(file)

                    //intValue,img.h.intValue-2  防止截取图片尺寸超过图片本身尺寸
                    val newImage = originImage.getSubimage(img.x1.intValue,img.y1.intValue,img.w.intValue-2,img.h.intValue-2)

                    val  os = new ByteArrayOutputStream();

                    ImageIO.write(newImage, "jpeg", os);

                    val inputStream = new ByteArrayInputStream(os.toByteArray());

                    val uploadedFile = gridFs.createFile(inputStream)

                    uploadedFile.contentType = photo.contentType.orNull
                    uploadedFile.save()
                    Redirect(auth.routes.Users.saveImg(uploadedFile._id.get))
                }
            )
        }.getOrElse(Ok(Html("无图片")))
    }


    
    /**
     * 根据出生年月得到相应日期的年龄
     */
    def getAge(birthday : Date) : Long ={
      val currentTime = new Date().getTime()
      val birthdayTime = birthday.getTime()
      val time = currentTime - birthdayTime
      val age = time/1000/3600/24/365
      age
    }
            
    /**
     *  ajax fileupload 输出图片id到页面对应区域
     */
    def fileUploadAction = Action(parse.multipartFormData) { implicit request =>
    	request.body.file("Filedata") match {
            case Some(photo) =>{
            	val db = MongoConnection()("Picture")
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
            case Some(photo) =>{
                val db = MongoConnection()("Picture")
                val gridFs = GridFS(db)
                val uploadedFile = gridFs.createFile(photo.ref.file)
                uploadedFile.contentType = photo.contentType.orNull
                uploadedFile.save()
                Ok(uploadedFile._id.get.toString)
            }
            case None => BadRequest("no photo")
        }

    }
    
    


    val imgForm : Form[ImgForCrop] =Form(
        mapping(
            "x1"->bigDecimal,
            "y1"->bigDecimal,
            "x2"->bigDecimal,
            "y2"->bigDecimal,
            "w"->bigDecimal,
            "h"->bigDecimal)(ImgForCrop.apply)(ImgForCrop.unapply)
    )
    
    def getkeyWordsByajax(wordText:String) = Action{
      println("get keyword.."+wordText)
      val hotkeys = HotestKeyword.findHotestKeywordsByKW(wordText)
      val keys: String = hotkeys.stringPrefix
      println("keys "+hotkeys)
      Ok(keys)
      
    }
}
