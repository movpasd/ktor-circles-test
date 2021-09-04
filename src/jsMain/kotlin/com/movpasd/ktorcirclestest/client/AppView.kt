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
                while (isActive) {
                    ctx.fillStyle = "rgb(255, 255, 255)"
                    ctx.fillRect(0.0, 0.0, width, height)
                    ctx.fillStyle = "rgb(255, 196, 196)"
                    drawCircle(synch.serverModel.x, synch.serverModel.y, 10.0)
                    ctx.fillStyle = "rgb(255, 0, 0)"
                    drawCircle(synch.model.x, synch.model.y, 10.0)
                    delay(10)
                }
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


    private fun drawCircle(x: Double, y: Double, r: Double) {
        ctx.beginPath()
        ctx.arc(x, y, r, 0.0, 6.29)
        ctx.closePath()
        ctx.fill()
    }

}