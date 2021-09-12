package com.movpasd.ktorcirclestest.client.network

import com.movpasd.ktorcirclestest.model.AppModelUpdate
import com.movpasd.modeling.SynchServerProxy


class KtorClientSideProxy(private val appKtorClient: AppKtorClient) : SynchServerProxy<AppModelUpdate> {

    override val fromServer = appKtorClient.incomingUpdatesAsFlow()

    override suspend fun submit(update: AppModelUpdate): Boolean {
        return try {
            appKtorClient.sendClientUpdate(update)
            true
        } catch (e: Exception) {
            false
        }
    }

}