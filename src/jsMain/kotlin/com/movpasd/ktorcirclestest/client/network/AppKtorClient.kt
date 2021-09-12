package com.movpasd.ktorcirclestest.client.network

import com.movpasd.ktorcirclestest.model.AppModelUpdate
import com.movpasd.ktorcirclestest.network.toMessage
import com.movpasd.napier.Log
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext


// TODO: This class may be doing too many things ...
// Should split up the following functionality:
// 1) Wrapping of Ktor's HttpClient and decoding it to the project-level's Message abstraction
// 2) Dispatch of the Messages to the ClientMsgHandler and sending the updates back to the engine

class AppKtorClient(
    coroutineContext: CoroutineContext,
    automaticallyStart: Boolean = true,
) {

    private val log = Log(this::class)

    var running = false
        private set

    private val scope = CoroutineScope(coroutineContext + Job(coroutineContext.job))
    private var mainJob: Job? = null

    private val ktorClient = HttpClient { install(WebSockets) }

    private val msgHandler = ClientMsgHandler

    private val incomingUpdates: Channel<AppModelUpdate>? = null
    private val outgoingUpdates: Channel<AppModelUpdate>? = null

    init {
        if (automaticallyStart) { start() }
    }

    fun start() {
        if (!running) {
            running = true
            mainJob = scope.launch {

                log.info("Starting Ktor websocket client")

                ktorClient.webSocket(host = "localhost", path = "/ws") webSocketSession@ {

                    sendNew

                    val incomingJob = launch { processIncomingMessages(this@webSocketSession) }
                    val outgoingJob = launch { sendOutgoingMessages(this@webSocketSession) }

                    incomingJob.join()
                    outgoingJob.join()

                    close()

                }

            }

        } else {
            error("This KtorClient is already running")
        }
    }

    fun stop() {
        if (running) {
            running = false
            mainJob = null

            scope.cancel()
        } else {
            error("This KtorClient is already stopped")
        }
    }

    fun incomingUpdatesAsFlow(): Flow<AppModelUpdate> {
        return flow {
            while (running) {
                if (incomingUpdates.size == 0) {
                    yield()
                } else {
                    emit(incomingUpdates.removeFirst())
                }
            }
        }
    }

    fun sendClientUpdate(update: AppModelUpdate) {
        outgoingUpdates.add(update)
    }

    /*
     * Internal functions
     */

    private suspend fun processIncomingMessages(ctx: DefaultClientWebSocketSession) = ctx.apply {

        for (frame in incoming) {
            log.info("Receiving message")
            msgHandler.handle(frame.toMessage(), this@AppKtorClient)
        }

    }

    private suspend fun sendOutgoingMessages(ctx: DefaultClientWebSocketSession) = ctx.apply {



        while (running) {
            if (outgoingUpdates.size == 0) {
                yield()
            } else {
                val clientUpdate = outgoingUpdates.removeFirst()
                try {
                    send(Frame.Text(clientUpdate.serialized()))
                } catch (e: Exception) {
                    println(e)
                }
            }
        }

    }

}