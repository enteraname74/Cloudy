package com.github.enteraname74.cloudy.logging

import io.ktor.util.logging.KtorSimpleLogger
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class CloudyLogger<T : Any>(kClass: KClass<T>) {
    private val logger = KtorSimpleLogger(kClass.simpleName ?: kClass.jvmName)

    private fun String.format(
        feature: String?
    ): String {
        val functionName: String = Thread.currentThread().stackTrace.getOrNull(4)?.methodName?.let {
            "$it - "
        } ?: ""
        val formattedFeature: String = feature?.let { "$it - " } ?: ""

        return "$functionName$formattedFeature$this"
    }

    fun info(
        message: String,
        feature: String? = null
    ) = logger.info(message.format(feature = feature))

    fun warn(
        message: String,
        feature: String? = null
    ) = logger.error(message.format(feature = feature))

    fun error(
        message: String,
        feature: String? = null
    ) = logger.error(message.format(feature = feature))
}