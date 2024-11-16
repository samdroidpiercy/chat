package com.example.muzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzz.data.Message
import com.example.muzz.data.MessageRepository
import com.example.muzz.data.MessageStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing chat messages in the application.
 *
 * @property repository The repository for accessing and managing message data.
 */
@HiltViewModel
open class ChatViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {

    /**
     * LiveData object that holds all messages.
     *
     * Observed by the UI to display the current list of messages in real-time.
     * The data comes from the repository and is updated whenever the database changes.
     */
    open val messages = repository.allMessages

    /**
     * Sends a new message by creating a Message object and inserting it into the repository.
     *
     * @param content The content of the message to be sent.
     * @param status The status of the message (default is SENT).
     *
     * This function runs inside a coroutine on the ViewModel's scope (`viewModelScope`),
     * ensuring that the database operation is executed off the main thread.
     */
    open fun sendMessage(content: String, status: MessageStatus = MessageStatus.SENT) {
        viewModelScope.launch {
            // Create a new message with the provided content, status, and the current timestamp
            val message = Message(content = content, status = status, timestamp = System.currentTimeMillis())
            // Insert the message into the repository
            repository.insertMessage(message)
        }
    }
}