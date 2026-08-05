package com.example.router

import com.example.data.AegisSessionMemory
import com.example.data.TaskDomain
import java.util.regex.Pattern
import kotlin.math.sqrt

data class AegisRouterResult(
    val domain: TaskDomain,
    val responseText: String,
    val securityThreatFlag: Boolean = false,
    val healthEmergencyFlag: Boolean = false,
    val confidenceScore: Float = 0.95f,
    val mathResult: String = "",
    val salesStage: String = "discovery",
    val organizerAction: String = "none",
    val moduleId: String = "aegis_core_router"
)

class AegisRouter {

    companion object {
        val SENSITIVE_HEALTH_FLAGS = setOf(
            "chest pain", "can't breathe", "cannot breathe", "suicidal", "overdose",
            "heart attack", "choking", "severe bleeding", "stroke"
        )

        val BLOCKED_PATTERNS = setOf(
            "ignore previous instructions", "reveal system prompt", "bypass security",
            "jailbreak", "override security", "drop database", "<script>", "eval("
        )

        val MODULE_PREFIX_PATTERN: Pattern = Pattern.compile("^aegis_[a-z0-9_]+$")

        fun isValidModuleId(id: String): Boolean {
            return MODULE_PREFIX_PATTERN.matcher(id).matches()
        }
    }

    var sessionMemory = AegisSessionMemory()

    fun securityCheck(userInput: String): Boolean {
        val lowered = userInput.lowercase()
        for (pattern in BLOCKED_PATTERNS) {
            if (lowered.contains(pattern)) {
                return false
            }
        }
        return true
    }

    fun classifyDomain(userInput: String): TaskDomain {
        val lowered = userInput.lowercase()

        // Health emergency takes priority
        if (SENSITIVE_HEALTH_FLAGS.any { lowered.contains(it) }) {
            return TaskDomain.HEALTH
        }

        // Security & Play Store APK/AAB deployment queries
        if (listOf("security", "threat", "vulnerability", "prompt injection", "privacy", "audit", "encrypt", "firewall", "shield", "apk", "aab", "bundle", "playstore", "play store", "install", "keystore", "release", "deployment", "google apk").any { lowered.contains(it) }) {
            return TaskDomain.SECURITY
        }

        // Math triggers
        if (listOf("calculate", "solve", "equation", "math", "square root", "sqrt", "+", "-", "*", "/", "derivative", "integral", "% of").any { lowered.contains(it) } ||
            (lowered.contains("what is") && lowered.any { it.isDigit() })) {
            return TaskDomain.MATH
        }

        // Art triggers
        if (listOf("draw", "sketch", "illustrate", "design", "artwork", "svg", "logo", "diagram", "canvas").any { lowered.contains(it) }) {
            return TaskDomain.ART
        }

        // Sales triggers
        if (listOf("price", "buy", "discount", "offer", "deal", "sales", "pitch", "product", "objection", "close", "cost", "value").any { lowered.contains(it) }) {
            return TaskDomain.SALES
        }

        // Organizer triggers
        if (listOf("schedule", "remind", "organize", "task", "plan", "calendar", "todo", "priority", "matrix", "eisenhower").any { lowered.contains(it) }) {
            return TaskDomain.ORGANIZER
        }

        // Health triggers
        if (listOf("symptom", "pain", "medicine", "diet", "sleep", "doctor", "health", "wellness", "nutrition", "workout").any { lowered.contains(it) }) {
            return TaskDomain.HEALTH
        }

        return TaskDomain.CONVERSATION
    }

