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

object Application extends Controller {
	def index = Action {
		Ok(views.html.index("Your new application is ready."))
	}

  def login() = Action {
    Ok(views.html.user.login(Users.loginForm))
  }

  def register() = Action {
    Ok(views.html.user.register(Users.registerForm()))
  }
  
  def getPhoto(file: String) = Action {
    import com.mongodb.casbah.Implicits._
    import ExecutionContext.Implicits.global
    
    val db = MongoConnection()("picture")
    val gridFs = GridFS(db)

    gridFs.findOne(Map("filename" -> file)) match {
      case Some(f) => SimpleResult(
        ResponseHeader(OK, Map(
          CONTENT_LENGTH -> f.length.toString,
          CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
          DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate)
        )),
        Enumerator.fromStream(f.inputStream)
      )

      case None => NotFound
    }
  }

  
}
