plugins {
    id("java-library")
}

dependencies {
    implementation(project(":data"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-common:${project.properties["springdocVersion"]}")

    implementation("org.springframework:spring-tx")
    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-context-support")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-core")
    implementation("io.jsonwebtoken:jjwt-api:${project.properties["jwtVersion"]}")
    implementation("io.jsonwebtoken:jjwt-impl:${project.properties["jwtVersion"]}")
    implementation("io.jsonwebtoken:jjwt-jackson:${project.properties["jwtVersion"]}")

    implementation("org.mapstruct:mapstruct:${project.properties["mapstructVersion"]}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${project.properties["mapstructVersion"]}")
    implementation("org.aspectj:aspectjweaver")

    implementation("org.apache.commons:commons-lang3")
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:sqs")
    implementation("software.amazon.awssdk:rekognition")
    implementation("net.coobird:thumbnailator:${project.properties["thumbnailatorVersion"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}
