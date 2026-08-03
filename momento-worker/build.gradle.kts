plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.momentoworker"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.momentoworker.Application"
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    tasks.withType<Jar> {
        enabled = true
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.4")
            mavenBom("software.amazon.awssdk:bom:${project.properties["awsSdkVersion"]}")
        }
    }

    dependencies {
        compileOnly(
                "org.projectlombok:lombok"
        )

        testCompileOnly(
                "org.projectlombok:lombok"
        )

        annotationProcessor(
                "org.projectlombok:lombok"
        )

        testAnnotationProcessor("org.projectlombok:lombok")

        implementation(
                "org.slf4j:slf4j-api"
        )
    }
}
