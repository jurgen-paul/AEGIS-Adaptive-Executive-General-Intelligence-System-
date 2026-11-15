package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.service.BiometricAuthManager

enum class PortalPageType(val assetFileName: String) {
    STATUS("status.html"),
    KLANTSUPPORT("klantsupport.html")
}

private fun isNetworkOnline(context: Context): Boolean {
    return try {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val network = cm?.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } catch (_: Exception) {
        false
    }
}

private fun loadHtmlAsset(context: Context, fileName: String): String {
    return try {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (_: Exception) {
        "<html><body><h3>AEGIS Portal</h3><p>Unable to load bundled page: $fileName</p></body></html>"
    }
}

/**
 * Material 3 card exposing live AEGIS Status URL & Klantsupport URL endpoints,
 * live device health telemetry, and an offline-capable hardened WebView preview.
 */
@Composable
fun StatusAndKlantsupportComponent(
    securityMode: String,
    activeTaskCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val statusUrl = stringResource(R.string.status_page_url)
    val supportUrl = stringResource(R.string.klantsupport_page_url)
    val supportEmail = stringResource(R.string.support_email)
    val statusPageTitle = stringResource(R.string.status_page_title)
    val klantsupportPageTitle = stringResource(R.string.klantsupport_page_title)

    val isBiometricReady = remember(context) { BiometricAuthManager.isBiometricAvailable(context) }
    val isOnline = remember(context) { isNetworkOnline(context) }

    var activePortalPage by remember { mutableStateOf<PortalPageType?>(null) }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("status_klantsupport_card")
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "Status & Klantsupport Portal",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "STATUS & KLANTSUPPORT PORTAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = "Public status URL, live health & customer support",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            text = "OPERATIONAL",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Live Device Telemetry Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelemetryChip(
                    label = "Vault",
                    value = if (isBiometricReady) "Biometric Ready" else "PIN Ready",
                    modifier = Modifier.weight(1f)
                )
                TelemetryChip(
                    label = "Network",
                    value = if (isOnline) "Connected" else "Offline-First",
                    modifier = Modifier.weight(1f)
                )
                TelemetryChip(
                    label = "Policy",
                    value = securityMode.uppercase(),
                    modifier = Modifier.weight(1f)
                )
            }

            if (activeTaskCount > 0) {
                Text(
                    text = "Room Persistence active • $activeTaskCount executive tasks tracked",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 1. Status URL Endpoint Box
            PortalUrlRow(
                title = statusPageTitle,
                url = statusUrl,
                buttonLabel = "View Status Page",
                icon = Icons.Default.Public,
                openTag = "open_status_page_btn",
                copyTag = "copy_status_url_btn",
                onViewInApp = { activePortalPage = PortalPageType.STATUS },
                onCopyUrl = {
                    clipboardManager.setText(AnnotatedString(statusUrl))
                    feedbackBanner = "Status URL copied to clipboard."
                },
                onOpenExternal = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(statusUrl)))
                    } catch (_: Exception) {
                        feedbackBanner = "Opened bundled offline Status Page instead."
                        activePortalPage = PortalPageType.STATUS
                    }
                }
            )

            // 2. Klantsupport URL Endpoint Box
            PortalUrlRow(
                title = klantsupportPageTitle,
                url = supportUrl,
                buttonLabel = "View Klantsupport",
                icon = Icons.Default.SupportAgent,
                openTag = "open_klantsupport_page_btn",
                copyTag = "copy_klantsupport_url_btn",
                onViewInApp = { activePortalPage = PortalPageType.KLANTSUPPORT },
                onCopyUrl = {
                    clipboardManager.setText(AnnotatedString(supportUrl))
                    feedbackBanner = "Klantsupport URL copied to clipboard."
                },
                onOpenExternal = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(supportUrl)))
                    } catch (_: Exception) {
                        feedbackBanner = "Opened bundled offline Klantsupport Page instead."
                        activePortalPage = PortalPageType.KLANTSUPPORT
                    }
                }
            )

            // Direct Email Klantsupport CTA
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$supportEmail")
                        putExtra(Intent.EXTRA_SUBJECT, "AEGIS Klantsupport Aanvraag")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        clipboardManager.setText(AnnotatedString(supportEmail))
                        feedbackBanner = "No mail client detected. Support email copied: $supportEmail"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("email_klantsupport_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Klantsupport",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mail Klantsupport ($supportEmail)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (feedbackBanner != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = feedbackBanner!!,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }

    // In-App Hardened WebView Preview Dialog for Status & Klantsupport Pages
    activePortalPage?.let { initialPage ->
        PortalWebViewDialog(
            initialPage = initialPage,
            onDismiss = { activePortalPage = null }
        )
    }
}

@Composable
private fun TelemetryChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PortalUrlRow(
    title: String,
    url: String,
    buttonLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    openTag: String,
    copyTag: String,
    onViewInApp: () -> Unit,
    onCopyUrl: () -> Unit,
    onOpenExternal: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onCopyUrl,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag(copyTag)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy $title URL",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = url,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewInApp,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag(openTag),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = buttonLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onOpenExternal,
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open $title in browser",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PortalWebViewDialog(
    initialPage: PortalPageType,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPage by remember { mutableStateOf(initialPage) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("portal_webview_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar with Page Switcher & Close
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { currentPage = PortalPageType.STATUS },
                            colors = if (currentPage == PortalPageType.STATUS) {
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            Text("Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { currentPage = PortalPageType.KLANTSUPPORT },
                            colors = if (currentPage == PortalPageType.KLANTSUPPORT) {
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            Text("Klantsupport", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("close_portal_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Portal Viewer"
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.allowFileAccess = false
                            settings.allowContentAccess = false
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val urlStr = request?.url?.toString().orEmpty()
                                    return when {
                                        urlStr.endsWith("status.html") -> {
                                            currentPage = PortalPageType.STATUS
                                            true
                                        }
                                        urlStr.endsWith("klantsupport.html") -> {
                                            currentPage = PortalPageType.KLANTSUPPORT
                                            true
                                        }
                                        urlStr.startsWith("mailto:") -> {
                                            try {
                                                ctx.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(urlStr)))
                                            } catch (_: Exception) {
                                            }
                                            true
                                        }
                                        else -> true
                                    }
                                }
                            }
                        }
                    },
                    update = { webView ->
                        val html = loadHtmlAsset(context, currentPage.assetFileName)
                        webView.loadDataWithBaseURL(
                            "https://aegis.local/",
                            html,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}
