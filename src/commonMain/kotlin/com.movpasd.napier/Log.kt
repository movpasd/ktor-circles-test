package com.movpasd.napier

import io.github.aakira.napier.Napier
import kotlin.reflect.KClass

class Log(val tag: String) {

    constructor(cls: KClass<*>): this(cls.simpleName ?: "<?CLASSNAME>")
    constructor(obj: Any?) : this(obj.toString())

    /**
     * Log at VERBOSE-level
     */
    fun verbose(msg: String, throwable: Throwable? = null) = Napier.v(msg, throwable, tag)

    /**
     * Log at DEBUG-level
     */
    fun debug(msg: String, throwable: Throwable? = null) = Napier.d(msg, throwable, tag)

    /**
     * Log at INFO-level
     */
    fun info(msg: String, throwable: Throwable? = null) = Napier.i(msg, throwable, tag)

    /**
     * Log at WARNING-level
     */
    fun warn(msg: String, throwable: Throwable? = null) = Napier.w(msg, throwable, tag)

    /**
     * Log at ERROR-level
     */
    fun err(msg: String, throwable: Throwable? = null) = Napier.e(msg, throwable, tag)

}