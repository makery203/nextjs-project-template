package com.eventmanagement.app.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Event(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var dateTime: Timestamp? = null,
    var location: GeoPoint? = null,
    var category: String = "",
    var organizerId: String = "",
    var imageUrl: String = "",
    var status: String = "active"
)
