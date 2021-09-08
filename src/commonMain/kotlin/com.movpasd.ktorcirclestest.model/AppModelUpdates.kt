package com.movpasd.ktorcirclestest.model

import kotlinx.serialization.Serializable


@Serializable
sealed class AppModelUpdate

@Serializable
object EmptyUpdate : AppModelUpdate()

@Serializable
data class CompoundUpdate(val elements: List<AppModelUpdate>) : AppModelUpdate()

@Serializable
data class ReplaceModelUpdate(val model: AppModel) : AppModelUpdate()

@Serializable
data class MovePlayerUpdate(val movement: PlayerMovement) : AppModelUpdate()

@Serializable
data class NewPlayerUpdate(val player: Player) : AppModelUpdate()

@Serializable
data class KillPlayerUpdate(val playerId: Int) : AppModelUpdate()