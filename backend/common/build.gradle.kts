plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.gson) // los microservicios usan Gson para sus rutas
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}

tasks.test {
    useJUnitPlatform()
}
