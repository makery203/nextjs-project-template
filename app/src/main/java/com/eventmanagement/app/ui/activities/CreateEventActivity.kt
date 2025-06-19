package com.eventmanagement.app.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.eventmanagement.app.R
import com.eventmanagement.app.databinding.ActivityCreateEventBinding
import com.eventmanagement.app.models.Event
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class CreateEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEventBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var selectedImageUri: Uri? = null
    private var selectedDateTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategorySpinner()
        setupDateTimePickers()
        setupImagePicker()
        setupSaveButton()
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.event_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupDateTimePickers() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, month)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.etDate.setText("$dayOfMonth/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDateTime.set(Calendar.MINUTE, minute)
                binding.etTime.setText(String.format("%02d:%02d", hourOfDay, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun setupImagePicker() {
        binding.ivEventImage.setOnClickListener {
            // TODO: Implement image picker intent
            Toast.makeText(this, "Image picker not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val category = binding.spinnerCategory.selectedItem.toString()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val event = Event(
                title = title,
                description = description,
                dateTime = Timestamp(selectedDateTime.time),
                category = category,
                organizerId = auth.currentUser?.uid ?: "",
                imageUrl = "" // Will be updated after image upload
            )

            saveEvent(event)
        }
    }

    private fun saveEvent(event: Event) {
        firestore.collection("events")
            .add(event)
            .addOnSuccessListener {
                Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create event: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
