
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20" apply false
}

group = "com.example"
version = "0.0.1"

repositories {
    mavenCentral()
}