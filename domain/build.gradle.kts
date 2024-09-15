plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "com.github.enteraname74.cloudy"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}