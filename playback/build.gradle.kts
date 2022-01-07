import deps.Deps

plugins {
  id("com.android.library")
  id("kotlin-android")
  id("com.squareup.anvil")
}

anvil {
  generateDaggerFactories.set(true)
}

dependencies {
  implementation(project(":common"))
  implementation(project(":strings"))
  implementation(project(":data"))
  implementation(project(":prefs"))

  implementation(Deps.timber)
  implementation(Deps.Kotlin.coroutines)
  implementation(Deps.AndroidX.mediaCompat)
  implementation(Deps.picasso)
  implementation(Deps.AndroidX.ktx)
  implementation(Deps.Prefs.core)

  implementation(Deps.Dagger.core)

  implementation(Deps.ExoPlayer.core)
  implementation(Deps.ExoPlayer.flac) { isTransitive = false }
}
