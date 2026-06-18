package com.rido.eventifycampus.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.rido.eventifycampus.R
import com.rido.eventifycampus.data.EventRepository
import com.rido.eventifycampus.model.AppNotification
import com.rido.eventifycampus.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    private val _allEvents = mutableStateListOf<Event>()
    val allEvents: List<Event> = _allEvents

    private val _registeredEvents = mutableStateListOf<Event>()
    val registeredEvents: List<Event> = _registeredEvents

    private val _finishedEvents = mutableStateListOf<Event>()
    val finishedEvents: List<Event> = _finishedEvents

    private val _notifications = mutableStateListOf<AppNotification>()
    val notifications: List<AppNotification> = _notifications

    init {
        fetchData()
    }

    private fun fetchData() {
        // Mengambil daftar semua event
        repository.getEvents { events ->
            _allEvents.clear()
            _allEvents.addAll(events)
            
            // LOGIKA PERBAIKAN: 
            // Hanya isi data dummy jika di Firebase benar-benar kosong (0 event)
            if (events.isEmpty()) {
                seedInitialData()
            }
        }

        // Mengambil event yang didaftar oleh user
        repository.getRegisteredEvents { events ->
            _registeredEvents.clear()
            _registeredEvents.addAll(events)
        }

        // Mengambil event yang sudah diselesaikan
        repository.getFinishedEvents { events ->
            _finishedEvents.clear()
            _finishedEvents.addAll(events)
        }

        // Mengambil daftar notifikasi
        repository.getNotifications { notifications ->
            _notifications.clear()
            _notifications.addAll(notifications)
        }
    }

    private fun seedInitialData() {
        val dummyEvents = listOf(
            Event(
                id = "1",
                title = "Seminar Nasional Teknologi",
                description = "Membahas masa depan Kecerdasan Buatan (AI) dan Komputasi Awan di industri Indonesia.",
                date = "15 Mei 2026",
                time = "09:00 - 12:00",
                location = "Aula Utama Kampus",
                organizer = "Himpunan Mahasiswa TI",
                category = "Seminar",
                imageRes = R.drawable.img,
                isPopular = true
            ),
            Event(
                id = "2",
                title = "Lokakarya Pengembangan Mobile",
                description = "Belajar membuat aplikasi Android modern menggunakan Jetpack Compose dari dasar.",
                date = "10 Juni 2026",
                time = "13:00 - 16:00",
                location = "Lab Komputer 3",
                organizer = "Google Developer Students Club",
                category = "Workshop",
                imageRes = R.drawable.img_1,
                isPopular = false
            )
        )
        repository.seedEvents(dummyEvents)
    }

    // Fungsi untuk mendaftar event
    fun registerForEvent(event: Event) {
        repository.registerForEvent(event) { success ->
            if (success) {
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                repository.addNotification(
                    AppNotification(
                        id = System.currentTimeMillis().toString(),
                        title = "Pendaftaran Berhasil!",
                        message = "Kamu telah terdaftar di acara ${event.title}",
                        time = currentTime
                    )
                )
            }
        }
    }

    // Fungsi untuk menyelesaikan event
    fun completeEvent(event: Event) {
        repository.completeEvent(event) { success ->
            if (success) {
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                repository.addNotification(
                    AppNotification(
                        id = System.currentTimeMillis().toString(),
                        title = "Acara Selesai!",
                        message = "Selamat! Kamu telah menyelesaikan acara ${event.title}",
                        time = currentTime
                    )
                )
            }
        }
    }

    fun clearNotifications() {
        repository.clearNotifications()
    }
}
