package com.movpasd.ktorcirclestest.client

import com.movpasd.ktorcirclestest.model.AppModelUpdate
import com.movpasd.modeling.SynchServerProxy


class KtorClientSideProxy(private val ktorClient: KtorClient) : SynchServerProxy<AppModelUpdate> {

    override val fromServer = ktorClient.incomingUpdatesAsFlow()

    override suspend fun submit(update: AppModelUpdate): Boolean {
        return try {
            ktorClient.sendClientUpdate(update)
            true
        } catch (e: Exception) {
            false
        }
    }

}
