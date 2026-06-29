plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.enteraname74.cloudy"
description = "Handles file access and operation"
version = "0.0.1"

dependencies {
    implementation(project(":logging"))
    implementation(project(":domain"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}