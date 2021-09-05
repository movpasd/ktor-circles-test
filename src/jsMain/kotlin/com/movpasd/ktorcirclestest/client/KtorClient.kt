package com.movpasd.ktorcirclestest.client

import com.movpasd.ktorcirclestest.model.AppModelUpdate
import com.movpasd.ktorcirclestest.model.deserializeToUpdate
import com.movpasd.ktorcirclestest.model.serialized
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext


class KtorClient(
    coroutineContext: CoroutineContext,
    automaticallyStart: Boolean = true,
) {

    // TODO: This class feels hacked together, might need to be redone/rethought
    // Like the incoming and outgoing queues should clearly be some kind of flow or channel.
    // And start() is too big.

    var running = false
        private set

    private val scope = CoroutineScope(coroutineContext + Job(coroutineContext.job))

    private val ktorClient = HttpClient { install(WebSockets) }

    private val incomingQueue = mutableListOf<AppModelUpdate>()
    private val outgoingQueue = mutableListOf<AppModelUpdate>()

    init {
        if (automaticallyStart) { start() }
    }

    fun start() {
        if (!running) {
            running = true
            scope.launch {
                println("Started ktorClient")
                ktorClient.webSocket(host = "localhost", path = "/ws") {
                    val incomingJob = launch {
                        for (frame in incoming) {
                            println("Receiving a frame: ")
                            try {
                                println((frame as? Frame.Text)?.readText() ?: "Couldn't read frame")
                                val serverUpdate = deserializeToUpdate((frame as? Frame.Text ?: continue).readText())
                                incomingQueue.add(serverUpdate)
                            } catch (e: Exception) {
                                println(e)
                            }
                        }
                    }
                    val outgoingJob = launch {
                        while (isActive) {
                            if (outgoingQueue.size == 0) {
                                yield()
                            } else {
                                val clientUpdate = outgoingQueue.removeFirst()
                                try {
                                    send(Frame.Text(clientUpdate.serialized()))
                                } catch (e: Exception) {
                                    println(e)
                                }
                            }
                        }
                    }
                    incomingJob.join()
                    outgoingJob.join()
                }
            }
        } else {
            error("This KtorClient is already running")
        }
    }

    fun stop() {
        if (running) {
            running = false
            scope.cancel()
        } else {
            error("This KtorClient is already stopped")
        }
    }

    fun incomingUpdatesAsFlow(): Flow<AppModelUpdate> {
        return flow {
            while (running) {
                if (incomingQueue.size == 0) {
                    yield()
                } else {
                    emit(incomingQueue.removeFirst())
                }
            }
        }
    }

    fun sendClientUpdate(update: AppModelUpdate) {
        outgoingQueue.add(update)
    }

}