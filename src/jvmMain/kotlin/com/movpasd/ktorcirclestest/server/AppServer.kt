package com.movpasd.ktorcirclestest.server

import com.movpasd.ktorcirclestest.network.ConnectedResponse
import com.movpasd.ktorcirclestest.network.toFrame
import com.movpasd.ktorcirclestest.network.toMessage
import io.ktor.http.cio.websocket.*
import io.ktor.util.date.*
import io.ktor.websocket.*
import java.time.Instant.now
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Level
import java.util.logging.Logger


class AppServer {

    class Connection(val session: DefaultWebSocketSession) {

        companion object {
            private var lastId = AtomicInteger(0)
        }

        val id = lastId.getAndIncrement()

    }

    private object Log {
        private val logger = Logger.getLogger("AppServer")
        fun info(s: String) = logger.log(Level.INFO, s)
        fun warn(s: String) = logger.log(Level.WARNING, s)
        fun err(s: String) = logger.log(Level.SEVERE, s)
    }


    private val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    private val messageHandler = ServerMsgHandler

    suspend fun connectWebSocket(session: DefaultWebSocketServerSession) = session.apply {

        val thisConnection = Connection(this)
        connections += thisConnection

        Log.info("New connection with User #${thisConnection.id}")

        send(ConnectedResponse(getTimeMillis(), thisConnection.id).toFrame())

        for (frame in incoming) {
            Log.info("Received frame from user #${thisConnection.id}")
            val message = frame.toMessage()
            messageHandler.handle(message, this@AppServer, thisConnection)
        }

        Log.info("Ended connection with User #${thisConnection.id}")
    }

}