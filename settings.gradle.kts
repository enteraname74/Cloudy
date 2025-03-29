pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "com.github.enteraname74.cloudy"

include("controller")
include("domain")
include("repository")
include("local-db")
include("config")
include("metadata")
include("metadata")
include("logging")
include("file-access")

project(":repository").projectDir = file("data/repository")
project(":local-db").projectDir = file("data/local-db")
project(":file-access").projectDir = file("data/file-access")
