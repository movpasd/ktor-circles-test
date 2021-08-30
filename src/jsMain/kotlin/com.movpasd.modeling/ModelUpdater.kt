package com.movpasd.modeling

/**
 * Applies model updates to models.
 * Should preferably be used with immutable models.
 *
 * @param M Model class
 * @param U Model update class
 */
abstract class ModelUpdater<M, U> {

    /**
     * Applies an update to a model and returns the updated model
     */
    abstract fun apply(model: M, update: U): M

    /**
     * Combines two model updates into one
     */
    abstract fun combine(firstUpdate: U, secondUpdate: U): U

    fun U.after(firstUpdate: U): U = combine(firstUpdate, this)
    fun U.before(secondUpdate: U): U = combine(secondUpdate, this)

}