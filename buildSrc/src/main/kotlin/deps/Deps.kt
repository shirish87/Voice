package deps

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

object Versions {
  const val versionCode = 3060342
  const val versionName = "5.0.2"
}

object Deps {

  object AndroidX {
    const val appCompat = "androidx.appcompat:appcompat:1.3.0-rc01"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
    const val transitions = "androidx.transition:transition:1.4.0"
    const val palette = "androidx.palette:palette:1.0.0"
    const val mediaCompat = "androidx.media:media:1.2.1"
    const val ktx = "androidx.core:core-ktx:1.3.2"

    object Room {
      private const val version = "2.3.0"
      const val runtime = "androidx.room:room-ktx:$version"
      const val compiler = "androidx.room:room-compiler:$version"
      const val testing = "androidx.room:room-testing:$version"
    }

    object Test {
      const val runner = "androidx.test:runner:1.3.0"
      const val junit = "androidx.test.ext:junit:1.1.2"
      const val core = "androidx.test:core:1.3.0"
    }
  }

  const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-rc01"
  const val material = "com.google.android.material:material:1.3.0"
  const val floatingActionButton = "com.getbase:floatingactionbutton:1.10.1"
  const val materialCab = "com.afollestad:material-cab:2.0.1"
  const val picasso = "com.squareup.picasso:picasso:2.8"
  const val tapTarget = "com.getkeepsafe.taptargetview:taptargetview:1.13.0"
  const val lifecycle = "androidx.lifecycle:lifecycle-common-java8:2.3.0"
  const val groupie = "com.xwray:groupie:2.8.1"
  const val ffmpeg = "com.arthenica:mobile-ffmpeg-audio:4.4"

  object Conductor {
    private const val version = "3.0.1"
    const val core = "com.bluelinelabs:conductor:$version"
    const val transition = "com.bluelinelabs:conductor-androidx-transition:$version"
  }

  object Prefs {
    private const val version = "1.0.1"
    const val core = "com.github.PaulWoitaschek.FlowPref:core:$version"
    const val android = "com.github.PaulWoitaschek.FlowPref:android:$version"
    const val inMemory = "com.github.PaulWoitaschek.FlowPref:in-memory:$version"
  }

  object MaterialDialog {
    private const val version = "3.3.0"
    const val core = "com.afollestad.material-dialogs:core:$version"
    const val input = "com.afollestad.material-dialogs:input:$version"
  }

  object Dagger {
    private const val version = "2.38"
    const val core = "com.google.dagger:dagger:$version"
    const val compiler = "com.google.dagger:dagger-compiler:$version"
  }

  object ExoPlayer {
    const val core = "com.google.android.exoplayer:exoplayer-core:2.14.0"
    private const val extensionVersion = "2.14.0"
    const val flac = "com.github.PaulWoitaschek.ExoPlayer-Extensions:extension-flac:$extensionVersion"
  }

  const val timber = "com.jakewharton.timber:timber:4.7.1"

  object Kotlin {
    private const val versionKotlin = "1.5.10"
    private const val versionCoroutines = "1.4.3"

    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$versionCoroutines"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$versionCoroutines"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$versionCoroutines"
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$versionKotlin"

    object Serialization {
      const val core = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"
      const val gradlePlugin = "org.jetbrains.kotlin:kotlin-serialization:$versionKotlin"
    }
  }

  const val junit = "junit:junit:4.13.2"
  const val mockk = "io.mockk:mockk:1.11.0"
  const val truth = "com.google.truth:truth:1.1.2"
  const val robolectric = "org.robolectric:robolectric:4.5.1"
}

fun DependencyHandler.composeImplementation() {

  val composeVersion = "1.0.0-rc02"
  add("implementation", "androidx.compose.ui:ui:$composeVersion")
  add("implementation", "androidx.compose.ui:ui-tooling:$composeVersion")
  add("implementation", "androidx.compose.foundation:foundation:$composeVersion")
  add("implementation", "androidx.compose.material:material:$composeVersion")
  add("implementation", "androidx.compose.material:material-icons-core:$composeVersion")
  add("implementation", "androidx.compose.material:material-icons-extended:$composeVersion")
  add("implementation", "androidx.compose.material:material-icons-extended:$composeVersion")
  add("implementation", "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.1")
  add("implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
  add("implementation", "androidx.core:core-ktx:1.6.0")
}

@Suppress("UnstableApiUsage")
fun configureBaseRepos(repositoryHandler: RepositoryHandler) {
  repositoryHandler.apply {

    google()

    exclusiveContent {
      forRepository {
        maven(url = "https://jitpack.io")
      }
      filter {
        includeGroupByRegex("com.github.PaulWoitaschek.*")
      }
    }

    mavenCentral().mavenContent { releasesOnly() }
    jcenter().mavenContent { releasesOnly() }
  }
}
