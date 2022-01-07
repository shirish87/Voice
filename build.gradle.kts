@file:Suppress("UnstableApiUsage")

buildscript {
  dependencies {
    classpath(libs.androidPluginForGradle)
    classpath(libs.kotlin.pluginForGradle)
  }
}

plugins {
  id("com.github.ben-manes.versions") version "0.39.0"
  id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

tasks.wrapper {
  distributionType = Wrapper.DistributionType.ALL
}

allprojects {
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf(
        "-Xinline-classes",
        "-progressive",
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlin.ExperimentalStdlibApi",
        "-Xopt-in=kotlin.time.ExperimentalTime",
        "-Xopt-in=kotlinx.coroutines.FlowPreview",
        "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xopt-in=kotlin.contracts.ExperimentalContracts",
        "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
        "-Xopt-in=androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi",
      )
    }
  }
}

subprojects {
  fun addCoreDependencies() {
    if (path != ":core") {
      dependencies.add("implementation", projects.core)
    }
  }
  apply(plugin = "org.jlleitschuh.gradle.ktlint")
  plugins.withId("kotlin") {
    addCoreDependencies()
  }
  plugins.withId("kotlin-android") {
    addCoreDependencies()
  }
}

tasks {
  register<Exec>("importStrings") {
    executable = "sh"
    args("-c", "tx pull -af --minimum-perc=5")
    finalizedBy(":core:lintDebug")
  }

  register<TestReport>("allUnitTests") {
    val tests = subprojects.mapNotNull { subProject ->
      val tasks = subProject.tasks
      (
        tasks.findByName("testReleaseUnitTest")
          ?: tasks.findByName("testDebugUnitTest")
          ?: tasks.findByName("test")
        ) as? Test
    }
    val artifactFolder = File("${rootDir.absolutePath}/artifacts")
    destinationDir = File(artifactFolder, "testResults")
    reportOn(tests)
  }
}

enum class DependencyStability(private val regex: Regex) {
  Dev(".*dev.*".toRegex()),
  Eap("eap".toRegex()),
  Milestone("M1".toRegex()),
  Alpha("alpha".toRegex()),
  Beta("beta".toRegex()),
  Rc("rc".toRegex()),
  Stable(".*".toRegex());

  companion object {
    fun ofVersion(version: String): DependencyStability {
      return values().first {
        it.regex.containsMatchIn(version)
      }
    }
  }
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
  rejectVersionIf {
    DependencyStability.ofVersion(candidate.version) < DependencyStability.ofVersion(currentVersion)
  }
}
