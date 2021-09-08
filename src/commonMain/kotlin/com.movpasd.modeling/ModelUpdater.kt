package com.movpasd.modeling

/**
 * Applies model updates to models.
 * Should preferably be used with immutable models.
 *
 * @param M Model class
 * @param U Model update class
 */
interface ModelUpdater<M, U> {

    /**
     * Applies an update to a model and returns the updated model
     */
    fun apply(model: M, update: U): M

}