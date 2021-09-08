package com.movpasd.ktorcirclestest.server

import com.movpasd.ktorcirclestest.network.*
import com.movpasd.napier.Log
import io.ktor.util.date.*

object ServerMsgHandler {

    private val log = Log(this::class)

    // TODO: appServer really ought to be extracted to a interface to control access
    fun handle(msg: Message, appServer: AppServer, connection: AppServer.Connection) {

        when (msg) {
            is NewPlayerRequest  -> appServer.narrowcast(RejectionResponse("Not implemented"), connection)
            is MovePlayerRequest -> appServer.narrowcast(RejectionResponse("Not implemented"), connection)
            is KillPlayerRequest -> appServer.narrowcast(RejectionResponse("Not implemented"), connection)
            else                 -> log.warn("Don't know how to handle message of type ${msg::class}: \n $msg")
        }

    }

}