package com.movpasd.ktorcirclestest.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


data class AppModel(
    val x: Double = 0.0,
    val y: Double = 0.0,
)

@Serializable
data class AppModelUpdate(
    val newx: Double,
    val newy: Double,
)


fun AppModelUpdate.serialized(): String {
    return Json.encodeToString(this)
}


fun deserializeUpdate(s: String): AppModelUpdate {
    return Json.decodeFromString(s)
}