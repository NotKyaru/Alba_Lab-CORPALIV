plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "com.corpaliv.backend.catalogo.MainKt"
}

dependencies {
    implementation(project(":common"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.gson)
    runtimeOnly(libs.slf4j.simple) // muestra en consola el puerto en que quedó escuchando

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}

tasks.test {
    useJUnitPlatform()
}
