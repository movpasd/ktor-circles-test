package com.movpasd.ktorcirclestest.client.network

import com.movpasd.ktorcirclestest.network.Message
import com.movpasd.napier.Log

object ClientMsgHandler {

    private val log = Log(this::class)

    // TODO: Extract appClient as an interface
    fun handle(msg: Message, appClient: AppKtorClient) {
        when (msg) {

            else -> log.warn("Couldn't handle message of type ${msg::class}:\n$msg")
        }


    }


}