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
    registeredEvents: List<Event>,
    finishedEvents: List<Event>,
    onEventClick: (Event) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Akan Datang", "Selesai")

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

            val currentList = if (selectedTab == 0) registeredEvents else finishedEvents
            val emptyMessage = if (selectedTab == 0) "Belum ada event yang didaftar." else "Belum ada event yang selesai."

            if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emptyMessage)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentList) { event ->
                        EventCard(event = event, onClick = { onEventClick(event) })
                    }
                }
            }
        }
    }
}