plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.github.enteraname74.cloudy"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
}

tasks.test {
    useJUnitPlatform()
}