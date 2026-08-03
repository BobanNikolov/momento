pluginManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    plugins {
        id("org.springframework.boot") version "3.3.4"
        id("io.spring.dependency-management") version "1.1.6"
    }
}



rootProject.name = "momento-worker"
include("api")
include("service")
include("data")
include("application")


