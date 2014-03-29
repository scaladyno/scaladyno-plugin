import sbt._
import Keys._
import Process._

object DynoBuild extends Build {
  val defaults = Defaults.defaultSettings ++ Seq(

    scalaSource in Compile := baseDirectory.value / "src",
    javaSource in Compile := baseDirectory.value / "src",
    scalaSource in Test := baseDirectory.value / "test",
    javaSource in Test := baseDirectory.value / "test",
    resourceDirectory in Compile := baseDirectory.value / "resources",
    compileOrder := CompileOrder.JavaThenScala,

    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
    unmanagedSourceDirectories in Test := Seq((scalaSource in Test).value),
    //http://stackoverflow.com/questions/10472840/how-to-attach-sources-to-sbt-managed-dependencies-in-scala-ide#answer-11683728
    com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys.withSource := true,

    resolvers in ThisBuild ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),

    scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-Xlint"),
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,

    publishArtifact in packageDoc := false,

    scalaHome := {
      val scalaHome = System.getProperty("stagium.scala.home")
      if (scalaHome != null) {
        println(s"Going for custom scala home at $scalaHome")
        Some(file(scalaHome))
      } else None
    }
  )

  val pluginDeps = Seq(
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
  )

  val junitDeps: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      // "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
      "com.novocode" % "junit-interface" % "0.10-M2" % "test"
    ),
    parallelExecution in Test := false,
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
  )

  val partestLikeDeps: Seq[Setting[_]] = junitDeps ++ Seq(
    fork in Test := true,
    javaOptions in Test <+= (dependencyClasspath in Runtime, packageBin in Compile in plugin) map { (path, _) =>
      def isBoot(file: java.io.File) =
        ((file.getName() startsWith "scala-") && (file.getName() endsWith ".jar")) ||
        (file.toString contains "target/scala-2.11") // this makes me cry, seriously sbt...

      val cp = "-Xbootclasspath/a:"+path.map(_.data).filter(isBoot).mkString(":")
      // println(cp)
      cp
    },
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-partest" % "1.0.0",
      "com.googlecode.java-diff-utils" % "diffutils" % "1.2.1"
    )
  )

  lazy val dyno        = Project(id = "dyno",         base = file("."),                      settings = defaults) aggregate (plugin, tests)
  lazy val plugin      = Project(id = "dyno-plugin",  base = file("components/plugin"),      settings = defaults ++ pluginDeps)
  lazy val tests       = Project(id = "dyno-tests",   base = file("tests/correctness"),      settings = defaults ++ pluginDeps ++ partestLikeDeps) dependsOn(plugin)
}
