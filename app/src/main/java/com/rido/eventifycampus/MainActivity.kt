package com.rido.eventifycampus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
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
import com.rido.eventifycampus.ui.EventViewModel
import com.rido.eventifycampus.ui.auth.LoginScreen
import com.rido.eventifycampus.ui.auth.RegisterScreen
import com.rido.eventifycampus.ui.calendar.CalendarScreen
import com.rido.eventifycampus.ui.event.BiodataScreen
import com.rido.eventifycampus.ui.event.EventDetailScreen
import com.rido.eventifycampus.ui.home.HomeScreen
import com.rido.eventifycampus.ui.myevents.MyEventsScreen
import com.rido.eventifycampus.ui.notification.NotificationScreen
import com.rido.eventifycampus.ui.profile.ProfileScreen
import com.rido.eventifycampus.ui.splash.SplashScreen
import com.rido.eventifycampus.ui.theme.EventifycampusTheme

class MainActivity : ComponentActivity() {

    private val eventViewModel: EventViewModel by viewModels()

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

                NavHost(navController = navController, startDestination = "splash") {

                    composable("splash") {
                        SplashScreen(
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { name ->
                                userName = name
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
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
                            allEvents = eventViewModel.allEvents,
                            registeredEvents = eventViewModel.registeredEvents,
                            finishedEvents = eventViewModel.finishedEvents,
                            onEventClick = { event ->
                                selectedEvent = event
                                navController.navigate("detail")
                            },
                            onNotificationClick = {
                                navController.navigate("notifications")
                            },
                            onLogout = {
                                navController.navigate("splash") {
                                    popUpTo(0)
                                }
                            }
                        )
                    }

                    composable("notifications") {
                        NotificationScreen(
                            notifications = eventViewModel.notifications,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onDeleteAllClick = {
                                eventViewModel.clearNotifications()
                            }
                        )
                    }

                    composable("detail") {
                        selectedEvent?.let { event ->
                            val isRegisteredInVM = eventViewModel.registeredEvents.any { it.id == event.id }
                            val isFinishedInVM = eventViewModel.finishedEvents.any { it.id == event.id }
                            
                            val displayEvent = event.copy(isRegistered = isRegisteredInVM)

                            EventDetailScreen(
                                event = displayEvent,
                                isFinished = isFinishedInVM,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onRegisterClick = {
                                    navController.navigate("biodata")
                                },
                                onCompleteClick = {
                                    eventViewModel.completeEvent(displayEvent)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    composable("biodata") {
                        selectedEvent?.let { event ->
                            BiodataScreen(
                                eventTitle = event.title,
                                onSubmitClick = {
                                    eventViewModel.registerForEvent(event)

                                    showNotificationIfAllowed(
                                        title = "Pendaftaran Berhasil!",
                                        message = "Kamu sudah terdaftar di ${event.title}"
                                    )

                                    NotificationHelper.scheduleReminderSimple(
                                        context = this@MainActivity,
                                        title = "Pengingat Acara",
                                        message = "Jangan lupa hadir di ${event.title}!",
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
    allEvents: List<Event>,
    registeredEvents: List<Event>,
    finishedEvents: List<Event>,
    onEventClick: (Event) -> Unit,
    onNotificationClick: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf("Beranda", "Kalender", "Acara Saya", "Profil")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.DateRange,
        Icons.AutoMirrored.Filled.List,
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
                events = allEvents,
                onEventClick = onEventClick,
                onNotificationClick = onNotificationClick
            )

            1 -> CalendarScreen(
                modifier = modifier,
                events = allEvents,
                onEventClick = onEventClick
            )

            2 -> MyEventsScreen(
                modifier = modifier,
                registeredEvents = registeredEvents,
                finishedEvents = finishedEvents,
                onEventClick = onEventClick
            )

            3 -> ProfileScreen(
                modifier = modifier,
                onLogoutClick = onLogout
            )
        }
    }
}
