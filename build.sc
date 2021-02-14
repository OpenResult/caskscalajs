import mill._
import mill.scalalib._
import mill.scalajslib._

val utestVersion = "0.7.7"
val upickle = "1.2.3"

trait Common extends ScalaModule {
  def scalaVersion = "2.13.4"
  def ivyDeps = super.ivyDeps() ++ Agg(ivy"com.lihaoyi::upickle::$upickle")
  def sources = T.sources(
    millSourcePath / "src",
    millSourcePath / os.up / "shared" / "src"
  )
}

object server extends Common {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.lihaoyi::cask:0.7.5"
  )
  override def compile =
    T {
      js.fastOpt.apply()
      super.compile.apply()
    }
}

object js extends ScalaJSModule with Common {
  def scalaJSVersion = "1.4.0"
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"org.scala-js::scalajs-dom::1.1.0"
  )
}