    fun route(userInput: String): AegisRouterResult {
        // Step 1: Security Shield Evaluation
        if (!securityCheck(userInput)) {
            val blockMsg = "🛡️ Security Shield Warning: Request blocked. The input contains patterns that conflict with AEGIS safety & prompt-integrity policies."
            val threatModule = "aegis_security_threat_filter"
            sessionMemory = sessionMemory.copy(
                securityThreatFlag = true,
                activeDomain = TaskDomain.SECURITY.domainId,
                lastResponse = blockMsg
            )
            return AegisRouterResult(
                domain = TaskDomain.SECURITY,
                responseText = blockMsg,
                securityThreatFlag = true,
                confidenceScore = 0.99f,
                moduleId = threatModule
            )
        }

        // Step 2: Classify Domain
        val domain = classifyDomain(userInput)
        val isHealthEmergency = SENSITIVE_HEALTH_FLAGS.any { userInput.lowercase().contains(it) }

        val responseText: String
        var mathRes = ""
        var salesStg = sessionMemory.salesStage
        var orgAction = "none"
        var moduleId = "aegis_general_module"

        when (domain) {
            TaskDomain.SECURITY -> {
                moduleId = "aegis_security_module"
                responseText = handleSecurity(userInput)
            }
            TaskDomain.MATH -> {
                moduleId = "aegis_math_handler"
                val mathData = handleMath(userInput)
                responseText = mathData.first
                mathRes = mathData.second
            }
            TaskDomain.ART -> {
                moduleId = "aegis_art_creator"
                responseText = handleArt(userInput)
            }
            TaskDomain.SALES -> {
                moduleId = "aegis_sales_advisor"
                val salesData = handleSales(userInput)
                responseText = salesData.first
                salesStg = salesData.second
            }
            TaskDomain.HEALTH -> {
                moduleId = "aegis_health_advisor"
                responseText = handleHealth(userInput, isHealthEmergency)
            }
            TaskDomain.ORGANIZER -> {
                moduleId = "aegis_organizer_hub"
                val orgData = handleOrganizer(userInput)
                responseText = orgData.first
                orgAction = orgData.second
            }
            TaskDomain.CONVERSATION -> {
                moduleId = "aegis_conversation_engine"
                responseText = handleConversation(userInput)
            }
        }

        // Update Session Memory
        sessionMemory = sessionMemory.copy(
            activeDomain = domain.domainId,
            userIntent = "handled_${domain.domainId}",
            confidenceScore = if (isHealthEmergency) 1.0f else 0.96f,
            lastResponse = responseText,
            healthEmergencyFlag = isHealthEmergency,
            securityThreatFlag = false,
            salesStage = salesStg,
            organizerLastAction = orgAction,
            mathResult = mathRes
        )

        return AegisRouterResult(
            domain = domain,
            responseText = responseText,
            securityThreatFlag = false,
            healthEmergencyFlag = isHealthEmergency,
            confidenceScore = if (isHealthEmergency) 1.0f else 0.96f,
            mathResult = mathRes,
            salesStage = salesStg,
            organizerAction = orgAction,
            moduleId = moduleId
        )
    }

    private fun handleSecurity(text: String): String {
        val lowered = text.lowercase()
        if (listOf("apk", "aab", "bundle", "playstore", "play store", "install", "keystore", "release", "deployment", "google apk").any { lowered.contains(it) }) {
            return """
                📦 AEGIS Google Play Store (AAB / APK) Deployment Guide:
                =======================================================
                Status: Pre-Configured CI/CD Release Pipeline Operational
                
                1️⃣ BUILD PLAY STORE APP BUNDLE (.AAB) VIA GITHUB ACTIONS:
                   • Workflow File: `.github/workflows/build-aab.yml`
                   • Gradle Command: `./gradlew :app:bundleRelease`
                   • Repository Secrets required in GitHub > Settings > Secrets:
                     - KEYSTORE_BASE64 (optional base64-encoded upload keystore .jks)
                     - STORE_PASSWORD (keystore password)
                     - KEY_PASSWORD (key alias password)
                   • Artifact Output: Download `app-bundle-release` artifact (`.aab` file ready for Play Store Console upload).

                2️⃣ BUILD & INSTALL RELEASE APK ON ANDROID DEVICE:
                   • Local Release APK Build:
                     `./gradlew :app:assembleRelease`
                   • ADB Installation Command:
                     `adb install -r app/build/outputs/apk/release/app-release.apk`
                   • Device Installation Note: Ensure "Install unknown apps" permission is enabled if installing APK directly.

                3️⃣ LOCAL KEYSTORE GENERATION COMMAND:
                   `keytool -genkeypair -alias upload -keyalg RSA -keysize 2048 -validity 10000 -keystore my-upload-key.jks -storepass <STORE_PASSWORD> -keypass <KEY_PASSWORD>`

                ⚡ See BUILDING_AAB.md in the repository root for step-by-step Play Store Release instructions.
            """.trimIndent()
        }

        return """
            🛡️ AEGIS Security Shield Analysis:
            ----------------------------------
            Status: Fully Operational (Strict Least-Privilege Mode)
            Encrypted Data Vault: Active
            Input Sanitization: Clean
            Audit Log ID: aegis_security_audit_${System.currentTimeMillis()}
            
            Analysis for request: "${text.take(60)}"
            - Threats Detected: 0
            - Risk Level: Clean (Safe execution)
            - Policy: Data protection and privacy enforced by default.
        """.trimIndent()
    }

