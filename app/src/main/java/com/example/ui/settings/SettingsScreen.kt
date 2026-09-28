package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onNavigateToAbout: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Appearance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Appearance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Theme", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = state.isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Keep Screen Awake", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = state.keepScreenAwake,
                            onCheckedChange = { viewModel.toggleKeepAwake(it) }
                        )
                    }
                }
            }

            // Notifications & Feedback Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Feedback & Alerts", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibration", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = state.isVibrationEnabled,
                            onCheckedChange = { viewModel.toggleVibration(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sound Alerts", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = state.isSoundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) }
                        )
                    }
                }
            }

            // About Section Card
            Card(
                onClick = onNavigateToAbout,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "About", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("About Arvexa Time", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text("Version 1.0 • Arun Kumar Rana", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
