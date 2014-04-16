package controllers

import play.api.mvc._
import models._
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
import java.io.File
import java.io.InputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

object Application extends Controller {
    def index = Action {
        Ok(views.html.index("Your new application is ready."))
    }

    def login() = Action { implicit request =>
        Ok(views.html.user.login(auth.Users.loginForm))
    }

    def register() = Action {
        Ok(views.html.user.register(Users.registerForm()))
    }

    def salonLogin() = Action {
        Ok(views.html.salon.salonLogin(auth.Salons.salonLoginForm))
    }

    def salonRegister() = Action {
        val industry = Industry.findAll.toList
        Ok(views.html.salon.salonRegister(Salons.salonRegister,industry))
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
            
    def javascriptRoutes = Action { implicit request =>
    	Ok(Routes.javascriptRouter("jsRoutes")(auth.routes.javascript.MyFollows.addFollow)).as("text/javascript")
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
}
