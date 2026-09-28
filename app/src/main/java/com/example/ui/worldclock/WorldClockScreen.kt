package com.example.ui.worldclock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WbSunny
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
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldClockScreen(viewModel: WorldClockViewModel) {
    val clocks by viewModel.worldClocks.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("World Clock", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add City")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(clocks, key = { it.id }) { clock ->
                    val zoneId = try { ZoneId.of(clock.timeZoneId) } catch (e: Exception) { ZoneId.systemDefault() }
                    val nowInZone = ZonedDateTime.now(zoneId)
                    val localNow = ZonedDateTime.now(ZoneId.systemDefault())

                    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
                    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

                    val hour = nowInZone.hour
                    val isDaytime = hour in 6..18
                    val diffHours = java.time.Duration.between(localNow.toInstant(), nowInZone.toInstant()).toHours()
                    val diffStr = if (diffHours >= 0) "+$diffHours hrs" else "$diffHours hrs"

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
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isDaytime) Icons.Default.WbSunny else Icons.Default.Brightness3,
                                        contentDescription = "Day/Night",
                                        tint = if (isDaytime) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = clock.cityName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${clock.countryName} • $diffStr",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = nowInZone.format(dateFormatter),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = nowInZone.format(timeFormatter),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { viewModel.deleteCity(clock.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete City", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        val filteredCities = viewModel.availableCities.filter {
            it.cityName.contains(searchQuery, ignoreCase = true) || it.countryName.contains(searchQuery, ignoreCase = true)
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add City to World Clock") },
            text = {
                Column(modifier = Modifier.height(300.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search City") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredCities) { city ->
                            TextButton(
                                onClick = {
                                    viewModel.addCity(city)
                                    showAddDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(city.cityName, fontWeight = FontWeight.Bold)
                                    Text(city.countryName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
