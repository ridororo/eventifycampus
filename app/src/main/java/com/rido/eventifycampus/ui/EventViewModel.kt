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
        repository.getEvents { events ->
            _allEvents.clear()
            _allEvents.addAll(events)
            
            // Selalu panggil seed agar data di Firebase sinkron dengan data Indonesia
            seedInitialData()
        }

        repository.getRegisteredEvents { events ->
            _registeredEvents.clear()
            _registeredEvents.addAll(events)
        }

        repository.getFinishedEvents { events ->
            _finishedEvents.clear()
            _finishedEvents.addAll(events)
        }

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
            ),
            Event(
                id = "3",
                title = "Kampus Expo 2026",
                description = "Pameran karya inovasi mahasiswa dan bursa kerja perusahaan ternama.",
                date = "22 Juli 2026",
                time = "08:00 - 17:00",
                location = "Lapangan Utama",
                organizer = "BEM Universitas",
                category = "Pameran",
                imageRes = R.drawable.img_2,
                isPopular = true
            ),
            Event(
                id = "4",
                title = "Kompetisi Pemrograman",
                description = "Tantangan 24 jam membangun solusi digital kreatif untuk masalah sosial.",
                date = "05 Agustus 2026",
                time = "09:00",
                location = "Gedung Robotika",
                organizer = "Himpunan Mahasiswa Elektro",
                category = "Lomba",
                imageRes = R.drawable.img_3,
                isPopular = false
            ),
            Event(
                id = "5",
                title = "Bincang Karir: Pekerjaan Masa Depan",
                description = "Persiapkan dirimu menghadapi dunia kerja di era industri 4.0 dan ekonomi digital.",
                date = "12 September 2026",
                time = "10:00 - 12:00",
                location = "Auditorium Lantai 2",
                organizer = "Pusat Karir",
                category = "Talkshow",
                imageRes = R.drawable.img_4,
                isPopular = true
            )
        )
        repository.seedEvents(dummyEvents)
    }

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
