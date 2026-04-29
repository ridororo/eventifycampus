package com.rido.eventifycampus.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rido.eventifycampus.model.Event
import com.rido.eventifycampus.ui.components.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    onEventClick: (Event) -> Unit
) {
    val dummyEvents = remember {
        listOf(
            Event("1", "Seminar Nasional Teknologi", "Deskripsi seminar...", "12 Mei 2024", "09:00", "Aula Gedung A", "Himpunan Mahasiswa", "Seminar"),
            Event("2", "Lomba Coding Se-Kampus", "Deskripsi lomba...", "15 Mei 2024", "08:00", "Lab Komputer 3", "UKM Programming", "Lomba"),
            Event("4", "Dies Natalis Campus", "Deskripsi acara...", "25 Mei 2024", "19:00", "Lapangan Utama", "Rektorat", "Hiburan")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kalender Acara") })
        }
    ) { padding ->
        Column(modifier = modifier.padding(padding).padding(horizontal = 16.dp)) {
            // Simple Month Header
            Text(
                "Mei 2024",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn {
                items(dummyEvents) { event ->
                    EventCard(event = event, onClick = { onEventClick(event) })
                }
            }
        }
    }
}
