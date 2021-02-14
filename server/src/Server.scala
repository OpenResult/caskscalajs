import upickle.default.{ReadWriter => RW, macroRW}
import upickle.default._
import java.time.Instant
import cask.router.NoOpParser
import scala.util.Random
import java.time.Clock
import shared.Protocol
import castor.SimpleActor
import cask.endpoints.WsChannelActor

class WebServer() {}
object WebServer extends cask.Main {
  override def port: Int = 8384
  override def host: String = "0.0.0.0"
  val allRoutes = Seq(
    WebPageRoutes()
  )
  println(s"Starting/listening on $host:$port")
}

case class WebPageRoutes()(implicit cc: castor.Context, log: cask.Logger)
    extends cask.Routes {

  @cask.staticFiles(
    "/js/scala",
    headers = Seq("Content-Type" -> "text/javascript")
  )
  def staticFiles() = "out/js/fastOpt/dest"

  @cask.get("/")
  def index() = {
    cask.Redirect("/htm/index.html")
  }

  @cask.staticFiles("/htm")
  def staticFileRoutes() = "server/resources/wwwroot/"

  @cask.websocket("/connect/:userName")
  def connect(userName: String): cask.WebsocketResult = {
    if (userName == "haoyi") cask.Response("", statusCode = 403)
    else
      cask.WsHandler { channel =>
        Chat.send(ChatProtocol.NewMember(userName, channel))
        cask.WsActor {
          case cask.Ws.Text("") =>
            Chat.send(ChatProtocol.MemberLeving(userName))
            channel.send(cask.Ws.Close())
          case cask.Ws.Text(data) =>
            Chat.send(ChatProtocol.NewMessage(userName, data))
          case cask.Ws.Close(_, _) =>
            Chat.send(ChatProtocol.MemberLeving(userName))
        }
      }
  }

  object ChatProtocol {
    sealed trait Message
    case class NewMember(name: String, actor: WsChannelActor) extends Message
    case class NewMessage(name: String, actor: String) extends Message
    case class MemberLeving(name: String) extends Message
  }

  object Chat extends SimpleActor[ChatProtocol.Message] {
    import ChatProtocol._
    private var members: Map[String, WsChannelActor] = Map.empty

    override def run(msg: ChatProtocol.Message): Unit = msg match {
      case NewMember(name, actor) =>
        members = members + (name -> actor)
        broadcast(Protocol.Joined(name, members.keys.toSeq))
      case NewMessage(name, msg) =>
        broadcast(Protocol.ChatMessage(name, msg))
      case MemberLeving(name) =>
        members = members - name
        broadcast(Protocol.Left(name, members.keys.toSeq))
    }

    private def broadcast(msg: Protocol.Message) {
      val e = cask.Ws.Text(write(msg))
      members.values.foreach(_.send(e))
    }
  }

  initialize()
}