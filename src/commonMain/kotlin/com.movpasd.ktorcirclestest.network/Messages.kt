package com.movpasd.ktorcirclestest.network

import com.movpasd.ktorcirclestest.model.Player
import io.ktor.http.cio.websocket.*
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
    override val clientId: Int,
    override val time: Long,
    val player: Player,
) : ToServerMessage()

@Serializable
data class MovePlayerRequest(
    override val clientId: Int,
    override val time: Long,
    val player: Player,
) : ToServerMessage()

@Serializable
data class KillPlayerRequest(
    override val clientId: Int,
    override val time: Long,
    val player: Player,
) : ToServerMessage()


/*
 * Server messages
 */

@Serializable
sealed class ToClientMessage : Message()

@Serializable
data class ConnectedResponse(
    override val time: Long,
    val clientId: Int,
) : ToClientMessage()

@Serializable
data class NewPlayerOrder(
    override val time: Long,
    val player: Player,
) : ToClientMessage()

@Serializable
data class MovePlayerOrder(
    override val time: Long,
    val player: Player,
) : ToClientMessage()

@Serializable
data class KillPlayerOrder(
    override val time: Long,
    val player: Player,
) : ToClientMessage()