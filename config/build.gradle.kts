plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.enteraname74.cloudy"
version = "0.0.1"

dependencies {
    implementation(project(":domain"))
    implementation(project(":repository"))
    implementation(project(":local-db"))
    implementation(project(":metadata"))
    implementation(project(":file-access"))

    // DI
    implementation(libs.bundles.koin)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.websockets)

    // Serialization
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // HTTP
    implementation(libs.ktor.server.cors)

    // Security
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    // Status page
    implementation(libs.ktor.server.status.page)

    api(libs.logback.classic)
}

tasks.test {
    useJUnitPlatform()
}