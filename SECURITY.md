# Security Policy — AEGIS Intelligence Systems

## 🛡️ Commitment to Zero-Trust Security

AEGIS (Adaptive Executive & General Intelligence System) is engineered around zero-trust principles to safeguard executive tasks, assistant session memory, cryptographic export files, and AI interactions on Android.

---

## 📦 Supported Versions

| Version | Release Track | Security Support |
|:---|:---|:---|
| `1.x` (Current) | `main` / Target SDK 36 | ✅ Active Security Updates |
| `< 1.0` | Pre-release / Legacy | ❌ Unsupported |

---

## 🐞 Reporting a Vulnerability

If you discover a security vulnerability, cryptographic weakness, prompt injection bypass, or sensitive data exposure in AEGIS, **please do not open a public GitHub issue**.

1. **Private Disclosure**: Contact the security maintainer directly at **`oistarsentertainment@gmail.com`** with the subject `[AEGIS SECURITY] <Brief Summary>`.
2. **Include Details**:
   - Affected component (e.g., `PromptDefenseLayer`, `BiometricAuthManager`, `SecureExportManager`, `AegisDatabase`).
   - Steps to reproduce or minimal proof-of-concept.
   - Potential impact and suggested remediation.
3. **Response SLAs**:
   - **Acknowledgment**: Within **48 hours** of report receipt.
   - **Triage & Severity Classification**: Within **5 business days**.
   - **Patch & Coordinated Release**: Critical/High issues targeted within **14 days**.

---

## 🔐 Core Security Controls Implemented

### 1. Hardware-Isolated Biometric Vault (`BiometricAuthManager` & `AegisLockScreen`)
- Enforces `androidx.biometric.BiometricPrompt` using `BIOMETRIC_STRONG` (Class 3 biometric hardware) with `DEVICE_CREDENTIAL` fallback.
- Automated **5-minute idle watchdog** in `MainActivity` locks the UI and calls `viewModel.clearActiveSessionContext()` to clear active session memory (`AegisSessionMemory`) and conversation tables upon inactivity.

### 2. AI Prompt Defense Layer (`PromptDefenseLayer`)
- Evaluates every prompt prior to routing or LLM execution against OWASP LLM01 / injection patterns:
  - System prompt overrides (`"ignore previous instructions"`, `"DAN"`, `"jailbreak"`).
  - System instruction/secret exfiltration attempts (`"reveal system prompt"`, `"dump api key"`).
  - Malicious code execution & SQL/shell injection payloads.
- Supports configurable defense postures (`Strict`, `Balanced`, `Monitor`) and logs every incident to Room (`aegis_security_events`) with severity (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).

### 3. Encrypted Export & PII Scrubbing (`SecureExportManager`)
- Scrubs sensitive PII patterns (emails, phone numbers, SSN-like identifiers, API tokens) before export.
- Encrypts exported chat logs using **AES-256-CBC with PKCS5Padding** and random 16-byte IV prepended to the output inside app-private storage (`context.filesDir`).

### 4. Network & Telemetry Hardening (`AegisSecurityHeaderInterceptor`)
- Attaches zero-trust request correlation and policy headers (`X-Aegis-Security-Mode`, `X-Content-Type-Options: nosniff`, `Cache-Control: no-store`) to outbound HTTP requests.
- Enforces least-privilege permissions in `AndroidManifest.xml` (`INTERNET`, `ACCESS_NETWORK_STATE`, `USE_BIOMETRIC`, `RECORD_AUDIO` — zero broad storage permissions such as `READ_EXTERNAL_STORAGE`).

### 5. Bytecode Hardening (`app/proguard-rules.pro`)
- Release builds strip verbose and debug logging (`android.util.Log.v`, `android.util.Log.d`) via `-assumenosideeffects` to prevent sensitive runtime telemetry leakage.
- Repackages internal classes under `com.aistudio.aegis.internal` while preserving Room DAOs, Entities, Biometric callbacks, and Moshi/Retrofit reflection contracts.

---

## 📜 Compliance Alignment

- **NIST SSDF (SP 800-218)** & **OpenSSF Best Practices** — See [`docs/SSF_DOCUMENTATION.md`](./docs/SSF_DOCUMENTATION.md).
- **OWASP MASVS v2.0 (L2)** & **OWASP Top 10 for LLM Applications** — Prompt injection filtering and local storage isolation.
- **Google Play Developer Policy** — Zero broad media/storage permissions; mandatory medical disclaimer gating for health queries (`HealthDisclaimerBanner`).
