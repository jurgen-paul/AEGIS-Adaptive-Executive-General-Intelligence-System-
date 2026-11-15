# Contributing to AEGIS

Thank you for contributing to **AEGIS — Adaptive Executive & General Intelligence System**!

---

## 📜 Licensing & DCO

By contributing to this project, you agree that your contributions will be licensed under the **[Apache License 2.0](./LICENSE)** and attributed in accordance with **[NOTICE](./NOTICE)**.

---

## 🛠️ Development Workflow

1. **Fork & Branch**: Create a feature or fix branch from `main` (e.g., `feat/eisenhower-filter` or `fix/proguard-rule`).
2. **Environment & Secrets**:
   - Do **not** commit `.env`, `my-upload-key.jks`, or `google-services.json` secrets.
   - Use `.env.example` for documenting non-sensitive configuration keys.
3. **Code Standards**:
   - Write UI exclusively in **Kotlin + Jetpack Compose (Material 3)**.
   - Follow MVVM + Repository patterns; keep DAOs strictly behind repositories and `ViewModel`.
   - Assign `Modifier.testTag(...)` on interactive Compose elements and non-null `contentDescription` on actionable icons/images.
   - Use `Icons.AutoMirrored.*` for directional icons.
4. **Testing & Verification**:
   Run unit and Robolectric tests before opening a pull request:
   ```bash
   gradle :app:assembleDebug
   gradle :app:testDebugUnitTest
   ```
5. **Security Checklist**:
   - Avoid requesting broad permissions (never add `READ_EXTERNAL_STORAGE`, `READ_MEDIA_IMAGES`, or `READ_MEDIA_VIDEO`).
   - Route external AI inputs through `PromptDefenseLayer` before transmission.
   - Update [`app/proguard-rules.pro`](./app/proguard-rules.pro) whenever introducing new `@Entity`, `@Dao`, `@JsonClass`, or Retrofit interfaces.

---

## 🔐 Reporting Security Issues

Do **not** report security vulnerabilities via public Pull Requests or Issues. Follow the responsible disclosure process in **[SECURITY.md](./SECURITY.md)**.
