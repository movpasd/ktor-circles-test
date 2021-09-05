import com.movpasd.ktorcirclestest.model.deserializeToUpdate
import com.movpasd.ktorcirclestest.model.serialized
import io.ktor.application.*
import io.ktor.html.respondHtml
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.html.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet


fun HTML.index() {

    head {
        title("Circles test")
    }

    body {
        div { id = "root" }
        script(src = "/static/ktor-circles-test.js") {}
    }

}


class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val id = lastId.getAndIncrement()
}


fun main() {


    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())


    embeddedServer(Netty, port = 80, host = "localhost") {

        // TODO: Extract all this to a proper Application.module, rewrite the whole thing to be nice

        install(WebSockets)

        routing {

            route("/") {
                get {
                    call.respondHtml(HttpStatusCode.OK, HTML::index)
                }
                static("/static") {
                    resources()
                }
            }

            route("/ws") {
                webSocket {
                    val thisConnection = Connection(this)
                    connections += thisConnection
                    println("New user ${thisConnection.id}")
                    for (frame in incoming) {
                        println("Received frame from user ${thisConnection.id}")
                        val frameText = (frame as? Frame.Text)?.readText()
                        println(frameText ?: "Unknown format")
                        frameText ?: continue
                        val updateObject = deserializeToUpdate(frameText)
                        connections.forEach {
                            println("\tSending to user ${it.id}")
                            it.session.send(Frame.Text(updateObject.serialized()))
                        }
                    }
                    println("User ${thisConnection.id} disconnected")
                }
            }

        }

    }
        .start(wait = true)

}

