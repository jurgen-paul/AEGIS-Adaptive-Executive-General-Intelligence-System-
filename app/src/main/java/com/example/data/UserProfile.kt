package com.example.data

data class UserProfile(
    val fullName: String = "Alex Vance",
    val email: String = "alex.vance@aegis-systems.io",
    val title: String = "Chief Executive Lead & System Administrator",
    val clearanceLevel: String = "LEVEL 5 - TOP SECRET / RESTRICTED",
    val department: String = "Executive Intelligence & Defense Division",
    val biometricLockEnabled: Boolean = true,
    val autoLockTimeout: String = "5 Minutes", // Immediately, 1 Minute, 5 Minutes, 15 Minutes, Never
    val securityNotificationsEnabled: Boolean = true,
    val aiPersonaTone: String = "Executive & Concise", // Executive & Concise, Honest & Persuasive, Analytical & Detailed, Friendly & Empathetic
    val securityDefenseLevel: String = "Strict", // Strict, Standard, Relaxed
    val preferredLanguage: String = "English (US)",
    val primaryAiModel: String = "Gemini 1.5 Pro"
)
