# How to generate an Android App Bundle (AAB) for Play Store

This repository now includes a GitHub Actions workflow at `.github/workflows/build-aab.yml` that builds a signed release App Bundle and uploads it as a workflow artifact.

What I added
- Workflow: `.github/workflows/build-aab.yml` — builds `:app:bundleRelease` and uploads the resulting AAB.

What you must configure
1. Keystore and passwords (recommended):
   - In your repository settings > Secrets and variables > Actions, add these secrets:
     - KEYSTORE_BASE64 (optional): the base64-encoded contents of your upload keystore (.jks). If provided, the workflow will decode it to `my-upload-key.jks` before building.
     - STORE_PASSWORD: the keystore password
     - KEY_PASSWORD: the key (alias) password
   - Alternatively, you can commit your keystore to the repository root as `my-upload-key.jks` (not recommended).

2. The `app/build.gradle.kts` expects the keystore env lookups in the signing config. The workflow sets KEYSTORE_PATH, STORE_PASSWORD and KEY_PASSWORD from secrets so the build will sign the bundle.

How to run
- Open the Actions tab in GitHub, select "Build Android App Bundle" workflow, and click "Run workflow" (or push to main).
- When finished, download the artifact named `app-bundle-release` — it contains the generated `.aab` file.

Notes & troubleshooting
- The workflow attempts to use the Gradle wrapper if present; otherwise it installs Gradle via SDKMAN and runs `gradle`.
- I detected no `gradlew` in the repo. I recommend adding the Gradle wrapper to the repo. If you don't, the workflow installs Gradle (currently set to 8.4.1) — you may need to change this to a version compatible with your Android Gradle Plugin.
- If the build fails due to Gradle/AGP mismatch, update the `sdk install gradle 8.4.1` line in the workflow to a compatible version.

Generate keystore (locally)
```bash
keytool -genkeypair \
  -alias upload \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -keystore my-upload-key.jks \
  -storepass <STORE_PASSWORD> \
  -keypass <KEY_PASSWORD> \
  -dname "CN=Your Name, OU=YourOrg, O=YourCompany, L=City, ST=State, C=US"
```
