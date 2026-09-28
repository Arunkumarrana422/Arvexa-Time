package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(key1 = true) {
        while (true) {
            currentTime = LocalTime.now()
            currentDate = LocalDate.now()
            delay(1000)
        }
    }

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a")
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")

    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Arvexa Time", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Main Time Display Hero Card (Glass Effect in Dark Mode)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
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
                            text = currentTime.format(timeFormatter),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentDate.format(dateFormatter),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Shortcuts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shortcuts Grid with Glass Effect Cards in Dark Mode
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShortcutCard(
                        title = "Stopwatch",
                        subtitle = "High precision",
                        icon = Icons.Default.Watch,
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("stopwatch") }
                    )
                    ShortcutCard(
                        title = "Timer",
                        subtitle = "Presets & custom",
                        icon = Icons.Default.Timer,
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("timer") }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShortcutCard(
                        title = "World Clock",
                        subtitle = "Global time zones",
                        icon = Icons.Default.Public,
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("worldclock") }
                    )
                    ShortcutCard(
                        title = "Time Tools",
                        subtitle = "Converters & diff",
                        icon = Icons.Default.Build,
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("tools") }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShortcutCard(
                        title = "Saved Sessions",
                        subtitle = "History & records",
                        icon = Icons.Default.Bookmark,
                        isDark = isDark,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigate("sessions") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    val border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

    Card(
        onClick = onClick,
        modifier = modifier.height(88.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.2f else 0.12f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}
