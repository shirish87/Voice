@file:Suppress("UnstableApiUsage")

plugins {
  id("com.android.library")
  id("kotlin-android")
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(VoiceVersions.javaLanguageVersion)
  }
}

android {
  defaultConfig {
    multiDexEnabled = true
    minSdk = VoiceVersions.minSdk
    targetSdk = VoiceVersions.targetSdk
  }
  compileSdk = VoiceVersions.compileSdk
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = VoiceVersions.javaCompileVersion
    targetCompatibility = VoiceVersions.javaCompileVersion
  }
  kotlinOptions {
    jvmTarget = VoiceVersions.javaCompileVersion.toString()
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.composeVersion
  }
}

dependencies {
  coreLibraryDesugaring(libs.desugar)
}
