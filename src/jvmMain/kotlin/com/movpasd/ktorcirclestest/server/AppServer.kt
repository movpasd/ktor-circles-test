package com.movpasd.ktorcirclestest.server

import com.movpasd.ktorcirclestest.model.*
import com.movpasd.ktorcirclestest.network.*
import com.movpasd.napier.Log
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext


class AppServer(enclosingContext: CoroutineContext) {

    class Connection(private val session: DefaultWebSocketSession) {

        companion object {
            private val lastId = AtomicInteger(0)
        }

        val id = lastId.getAndIncrement()

        suspend fun close(reason: String = "Server closed") {
            session.send(ServerClosedAnnouncement(reason).toFrame())
        }

        suspend fun send(msg: Message) {
            session.send(msg.toFrame())
        }

    }

    private val log = Log(this::class)

    // Network properties
    private val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    private val messageHandler = ServerMsgHandler

    // Asynchronicity properties
    private val scope = CoroutineScope(enclosingContext + Job(enclosingContext[Job]))

    // Game logic properties
    var model = AppModel()
    val updater = AppModelUpdater()
    val playerCounter = AtomicInteger(0)


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

        // TODO: all Ktor objects ought to be abstracted to an interface

        // Register connection
        val thisConnection = Connection(this)
        connections += thisConnection

        // Set up connection including connection id assignment
        log.info("New connection with User #${thisConnection.id}")
        thisConnection.send(IdAssignmentOrder(thisConnection.id))

        // Create a new player
        val thisPlayerId = createNewPlayer(thisConnection)

        // Dispatch incoming messages to the handler
        for (frame in incoming) {
            log.info("Received frame from user #${thisConnection.id}")
            messageHandler.handle(frame.toMessage(), this@AppServer, thisConnection)
        }

        // Kill player
        killPlayer(thisPlayerId)

        // Disconnect player
        thisConnection.close("Client disconnected")
        log.info("Ended connection with User #${thisConnection.id}")

    }

    /*
     * Game logic TODO: should be separated into an engine class
     */

    // TODO: Add client message authentication

    fun createNewPlayer(controllerCxn: Connection): Int {
        val newPlayerId = playerCounter.getAndIncrement()
        val update = NewPlayerUpdate(Player(
            id = newPlayerId,
            color = "rgb(255, 0, 0)",
            x = 100.0, y = 100.0,
        ))
        model = updater.apply(model, update)
        val msg = UpdateModelOrder(update)
        scope.launch {
            broadcastWithJob(msg)
                .join()
            controllerCxn.send(TakeControlOrder(newPlayerId))
        }
        return newPlayerId
    }

    fun killPlayer(playerId: Int) {
        val update = KillPlayerUpdate(playerId)
        model = updater.apply(model, update)
        broadcast(UpdateModelOrder(update))
    }

    /*
     * Interface to ServerMsgHandler
     */

    fun broadcast(msg: ToClientMessage) {
        connections.forEach { scope.launch { it.send(msg) } }
    }

    fun broadcastWithJob(msg: ToClientMessage): Job = scope.launch {
        connections.map { launch {it.send(msg)} }
            .joinAll()
    }

    fun narrowcast(msg: ToClientMessage, connection: Connection) = scope.launch {
        connection.send(msg)
    }

}