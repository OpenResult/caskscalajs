import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom.raw._

import org.scalajs.dom

import upickle.default._
import shared.Protocol

@JSExportTopLevel("MainScalaJs")
object MainScalaJs {
  @JSExport
  def start(): Unit =
    val sendButton =
      dom.document.getElementById("send").asInstanceOf[HTMLButtonElement]
    val joinButton =
      dom.document.getElementById("join").asInstanceOf[HTMLButtonElement]
    val nameField =
      dom.document.getElementById("name").asInstanceOf[HTMLInputElement]
    joinButton.onclick = { (event: MouseEvent) =>
      joinChat(nameField.value, joinButton, sendButton)
      event.preventDefault()
    }

    nameField.focus()
    nameField.onkeypress = { (event: KeyboardEvent) =>
      if (event.keyCode == 13) {
        joinButton.click()
        event.preventDefault()
      }
    }

  def joinChat(
      name: String,
      joinButton: HTMLButtonElement,
      sendButton: HTMLButtonElement
  ): Unit = {
    joinButton.disabled = true
    val playground = dom.document.getElementById("playground")
    playground.innerHTML = s"Trying to join chat as '$name'..."
    val chat = new dom.WebSocket(getWebsocketUri(dom.document, name))
    chat.onopen = { (event: Event) =>
      playground.insertBefore(
        p("Chat connection was successful!"),
        playground.firstChild
      )
      sendButton.disabled = false

      val messageField =
        dom.document.getElementById("message").asInstanceOf[HTMLInputElement]
      messageField.focus()
      messageField.onkeypress = { (event: KeyboardEvent) =>
        if (event.keyCode == 13) {
          sendButton.click()
          event.preventDefault()
        }
      }

      sendButton.onclick = { (event: Event) =>
        chat.send(messageField.value)
        messageField.value = ""
        messageField.focus()
        event.preventDefault()
      }

      event
    }
    chat.onerror = { (event: Event) =>
      playground.insertBefore(
        p(s"Failed: code: ${event.asInstanceOf[ErrorEvent].colno}"),
        playground.firstChild
      )
      joinButton.disabled = false
      sendButton.disabled = true
    }
    chat.onmessage = { (event: MessageEvent) =>
      val wsMsg = read[Protocol.Message](event.data.toString)

      wsMsg match {
        case Protocol.ChatMessage(sender, message) =>
          writeToArea(s"$sender said: $message")
        case Protocol.Joined(member, _) => writeToArea(s"$member joined!")
        case Protocol.Left(member, _)   => writeToArea(s"$member left!")
      }
    }
    chat.onclose = { (event: Event) =>
      playground.insertBefore(
        p("Connection to chat lost. You can try to rejoin manually."),
        playground.firstChild
      )
      joinButton.disabled = false
      sendButton.disabled = true
    }

    def writeToArea(text: String): Unit =
      playground.insertBefore(p(text), playground.firstChild)
  }

  def getWebsocketUri(
      document: Document,
      nameOfChatParticipant: String
  ): String = {
    val wsProtocol =
      if (dom.document.location.protocol == "https:") "wss" else "ws"

    s"$wsProtocol://${dom.document.location.host}/connect/$nameOfChatParticipant"
  }

  def p(msg: String) = {
    val paragraph = dom.document.createElement("p")
    paragraph.innerHTML = msg
    paragraph
  }
}
