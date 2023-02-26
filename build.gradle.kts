import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("application")
    application
}
version = "0.1.0"

val tornadofxVersion: String by rootProject
val jacksonCSVVersion: String by rootProject
val jacksonModuleKotlinVersion: String by rootProject
val virtualizedfxVersion: String by rootProject
val coroutinesVersion: String by rootProject
val controlsfxVersion: String by rootProject

repositories {
    mavenCentral()
    maven(uri("https://oss.sonatype.org/content/repositories/snapshots"))
}

application {
    mainClass.set("solve.SolveApp")
    applicationDefaultJvmArgs = listOf("--add-opens", "javafx.graphics/javafx.scene=ALL-UNNAMED")
}

javafx {
    version = "17"
    modules("javafx.controls")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
    testImplementation ("org.junit.jupiter:junit-jupiter-params:5.8.0-M1")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.0-M1")
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:$tornadofxVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:$jacksonCSVVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonModuleKotlinVersion")
    implementation("io.github.palexdev:virtualizedfx:$virtualizedfxVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$coroutinesVersion")
    implementation("junit:junit:4.13.1")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(
            TestLogEvent.PASSED,
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED
        )
    }
}
