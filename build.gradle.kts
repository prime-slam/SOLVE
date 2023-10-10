import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.openjfx.javafxplugin") version "0.0.10"
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
val openglfxVerson: String by rootProject
val lwjglVersion: String by rootProject

val lwjglNativesParam: String = when (OperatingSystem.current()) {
    OperatingSystem.LINUX -> "natives-linux"
    OperatingSystem.WINDOWS -> "natives-windows"
    OperatingSystem.MAC_OS -> "natives-macos"
    else -> ""
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
        "-Xmx4g",
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

    implementation("com.github.husker-dev.openglfx:core:3.0.5")
    implementation("com.github.husker-dev.openglfx:lwjgl:3.0.5")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNativesParam)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNativesParam)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNativesParam)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNativesParam)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNativesParam)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNativesParam)
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
