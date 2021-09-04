package com.movpasd.ktorcirclestest.client

import com.movpasd.ktorcirclestest.model.AppModelSynchronizer
import com.movpasd.ktorcirclestest.model.AppModelUpdate
import kotlinx.coroutines.*
import org.w3c.dom.Document
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import kotlin.coroutines.CoroutineContext
import kotlin.js.Date
import kotlin.jvm.Volatile

class AppEngine(
    private val synch: AppModelSynchronizer,
    private val document: Document,
    coroutineContext: CoroutineContext,
    startAutomatically: Boolean = true,
) {

    var running = false
        private set

    private val scope = CoroutineScope(coroutineContext + Job(coroutineContext.job))

    private val TICK_TIME = 200.0
    private val SPEED = 50.0

    private enum class Keys { LEFT, RIGHT, UP, DOWN }

    @Volatile private val areKeysDown = mutableMapOf(
        Keys.LEFT to false,
        Keys.RIGHT to false,
        Keys.UP to false,
        Keys.DOWN to false,
    )
    private var keyDownListener: EventListener? = null
    private var keyUpListener: EventListener? = null


    init {
        if (startAutomatically) {
            start()
        }
    }

    fun start() {
        if (!running) {
            running = true
            setupKeyListeners()
            scope.launch {
                var timeBefore = Date.now()
                var deltaTime = 0.0
                while (isActive) {
                    tick()
                    deltaTime = TICK_TIME - (Date.now() - timeBefore)
                    if (deltaTime > 0) {
                        delay(deltaTime.toLong())
                    } else {
                        yield()
                        console.warn("Tick overran by ${-deltaTime} ms")
                    }
                    timeBefore = Date.now()
                }
            }
        } else {
            error("This AppEngine is already running")
        }
    }

    fun stop() {
        if (running) {
            running = false
            scope.cancel()
            removeKeyListeners()
        } else {
            error("This AppEngine is already stopped")
        }
    }

    private fun tick() {
        synch.apply {
            if (areKeysDown[Keys.LEFT] == true) {
                submitUpdate(AppModelUpdate(model.x - SPEED, model.y))
            }
            if (areKeysDown[Keys.RIGHT] == true) {
                submitUpdate(AppModelUpdate(model.x + SPEED, model.y))
            }
            if (areKeysDown[Keys.UP] == true) {
                submitUpdate(AppModelUpdate(model.x, model.y - SPEED))
            }
            if (areKeysDown[Keys.DOWN] == true) {
                submitUpdate(AppModelUpdate(model.x, model.y + SPEED))
            }
        }
    }

    private fun setupKeyListeners() {
        keyDownListener = EventListener(::onKeyDown)
        keyUpListener = EventListener(::onKeyUp)
        document.addEventListener("keydown", keyDownListener!!)
        document.addEventListener("keyup", keyUpListener!!)
    }

    private fun removeKeyListeners() {
        document.removeEventListener("keydown", keyDownListener!!)
        document.removeEventListener("keyup", keyUpListener!!)
        keyDownListener = null
        keyUpListener = null
    }

    private fun onKeyDown(event: Event) {
        val keyboardEvent = event as? KeyboardEvent ?: return
        when (keyboardEvent.key) {
            "ArrowLeft" -> areKeysDown[Keys.LEFT] = true
            "ArrowRight" -> areKeysDown[Keys.RIGHT] = true
            "ArrowUp" -> areKeysDown[Keys.UP] = true
            "ArrowDown" -> areKeysDown[Keys.DOWN] = true
        }
    }

    private fun onKeyUp(event: Event) {
        val keyboardEvent = event as? KeyboardEvent ?: return
        when (keyboardEvent.key) {
            "ArrowLeft" -> areKeysDown[Keys.LEFT] = false
            "ArrowRight" -> areKeysDown[Keys.RIGHT] = false
            "ArrowUp" -> areKeysDown[Keys.UP] = false
            "ArrowDown" -> areKeysDown[Keys.DOWN] = false
        }

    }

}