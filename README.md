# AEGIS — Adaptive Executive & General Intelligence System

AEGIS is a security-first, high-performance executive intelligence assistant built with **Kotlin**, **Jetpack Compose**, and **Room Database**. Designed with a zero-trust architecture, AEGIS secures sensitive organizational data, provides decision-support tools, and enforces automated health and security protocols.

---

## 🌟 Key Features

### 🛡️ 1. 4D Biometric Glass Security Vault
- **Zero-Trust Launch Layer**: Secured using `androidx.biometric` for Fingerprint, Face ID, and fallback PIN verification.
- **Glassmorphic 4D UI**: Glowing spatial ambient gradients with interactive pulse animations.
- **Idle Auto-Lock**: Automatically locks the vault after 5 minutes of inactivity to protect sensitive assistant memory.

### 📊 2. Executive Organizer & Briefing
- **Executive Summary Dashboard**: Real-time briefing overview featuring critical alert feeds, urgent item focus, and task completion metrics.
- **Eisenhower Priority Matrix**: Interactive 4-quadrant layout (Do First, Schedule, Delegate, Routine) for strategic decision management.
- **Task Management**: Fully persisted local Room task engine with priority tagging, status toggles, and instant status updates.

### 🛡️ 3. Security Shield & Prompt Defense
- **Prompt Injection Filter**: Intercepts unauthorized system overrides, data leaks, and malicious inputs before AI model execution.
- **PII Scrubbing & Secure Export**: AES-256 encrypted chat history export with automated scrubbing of sensitive personal identifiers.
- **Security Event Logging**: Real-time logging of threats and security mode changes.

### ⚕️ 4. Health Protocol & Disclaimer Modal
- **Automated Health Intent Detection**: Intercepts medical and wellness queries before processing.
- **Global Health Disclaimer Modal**: Mandates user acknowledgment regarding non-medical advice before displaying health analysis.
- **Persistent Disclaimer History**: Records accepted health disclaimers in Room local storage.

### 🧠 5. Intelligent Decision Router
- **Multi-Domain Intelligence**: Automatically routes inputs across Core, Security, Executive Organizer, Health, and Sales domains.
- **Gemini AI Integration**: Connects with server-side Gemini models for natural, context-aware assistant responses.

---

## 🛠️ Technology Stack

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture principles
- **Local Persistence**: Android Room Database with KSP (Kotlin Symbol Processing)
- **Security**: `androidx.biometric`, Java Cryptography Architecture (AES-256)
- **AI Integration**: Gemini REST API via Ktor / Retrofit

---

## 🚀 Getting Started

### Building the Project
You can compile and build the Android application package using standard Gradle tasks:

```bash
# Assemble debug APK
gradle assembleDebug

# Run unit and local JVM tests
gradle testDebugUnitTest
```

---

## 📄 License

Copyright © 2026 AEGIS Intelligence Systems. All rights reserved.
