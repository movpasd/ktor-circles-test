package com.movpasd.ktorcirclestest.network

import com.movpasd.ktorcirclestest.model.Player
import kotlinx.serialization.Serializable

/*
 * Client messages
 */

@Serializable
sealed class ClientMessage {
    abstract val clientId: Int
    abstract val time: Long
}

@Serializable
data class NewPlayerRequest(
    override val clientId: Int,
    override val time: Long,
    val player: Player,
) : ClientMessage()

@Serializable
data class MovePlayerRequest(
    override val clientId: Int,
    override val time: Long,
    val player: Player,
) : ClientMessage()

@Serializable
data class KillPlayerRequest(
    override val clientId: Int,
    override val time: Long,
    val player: Player,
) : ClientMessage()


/*
 * Server messages
 */

@Serializable
sealed class ServerMessage {
    abstract val time: Long
}

@Serializable
data class NewPlayerOrder(
    override val time: Long,
    val player: Player,
) : ServerMessage()

@Serializable
data class MovePlayerOrder(
    override val time: Long,
    val player: Player,
) : ServerMessage()

@Serializable
data class KillPlayerOrder(
    override val time: Long,
    val player: Player,
) : ServerMessage()