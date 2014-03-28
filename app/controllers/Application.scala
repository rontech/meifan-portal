package controllers

import play.api.mvc._
import play.api._

import models._

import se.radley.plugin.salat.Binders._
import se.radley.plugin.salat._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._
import com.mongodb.casbah.gridfs.GridFS
import java.text.SimpleDateFormat
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import controllers.noAuth._

object Application extends Controller {
    def index = Action {
        Ok(views.html.index("Your new application is ready."))
    }

    def login() = Action { implicit request =>
        Ok(views.html.user.login(auth.Users.loginForm))
    }

    def register() = Action {
        Ok(views.html.user.register(noAuth.Users.registerForm()))
    }

    def salonLogin() = Action {
        Ok(views.html.salon.salonLogin(SalonInfo.salonLogin))
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

            case None => NotFound
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

}
