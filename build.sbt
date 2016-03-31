enablePlugins(ScalaJSPlugin)
name := "chilternquizleague root project"

lazy val root = project.in(file(".")).
  aggregate(fooJS, fooJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val foo = crossProject.in(file(".")).
  settings(
    name := "chilternquizleague",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.7",
    libraryDependencies += "com.google.appengine" % "appengine-api-1.0-sdk" % "1.9.34",
    libraryDependencies += "com.googlecode.objectify" % "objectify" % "5.1.12",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.7.3",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.7.3",
    libraryDependencies += "com.google.appengine.tools" % "appengine-gcs-client" % "0.5",
    libraryDependencies += "commons-io" % "commons-io" % "2.4",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.3"


    

  ).
  jvmSettings(
    name := "chilternquizleague-jvm"
  ).
  jsSettings(
    name := "chilternquizleague-js",
    libraryDependencies += "com.greencatsoft" % "scalajs-angular_sjs0.6_2.11" % "0.6"
  )

lazy val fooJVM = foo.jvm
lazy val fooJS = foo.js