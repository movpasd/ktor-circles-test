package com.movpasd.modeling

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Volatile

/**
 * Asynchronous client-side class that attempts to synchronize a client model and a server model,
 * allowing for purely client-side "virtual" effects while waiting for server updates.
 * Best enjoyed with an immutable model data class :) (to ensure thread-safety).
 *
 * @param M Model class
 * @param U Model update class
 *
 * @param initialModel Initial instance of M
 * @param modelUpdater ModelUpdater for the model
 * @param serverApi SynchronizerServerApi instance: connect this up to the server
 * @param enclosingContext Optional context for the synchronizer coroutines. Defaults to Dispatchers.Default
 * @param startListening Whether to start the server-listen coroutines. If set to false, you must manually .start()
 */
open class ModelSynchronizer<M, U>(
    initialModel: M,
    private val modelUpdater: ModelUpdater<M, U>,
    private val serverApi: SynchronizerServerApi<U>,
    enclosingContext: CoroutineContext = Dispatchers.Default,
    startListening: Boolean = true,
) {


    /*
     * --- Exposed properties ---
     */

    val model: M
        get() = lastClientModel

    @Volatile var running: Boolean = false
        private set


    /*
     * --- Internal properties ---
     */

    // ModelSynchronizer gets its own CoroutineScope, by default in Dispatchers.Default,
    private val scope: CoroutineScope = CoroutineScope(enclosingContext + Job())
    private lateinit var mainJob: Job

    @Volatile private var lastServerModel: M = initialModel
    @Volatile private var lastClientModel: M = initialModel

    private val serverModelMutex = Mutex()
    private val clientModelMutex = Mutex()


    /*
     * --- Constructors ---
     */


    init {
        if (startListening) {
            start()
        }
    }


    /*
     * --- Exposed functions ---
     */


    /**
     * Submit an update to _both_ the client-side model _and_ the server.
     */
    fun submitUpdate(update: U) {

        val processedUpdate = onSubmittedToSynchronizer(update, false)

        scope.launch {

            clientModelMutex.withLock {
                lastClientModel = modelUpdater.apply(lastClientModel, processedUpdate)
            }

            onClientModelUpdated(update, false)

            try {
                val success = serverApi.submit(processedUpdate)
                if (success) {
                    onSubmissionSuccess(processedUpdate)
                } else {
                    onSubmissionFailure(processedUpdate)
                }
            } catch (e: Exception) {
                onSubmissionFailure(processedUpdate, e)
                throw e
            }

        }

        onSubmittedToServer(update)

    }


    /**
     * Start the ModelSynchronizer
     */
    fun start() {
        if (!running) {
            onStart()
            mainJob = scope.launch {
                running = true
                serverApi.fromServer.collect { receiveServerUpdate(it) }
                running = false
                stop()
            }
        } else {
            error("This ModelSynchronizer is already running")
        }
    }


    fun stop() {
        if (running) {
            onStop()
            mainJob.cancel()
            onMainJobCancelled()
        } else {
            error("This ModelSynchronizer is already stopped")
        }
    }


    /**
     * Submit an update to _only_ the client-side.
     * Useful for "virtual" effects (client-side predictions while waiting for server updates)
     */
    fun submitVirtualUpdate(update: U) {
        val processedUpdate = onSubmittedToSynchronizer(update, true)
        lastClientModel = modelUpdater.apply(lastClientModel, processedUpdate)
        onClientModelUpdated(update, true)
    }


    /*
     * --- Hooks for inheritance ---
     */


    /**
     * Called before submission to server. Can be used to process updates coming into the synchronizer.
     */
    open fun onSubmittedToSynchronizer(update: U, isVirtual: Boolean): U = update

    /**
     * Called once the client model has been updated. Can be used to process updates before submitting to the server.
     */
    open fun onClientModelUpdated(update: U, isVirtual: Boolean): U = update

    /**
     * Called after the model update has been submitted to the server.
     */
    open fun onSubmittedToServer(update: U) {}

    /**
     * Called if the submission to the server has succeeded.
     */
    open fun onSubmissionSuccess(update: U) {}

    /**
     * Called if the submission to the server has failed.
     * @param update The update which failed
     * @param exception If failure was due to exception, the exception, otherwise null
     */
    open fun onSubmissionFailure(update: U, exception: Exception? = null) {}

    /**
     * Called as soon as update is collected from serverApi's flow. Can be used to process server updates.
     */
    open fun onServerUpdateReceived(update: U): U = update

    /**
     * Called once server update has been applied to the model.
     */
    open fun onServerUpdateApplied(update: U): U = update

    /**
     * Called once started (right before main job is launched)
     */
    open fun onStart() {}

    /**
     * Called when stopping right before main job is cancelled.
     * See also: onMainJobCancelled().
     */
    open fun onStop() {}

    /**
     * Called once main job has been cancelled, after onStop().
     */
    open fun onMainJobCancelled() {}


    /*
     * --- Internal functions ---
     */


    private suspend fun receiveServerUpdate(update: U) {

        val processedUpdate = onServerUpdateReceived(update)

        clientModelMutex.withLock {
            serverModelMutex.withLock {
                lastServerModel = modelUpdater.apply(lastServerModel, processedUpdate)
                lastClientModel = lastServerModel
            }
        }

        onServerUpdateApplied(update)

    }


}


/**
 * Server API for ModelSynchronizer
 */
interface SynchronizerServerApi<U> {

    /**
     * Flow from server.
     */
    val fromServer: Flow<U>

    /**
     * Submits a model update to the server.
     * Should return whether the update has succeeded.
     */
    suspend fun submit(update: U): Boolean
}