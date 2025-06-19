package com.eventmanagement.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.eventmanagement.app.R
import com.eventmanagement.app.databinding.FragmentProfileBinding
import com.eventmanagement.app.ui.activities.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupProfile()
        setupClickListeners()
    }

    private fun setupProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.tvName.text = currentUser.displayName
            binding.tvEmail.text = currentUser.email

            // Load profile image if exists
            currentUser.photoUrl?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_person)
                    .circleCrop()
                    .into(binding.ivProfile)
            }

            // Load user's events
            loadUserEvents(currentUser.uid)
        }
    }

    private fun setupClickListeners() {
        // Edit Profile
        binding.btnEditProfile.setOnClickListener {
            // TODO: Navigate to edit profile screen
        }

        // My Events
        binding.cardMyEvents.setOnClickListener {
            // TODO: Navigate to user's events screen
        }

        // My Tickets
        binding.cardMyTickets.setOnClickListener {
            // TODO: Navigate to user's tickets screen
        }

        // Settings
        binding.cardSettings.setOnClickListener {
            // TODO: Navigate to settings screen
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            // Navigate to auth screen
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun loadUserEvents(userId: String) {
        firestore.collection("events")
            .whereEqualTo("organizerId", userId)
            .get()
            .addOnSuccessListener { documents ->
                binding.tvEventCount.text = documents.size().toString()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
