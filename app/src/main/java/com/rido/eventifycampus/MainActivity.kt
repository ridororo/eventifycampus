package com.rido.eventifycampus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rido.eventifycampus.model.Event
import com.rido.eventifycampus.ui.auth.LoginScreen
import com.rido.eventifycampus.ui.auth.RegisterScreen
import com.rido.eventifycampus.ui.calendar.CalendarScreen
import com.rido.eventifycampus.ui.event.BiodataScreen
import com.rido.eventifycampus.ui.event.EventDetailScreen
import com.rido.eventifycampus.ui.home.HomeScreen
import com.rido.eventifycampus.ui.myevents.MyEventsScreen
import com.rido.eventifycampus.ui.profile.ProfileScreen
import com.rido.eventifycampus.ui.theme.EventifycampusTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)
        requestNotificationPermission()

        enableEdgeToEdge()

        setContent {
            EventifycampusTheme {
                val navController = rememberNavController()
                var selectedEvent by remember { mutableStateOf<Event?>(null) }
                var userName by remember { mutableStateOf("Mahasiswa") }

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { name ->
                                userName = name
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("login")
                            },
                            onNavigateToLogin = {
                                navController.navigate("login")
                            }
                        )
                    }

                    composable("home") {
                        MainScreenContainer(
                            userName = userName,
                            onEventClick = { event ->
                                selectedEvent = event
                                navController.navigate("detail")
                            },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            }
                        )
                    }

                    composable("detail") {
                        selectedEvent?.let { event ->
                            EventDetailScreen(
                                event = event,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onRegisterClick = {
                                    navController.navigate("biodata")
                                }
                            )
                        }
                    }

                    composable("biodata") {
                        selectedEvent?.let { event ->
                            BiodataScreen(
                                eventTitle = event.title,
                                onSubmitClick = {
                                    showNotificationIfAllowed(
                                        title = "Pendaftaran Berhasil!",
                                        message = "Kamu sudah terdaftar"
                                    )

                                    NotificationHelper.scheduleReminderSimple(
                                        context = this@MainActivity,
                                        title = "Reminder Event",
                                        message = "Jangan lupa hadir ya!",
                                        delayMillis = 20000
                                    )

                                    navController.popBackStack("home", false)
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    private fun showNotificationIfAllowed(title: String, message: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationHelper.showNotification(
                context = this,
                title = title,
                message = message
            )
        }
    }
}

@Composable
fun MainScreenContainer(
    userName: String,
    onEventClick: (Event) -> Unit,
    onLogout: () -> Unit
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf("Beranda", "Kalender", "Event Saya", "Profil")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.DateRange,
        Icons.Default.List,
        Icons.Default.Person
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = item
                            )
                        },
                        label = {
                            Text(item)
                        },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when (selectedItem) {
            0 -> HomeScreen(
                modifier = modifier,
                userName = userName,
                onEventClick = onEventClick
            )

            1 -> CalendarScreen(
                modifier = modifier,
                onEventClick = onEventClick
            )

            2 -> MyEventsScreen(
                modifier = modifier,
                onEventClick = onEventClick
            )

            3 -> ProfileScreen(
                modifier = modifier,
                onLogoutClick = onLogout
            )
        }
    }
}