plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.enteraname74.cloudy"
version = "0.0.1"

dependencies {
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.logging)
    implementation(libs.ktor.server.core)
}

tasks.test {
    useJUnitPlatform()
}