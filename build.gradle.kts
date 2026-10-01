plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.10" apply false
}

group = "com.github.enteraname74.cloudy"
version = "0.0.1"

subprojects {
    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper> {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }
    }
}