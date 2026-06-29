plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.enteraname74.cloudy"
description = "Handles data access for Cloudy"
version = "0.0.1"

dependencies {
    implementation(project(":domain"))
    implementation(project(":metadata"))
    implementation(project(":file-access"))
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

tasks.test {
    useJUnitPlatform()
}