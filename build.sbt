name := "gomelon"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

// add by rontech@2014/02/20
// For mongodb operation.
// libraryDependencies += "org.mongodb" %% "casbah" % "2.6.3"
libraryDependencies +="com.novus" %% "salat" % "1.9.5"

play.Project.playScalaSettings

// add by rontech@2014/02/20
// For [routes] file to recognize the [ObjectId] type.
routesImport += "se.radley.plugin.salat.Binders._"

templatesImport += "org.bson.types.ObjectId"

