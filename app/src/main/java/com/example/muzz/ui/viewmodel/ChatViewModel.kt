package com.example.muzz.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzz.data.Message
import com.example.muzz.data.MessageRepository
import com.example.muzz.data.MessageStatus
import com.example.muzz.util.TimeProvider
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
    private val repository: MessageRepository,
    private val timeProvider: TimeProvider
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

    private var lastSentTimestamp: Long = 0L // Tracks the last sent message's timestamp


    init {
        viewModelScope.launch {
            // Observe messages and map them to UI state
            repository.allMessages.observeForever { messages ->
                _uiState.value = transformMessagesToUIState(messages)
            }
        }
    }

    /**
     * Sends a message with the given content and status.
     *
     * It then schedules schedules an auto-reply
     *
     * @param content The text content of the message.
     */
    open fun sendMessage(content: String) {
        viewModelScope.launch {
            val now = timeProvider.currentTimeMillis()
            lastSentTimestamp = now

            val message = Message(
                content = content,
                status = MessageStatus.SENT,
                timestamp = now
            )
            repository.insertMessage(message)

            scheduleAutoReply(content)
        }
    }

    /**
     * @param sentMessage The content of the message sent by the user
     * Sends a scrambled version of the last message in response to a sent message,
     * it waits for some time before sending and will not send if the user sends a message in the
     * meantime
     */
    private fun scheduleAutoReply(sentMessage: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(FIVE_SECONDS_IN_MS) // Wait 5 seconds

            // Check if another message was sent within the last 5 seconds
            val now = timeProvider.currentTimeMillis()

            if (now - lastSentTimestamp >= FIVE_SECONDS_IN_MS) {
                val scrambledMessage = sentMessage.toCharArray().apply {
                    shuffle()
                }.concatToString()

                val reply = Message(
                    content = scrambledMessage,
                    status = MessageStatus.RECEIVED,
                    timestamp = now
                )
                repository.insertMessage(reply)
            }
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

            uiStateList.add(
                MessageUIState(
                    message,
                    timestamp,
                    smallGap
                )
            )
        }

        return uiStateList
    }

    /**
     * @param current message being processed
     * @param previous previous message if there is one
     * @return Boolean indicating a smaller gap between messages should be shown if
     * the message was sent by the same user quickly after the last- or if first message as gap to
     * much at top otherwise
     */
    private fun shouldTightenGap(current: Message, previous: Message?): Boolean {
        return previous == null || current.status == previous.status && (current.timestamp - previous.timestamp) < TWENTY_SECONDS_IN_MS
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
        const val FIVE_SECONDS_IN_MS = 5000L
    }

}