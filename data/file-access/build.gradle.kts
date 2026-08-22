plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "com.github.enteraname74.cloudy"
description = "Handles file access and operation"
version = "0.0.1"

dependencies {
    implementation(project(":logging"))
    implementation(project(":domain"))
    testImplementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines)
    implementation(libs.ktor.serialization.kotlinx.json)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}