package com.movpasd.ktorcirclestest.server

import com.movpasd.ktorcirclestest.network.*
import java.util.logging.Level
import java.util.logging.Logger

object ServerMsgHandler {

    private object Log {
        private val logger = Logger.getLogger("ServerMsgHandler")
        fun info(s: String) = logger.log(Level.INFO, s)
        fun warn(s: String) = logger.log(Level.WARNING, s)
        fun err(s: String) = logger.log(Level.SEVERE, s)
    }


    fun handle(msg: Message, acceptor: AppServer, connection: AppServer.Connection) {
        Log.warn("Don't know how to handle message of type ${msg::class}: \n $msg")
    }

}