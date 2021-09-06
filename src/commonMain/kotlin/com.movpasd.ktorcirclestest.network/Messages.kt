/**
 * This file defines the network interface that server and client use to communicate
 */
package com.movpasd.ktorcirclestest.network

import com.movpasd.ktorcirclestest.model.AppModelUpdate
import com.movpasd.ktorcirclestest.model.Player
import io.ktor.http.cio.websocket.*
import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString


/*
 * Methods
 */

fun Message.toFrame() = Frame.Text(Json.encodeToString(this))
fun Frame.toMessage(): Message {
    return try {
        Json.decodeFromString<Message>((this as Frame.Text).readText())
    } catch (e: Exception) {
        UnreadableMessage(e.message)
    }
}

/**
 * Base class for type-safe client-server messaging
 *
 * @property time Unix time-stamp in milliseconds.
 */
@Serializable
sealed class Message {
    abstract val time: Long
}

@Serializable
class UnreadableMessage(val exceptionMessage: String? = null) : Message() {
    override val time = -1L
}


/*
 * Client messages
 */

@Serializable
sealed class ToServerMessage : Message() {
    abstract val clientId: Int
}

@Serializable
data class NewPlayerRequest(
    val player: Player,
    override val clientId: Int,
    override val time: Long = getTimeMillis(),
) : ToServerMessage()

@Serializable
data class MovePlayerRequest(
    val player: Player,
    override val clientId: Int,
    override val time: Long = getTimeMillis(),
) : ToServerMessage()

@Serializable
data class KillPlayerRequest(
    val player: Player,
    override val clientId: Int,
    override val time: Long = getTimeMillis(),
) : ToServerMessage()


/*
 * Server messages
 */

@Serializable
sealed class ToClientMessage : Message()

@Serializable
data class ConnectedResponse(
    val clientId: Int,
    override val time: Long = getTimeMillis(),
) : ToClientMessage()

@Serializable
data class ServerClosedAnnouncement(
    val reason: String = "Server has closed",
    override val time: Long = getTimeMillis(),
) : ToClientMessage()

@Serializable
data class NewPlayerOrder(
    val player: Player,
    override val time: Long = getTimeMillis(),
) : ToClientMessage()

@Serializable
data class MovePlayerOrder(
    val player: Player,
    override val time: Long = getTimeMillis(),
) : ToClientMessage()

@Serializable
data class UpdateModelOrder(
    val model: AppModelUpdate,
    override val time: Long = getTimeMillis(),
) : ToClientMessage()

@Serializable
data class KillPlayerOrder(
    val player: Player,
    override val time: Long = getTimeMillis(),
) : ToClientMessage()