package com.rido.eventifycampus.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rido.eventifycampus.R
import com.rido.eventifycampus.model.Event
import com.rido.eventifycampus.ui.components.EventCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String,
    onEventClick: (Event) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    val dummyEvents = remember {
        listOf(
            Event(
                "1",
                "Seminar Nasional Teknologi",
                "Seminar Nasional Teknologi merupakan acara edukatif yang membahas perkembangan teknologi terkini di era digital.",
                "12 Mei 2026",
                "09:00",
                "Aula Gedung A",
                "Himpunan Mahasiswa",
                "Seminar",
                R.drawable.img,
                isPopular = true
            ),
            Event(
                "2",
                "Lomba Coding Se-Kampus",
                "Lomba Coding Se-Kampus adalah kompetisi pemrograman yang menguji logika dan kreativitas mahasiswa.",
                "20 Agustus 2026",
                "08:00",
                "Lab Komputer 3",
                "UKM Programming",
                "Lomba",
                R.drawable.img_1
            ),
            Event(
                "3",
                "Workshop UI/UX Design",
                "Workshop UI/UX Design membahas desain antarmuka dan pengalaman pengguna.",
                "28 November 2026",
                "13:00",
                "Gedung Serbaguna",
                "Creative Community",
                "Workshop",
                R.drawable.img_2,
                isPopular = true
            ),
            Event(
                "4",
                "Dies Natalis Campus",
                "Dies Natalis Campus merupakan perayaan hari jadi kampus dengan hiburan dan seni.",
                "28 November 2026",
                "19:00",
                "Lapangan Utama",
                "Rektorat",
                "Hiburan",
                R.drawable.img_3,
                isPopular = true
            ),
            Event(
                "5",
                "Talkshow Karier Digital",
                "Talkshow Karier Digital membahas peluang kerja teknologi dan persiapan karier mahasiswa.",
                "10 Desember 2026",
                "10:00",
                "Auditorium Kampus",
                "Career Development Center",
                "Talkshow",
                R.drawable.img_4
            )
        )
    }

    val filteredEvents = if (searchText.isBlank()) {
        dummyEvents
    } else {
        dummyEvents.filter { event ->
            event.title.contains(searchText, ignoreCase = true) ||
                    event.description.contains(searchText, ignoreCase = true) ||
                    event.category.contains(searchText, ignoreCase = true) ||
                    event.location.contains(searchText, ignoreCase = true) ||
                    event.organizer.contains(searchText, ignoreCase = true) ||
                    event.date.contains(searchText, ignoreCase = true) ||
                    event.time.contains(searchText, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Halo, $userName!",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Cek event terbaru hari ini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifikasi"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Cari event...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Cari")
                },
                trailingIcon = {
                    if (searchText.isNotBlank()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        if (searchText.isBlank()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Event Populer",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(dummyEvents.filter { it.isPopular }) { event ->
                        Box(modifier = Modifier.width(280.dp)) {
                            EventCard(
                                event = event,
                                onClick = { onEventClick(event) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (searchText.isBlank()) "Daftar Event" else "Hasil Pencarian",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (filteredEvents.isEmpty()) {
            item {
                Text(
                    text = "Event tidak ditemukan.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        } else {
            items(filteredEvents) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}