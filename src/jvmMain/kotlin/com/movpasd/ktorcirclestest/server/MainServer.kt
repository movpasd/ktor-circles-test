import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.http.content.resources
import io.ktor.http.content.static
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

fun main() {

    embeddedServer(Netty, port = 80, host = "localhost") {
        routing {

            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }

            static("/static") {
                resources()
            }

        }
    }
        .start(wait = true)

}