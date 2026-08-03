plugins {
    id("java-library")
    id("io.spring.dependency-management")
}
dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    api("jakarta.persistence:jakarta.persistence-api")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.hibernate.orm:hibernate-core")
    implementation("org.hibernate.orm:hibernate-envers")
    implementation("org.springframework.boot:spring-boot-starter-security")
    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
}
