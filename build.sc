import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.define.TaskModule

val utestVersion = "0.8.1"
val upickle = "2.0.0"
val sv = "3.2.1"

trait Common extends ScalaModule {
  def scalaVersion = sv
  def ivyDeps = super.ivyDeps() ++ Agg(ivy"com.lihaoyi::upickle::$upickle")
  def sources = T.sources(
    millSourcePath / "src",
    millSourcePath / os.up / "shared" / "src"
  )
}

object server extends Common {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.lihaoyi::cask:0.8.3"
  )
  override def sources = T.sources {
    super.sources() ++ vuegui.sources()
  }
  override def compile =
    T {
      js.fastOpt.apply()
      vuegui.npmRunBuild.apply()
      super.compile.apply()
    }
}

object vuegui extends Module with TaskModule {
  override def defaultCommandName(): String = "npmRunBuild"
  def npmRunBuild() = T.command {
    val wd = os.pwd / 'vuegui
    val invoked = os.proc("npm", "run", "generate").call(cwd = wd)
    println(invoked.out.trim)
  }
  def sources = T.sources(
    millSourcePath / "src",
    millSourcePath / "public",
    millSourcePath / "pages",
    millSourcePath / "components"
  )
}

object js extends ScalaJSModule with Common {
  def scalaJSVersion = "1.12.0"
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"org.scala-js::scalajs-dom::2.3.0"
  )
}
