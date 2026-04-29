package com.rido.eventifycampus.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rido.eventifycampus.model.Event
import com.rido.eventifycampus.ui.components.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onEventClick: (Event) -> Unit
) {
    val dummyEvents = remember {
        listOf(
            Event("1", "Seminar Nasional Teknologi", "Deskripsi seminar...", "12 Mei 2024", "09:00", "Aula Gedung A", "Himpunan Mahasiswa", "Seminar", isPopular = true),
            Event("2", "Lomba Coding Se-Kampus", "Deskripsi lomba...", "15 Mei 2024", "08:00", "Lab Komputer 3", "UKM Programming", "Lomba"),
            Event("3", "Workshop UI/UX Design", "Deskripsi workshop...", "20 Mei 2024", "13:00", "Gedung Serbaguna", "Creative Community", "Workshop", isPopular = true)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Halo, Mahasiswa!", style = MaterialTheme.typography.titleLarge)
                        Text("Cek event terbaru hari ini", style = MaterialTheme.typography.bodyMedium)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar Placeholder
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Cari event...") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Popular Events Section
            item {
                Text(
                    "Event Populer",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(dummyEvents.filter { it.isPopular }) { event ->
                        Box(modifier = Modifier.width(280.dp)) {
                            EventCard(event = event, onClick = { onEventClick(event) })
                        }
                    }
                }
            }

            // All Events Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Daftar Event",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(dummyEvents) { event ->
                EventCard(event = event, onClick = { onEventClick(event) })
            }
        }
    }
}
