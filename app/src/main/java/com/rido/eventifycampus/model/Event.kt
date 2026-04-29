package com.rido.eventifycampus.model

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val organizer: String,
    val category: String,
    val imageUrl: String = "",
    val isPopular: Boolean = false,
    val isRegistered: Boolean = false
)
