package com.movpasd.ktorcirclestest.model

import com.movpasd.modeling.ModelSynchronizer
import com.movpasd.modeling.SynchServerProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class AppModelSynchronizer(
    serverProxy: SynchServerProxy<AppModelUpdate>,
    coroutineContext: CoroutineContext,
    startListening: Boolean = true,
) : ModelSynchronizer<AppModel, AppModelUpdate>(
    initialModel = AppModel(),
    modelUpdater = AppModelUpdater(),
    serverProxy = serverProxy,
    coroutineContext = coroutineContext,
    startListening = startListening,
) {

    override fun onClientModelUpdated(update: AppModelUpdate, isVirtual: Boolean): AppModelUpdate {
        return update
    }

    override fun onServerUpdateApplied(update: AppModelUpdate): AppModelUpdate {
        return update
    }

}