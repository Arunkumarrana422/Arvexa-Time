package com.example.ui.stopwatch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
fun StopwatchScreen(viewModel: StopwatchViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSaveDialog by remember { mutableStateOf(false) }
    var sessionNameInput by remember { mutableStateOf("Stopwatch Session") }

    val listState = rememberLazyListState()

    LaunchedEffect(state.laps.size) {
        if (state.laps.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stopwatch", fontWeight = FontWeight.Bold) },
                actions = {
                    if (state.timeMillis > 0L) {
                        IconButton(onClick = { showSaveDialog = true }) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Save Session")
                        }
                    }
                },
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

            // Main Stopwatch Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 3.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = StopwatchViewModel.formatMillis(state.timeMillis),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.isRunning) "RUNNING" else if (state.timeMillis > 0L) "PAUSED" else "READY",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Control Buttons Row (Perfect Circles)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (state.isRunning) viewModel.lap() else viewModel.reset()
                    },
                    modifier = Modifier.size(92.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (state.isRunning) Icons.Default.Flag else Icons.Default.Refresh,
                            contentDescription = if (state.isRunning) "Lap" else "Reset",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = if (state.isRunning) "Lap" else "Reset", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        if (state.isRunning) viewModel.pause() else viewModel.start()
                    },
                    modifier = Modifier.size(104.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isRunning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
                        contentColor = if (state.isRunning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isRunning) "Pause" else "Start",
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = if (state.isRunning) "Pause" else "Start", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(visible = state.sessionSavedMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.sessionSavedMessage ?: "",
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = { viewModel.clearSavedMessage() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Laps (${state.laps.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (state.laps.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearLaps() }) {
                        Text("Clear All")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.laps, key = { it.lapNumber }) { lap ->
                    val isBest = state.bestLapNumber == lap.lapNumber
                    val isSlowest = state.slowestLapNumber == lap.lapNumber

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = cardBorder,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Lap ${lap.lapNumber}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (isBest) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("Fastest") }
                                }
                                if (isSlowest) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Badge(containerColor = MaterialTheme.colorScheme.error) { Text("Slowest") }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = StopwatchViewModel.formatMillis(lap.lapTimeMillis),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "+${StopwatchViewModel.formatMillis(lap.totalTimeMillis)}",
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { viewModel.deleteLap(lap.lapNumber) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Lap",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Stopwatch Session") },
            text = {
                Column {
                    Text("Enter a name for this session:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sessionNameInput,
                        onValueChange = { sessionNameInput = it },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveSession(sessionNameInput)
                    showSaveDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
