# Mill, Cask, Scala.js with shared js code between server and client
Ripped from https://github.com/jrudolph/akka-http-scala-js-websocket-chat

Now using:
- [Mill build tool](https://www.lihaoyi.com/mill/)
- [Cask web server](https://www.lihaoyi.com/cask/)
- [Scala.js](https://www.scala-js.org) example client with [shared](build.sc) code between [server](server/src/Server.scala) and [client](js/src/MainJs.scala).
- Vuejs gui with vue cli integrated in mill build


Set up for development, use: `mill -w server.runBackground`