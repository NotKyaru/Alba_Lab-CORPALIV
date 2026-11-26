plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring) // abre las clases para que Spring pueda crear proxies
    alias(libs.plugins.kotlin.jpa)    // constructor vacío para las entidades JPA
    alias(libs.plugins.spring.boot)
}

kotlin {
    jvmToolchain(17)
}

// Hibernate necesita poder extender las entidades.
allOpen {
    annotation("jakarta.persistence.Entity")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.h2console)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    runtimeOnly(libs.h2)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}
