plugins {
    alias(libs.plugins.ktor)
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "com.github.enteraname74.cloudy"
version = "0.0.1"

ktor {
    docker {
        localImageName.set("cloudy-docker-image")
        imageTag.set("0.0.1")
        jreVersion.set(JavaVersion.VERSION_17)
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":config"))
    implementation(project(":logging"))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)

    implementation(libs.ktor.simple.cache)
    implementation(libs.ktor.server.netty)

    testImplementation(libs.kotlin.test.junit)

    implementation(libs.bundles.koin)
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

tasks.test {
    useJUnitPlatform()
}