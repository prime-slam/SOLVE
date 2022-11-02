plugins {
    kotlin("jvm") version "1.7.20"
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("application")
    application
}
version = "0.1.0"

val tornadofxVersion: String by rootProject
val virtualizedfxVersion: String by rootProject

repositories {
    mavenCentral()
    maven(uri("https://oss.sonatype.org/content/repositories/snapshots"))
}

application {
    mainClass.set("sliv.tool.SlivToolApp")
}

javafx {
    version = "17"
    modules("javafx.controls")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:$tornadofxVersion")
    implementation("io.github.palexdev:virtualizedfx:$virtualizedfxVersion")

    testImplementation(kotlin("test"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
