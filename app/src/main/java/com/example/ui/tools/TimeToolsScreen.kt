package com.example.ui.tools

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeToolsScreen(viewModel: TimeToolsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Time Tools", fontWeight = FontWeight.Bold) },
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

            // 1. Time Unit Converter Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Time Unit Converter", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.converterValue,
                        onValueChange = { viewModel.updateConverterValue(it) },
                        label = { Text("Value in Hours") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val inputVal = state.converterValue.toDoubleOrNull() ?: 0.0
                    val minutes = inputVal * 60
                    val seconds = inputVal * 3600
                    val days = inputVal / 24

                    Text("• Minutes: ${String.format("%.2f", minutes)} min", style = MaterialTheme.typography.bodyMedium)
                    Text("• Seconds: ${String.format("%.2f", seconds)} sec", style = MaterialTheme.typography.bodyMedium)
                    Text("• Days: ${String.format("%.4f", days)} days", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // 2. Timestamp Converter Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Unix Timestamp Converter", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.timestampInput,
                        onValueChange = { viewModel.updateTimestampInput(it) },
                        label = { Text("Timestamp (Millis or Sec)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val ts = state.timestampInput.toLongOrNull() ?: System.currentTimeMillis()
                    val millis = if (ts < 10000000000L) ts * 1000 else ts
                    val formattedDate = try {
                        val instant = Instant.ofEpochMilli(millis)
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z").withZone(ZoneId.systemDefault())
                        formatter.format(instant)
                    } catch (e: Exception) {
                        "Invalid Timestamp"
                    }

                    Text("Converted Date & Time:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedDate,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 3. Current Time Format Preference
            Card(
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
                    Column {
                        Text("24-Hour Time Format", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Toggle between 12-hour and 24-hour display", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = state.is24HourFormat,
                        onCheckedChange = { viewModel.toggle24HourFormat(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
