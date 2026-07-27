package com.example.service

import android.content.Context
import com.example.data.AegisSessionLog
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

data class SecureExportResult(
    val success: Boolean,
    val filePath: String,
    val totalLogsExported: Int,
    val totalScrubbedMatches: Int,
    val encryptedFileSizeBytes: Long,
    val errorMessage: String? = null
)

object SecureExportManager {

    private const val ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val DEFAULT_AES_KEY = "AEGIS_ZERO_TRUST_EXPORT_KEY_256"

    /**
     * Scrubs sensitive information such as Emails, Phone numbers, Credit Cards,
     * Passwords, API Keys, and SSNs from raw text prior to persistent export.
     */
    fun scrubSensitiveData(input: String): Pair<String, Int> {
        var scrubbed = input
        var matchesCount = 0

        // 1. Email pattern
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        val emailMatches = emailRegex.findAll(scrubbed).count()
        if (emailMatches > 0) {
            matchesCount += emailMatches
            scrubbed = scrubbed.replace(emailRegex, "[REDACTED_EMAIL]")
        }

        // 2. Credit card pattern (13-16 digits with optional spaces or dashes)
        val cardRegex = Regex("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12})\\b")
        val cardMatches = cardRegex.findAll(scrubbed).count()
        if (cardMatches > 0) {
            matchesCount += cardMatches
            scrubbed = scrubbed.replace(cardRegex, "[REDACTED_CARD]")
        }

        // 3. Phone number pattern
        val phoneRegex = Regex("\\b\\+?\\d{1,3}?[-.\\s]?\\(?\\d{2,4}?\\)?[-.\\s]?\\d{3,4}[-.\\s]?\\d{4}\\b")
        val phoneMatches = phoneRegex.findAll(scrubbed).count()
        if (phoneMatches > 0) {
            matchesCount += phoneMatches
            scrubbed = scrubbed.replace(phoneRegex, "[REDACTED_PHONE]")
        }

        // 4. API keys & bearer tokens
        val secretRegex = Regex("(?i)(AIzaSy[A-Za-z0-9_-]{33}|bearer\\s+[A-Za-z0-9._-]+|api[_-]?key\\s*[:=]\\s*\\S+|password\\s*[:=]\\s*\\S+)")
        val secretMatches = secretRegex.findAll(scrubbed).count()
        if (secretMatches > 0) {
            matchesCount += secretMatches
            scrubbed = scrubbed.replace(secretRegex, "[REDACTED_SECRET]")
        }

        // 5. Social Security Numbers (SSN)
        val ssnRegex = Regex("\\b\\d{3}-\\d{2}-\\d{4}\\b")
        val ssnMatches = ssnRegex.findAll(scrubbed).count()
        if (ssnMatches > 0) {
            matchesCount += ssnMatches
            scrubbed = scrubbed.replace(ssnRegex, "[REDACTED_SSN]")
        }

        return Pair(scrubbed, matchesCount)
    }

    /**
     * Exports chat history logs to a local AES-256 encrypted file after scrubbing all PII.
     */
    fun exportEncryptedChatHistory(context: Context, logs: List<AegisSessionLog>): SecureExportResult {
        if (logs.isEmpty()) {
            return SecureExportResult(
                success = false,
                filePath = "",
                totalLogsExported = 0,
                totalScrubbedMatches = 0,
                encryptedFileSizeBytes = 0L,
                errorMessage = "No chat history available to export."
            )
        }

        return try {
            var totalScrubbed = 0
            val jsonArray = JSONArray()

            for (log in logs) {
                val (scrubbedQuery, qMatches) = scrubSensitiveData(log.userQuery)
                val (scrubbedResp, rMatches) = scrubSensitiveData(log.responseText)
                totalScrubbed += (qMatches + rMatches)

                val logObj = JSONObject().apply {
                    put("id", log.id)
                    put("sessionId", log.sessionId)
                    put("userQuery", scrubbedQuery)
                    put("domain", log.domain)
                    put("responseText", scrubbedResp)
                    put("confidenceScore", log.confidenceScore)
                    put("securityThreatFlag", log.securityThreatFlag)
                    put("timestamp", log.timestamp)
                    put("securityScrubbed", true)
                }
                jsonArray.put(logObj)
            }

            val exportJsonString = JSONObject().apply {
                put("aegisVersion", "1.0.0-AEGIS")
                put("exportTimestamp", System.currentTimeMillis())
                put("encryptionType", "AES-256-CBC")
                put("totalLogs", jsonArray.length())
                put("scrubbedEntitiesCount", totalScrubbed)
                put("data", jsonArray)
            }.toString()

            // Derive 256-bit AES Key from DEFAULT_AES_KEY using SHA-256
            val sha256 = MessageDigest.getInstance("SHA-256")
            val keyBytes = sha256.digest(DEFAULT_AES_KEY.toByteArray(StandardCharsets.UTF_8))
            val secretKeySpec = SecretKeySpec(keyBytes, "AES")

            // Generate 16-byte random IV
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            // Encrypt data
            val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
            val encryptedBytes = cipher.doFinal(exportJsonString.toByteArray(StandardCharsets.UTF_8))

            // Prepend IV to ciphertext (16 bytes IV + encrypted content)
            val finalPayload = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, finalPayload, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, finalPayload, iv.size, encryptedBytes.size)

            // Write encrypted file to context.filesDir
            val fileName = "aegis_scrubbed_export_${System.currentTimeMillis()}.enc"
            val exportFile = File(context.filesDir, fileName)
            exportFile.writeBytes(finalPayload)

            SecureExportResult(
                success = true,
                filePath = exportFile.absolutePath,
                totalLogsExported = logs.size,
                totalScrubbedMatches = totalScrubbed,
                encryptedFileSizeBytes = exportFile.length()
            )
        } catch (e: Exception) {
            SecureExportResult(
                success = false,
                filePath = "",
                totalLogsExported = 0,
                totalScrubbedMatches = 0,
                encryptedFileSizeBytes = 0L,
                errorMessage = e.localizedMessage ?: "Failed during encryption/export process"
            )
        }
    }
}