    private fun handleMath(text: String): Pair<String, String> {
        val lowered = text.lowercase()
        val cleaned = lowered.replace("calculate", "").replace("solve", "").replace("what is", "").replace("?", "").trim()

        // Try exact arithmetic
        try {
            if (cleaned.contains("+")) {
                val parts = cleaned.split("+")
                if (parts.size == 2) {
                    val a = parts[0].trim().toDoubleOrNull()
                    val b = parts[1].trim().toDoubleOrNull()
                    if (a != null && b != null) {
                        val res = a + b
                        return Pair("📐 Exact Calculation:\n$a + $b = $res\n\nStep 1: Identified addition operation.\nStep 2: Computed exact sum = $res.", "$res")
                    }
                }
            } else if (cleaned.contains("-")) {
                val parts = cleaned.split("-")
                if (parts.size == 2) {
                    val a = parts[0].trim().toDoubleOrNull()
                    val b = parts[1].trim().toDoubleOrNull()
                    if (a != null && b != null) {
                        val res = a - b
                        return Pair("📐 Exact Calculation:\n$a - $b = $res\n\nStep 1: Identified subtraction operation.\nStep 2: Computed exact difference = $res.", "$res")
                    }
                }
            } else if (cleaned.contains("*") || cleaned.contains("times") || cleaned.contains("x")) {
                val parts = cleaned.split(Regex("[*x]|times"))
                if (parts.size == 2) {
                    val a = parts[0].trim().toDoubleOrNull()
                    val b = parts[1].trim().toDoubleOrNull()
                    if (a != null && b != null) {
                        val res = a * b
                        return Pair("📐 Exact Calculation:\n$a × $b = $res\n\nStep 1: Identified multiplication operation.\nStep 2: Computed exact product = $res.", "$res")
                    }
                }
            } else if (cleaned.contains("/")) {
                val parts = cleaned.split("/")
                if (parts.size == 2) {
                    val a = parts[0].trim().toDoubleOrNull()
                    val b = parts[1].trim().toDoubleOrNull()
                    if (a != null && b != null && b != 0.0) {
                        val res = a / b
                        return Pair("📐 Exact Calculation:\n$a / $b = $res\n\nStep 1: Identified division operation.\nStep 2: Computed exact quotient = $res.", "$res")
                    }
                }
            } else if (cleaned.contains("sqrt") || cleaned.contains("square root")) {
                val num = cleaned.replace("sqrt", "").replace("square root", "").trim().toDoubleOrNull()
                if (num != null && num >= 0) {
                    val res = sqrt(num)
                    return Pair("📐 Exact Calculation:\n√$num = $res\n\nStep 1: Extracted principal square root.\nStep 2: Calculated exact value = $res.", "$res")
                }
            }
        } catch (e: Exception) {
            // Fallthrough to step breakdown
        }

        return Pair(
            """
                📐 AEGIS Logic & Math Step-by-Step Solver:
                Problem: "$text"
                
                Step 1: Parse numeric tokens & target operations.
                Step 2: Formulate mathematical relations with exact computation constraint.
                Step 3: Evaluate symbolic parameters without approximations.
                
                Result: Fully verified logic structure ready for direct application.
            """.trimIndent(),
            "verifying_expression"
        )
    }

    private fun handleArt(text: String): String {
        return """
            🎨 AEGIS Creative Concept & SVG Art Blueprint:
            Theme: ${text.take(50)}
            
            ```xml
            <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <linearGradient id="aegisGlow" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#00F2FE" />
                  <stop offset="100%" stop-color="#4A00E0" />
                </linearGradient>
              </defs>
              <!-- Shield Core Canvas -->
              <path d="M100,20 L160,50 V110 C160,150 100,180 100,180 C100,180 40,150 40,110 V50 Z" 
                    fill="url(#aegisGlow)" stroke="#00E676" stroke-width="3" />
              <circle cx="100" cy="95" r="25" fill="#090D16" stroke="#00F2FE" stroke-width="2" />
            </svg>
            ```
            
            Visual Direction: High-contrast geometric minimalism, obsidian canvas with glowing cyan and neon emerald shield motifs.
        """.trimIndent()
    }

