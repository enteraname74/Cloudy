package com.github.enteraname74.cloudy.logging

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.application
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class CloudyLogger<T : Any>(kClass: KClass<T>) {
    private val logger: Logger = KtorSimpleLogger(kClass.simpleName ?: kClass.jvmName)

    private fun String.format(
        feature: String?
    ): String {
        val functionName: String = Thread.currentThread().stackTrace.getOrNull(4)?.methodName?.let {
            "$it - "
        } ?: ""
        val formattedFeature: String = feature?.let { "$it - " } ?: ""

        return "$functionName$formattedFeature$this"
    }

    fun trace(
        message: String,
        feature: String? = null
    ) = logger.trace(message.format(feature = feature))

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

fun Route.CloudyLogger(): CloudyLogger<*> = CloudyLogger(this.application::class)
val PipelineContext<Unit, ApplicationCall>.logger: CloudyLogger<*>
    get() = CloudyLogger(this.application::class)