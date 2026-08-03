plugins {
    id("java-library")
}
dependencies {
    implementation(project(":service"))
    implementation(project(":data"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${project.properties["springdocVersion"]}")
    implementation("org.mapstruct:mapstruct:${project.properties["mapstructVersion"]}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${project.properties["mapstructVersion"]}")
    implementation("org.aspectj:aspectjweaver")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
