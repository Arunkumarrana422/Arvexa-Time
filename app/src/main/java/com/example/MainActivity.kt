package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.about.AboutScreen
import com.example.ui.home.HomeScreen
import com.example.ui.sessions.SavedSessionsScreen
import com.example.ui.sessions.SessionsViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.splash.SplashScreen
import com.example.ui.stopwatch.StopwatchScreen
import com.example.ui.stopwatch.StopwatchViewModel
import com.example.ui.theme.ArvexaTimeTheme
import com.example.ui.timer.TimerScreen
import com.example.ui.timer.TimerViewModel
import com.example.ui.tools.TimeToolsScreen
import com.example.ui.tools.TimeToolsViewModel
import com.example.ui.worldclock.WorldClockScreen
import com.example.ui.worldclock.WorldClockViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.state.collectAsState()

            ArvexaTimeTheme(darkTheme = settingsState.isDarkMode) {
                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    MainAppScaffold()
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Stopwatch : Screen("stopwatch", "Stopwatch", Icons.Default.Watch)
    object Timer : Screen("timer", "Timer", Icons.Default.Timer)
    object Tools : Screen("tools", "Tools", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Stopwatch, Screen.Timer, Screen.Tools)

    val stopwatchViewModel: StopwatchViewModel = viewModel()
    val timerViewModel: TimerViewModel = viewModel()
    val worldClockViewModel: WorldClockViewModel = viewModel()
    val timeToolsViewModel: TimeToolsViewModel = viewModel()
    val sessionsViewModel: SessionsViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Only show bottom navigation on main tabs
            if (currentRoute in items.map { it.route }) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(Screen.Stopwatch.route) {
                StopwatchScreen(viewModel = stopwatchViewModel)
            }
            composable(Screen.Timer.route) {
                TimerScreen(viewModel = timerViewModel)
            }
            composable(Screen.Tools.route) {
                // Tools tab can go to TimeTools, and also access World Clock, Saved Sessions, Settings
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Time Utilities & Settings", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("tools_screen") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Time Converters & Timestamp Tools")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { navController.navigate("worldclock") }, modifier = Modifier.fillMaxWidth()) {
                        Text("World Clock")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { navController.navigate("sessions") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Saved Stopwatch & Timer Sessions")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { navController.navigate("settings") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Settings & Preferences")
                    }
                }
            }
            composable("tools_screen") {
                TimeToolsScreen(viewModel = timeToolsViewModel)
            }
            composable("worldclock") {
                WorldClockScreen(viewModel = worldClockViewModel)
            }
            composable("sessions") {
                SavedSessionsScreen(viewModel = sessionsViewModel)
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToAbout = { navController.navigate("about") }
                )
            }
            composable("about") {
                AboutScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
