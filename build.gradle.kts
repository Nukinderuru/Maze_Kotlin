plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.3"
    application
}

group = "com.school21"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:3.2.3")
    implementation("io.ktor:ktor-server-netty-jvm:3.2.3")
    implementation("io.ktor:ktor-server-status-pages-jvm:3.2.3")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.2.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.3")
    implementation("io.ktor:ktor-server-cors-jvm:3.2.3")
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.2.3")
}

application {
    mainClass = "com.school21.app.MainKt"
}

kotlin {
    jvmToolchain(21)
}

javafx {
    version = "21"
    modules = listOf("javafx.controls")
}

sourceSets {
    named("main") {
        java.setSrcDirs(listOf("main/kotlin"))
        resources.setSrcDirs(listOf("main/resources"))
    }
    named("test") {
        java.setSrcDirs(listOf("test/kotlin"))
        resources.setSrcDirs(listOf("test/resources"))
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runWeb") {
    group = "application"
    description = "Runs the Ktor web interface"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.school21.app.WebMainKt")
    jvmArgs("-Dweb.port=8081")
}

kover {
    reports {
        verify {
            rule {
                minBound(90)
            }
        }
    }
}
