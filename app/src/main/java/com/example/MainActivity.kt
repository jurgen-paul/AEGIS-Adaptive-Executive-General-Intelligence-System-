package com.example.MainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AegisViewModel
import com.example.ui.components.AegisTopAppBar
import com.example.ui.components.SecurityAlertBanner
import com.example.ui.screens.CommandHubScreen
import com.example.ui.screens.ExecutiveOrganizerScreen
import com.example.ui.screens.SecurityShieldScreen
import com.example.ui.screens.SessionMemoryScreen
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekCardBg
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.AegisTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AegisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AegisTheme {
                AegisMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AegisMainApp(viewModel: AegisViewModel) {
    val sessionMemory by viewModel.sessionMemory.collectAsStateWithLifecycle()
    val sessionLogs by viewModel.sessionLogs.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val securityEvents by viewModel.securityEvents.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                AegisTopAppBar(
                    sessionMemory = sessionMemory,
                    threatCount = securityEvents.size,
                    onShieldClick = { viewModel.switchTab(2) },
                    onMemoryClick = { viewModel.switchTab(3) }
                )

                SecurityAlertBanner(
                    isSecurityThreat = sessionMemory.securityThreatFlag,
                    isHealthEmergency = sessionMemory.healthEmergencyFlag,
                    onInspectSecurity = { viewModel.switchTab(2) },
                    onEmergencyDismiss = {
                        viewModel.processUserPrompt("dismiss emergency banner")
                    }
                )
            }
        },
        bottomBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SleekBorder)
                )
                NavigationBar(
                    containerColor = SleekCardBg,
                    contentColor = SleekPrimary,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("aegis_bottom_navigation")
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { viewModel.switchTab(0) },
                        icon = { Text("🏠", fontSize = 18.sp) },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SleekPrimary,
                            selectedTextColor = SleekPrimary,
                            indicatorColor = SleekPrimaryContainer,
                            unselectedIconColor = SleekTextSecondary,
                            unselectedTextColor = SleekTextSecondary
                        ),
                        modifier = Modifier.testTag("aegis_tab_command_hub")
                    )

                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { viewModel.switchTab(1) },
                        icon = { Text("📅", fontSize = 18.sp) },
                        label = { Text("Organizer", fontSize = 10.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SleekPrimary,
                            selectedTextColor = SleekPrimary,
                            indicatorColor = SleekPrimaryContainer,
                            unselectedIconColor = SleekTextSecondary,
                            unselectedTextColor = SleekTextSecondary
                        ),
                        modifier = Modifier.testTag("aegis_tab_organizer")
                    )

                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { viewModel.switchTab(2) },
                        icon = { Text("🛡️", fontSize = 18.sp) },
                        label = { Text("Vault", fontSize = 10.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SleekPrimary,
                            selectedTextColor = SleekPrimary,
                            indicatorColor = SleekPrimaryContainer,
                            unselectedIconColor = SleekTextSecondary,
                            unselectedTextColor = SleekTextSecondary
                        ),
                        modifier = Modifier.testTag("aegis_tab_security")
                    )

                    NavigationBarItem(
                        selected = activeTab == 3,
                        onClick = { viewModel.switchTab(3) },
                        icon = { Text("⚙️", fontSize = 18.sp) },
                        label = { Text("Config", fontSize = 10.sp, fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SleekPrimary,
                            selectedTextColor = SleekPrimary,
                            indicatorColor = SleekPrimaryContainer,
                            unselectedIconColor = SleekTextSecondary,
                            unselectedTextColor = SleekTextSecondary
                        ),
                        modifier = Modifier.testTag("aegis_tab_memory")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> CommandHubScreen(
                    sessionMemory = sessionMemory,
                    sessionLogs = sessionLogs,
                    isGenerating = isGenerating,
                    onSendPrompt = { viewModel.processUserPrompt(it) }
                )
                1 -> ExecutiveOrganizerScreen(
                    tasks = tasks,
                    onAddTask = { title, desc, urgent, important, cat ->
                        viewModel.addTask(title, desc, urgent, important, cat)
                    },
                    onUpdateStatus = { task, status ->
                        viewModel.updateTaskStatus(task, status)
                    },
                    onDeleteTask = { id ->
                        viewModel.deleteTask(id)
                    }
                )
                2 -> SecurityShieldScreen(
                    sessionMemory = sessionMemory,
                    securityEvents = securityEvents,
                    onToggleSecurityMode = { viewModel.toggleSecurityMode(it) },
                    onClearSecurityEvents = { viewModel.clearSecurityEvents() }
                )
                3 -> SessionMemoryScreen(
                    sessionMemory = sessionMemory,
                    onClearLogs = { viewModel.clearLogs() }
                )
            }
        }
    }
}
