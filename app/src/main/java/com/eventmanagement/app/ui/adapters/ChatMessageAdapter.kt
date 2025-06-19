package com.eventmanagement.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventmanagement.app.databinding.ItemChatMessageBinding
import com.eventmanagement.app.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth

class ChatMessageAdapter(
    private var messages: List<ChatMessage>
) : RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    inner class MessageViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            val isCurrentUser = message.senderId == currentUserId
            binding.tvMessage.text = message.message
            binding.tvMessage.textAlignment = if (isCurrentUser) android.view.View.TEXT_ALIGNMENT_TEXT_END else android.view.View.TEXT_ALIGNMENT_TEXT_START
            // Additional styling can be added here for sender/receiver
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
