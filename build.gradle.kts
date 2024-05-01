import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.openjfx.javafxplugin") version "0.0.14"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
    id("application")
    application
}
version = "0.1.0"

val tornadofxVersion: String by rootProject
val jacksonCSVVersion: String by rootProject
val jacksonModuleKotlinVersion: String by rootProject
val virtualizedfxVersion: String by rootProject
val materialfxVersion: String by rootProject
val coroutinesVersion: String by rootProject
val controlsfxVersion: String by rootProject
val junitVersion: String by rootProject
val junitJupiterVersion: String by rootProject
val junitJupiterEngineVersion: String by rootProject
val junitJupiterParamsVersion: String by rootProject
val testfxVersion: String by rootProject
val testfxMonocleVersion: String by rootProject
val mockkVersion: String by rootProject
val openglfxVersion: String by rootProject
val lwjglVersion: String by rootProject
val jomlVersion: String by rootProject

var lwjglNativesClassifier: String = ""
when {
    OperatingSystem.current().isLinux -> {
        val archProperty = System.getProperty("os.arch")
        if (archProperty.startsWith("arm") || archProperty.startsWith("aarch64")) {
            val postfix = if (archProperty.contains("64") || archProperty.startsWith("armv8")) {
                "arm64"
            } else {
                "arm32"
            }
            lwjglNativesClassifier = "natives-linux-$postfix"
        } else {
            lwjglNativesClassifier = "natives-linux"
        }
    }
    OperatingSystem.current().isWindows ->
        lwjglNativesClassifier =
            if (System.getProperty("os.arch").contains("64")) {
                "natives-windows"
            } else {
                "natives-windows-x86"
            }
    OperatingSystem.current().isMacOsX -> lwjglNativesClassifier = "natives-macos"
    else -> throw IllegalArgumentException("Unexpected OS type!")
}

repositories {
    mavenCentral()
    maven(uri("https://oss.sonatype.org/content/repositories/snapshots"))
    maven(uri("https://jitpack.io"))
}

application {
    mainClass.set("solve.SolveApp")
    applicationDefaultJvmArgs = listOf(
        "--add-modules", "javafx.controls",
        "--add-opens", "javafx.graphics/javafx.scene=ALL-UNNAMED",
        "--add-opens", "javafx.base/com.sun.javafx=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.prism=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.prism.d3d=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.javafx.scene.layout=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/javafx.scene.image=ALL-UNNAMED",
        "--add-exports", "javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
        "-Xms2g",
        "-Xmx4g"
    )
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.graphics")
}

dependencies {
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:$tornadofxVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:$jacksonCSVVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonModuleKotlinVersion")
    implementation("io.github.palexdev:virtualizedfx:$virtualizedfxVersion")
    implementation("io.github.palexdev:materialfx:$materialfxVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$coroutinesVersion")
    implementation("junit:junit:$junitVersion")
    implementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    implementation("org.controlsfx:controlsfx:$controlsfxVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterParamsVersion")
    testImplementation("org.testfx:testfx-junit5:$testfxVersion")
    testImplementation("org.testfx:openjfx-monocle:$testfxMonocleVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    implementation("com.github.husker-dev.openglfx:core:$openglfxVersion")
    implementation("com.github.husker-dev.openglfx:lwjgl:$openglfxVersion")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-assimp:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-openal:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNativesClassifier")
    runtimeOnly("org.lwjgl:lwjgl-assimp:$lwjglVersion:$lwjglNativesClassifier")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNativesClassifier")
    runtimeOnly("org.lwjgl:lwjgl-openal:$lwjglVersion:$lwjglNativesClassifier")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNativesClassifier")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:$lwjglNativesClassifier")
    implementation("org.joml:joml:$jomlVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.allWarningsAsErrors = true
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.test {
    jvmArgs = listOf("-Dheadless=true", "-Xmx4g")
    useJUnitPlatform()
    testLogging {
        events(
            TestLogEvent.PASSED,
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED
        )
    }
}
