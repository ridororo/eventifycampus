package com.rido.eventifycampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rido.eventifycampus.ui.auth.LoginScreen
import com.rido.eventifycampus.ui.auth.RegisterScreen
import com.rido.eventifycampus.ui.home.HomeScreen
import com.rido.eventifycampus.ui.calendar.CalendarScreen
import com.rido.eventifycampus.ui.myevents.MyEventsScreen
import com.rido.eventifycampus.ui.profile.ProfileScreen
import com.rido.eventifycampus.ui.event.EventDetailScreen
import com.rido.eventifycampus.ui.theme.EventifycampusTheme
import com.rido.eventifycampus.model.Event

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventifycampusTheme {
                val navController = rememberNavController()
                var selectedEvent by remember { mutableStateOf<Event?>(null) }
                
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                            onNavigateToRegister = { navController.navigate("register") }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = { navController.navigate("login") },
                            onNavigateToLogin = { navController.navigate("login") }
                        )
                    }
                    composable("home") {
                        MainScreenContainer(
                            onEventClick = { event ->
                                selectedEvent = event
                                navController.navigate("detail")
                            },
                            onLogout = { navController.navigate("login") { popUpTo(0) } }
                        )
                    }
                    composable("detail") {
                        selectedEvent?.let { event ->
                            EventDetailScreen(
                                event = event,
                                onBackClick = { navController.popBackStack() },
                                onRegisterClick = { /* Handle reg */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenContainer(
    onEventClick: (Event) -> Unit,
    onLogout: () -> Unit
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Beranda", "Kalender", "Event Saya", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.DateRange, Icons.Default.List, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (selectedItem) {
            0 -> HomeScreen(modifier = modifier, onEventClick = onEventClick)
            1 -> CalendarScreen(modifier = modifier, onEventClick = onEventClick)
            2 -> MyEventsScreen(modifier = modifier, onEventClick = onEventClick)
            3 -> ProfileScreen(modifier = modifier, onLogoutClick = onLogout)
        }
    }
}
