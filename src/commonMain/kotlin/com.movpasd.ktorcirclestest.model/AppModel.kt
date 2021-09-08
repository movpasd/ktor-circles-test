package com.movpasd.ktorcirclestest.model

import kotlinx.serialization.Serializable


@Serializable
data class AppModel(val players: List<Player> = listOf())

@Serializable
data class Player(
    val id: Int,
    val color: String,
    val x: Double,
    val y: Double,
)

@Serializable
data class PlayerMovement(
    val playerId: Int,
    val xNew: Double,
    val yNew: Double,
)


fun Player.moved(movement: PlayerMovement) = Player(this.id, this.color, movement.xNew, movement.yNew)
