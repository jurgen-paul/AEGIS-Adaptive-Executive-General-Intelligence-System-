# Third-Party Open-Source Licenses & SBOM Inventory

AEGIS integrates vetted, open-source libraries managed via the Gradle Version Catalog (`gradle/libs.versions.toml`). All production dependencies use permissive OSI-approved licenses compatible with the **Apache License 2.0**.

---

## 📦 Production Dependencies

| Library / Artifact | Group & Name | Version | License (SPDX) | Purpose |
|:---|:---|:---|:---|:---|
| **AndroidX Core KTX** | `androidx.core:core-ktx` | `1.18.0` | `Apache-2.0` | Kotlin Android system extensions |
| **Jetpack Compose BOM** | `androidx.compose:compose-bom` | `2024.09.00` | `Apache-2.0` | Declarative UI toolkit alignment |
| **Material Design 3** | `androidx.compose.material3:material3` | BOM-managed | `Apache-2.0` | Material 3 components & adaptive theming |
| **Material Icons Extended** | `androidx.compose.material:material-icons-*` | BOM-managed | `Apache-2.0` | Vector iconography & auto-mirrored icons |
| **Activity Compose** | `androidx.activity:activity-compose` | `1.10.1` | `Apache-2.0` | Edge-to-edge & Compose host integration |
| **Lifecycle & ViewModel** | `androidx.lifecycle:lifecycle-*-compose` | `2.8.7` | `Apache-2.0` | Lifecycle-aware `StateFlow` collection & ViewModel |
| **AndroidX Biometric** | `androidx.biometric:biometric` | `1.1.0` | `Apache-2.0` | Class 3 BiometricPrompt & device credential lock |
| **Room Persistence** | `androidx.room:room-runtime`, `room-ktx`, `room-compiler` | `2.7.0` | `Apache-2.0` | Local SQLite persistence & KSP codegen |
| **Kotlin Coroutines** | `org.jetbrains.kotlinx:kotlinx-coroutines-*` | `1.10.2` | `Apache-2.0` | Structured concurrency & Flow streams |
| **Firebase BOM & AI Logic** | `com.google.firebase:firebase-bom`, `firebase-ai` | `34.15.0` | `Apache-2.0` | Gemini generative AI & App Check protection |
| **Firebase App Check** | `com.google.firebase:firebase-appcheck-recaptcha` | BOM-managed | `Apache-2.0` | App integrity attestation |
| **Retrofit** | `com.squareup.retrofit2:retrofit`, `converter-moshi` | `2.12.0` | `Apache-2.0` | Type-safe REST networking & serialization |
| **OkHttp & Logging** | `com.squareup.okhttp3:okhttp`, `logging-interceptor` | `4.10.0` | `Apache-2.0` | HTTP client & security header interceptors |
| **Moshi** | `com.squareup.moshi:moshi-kotlin`, `moshi-kotlin-codegen` | `1.15.2` | `Apache-2.0` | Kotlin-safe JSON adapter generation |
| **Coil Compose** | `io.coil-kt:coil-compose` | `2.7.0` | `Apache-2.0` | Asynchronous Compose image loading |

---

## 🧪 Development, Build & Test Dependencies

| Tool / Library | Group & Name | Version | License (SPDX) | Scope |
|:---|:---|:---|:---|:---|
| **Android Gradle Plugin** | `com.android.application` | `9.1.1` | `Apache-2.0` | Build & R8/ProGuard packaging |
| **Kotlin & KSP** | `org.jetbrains.kotlin.*`, `com.google.devtools.ksp` | `2.2.10` / `2.3.5` | `Apache-2.0` | Compiler & symbol processing |
| **Secrets Gradle Plugin** | `com.google.android.libraries.mapsplatform.secrets-gradle-plugin` | `2.0.1` | `Apache-2.0` | `.env` / BuildConfig injection |
| **Google Services Plugin** | `com.google.gms.google-services` | `4.5.0` | `Apache-2.0` | Firebase configuration processing |
| **Robolectric** | `org.robolectric:robolectric` | `4.16.1` | `MIT` | Fast local JVM Android unit tests |
| **Roborazzi** | `io.github.takahirom.roborazzi:roborazzi*` | `1.59.0` | `Apache-2.0` | Compose screenshot regression tests |
| **JUnit 4** | `junit:junit` | `4.13.2` | `EPL-1.0` | JVM unit test runner |
| **AndroidX Test & Espresso** | `androidx.test.*`, `androidx.test.espresso:espresso-core` | `1.6.x` / `3.7.0` | `Apache-2.0` | Compose UI test rules & assertions |

---

## 📋 License Compliance Notes

- **No Copyleft Viral Licenses**: AEGIS contains zero GPL/AGPL runtime dependencies.
- **Attribution**: Top-level attribution notices are maintained in [`/NOTICE`](../NOTICE) and [`/LICENSE`](../LICENSE).
- **Secrets Isolation**: Build-time API placeholders are kept in `.env.example` and excluded from source control via `.gitignore`.
