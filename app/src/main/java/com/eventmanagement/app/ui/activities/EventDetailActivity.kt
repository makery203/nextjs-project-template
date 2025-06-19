package com.eventmanagement.app.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.eventmanagement.app.databinding.ActivityEventDetailBinding
import com.eventmanagement.app.models.Event
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getStringExtra("eventId")
        if (eventId != null) {
            loadEventDetails(eventId!!)
        } else {
            finish()
        }
    }

    private fun loadEventDetails(id: String) {
        firestore.collection("events").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val event = document.toObject(Event::class.java)
                    event?.let { displayEventDetails(it) }
                } else {
                    finish()
                }
            }
            .addOnFailureListener {
                finish()
            }
    }

    private fun displayEventDetails(event: Event) {
        binding.tvTitle.text = event.title
        binding.tvDescription.text = event.description
        binding.tvDateTime.text = event.dateTime?.toDate().toString()
        binding.tvCategory.text = event.category

        Glide.with(this)
            .load(event.imageUrl)
            .placeholder(com.eventmanagement.app.R.drawable.ic_event)
            .into(binding.ivEventImage)
    }
}
