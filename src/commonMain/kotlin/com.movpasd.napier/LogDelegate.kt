package com.movpasd.napier

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

object LogDelegate {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Log {
        return Log(thisRef?.let { it::class } ?: Any::class)
    }

}