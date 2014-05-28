import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "portal"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "se.radley" %% "play-plugins-salat" % "1.3.0",
    // ADD BY YS-HAN 2014-03-20 FOR AUTH
    "jp.t2v" %% "play2-auth"      % "0.11.0",
    "jp.t2v" %% "play2-auth-test" % "0.11.0" % "test",

    "org.mindrot" % "jbcrypt" % "0.3m",

    // Add by wu for mail
    "com.typesafe" %% "play-plugins-mailer" % "2.2.0"
  )
  
  // add reference to project framework and database.
  val refFrmwk = RootProject(file("../framework"))
  val refDB = RootProject(file("../database"))

  val main = play.Project(appName, appVersion, appDependencies).settings(
    routesImport += "se.radley.plugin.salat.Binders._",
    templatesImport += "org.bson.types.ObjectId"
  ).dependsOn(refFrmwk).aggregate(refFrmwk)
}
