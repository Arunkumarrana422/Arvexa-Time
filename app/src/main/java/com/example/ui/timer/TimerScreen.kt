package com.example.ui.timer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val presets by viewModel.timerPresets.collectAsStateWithLifecycle(initialValue = emptyList())
    var showSavePresetDialog by remember { mutableStateOf(false) }
    var presetTitleInput by remember { mutableStateOf("") }

    var hoursInput by remember { mutableStateOf(0) }
    var minutesInput by remember { mutableStateOf(5) }
    var secondsInput by remember { mutableStateOf(0) }

    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Countdown Timer", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Timer Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isFinished) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    else cardBg
                ),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 3.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (state.totalTimeMillis == 0L) "00:05:00" else TimerViewModel.formatTimerMillis(state.remainingMillis),
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (state.isFinished) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.isFinished) "TIME'S UP!" else if (state.isRunning) "RUNNING" else "READY",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Control Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.addTime(60 * 1000L) },
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add 1m")
                    Text("1m")
                }

                Button(
                    onClick = {
                        if (state.isRunning) viewModel.pause()
                        else {
                            if (state.remainingMillis == 0L) {
                                viewModel.setDuration((hoursInput * 3600 + minutesInput * 60 + secondsInput) * 1000L)
                            }
                            viewModel.start()
                        }
                    },
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isRunning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
                        contentColor = if (state.isRunning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isRunning) "Pause" else "Start",
                        modifier = Modifier.size(36.dp)
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.reset() },
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Presets Row
            Text("Quick Presets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val quickPresets = listOf(
                    "10s" to 10 * 1000L,
                    "30s" to 30 * 1000L,
                    "1m" to 60 * 1000L,
                    "5m" to 5 * 60 * 1000L,
                    "10m" to 10 * 60 * 1000L,
                    "15m" to 15 * 60 * 1000L,
                    "30m" to 30 * 60 * 1000L,
                    "1h" to 60 * 60 * 1000L
                )

                quickPresets.forEach { (label, duration) ->
                    AssistChip(
                        onClick = { viewModel.setDuration(duration) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Saved Presets Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Saved Presets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = { showSavePresetDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Preset")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save Current")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Presets LazyVerticalGrid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presets) { preset ->
                    Card(
                        onClick = { viewModel.setDuration(preset.durationMillis) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = cardBorder,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(text = preset.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = TimerViewModel.formatTimerMillis(preset.durationMillis),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSavePresetDialog) {
        AlertDialog(
            onDismissRequest = { showSavePresetDialog = false },
            title = { Text("Save Timer Preset") },
            text = {
                Column {
                    Text("Enter preset title (e.g., Study, Workout):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = presetTitleInput,
                        onValueChange = { presetTitleInput = it },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (presetTitleInput.isNotBlank()) {
                        viewModel.savePreset(presetTitleInput, state.totalTimeMillis, "Custom")
                        showSavePresetDialog = false
                        presetTitleInput = ""
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSavePresetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