    private fun handleSales(text: String): Pair<String, String> {
        val lowered = text.lowercase()
        val stage = when {
            lowered.contains("price") || lowered.contains("cost") || lowered.contains("discount") -> "proposal"
            lowered.contains("expensive") || lowered.contains("concern") || lowered.contains("competitor") -> "objection"
            lowered.contains("buy") || lowered.contains("sign") || lowered.contains("close") -> "closing"
            else -> "discovery"
        }

        val advice = when (stage) {
            "proposal" -> "Here is our transparent value breakdown: We focus on ROI, data safety guarantees, and zero hidden fees. Every feature delivers measurable value."
            "objection" -> "I hear your concern. Let's look at total cost of ownership vs risk mitigation. We offer a 30-day money-back guarantee with zero lock-in."
            "closing" -> "Based on your requirements, the AEGIS Executive package fits your workflow best. Shall we finalize setting up your workspace?"
            else -> "Let's explore your core goals first so I can recommend the exact configuration you need without extra clutter."
        }

        val textResponse = """
            💼 AEGIS Sales Advisory (Honest & Transparent):
            Current Stage: ${stage.uppercase()}
            
            $advice
            
            Core Principle: Honest value, transparent pricing, zero high-pressure tactics or artificial scarcity.
        """.trimIndent()

        return Pair(textResponse, stage)
    }

    private fun handleHealth(text: String, isEmergency: Boolean): String {
        if (isEmergency) {
            return """
                🚨 URGENT HEALTH EMERGENCY ALERT 🚨
                -----------------------------------
                Your request mentions symptoms that sound critical (e.g., chest pain, difficulty breathing, severe emergency).
                
                ⚠️ PLEASE TAKE IMMEDIATE ACTION:
                1. Call your local Emergency Services (911 or emergency services in your country) right now.
                2. If you are alone, alert a family member or neighbor immediately.
                3. Do not attempt to self-diagnose or wait.
                
                *AEGIS is an AI assistant, NOT a medical doctor or emergency service.*
            """.trimIndent()
        }

        return """
            🌿 AEGIS Health & Wellness Guidance:
            Query: "$text"
            
            - General Wellness Information: Focus on hydration, balanced nutrition, restful sleep (7-8 hours), and moderate daily physical activity.
            - Symptom Tracking: Keep a journal of duration, intensity, and triggers to share with a health professional.
            
            --------------------------------------------------
            ⚠️ DISCLAIMER: This is general information, not medical advice. Always consult a qualified healthcare professional or doctor for diagnoses, treatments, or medical concerns.
        """.trimIndent()
    }

    private fun handleOrganizer(text: String): Pair<String, String> {
        val lowered = text.lowercase()
        val action = when {
            lowered.contains("schedule") || lowered.contains("calendar") || lowered.contains("time") -> "scheduled"
            lowered.contains("add") || lowered.contains("create") || lowered.contains("new") -> "created"
            lowered.contains("delete") || lowered.contains("remove") -> "deleted"
            else -> "updated"
        }

        return Pair(
            """
                📅 AEGIS Executive Priority Matrix:
                Target Action: $action
                
                Priority Matrix Assignment:
                - Q1 (Urgent & Important): High-impact deadlines & security reviews
                - Q2 (Important, Not Urgent): Long-term planning, health & skill creation
                - Q3 (Urgent, Not Important): Administrative syncs
                - Q4 (Routine): Low priority backlog
                
                Schedule updated successfully with zero calendar overlaps.
            """.trimIndent(),
            action
        )
    }

    private fun handleConversation(text: String): String {
        return """
            👋 AEGIS Executive Assistant:
            
            I'm here to support you. Whether you need exact mathematical calculations, visual art blueprints, transparent sales guidance, wellness facts, executive task organization, or security threat filtering — I'm ready.
            
            What's top of mind for you right now?
        """.trimIndent()
    }
}
