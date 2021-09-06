package com.movpasd.ktorcirclestest.client

import com.movpasd.ktorcirclestest.client.network.AppKtorClient
import com.movpasd.ktorcirclestest.client.network.KtorClientSideProxy
import com.movpasd.ktorcirclestest.model.AppModelSynchronizer
import kotlinx.browser.document
import kotlinx.browser.window
import react.*
import react.dom.*
import kotlinx.coroutines.*
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent


// TODO: Extract Lifecycle behaviour from ModelSynchronizer, AppEngine, AppView, and KtorClient
// How exactly? Maybe as just a Lifecycle class which is instantiated in each object, and then you
// access the scope and such through myObjectWithLifecycle.lifecycle.scope etc
// Or if MyClassWithLifecycle also needs a contract, create a HasLifecycle interface and make Lifecycle
// take (parent: HasLifecycle) in the constructor, intended to be called as Lifecycle(this) within MyClassWithLifecycle.


fun main() {

    // Create window
    window.onload = {

        val linkElement = document.createElement("link")
        linkElement.setAttribute("rel", "stylesheet")
        linkElement.setAttribute("href", "/static/styles.css")
        document.head?.appendChild(linkElement)

        render(document.getElementById("root")) { child(fcReactAppComponent) }

        bindApp()

    }

}

fun bindApp() {

    val uiScope = MainScope()
    val bgScope = CoroutineScope(Dispatchers.Default)

    val canvas = try {
        document.getElementById("app_canvas") as HTMLCanvasElement
    } catch (e: Exception) {
        error("Couldn't find app canvas.\n${e.message}")
    }

    val renderingContext = canvas.getContext("2d") as CanvasRenderingContext2D

    val appKtorClient = AppKtorClient(bgScope.coroutineContext)
    val synch = AppModelSynchronizer(KtorClientSideProxy(appKtorClient), bgScope.coroutineContext)

    val engine = AppEngine(synch, document, uiScope.coroutineContext)
    val appView = AppView(synch, renderingContext, uiScope.coroutineContext)


    val quitListener = EventListener {
        val event = it as KeyboardEvent
        if (event.key == "`") {
            println("Stopping")
            appView.stop()
            engine.stop()
            synch.stop()
            appKtorClient.stop()

            uiScope.cancel()
            bgScope.cancel()
            println("Stopped")
        }
    }
    document.addEventListener("keypress", quitListener)
    document.addEventListener("beforeunload", quitListener)

}