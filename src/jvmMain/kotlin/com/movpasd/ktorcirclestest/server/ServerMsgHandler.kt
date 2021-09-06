package com.movpasd.ktorcirclestest.server

import com.movpasd.ktorcirclestest.network.*
import io.ktor.util.date.*
import java.util.logging.Level
import java.util.logging.Logger

object ServerMsgHandler {

    private object Log {
        private val logger = Logger.getLogger("ServerMsgHandler")
        fun info(s: String) = logger.log(Level.INFO, s)
        fun warn(s: String) = logger.log(Level.WARNING, s)
        fun err(s: String) = logger.log(Level.SEVERE, s)
    }


    // TODO: appServer really ought to be extracted to a interface to control access
    fun handle(msg: Message, appServer: AppServer, connection: AppServer.Connection) {
        Log.warn("Don't know how to handle message of type ${msg::class}: \n $msg")
    }

    fun handle(msg: NewPlayerRequest, appServer: AppServer, connection: AppServer.Connection) {
        appServer.model = appServer.model.withNewPlayer(msg.player)
        appServer.broadcastOrder(NewPlayerOrder(msg.player))
    }

    fun handle(msg: MovePlayerRequest, appServer: AppServer, connection: AppServer.Connection) {
        appServer.model = appServer.model.withPlayerUpdated(msg.player)
        appServer.broadcastOrder(MovePlayerOrder(msg.player))
    }

    fun handle(msg: KillPlayerRequest, appServer: AppServer, connection: AppServer.Connection) {
        appServer.model = appServer.model.withPlayerRemoved(msg.player)
        appServer.broadcastOrder(KillPlayerOrder(msg.player))
    }

}