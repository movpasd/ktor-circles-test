package com.movpasd.ktorcirclestest.server

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.html.*


fun HTML.index() {

    head {
        title("Circles test")
    }

    body {
        div { id = "root" }
        script(src = "/static/ktor-circles-test.js") {}
    }

}


fun Application.mainModule() {

    install(WebSockets)
    install(ContentNegotiation) { json() }

    val appServer = AppServer()

    routing {
        route("/") {
            get { call.respondHtml(HttpStatusCode.OK, HTML::index) }
            static("/static") { resources() }
        }
        route("/ws") {
            webSocket { appServer.connectWebSocket(this) }
        }
    }

}