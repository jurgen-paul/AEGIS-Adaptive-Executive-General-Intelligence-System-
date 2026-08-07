package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.GoogleFormEntryMapping
import com.example.service.GoogleSheetsIntegrationManager
import com.example.service.GoogleSheetsSyncConfig
import com.example.service.GoogleSheetsSyncResult
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun GoogleSheetsFormComponent(
    modifier: Modifier = Modifier,
    onSyncLogsRequested: (suspend (GoogleSheetsSyncConfig) -> Int)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var config by remember { mutableStateOf(GoogleSheetsIntegrationManager.loadConfig(context)) }
    var isSettingsExpanded by remember { mutableStateOf(false) }

    // Form Input States
    var formTitle by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf("SECURITY_AUDIT") }
    var formSeverity by remember { mutableStateOf("HIGH") }
    var formDetails by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var lastSyncResult by remember { mutableStateOf<GoogleSheetsSyncResult?>(null) }
    val submissionHistory = remember { mutableStateListOf<GoogleSheetsSyncResult>() }

    fun updateConfig(newConfig: GoogleSheetsSyncConfig) {
        config = newConfig
        GoogleSheetsIntegrationManager.saveConfig(context, newConfig)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SleekBorder, RoundedCornerShape(20.dp))
            .testTag("google_sheets_form_card"),
        colors = CardDefaults.cardColors(containerColor = SleekCardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = "Google Sheets",
                        tint = SleekSecurityGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Sheets & Forms Integration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SleekSecurityGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekSecurityGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(SleekSecurityGreen, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (config.formResponseUrl.contains("docs.google.com")) "FORM READY" else "TEST SYNC MODE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekSecurityGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Log security incidents, prompt defense alerts, and executive tasks directly to a connected Google Sheet or Google Form endpoint.",
                fontSize = 12.sp,
                color = SleekTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Preset Form Type Selector
            Text(
                text = "Form Type Preset:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val presets = listOf(
                    "SECURITY_INCIDENT" to "Shield Alert",
                    "PROMPT_DEFENSE" to "Defense Log",
                    "EXECUTIVE_TASK" to "Task Sync"
                )

                presets.forEach { (typeKey, label) ->
                    val isSelected = config.formType == typeKey
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) SleekPrimary else SleekBackground,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) SleekPrimary else SleekBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                updateConfig(config.copy(formType = typeKey))
                            }
                            .testTag("google_sheet_preset_$typeKey")
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else SleekTextPrimary,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Settings Toggle Accordion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isSettingsExpanded = !isSettingsExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Config",
                        tint = SleekTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Configure Endpoints & Field Mappings",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SleekPrimary
                    )
                }
                Icon(
                    imageVector = if (isSettingsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = SleekPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = isSettingsExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(SleekBackground, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Google Form Response URL:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = config.formResponseUrl,
                        onValueChange = { updateConfig(config.copy(formResponseUrl = it)) },
                        placeholder = { Text("https://docs.google.com/forms/d/e/.../formResponse", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("google_form_url_input"),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = SleekTextPrimary),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Google Apps Script Webhook URL (Optional):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = config.appsScriptUrl,
                        onValueChange = { updateConfig(config.copy(appsScriptUrl = it)) },
                        placeholder = { Text("https://script.google.com/macros/s/.../exec", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apps_script_url_input"),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = SleekTextPrimary),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Form Entry Field Mappings:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    config.entryMappings.forEachIndexed { idx, mapping ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mapping.fieldName,
                                fontSize = 11.sp,
                                color = SleekTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SleekCardBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
                            ) {
                                Text(
                                    text = mapping.entryId,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SleekPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Form Submission Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SleekBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Submit Record to Google Sheet",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = formTitle,
                        onValueChange = { formTitle = it },
                        placeholder = { Text("Event / Task Title (e.g., 'Unauthorized Access Attempt')", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("google_sheet_form_title_input"),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = SleekTextPrimary),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Category Selector
                        OutlinedTextField(
                            value = formCategory,
                            onValueChange = { formCategory = it },
                            placeholder = { Text("Category", fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("google_sheet_form_category_input"),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = SleekTextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        // Severity Selector
                        OutlinedTextField(
                            value = formSeverity,
                            onValueChange = { formSeverity = it },
                            placeholder = { Text("Severity", fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("google_sheet_form_severity_input"),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = SleekTextPrimary),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = formDetails,
                        onValueChange = { formDetails = it },
                        placeholder = { Text("Details / Query Payload...", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .testTag("google_sheet_form_details_input"),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = SleekTextPrimary),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isSubmitting = true
                                val fieldValues = mapOf(
                                    "Event/Task Title" to formTitle.ifBlank { "AEGIS Auto Entry" },
                                    "Domain/Category" to formCategory,
                                    "Threat Level/Status" to formSeverity,
                                    "Details/Query" to formDetails.ifBlank { "No payload provided." }
                                )

                                val res = GoogleSheetsIntegrationManager.submitFormToGoogleSheets(
                                    config = config,
                                    fieldValues = fieldValues
                                )

                                lastSyncResult = res
                                submissionHistory.add(0, res)
                                isSubmitting = false
                                if (res.success) {
                                    formTitle = ""
                                    formDetails = ""
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = SleekSecurityGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_to_google_sheet_button")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submitting to Google Sheet...", color = Color.White, fontSize = 12.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Submit Form to Google Sheet",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Sync Result Feedback Banner
            lastSyncResult?.let { res ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = if (res.success) SleekSecurityGreen.copy(alpha = 0.12f) else SleekThreatRed.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (res.success) SleekSecurityGreen else SleekThreatRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (res.success) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (res.success) SleekSecurityGreen else SleekThreatRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = res.message,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (res.success) SleekSecurityGreen else SleekThreatRed
                        )
                    }
                }
            }

            // Recent Submissions Log
            if (submissionHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Recent Google Sheet Submissions (${submissionHistory.size}):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                submissionHistory.take(3).forEach { hist ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SleekBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Target: ${hist.targetType}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekPrimary
                                )
                                Text(
                                    text = "HTTP ${hist.statusCode}",
                                    fontSize = 10.sp,
                                    color = SleekSecurityGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = hist.submittedFields.entries.joinToString(" | ") { "${it.key}: ${it.value}" },
                                fontSize = 10.sp,
                                color = SleekTextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
