import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.nexoralearn.qjvpk"
    minSdk = 24
    targetSdk = 36
    versionCode = 4
    versionName = "1.0.3"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH")
      val keyCandidates = listOfNotNull(
        if (keystorePath != null) file(keystorePath) else null,
        file("${rootDir}/my-upload-key.jks"),
        file("${projectDir}/my-upload-key.jks"),
        file("${projectDir}/../my-upload-key.jks")
      )
      var customKeyFile = keyCandidates.firstOrNull { it.exists() }
      if (customKeyFile == null) {
        val b64Candidates = listOf(
          file("${rootDir}/my-upload-key.jks.base64"),
          file("${projectDir}/my-upload-key.jks.base64"),
          file("${projectDir}/../my-upload-key.jks.base64")
        )
        val b64File = b64Candidates.firstOrNull { it.exists() }
        if (b64File != null) {
          val decoded = Base64.getDecoder().decode(b64File.readText().trim())
          val restored = file("${rootDir}/my-upload-key.jks")
          restored.writeBytes(decoded)
          customKeyFile = restored
        }
      }
      if (customKeyFile != null && customKeyFile.exists()) {
        storeFile = customKeyFile
        storePassword = System.getenv("STORE_PASSWORD") ?: "nexoralearn2026"
        keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
        keyPassword = System.getenv("KEY_PASSWORD") ?: "nexoralearn2026"
      } else {
        storeFile = file("${rootDir}/my-upload-key.jks")
        storePassword = System.getenv("STORE_PASSWORD") ?: "nexoralearn2026"
        keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
        keyPassword = System.getenv("KEY_PASSWORD") ?: "nexoralearn2026"
      }
    }
    create("debugConfig") {
      val debugCandidates = listOf(
        file("${rootDir}/debug.keystore"),
        file("${projectDir}/debug.keystore"),
        file("${projectDir}/../debug.keystore"),
        file("/app/applet/debug.keystore"),
        file("/app/debug.keystore")
      )
      var debugFile = debugCandidates.firstOrNull { it.exists() }
      if (debugFile == null) {
        val debugB64Candidates = listOf(
          file("${rootDir}/debug.keystore.base64"),
          file("${projectDir}/debug.keystore.base64"),
          file("${projectDir}/../debug.keystore.base64"),
          file("/app/applet/debug.keystore.base64")
        )
        val b64Debug = debugB64Candidates.firstOrNull { it.exists() }
        if (b64Debug != null) {
          val decoded = Base64.getDecoder().decode(b64Debug.readText().trim())
          val restored = file("${rootDir}/debug.keystore")
          restored.writeBytes(decoded)
          debugFile = restored
        }
      }
      storeFile = debugFile ?: file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  implementation(libs.firebase.firestore)

  // Firebase Auth and Credential dependencies are commented out as Auth is removed:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  // implementation(libs.firebase.appcheck.recaptcha)
  // implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
