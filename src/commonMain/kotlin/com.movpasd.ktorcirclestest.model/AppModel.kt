package com.movpasd.ktorcirclestest.model

import kotlinx.serialization.Serializable


data class AppModel(val players: List<Player> = listOf()) {

    fun withNewPlayer(player: Player): AppModel {
        return AppModel(this.players + player)
    }

    fun withPlayerRemoved(player: Player): AppModel {
        return AppModel(this.players.filter { it.id != player.id })
    }

    fun withPlayerUpdated(newPlayerState: Player): AppModel {
        return AppModel(this.players.map { player ->
            if (player.id == newPlayerState.id) {
                newPlayerState
            } else player
        })
    }

}


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
    constructor (model: AppModel) : this(model.players)
}