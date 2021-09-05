package com.movpasd.ktorcirclestest.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


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


fun AppModelUpdate.serialized(): String {
    return Json.encodeToString(this)
}


fun deserializeToUpdate(s: String): AppModelUpdate {
    return Json.decodeFromString(s)
}