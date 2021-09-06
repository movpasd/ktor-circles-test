package com.movpasd.ktorcirclestest.server

import com.movpasd.ktorcirclestest.model.AppModel
import com.movpasd.ktorcirclestest.model.AppModelUpdate
import com.movpasd.ktorcirclestest.model.Player
import com.movpasd.ktorcirclestest.network.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.date.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.html.currentTimeMillis
import java.time.Instant.now
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext


class AppServer(enclosingContext: CoroutineContext) {

    class Connection(val session: DefaultWebSocketSession) {

        companion object {
            private var lastId = AtomicInteger(0)
        }

        val id = lastId.getAndIncrement()
        val outgoingMessages = Channel<Message>()

        suspend fun close(reason: String = "Server closed") {
            session.send(ServerClosedAnnouncement(reason).toFrame())
        }

    }

    private object Log {
        private val logger = Logger.getLogger("AppServer")
        fun info(s: String) = logger.log(Level.INFO, s)
        fun warn(s: String) = logger.log(Level.WARNING, s)
        fun err(s: String) = logger.log(Level.SEVERE, s)
    }

    // Network properties
    private val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    private val messageHandler = ServerMsgHandler

    // Asynchronicity properties
    private val scope = CoroutineScope(enclosingContext + Job(enclosingContext[Job]))

    // Game logic properties
    var model = AppModel()


    fun close() {
        scope.launch {
            connections.map { scope.launch { it.close() } }
                .joinAll()
            scope.cancel()
        }
    }


    /*
     * Interface to KtorServer
     */

    suspend fun connectWebSocket(session: DefaultWebSocketServerSession) = session.apply {

        val thisConnection = Connection(this)
        connections += thisConnection

        Log.info("New connection with User #${thisConnection.id}")

        send(ConnectedResponse(thisConnection.id).toFrame())

        for (frame in incoming) {
            Log.info("Received frame from user #${thisConnection.id}")
            val message = frame.toMessage()
            messageHandler.handle(message, this@AppServer, thisConnection)
        }

        Log.info("Ended connection with User #${thisConnection.id}")
    }

    /*
     * Interface to ServerMsgHandler
     */

    fun broadcastOrder(msg: ToClientMessage) {
        connections.forEach { scope.launch { it.outgoingMessages.send(msg) } }
    }

}