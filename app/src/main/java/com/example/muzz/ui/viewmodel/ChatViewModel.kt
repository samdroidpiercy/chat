package com.example.muzz.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzz.data.Message
import com.example.muzz.data.MessageRepository
import com.example.muzz.data.MessageStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
     * LiveData object that holds the message ui states.
     *
     * Observed by the UI to display the current list of messages in real-time.
     * The data comes from the repository and is updated whenever the database changes.
     */
    private val _uiState = MutableLiveData<List<MessageUIState>>()
    open val uiState: LiveData<List<MessageUIState>> = _uiState

    private val timeFormatter = SimpleDateFormat("EEEE HH:mm", Locale.getDefault())

    init {
        viewModelScope.launch {
            // Observe messages and map them to UI state
            repository.allMessages.observeForever { messages ->
                _uiState.value = transformMessagesToUIState(messages)
            }
        }
    }

    open fun sendMessage(content: String, status: MessageStatus = MessageStatus.SENT) {
        viewModelScope.launch {
            val message = Message(
                content = content,
                status = status,
                timestamp = System.currentTimeMillis()
            )
            repository.insertMessage(message)
        }
    }

    /**
     * @param messages The list of messages to transform.
     * @return A list of [MessageUIState] representing the transformed messages, adding timestamps
     * and gaps where appropriate.
     */
    private fun transformMessagesToUIState(messages: List<Message>): List<MessageUIState> {
        val uiStateList = mutableListOf<MessageUIState>()

        messages.forEachIndexed { index, message ->
            val previousMessage = messages.getOrNull(index - 1)

            val smallGap = shouldTightenGap(message, previousMessage)
            val timestamp = if (shouldShowTimestamp(message, previousMessage)) {
                timeFormatter.format(Date(message.timestamp))
            } else null

            uiStateList.add(MessageUIState(message.copy(content = "these are just a load of really ;long messages for testing purposes"), timestamp, smallGap))
        }

        return uiStateList
    }

    /**
     * @param current message being processed
     * @param previous previous message if there is one
     * @return Boolean indicating a smaller gap between messages should be shown if
     * the message was sent by the same user quickly after the last
     */
    private fun shouldTightenGap(current: Message, previous: Message?): Boolean {
        if (previous == null) return false
        return current.status == previous.status && (current.timestamp - previous.timestamp) < TWENTY_SECONDS_IN_MS
    }

    /**
     * @param current message being processed
     * @param previous previous message if there is one
     * @return Boolean indicating a timestamp should be shown above the message, if there is no
     * previous message or it was sent over an hour before the current message.
     */
    private fun shouldShowTimestamp(current: Message, previous: Message?) =
        (previous == null) || current.timestamp - previous.timestamp > ONE_HOUR_IN_MS


    companion object {
        const val TWENTY_SECONDS_IN_MS = 20 * 1000
        const val ONE_HOUR_IN_MS = 60 * 60 * 1000
    }

}