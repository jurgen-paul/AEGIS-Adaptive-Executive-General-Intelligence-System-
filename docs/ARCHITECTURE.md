# AEGIS — Technical Architecture & Developer Guide

## 🏗️ 1. System Overview

AEGIS is a **Kotlin + Jetpack Compose (Material 3)** Android application structured using **MVVM + Repository** architecture with offline-first **Room SQLite** persistence, hardware-backed biometric locking, and a multi-domain intelligent command router.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER (Compose M3)                 │
│  MainActivity • AegisLockScreen • CommandHubScreen • SecurityShieldScreen│
│  ExecutiveOrganizerScreen • SessionMemoryScreen                         │
│  EisenhowerMatrixComponent • EisenhowerTaskManagerComponent              │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ StateFlow / Events
┌────────────────────────────────────▼────────────────────────────────────┐
│                           VIEWMODEL LAYER                               │
│                            AegisViewModel                               │
└──────────┬─────────────────────────┬─────────────────────────┬──────────┘
           │                         │                         │
┌──────────▼──────────┐  ┌───────────▼───────────┐  ┌──────────▼──────────┐
│    DOMAIN / ROUTER  │  │   SECURITY SERVICES   │  │     DATA LAYER      │
│  AegisRouter        │  │ PromptDefenseLayer    │  │ AegisDatabase (v4)  │
│  TaskDomain         │  │ BiometricAuthManager  │  │ AegisDao            │
│  GeminiApiService   │  │ SecureExportManager   │  │ EisenhowerTaskDao   │
│  GoogleSheetsMgr    │  │ SecurityHeaderInterc. │  │ Repositories (Room) │
└─────────────────────┘  └───────────────────────┘  └─────────────────────┘
```

---

## 📂 2. Package Structure

| Package | Key Components | Responsibility |
|:---|:---|:---|
| `com.example` | `MainActivity` | Edge-to-edge host, theme switching, 5-minute idle lock watchdog, bottom navigation across 4 tabs |
| `com.example.data` | `AegisDatabase`, `AegisDao`, `EisenhowerTaskDao`, `AegisEntities`, `EisenhowerTask`, `AegisChatRepository`, `EisenhowerRepository`, `UserProfile`, `TaskDomain` | Room entities, reactive `Flow` DAOs, and repository abstractions |
| `com.example.router` | `AegisRouter` | Intent classification across Executive, Security, Health, Sales, and Conversation domains |
| `com.example.service` | `PromptDefenseLayer`, `BiometricAuthManager`, `SecureExportManager`, `GeminiApiService`, `AegisSecurityHeaderInterceptor`, `GoogleSheetsIntegrationManager`, `VoiceSpeechManager` | Prompt injection firewall, biometric prompt, AES-256 export, HTTP headers, Google Sheets/Forms sync, speech recognition |
| `com.example.ui` | `AegisViewModel` | Unidirectional state management via `StateFlow` and coroutine background dispatch (`Dispatchers.IO`) |
| `com.example.ui.components` | `EisenhowerMatrixComponent`, `EisenhowerTaskManagerComponent`, `AegisLockScreen`, `GoogleSheetsFormComponent`, `AccountProfileSettingsComponent`, `SecurityAlertBanner`, `HealthDisclaimerBanner`, `ChatBubble`, `AegisTopAppBar` | Reusable Material 3 Compose UI modules |
| `com.example.ui.screens` | `CommandHubScreen`, `ExecutiveOrganizerScreen`, `SecurityShieldScreen`, `SessionMemoryScreen` | Primary tab destinations |

---

## 🗄️ 3. Room SQLite Persistence Schema (`AegisDatabase`, Version 4)

All user-authored records and forensic logs persist locally in `aegis_database`:

1. **`eisenhower_tasks` (`EisenhowerTask`)**: Dedicated Eisenhower Matrix persistence (`id`, `title`, `description`, `quadrant` [`DO`, `SCHEDULE`, `DELEGATE`, `ELIMINATE`], `isUrgent`, `isImportant`, `status`, `category`, `dueDate`, `createdAt`).
2. **`aegis_tasks` (`AegisTask`)**: General executive tasks synchronized with organizer workflows.
3. **`aegis_session_logs` (`AegisSessionLog`)**: Prompt/response interaction history with confidence scores and threat/health flags.
4. **`aegis_security_events` (`AegisSecurityEvent`)**: Forensic audit log for prompt defense blocks, mode switches, idle locks, and encrypted exports.
5. **`aegis_chat_sessions` & `aegis_chat_messages`**: Multi-session conversation history indexed by `sessionId`.
6. **`health_history_disclaimers` & `sales_notes`**: Compliance acknowledgments and domain notes.

---

## 📊 4. Eisenhower Priority Matrix Categorization

Both `EisenhowerMatrixComponent` and `EisenhowerTaskManagerComponent` categorize items across four actionable quadrants backed by Room:

| Quadrant | Urgency | Importance | Intent |
|:---|:---|:---|:---|
| 🔴 **DO** (`QUADRANT_DO`) | Urgent | Important | Immediate crisis resolution & critical security actions |
| 🔵 **SCHEDULE** (`QUADRANT_SCHEDULE`) | Not Urgent | Important | Strategic planning, architecture, and scheduled goals |
| 🟠 **DELEGATE** (`QUADRANT_DELEGATE`) | Urgent | Not Important | Operational handoffs and team delegations |
| 🔘 **ELIMINATE** (`QUADRANT_ELIMINATE`) | Not Urgent | Not Important | Distractions and low-value tasks to drop or purge |

---

## 🧪 5. Testing Strategy

AEGIS uses **local JVM unit tests & Robolectric** (no emulator required):

- `EisenhowerTaskTest`: Verifies quadrant resolution, urgency/importance mapping, and entity conversions.
- `EisenhowerRoomRobolectricTest`: Tests in-memory Room `AegisDatabase` + `EisenhowerTaskDao` CRUD and quadrant queries.
- `ExampleRobolectricTest`: Verifies string resources, valid decoding of `R.drawable.aegis_shield_bg`, and Compose rendering of `AegisLockScreen`.
- `GreetingScreenshotTest`: Roborazzi Compose screenshot harness.

Run all tests locally:
```bash
gradle :app:testDebugUnitTest
```
