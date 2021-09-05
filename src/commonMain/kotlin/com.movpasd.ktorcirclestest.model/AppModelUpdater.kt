package com.movpasd.ktorcirclestest.model

import com.movpasd.modeling.ModelUpdater

class AppModelUpdater : ModelUpdater<AppModel, AppModelUpdate>() {

    override fun apply(model: AppModel, update: AppModelUpdate): AppModel {
        // Combine model list with update list with the update elements taking precedence
        val newList = update.players.toMutableList()
        val updateIds = update.players.map { it.id }
        model.players.forEach { player ->
            if (player.id !in updateIds) {
                newList.add(player)
            }
        }
        return AppModel(newList.toList())
    }

    override fun combine(firstUpdate: AppModelUpdate, secondUpdate: AppModelUpdate): AppModelUpdate {
        // Combine the secondUpdate and firstUpdate lists, with the secondUpdate elements taking precedence
        val newList = secondUpdate.players.toMutableList()
        val secondUpdateIds = secondUpdate.players.map { it.id }
        firstUpdate.players.forEach { player ->
            if (player.id !in secondUpdateIds) {
                newList.add(player)
            }
        }
        return AppModelUpdate(newList.toList())
    }

}