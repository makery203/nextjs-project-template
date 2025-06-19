package com.eventmanagement.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eventmanagement.app.databinding.FragmentChatBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupTabs()
        setupChats()
    }

    private fun setupTabs() {
        // Setup ViewPager with tabs for Private and Group chats
        val tabTitles = arrayOf("Private Chats", "Group Chats")
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private lateinit var messageAdapter: com.eventmanagement.app.ui.adapters.ChatMessageAdapter
    private val messages = mutableListOf<com.eventmanagement.app.models.ChatMessage>()

    private fun setupChats() {
        // Setup RecyclerView adapter
        messageAdapter = com.eventmanagement.app.ui.adapters.ChatMessageAdapter(messages)
        binding.recyclerMessages.adapter = messageAdapter
        binding.recyclerMessages.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        val currentUser = auth.currentUser?.uid ?: return

        // Load messages from Firestore
        firestore.collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    messages.clear()
                    messages.addAll(documents.mapNotNull { it.toObject(com.eventmanagement.app.models.ChatMessage::class.java).apply { id = it.id } })
                    messageAdapter.notifyDataSetChanged()
                    binding.recyclerMessages.scrollToPosition(messages.size - 1)
                }
            }

        // Send message button
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val message = com.eventmanagement.app.models.ChatMessage(
                    senderId = currentUser,
                    message = text,
                    timestamp = com.google.firebase.Timestamp.now()
                )
                firestore.collection("messages").add(message)
                binding.etMessage.setText("")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
