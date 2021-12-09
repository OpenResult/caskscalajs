# Mill, Cask, Scala.js with shared js code between server and client
Ported from https://github.com/jrudolph/akka-http-scala-js-websocket-chat

Now using:
- [Mill build tool](https://www.lihaoyi.com/mill/)
- [Cask web server](https://www.lihaoyi.com/cask/)
- [Scala.js](https://www.scala-js.org) example client with [shared](build.sc) code between [server](server/src/Server.scala) and [client](js/src/MainJs.scala).
- Vuejs gui nuxt integrated in mill build
- VS Code Vetur configured. https://marketplace.visualstudio.com/items?itemName=octref.vetur

Currently needs initial `npm install` in vuegui folder.
Set up for development, use: `mill -w server.runBackground`

`http://localhost:8384/htm/`

Cask file serving has a nasty [hack](server/src/Server.scala) to be able to serve a default page required by vuejs routing. This would be handled by reverseproxy (eg nginx) in production scenario.