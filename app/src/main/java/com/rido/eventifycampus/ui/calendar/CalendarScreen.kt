package com.rido.eventifycampus.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rido.eventifycampus.R
import com.rido.eventifycampus.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    onEventClick: (Event) -> Unit
) {
    val events = remember {
        listOf(
            Event("1", "Seminar Nasional Teknologi", "Seminar nasional tentang teknologi digital.", "12 Mei 2026", "09:00", "Aula Gedung A", "Himpunan Mahasiswa", "Seminar", R.drawable.img),
            Event("2", "Lomba Coding Se-Kampus", "Kompetisi pemrograman antar mahasiswa.", "20 Agustus 2026", "08:00", "Lab Komputer 3", "UKM Programming", "Lomba", R.drawable.img_1),
            Event("3", "Workshop UI/UX Design", "Pelatihan desain UI/UX untuk pemula.", "28 November 2026", "13:00", "Gedung Serbaguna", "Creative Community", "Workshop", R.drawable.img_2),
            Event("4", "Dies Natalis Campus", "Perayaan hari jadi kampus.", "28 November 2026", "19:00", "Lapangan Utama", "Rektorat", "Hiburan", R.drawable.img_3),
            Event("5", "Talkshow Karier Digital", "Talkshow karier dan portofolio digital.", "10 Desember 2026", "10:00", "Auditorium Kampus", "Career Development Center", "Talkshow", R.drawable.img_4)
        )
    }

    val months = listOf("Mei", "Agustus", "November", "Desember")
    var selectedMonthIndex by remember { mutableIntStateOf(0) }
    val selectedMonth = months[selectedMonthIndex]

    val selectedEvents = events.filter {
        it.date.contains(selectedMonth, ignoreCase = true)
    }

    val eventDays = selectedEvents.mapNotNull { getDayFromDate(it.date) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Kalender Acara", fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = {
                                    if (selectedMonthIndex > 0) selectedMonthIndex--
                                }
                            ) {
                                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Sebelumnya")
                            }

                            Text(
                                text = "$selectedMonth 2026",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    if (selectedMonthIndex < months.lastIndex) selectedMonthIndex++
                                }
                            ) {
                                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Berikutnya")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CalendarGrid(
                            selectedMonth = selectedMonth,
                            eventDays = eventDays
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Event Bulan $selectedMonth",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (selectedEvents.isEmpty()) {
                item {
                    Text("Belum ada event di bulan ini.")
                }
            } else {
                items(selectedEvents) { event ->
                    CalendarEventCard(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    selectedMonth: String,
    eventDays: List<Int>
) {
    val daysOfWeek = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

    val totalDays = when (selectedMonth) {
        "Mei" -> 31
        "Agustus" -> 31
        "November" -> 30
        "Desember" -> 31
        else -> 30
    }

    val startOffset = when (selectedMonth) {
        "Mei" -> 4
        "Agustus" -> 6
        "November" -> 0
        "Desember" -> 1
        else -> 0
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val calendarCells = List(startOffset) { 0 } + (1..totalDays).toList()
        val rows = calendarCells.chunked(7)

        rows.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                week.forEach { day ->
                    CalendarDayItem(
                        day = day,
                        hasEvent = eventDays.contains(day),
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    day: Int,
    hasEvent: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(46.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (day != 0) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (hasEvent) MaterialTheme.colorScheme.primary else Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    color = if (hasEvent) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (hasEvent) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (hasEvent) {
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun CalendarEventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = event.imageRes),
                contentDescription = event.title,
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${event.date} • ${event.time}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

fun getDayFromDate(date: String): Int? {
    return date.split(" ").firstOrNull()?.toIntOrNull()
}