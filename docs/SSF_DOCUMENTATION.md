# AEGIS — Secure Software Framework (SSF) & Compliance Documentation

## 🛡️ Executive Summary

**AEGIS (Adaptive Executive & General Intelligence System)** is engineered in alignment with the **Secure Software Framework (SSF)**, **NIST Secure Software Development Framework (SSDF SP 800-218)**, **OpenSSF Best Practices**, **SLSA Level 3 Build Provenance**, and **OWASP MASVS v2.0**.

This document maps AEGIS's implemented Android architecture, build pipeline, and runtime defenses directly to verifiable SSF controls.

---

## 🏛️ 1. NIST SSDF (SP 800-218) Control Mapping

| SSDF Practice | Practice Title | AEGIS Implementation | Verification Artifact |
|:---|:---|:---|:---|
| **PO.1 / PO.3** | Define Security Requirements & Toolchain | Version-pinned Gradle Version Catalog (`gradle/libs.versions.toml`), Java 17 toolchain, Secrets Gradle Plugin | `gradle/libs.versions.toml`, `app/build.gradle.kts` |
| **PS.1 / PS.2** | Protect All Forms of Code & Verify Integrity | GitHub Actions CI/CD with SHA-256 checksum generation for debug APKs and release AABs; keystore decoded from GitHub Secrets only at build time | `.github/workflows/android-ci.yml`, `BUILDING_AAB.md` |
| **PW.1 / PW.4** | Design Software to Meet Security Requirements | Zero-trust biometric lock (`AegisLockScreen`), `PromptDefenseLayer`, `SecureExportManager` AES-256 encryption, Room SQLite isolation | `app/src/main/java/com/example/service/` |
| **PW.6 / PW.7** | Configure Compiler & Build Hardening | Production R8/ProGuard obfuscation, `-repackageclasses`, and `-assumenosideeffects` debug log stripping | `app/proguard-rules.pro` |
| **PW.8** | Test Executable Code to Identify Vulnerabilities | Automated Robolectric JVM unit & UI tests covering Room persistence, Eisenhower quadrants, and resource integrity | `app/src/test/java/com/example/` |
| **RV.1 / RV.2** | Vulnerability Identification & Response | Documented private disclosure policy, 48-hour SLA, and forensic security audit event log (`aegis_security_events`) | `SECURITY.md`, `AegisEntities.kt` |

---

## 📱 2. OWASP MASVS v2.0 Verification Matrix

| MASVS Domain | Requirement | Implementation in AEGIS |
|:---|:---|:---|
| **MASVS-STORAGE** | Sensitive data stored in app-private storage; no broad external storage permissions | Room SQLite (`aegis_database`) and AES-256-CBC encrypted exports written exclusively to app-private `filesDir`; `READ_EXTERNAL_STORAGE` omitted |
| **MASVS-CRYPTO** | Strong modern cryptography & no hardcoded keys | `SecureExportManager` uses AES/CBC/PKCS5Padding with random `SecureRandom` IVs and Android KeyStore-compatible key handling; Secrets injected via `BuildConfig` |
| **MASVS-AUTH** | Hardware-backed local authentication & session invalidation | `BiometricAuthManager` enforces `BIOMETRIC_STRONG \| DEVICE_CREDENTIAL`; `MainActivity` enforces a 5-minute inactivity lock and session wipe |
| **MASVS-NETWORK** | TLS-only endpoints & strict security headers | `AegisSecurityHeaderInterceptor` injects `Cache-Control: no-store` and `X-Content-Type-Options: nosniff`; Retrofit + OkHttp enforce HTTPS |
| **MASVS-RESILIENCE** | Binary obfuscation & debug telemetry removal | `app/proguard-rules.pro` strips `Log.v` / `Log.d` calls, repackages internal classes, and preserves only required reflection metadata |

---

## 🤖 3. OWASP Top 10 for LLM Applications (AI Defense Controls)

- **LLM01 — Prompt Injection**: `PromptDefenseLayer.evaluate()` inspects every incoming query prior to Gemini / router execution, blocking or sanitizing jailbreak overrides, role-hijacking payloads, and command injections.
- **LLM02 — Insecure Output / PII Leakage**: `SecureExportManager` scrubs emails, phone numbers, and secret-formatted strings prior to file export; `AegisSecurityEvent` records every flagged input for audit review in `SecurityShieldScreen`.
- **LLM06 — Excessive Agency**: `AegisRouter` enforces domain boundaries (`core`, `security`, `health`, `sales`, `organizer`) and intercepts health queries with a mandatory user-acknowledged disclaimer modal (`pendingHealthQuery` / `HealthDisclaimerBanner`).

---

## ⚙️ 4. CI/CD Supply Chain & SLSA Provenance

The repository includes dual GitHub Actions workflows:
- **`.github/workflows/android-ci.yml`**:
  1. Executes `:app:testDebugUnitTest` (Robolectric + Room SQLite + Matrix logic tests).
  2. Assembles Debug APK and generates `debug-apk-sha256.txt`.
  3. Builds signed Production Release AAB (`:app:bundleRelease`) with `release-aab-sha256.txt`.
  4. Audits presence of `app/proguard-rules.pro`, `docs/SSF_DOCUMENTATION.md`, and `SECURITY.md`, publishing an automated Step Summary.
- **`.github/workflows/build-aab.yml`**: Dedicated Play Store release bundle builder using encrypted `KEYSTORE_BASE64`, `STORE_PASSWORD`, and `KEY_PASSWORD` secrets.

---

## 📜 5. Google Play Developer Policy Compliance

- **Least-Privilege Manifest**: Declares only `INTERNET`, `ACCESS_NETWORK_STATE`, `USE_BIOMETRIC`, and `RECORD_AUDIO` (for optional voice input in `VoiceSpeechManager`).
- **Zero Broad Storage Access**: Never requests `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `READ_MEDIA_IMAGES`, or `READ_MEDIA_VIDEO`.
- **Medical Disclaimer Enforcement**: Health-related queries display an explicit non-diagnostic disclaimer and log user consent in Room (`health_history_disclaimers`).
