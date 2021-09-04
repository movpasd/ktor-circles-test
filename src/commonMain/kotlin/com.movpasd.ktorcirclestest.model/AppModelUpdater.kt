package com.movpasd.ktorcirclestest.model

import com.movpasd.modeling.ModelUpdater

class AppModelUpdater : ModelUpdater<AppModel, AppModelUpdate>() {

    override fun apply(model: AppModel, update: AppModelUpdate): AppModel {
        return AppModel(update.newx, update.newy)
    }

    override fun combine(firstUpdate: AppModelUpdate, secondUpdate: AppModelUpdate): AppModelUpdate {
        return secondUpdate
    }

}