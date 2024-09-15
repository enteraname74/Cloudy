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
    implementation(project(":domain"))

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.bundles.ktor.client)

    implementation(libs.jaudiotagger)
    implementation("io.ktor:ktor-client-cio-jvm:2.3.12")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}