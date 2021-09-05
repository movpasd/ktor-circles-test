package com.movpasd.ktorcirclestest.model

import kotlinx.serialization.Serializable


class AppModel(val players: List<Player> = listOf())


@Serializable
data class Player(
    val id: Int,
    val color: String,
    val x: Double,
    val y: Double,
)


@Serializable
data class AppModelUpdate(
    val players: List<Player>,
) {
    constructor (player: Player) : this(listOf(player))
}