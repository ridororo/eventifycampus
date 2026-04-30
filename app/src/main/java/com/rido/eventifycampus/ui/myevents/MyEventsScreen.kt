package com.rido.eventifycampus.ui.myevents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rido.eventifycampus.model.Event
import com.rido.eventifycampus.ui.components.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    modifier: Modifier = Modifier,
    onEventClick: (Event) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Akan Datang", "Selesai")

    val registeredEvents = remember {
        listOf(
            Event(
                "1",
                "Seminar Nasional Teknologi",
                "Deskripsi seminar...",
                "12 Mei 2024",
                "09:00",
                "Aula Gedung A",
                "Himpunan Mahasiswa",
                "Seminar",
                isRegistered = true
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Event Saya") })
        }
    ) { padding ->
        Column(modifier = modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                if (selectedTab == 0) {
                    items(registeredEvents) { event ->
                        EventCard(event = event, onClick = { onEventClick(event) })
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada event yang selesai.")
                        }
                    }
                }
            }
        }
    }
}