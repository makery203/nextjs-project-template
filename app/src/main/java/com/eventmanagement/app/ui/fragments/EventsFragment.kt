package com.eventmanagement.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eventmanagement.app.databinding.FragmentEventsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        loadEvents()
    }

    private fun setupViews() {
        // Setup FAB for creating new event
        binding.fabCreateEvent.setOnClickListener {
            val intent = android.content.Intent(requireContext(), com.eventmanagement.app.ui.activities.CreateEventActivity::class.java)
            startActivity(intent)
        }

        // Setup SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener {
            loadEvents()
        }

        // Setup RecyclerView
        binding.recyclerEvents.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = EventAdapter(emptyList()) { event ->
                val intent = android.content.Intent(requireContext(), com.eventmanagement.app.ui.activities.EventDetailActivity::class.java)
                intent.putExtra("eventId", event.id)
                startActivity(intent)
            }
        }
    }

    private fun loadEvents() {
        binding.swipeRefresh.isRefreshing = true
        
        firestore.collection("events")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                binding.swipeRefresh.isRefreshing = false
                val events = documents.mapNotNull { it.toObject(com.eventmanagement.app.models.Event::class.java).apply { id = it.id } }
                (binding.recyclerEvents.adapter as? com.eventmanagement.app.ui.adapters.EventAdapter)?.updateEvents(events)
                binding.layoutEmpty.visibility = if (events.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
            .addOnFailureListener { e ->
                binding.swipeRefresh.isRefreshing = false
                android.widget.Toast.makeText(requireContext(), "Failed to load events: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
