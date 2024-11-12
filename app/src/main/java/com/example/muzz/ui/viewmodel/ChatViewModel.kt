package com.example.muzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzz.data.Message
import com.example.muzz.data.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {
    val messages = repository.allMessages

    fun sendMessage(content: String, sender: String = "user") {
        viewModelScope.launch {
            val message = Message(content = content, sender = sender, timestamp = System.currentTimeMillis())
            repository.insertMessage(message)
        }
    }
}