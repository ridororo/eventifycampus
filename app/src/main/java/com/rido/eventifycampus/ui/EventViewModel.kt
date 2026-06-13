package com.rido.eventifycampus.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.rido.eventifycampus.model.AppNotification
import com.rido.eventifycampus.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventViewModel : ViewModel() {
    private val _registeredEvents = mutableStateListOf<Event>()
    val registeredEvents: List<Event> = _registeredEvents

    private val _finishedEvents = mutableStateListOf<Event>()
    val finishedEvents: List<Event> = _finishedEvents

    private val _notifications = mutableStateListOf<AppNotification>()
    val notifications: List<AppNotification> = _notifications

    fun registerForEvent(event: Event) {
        if (!_registeredEvents.any { it.id == event.id } && !_finishedEvents.any { it.id == event.id }) {
            _registeredEvents.add(event.copy(isRegistered = true))
            
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            addNotification(
                AppNotification(
                    id = System.currentTimeMillis().toString(),
                    title = "Pendaftaran Berhasil!",
                    message = "Kamu telah terdaftar di event ${event.title}",
                    time = currentTime
                )
            )
        }
    }

    fun completeEvent(event: Event) {
        val eventToComplete = _registeredEvents.find { it.id == event.id }
        if (eventToComplete != null) {
            _registeredEvents.remove(eventToComplete)
            _finishedEvents.add(eventToComplete)
            
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            addNotification(
                AppNotification(
                    id = System.currentTimeMillis().toString(),
                    title = "Event Selesai!",
                    message = "Selamat! Kamu telah menyelesaikan event ${event.title}",
                    time = currentTime
                )
            )
        }
    }

    private fun addNotification(notification: AppNotification) {
        _notifications.add(0, notification) // Add to top
    }
    
    fun clearNotifications() {
        _notifications.clear()
    }
}