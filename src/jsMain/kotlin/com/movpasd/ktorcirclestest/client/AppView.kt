package com.movpasd.ktorcirclestest.client

import com.movpasd.ktorcirclestest.model.AppModelSynchronizer
import kotlinx.coroutines.*
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.coroutines.CoroutineContext
import kotlin.js.Date

class AppView(
    private val synch: AppModelSynchronizer,
    private val ctx: CanvasRenderingContext2D,
    coroutineContext: CoroutineContext,
    startAutomatically: Boolean = true,
) {

    var running = false
        private set

    private val scope = CoroutineScope(coroutineContext + Job(coroutineContext.job))

    private val width = ctx.canvas.width.toDouble()
    private val height = ctx.canvas.height.toDouble()

    init {
        if (startAutomatically) { start() }
    }

    fun start() {
        if (!running) {
            running = true
            scope.launch {
                while (isActive) { paint() }
            }
        } else {
            error("This AppView is already running")
        }
    }

    fun stop() {
        if (running) {
            running = false
            scope.cancel()
        } else {
            error("This AppView is already stopped")
        }
    }


    private fun paint() {
        for (player in synch.model.players) {
            drawCircle(player.x, player.y, 10.0, player.color)
        }
    }


    private fun drawCircle(x: Double, y: Double, r: Double, style: String? = null) {
        style?.let { ctx.fillStyle = it }
        ctx.beginPath()
        ctx.arc(x, y, r, 0.0, 6.29)
        ctx.closePath()
        ctx.fill()
    }

}