plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "com.github.enteraname74.cloudy"
version = "0.0.1"

dependencies {
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.websockets)
    testImplementation(kotlin("test"))
    implementation(project(":logging"))
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

tasks.test {
    useJUnitPlatform()
}