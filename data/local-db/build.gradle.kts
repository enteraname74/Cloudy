plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "com.github.enteraname74.cloudy"
description = "Local data storage implementation for Cloudy"
version = "0.0.1"

dependencies {
    implementation(project(":domain"))
    implementation(project(":repository"))
    implementation(project(":logging"))

    implementation(libs.bundles.exposed)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.sqlite.jdbc)
    implementation(libs.postgresql)
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

tasks.test {
    useJUnitPlatform()
}