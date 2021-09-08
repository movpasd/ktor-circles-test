package com.movpasd.ktorcirclestest.model

import com.movpasd.modeling.ModelUpdater
import com.movpasd.napier.LogDelegate

class AppModelUpdater : ModelUpdater<AppModel, AppModelUpdate> {

    private val log by LogDelegate

    override fun apply(model: AppModel, update: AppModelUpdate): AppModel {

        return when (update) {
            is EmptyUpdate        -> model
            is CompoundUpdate     -> {
                var nextModel = model
                update.elements.forEach { e -> nextModel = apply(nextModel, e) }
                nextModel
            }
            is ReplaceModelUpdate -> update.model
            is MovePlayerUpdate   -> {
                AppModel(model.players.map { if (it.id == update.movement.playerId) it.moved(update.movement) else it })
            }
            is NewPlayerUpdate    -> AppModel(model.players + update.player)
            is KillPlayerUpdate   -> AppModel(model.players.filterNot { it.id == update.playerId })
            else                  -> {
                log.warn("Don't know how to handle app model update of type ${update::class}:\n$update")
                model
            }
        }

    }


}