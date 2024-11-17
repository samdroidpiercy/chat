package com.example.muzz.ui.screens

import ChatScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.muzz.data.Message
import com.example.muzz.data.MessageDao
import com.example.muzz.data.MessageRepositoryImpl
import com.example.muzz.data.MessageStatus
import com.example.muzz.ui.viewmodel.ChatViewModel
import com.example.muzz.ui.viewmodel.MessageUIState
import com.example.muzz.util.DefaultTimeProvider

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(viewModel = FakeChatViewModel(), paddingValues = PaddingValues())
}

class FakeChatViewModel : ChatViewModel(repository = MessageRepositoryImpl(FakeMessagesDao()), timeProvider = DefaultTimeProvider()) {
    override val uiState: LiveData<List<MessageUIState>> = MutableLiveData(
        listOf(
            MessageUIState(Message(1, "Hello!", MessageStatus.SENT, System.currentTimeMillis() - 3), "Today 10:30", true),
            MessageUIState(Message(1, "Hello!", MessageStatus.RECEIVED, System.currentTimeMillis() - 3), null, true),
            MessageUIState(Message(1, "Hello!", MessageStatus.SENT, System.currentTimeMillis() - 3), null, true),
            MessageUIState(Message(1, "Hello!", MessageStatus.RECEIVED, System.currentTimeMillis() - 3), null, true),
        )
    )

    override fun sendMessage(content: String) {
        //no op for preview
    }

}

private fun FakeMessagesDao() = object : MessageDao {
    override fun getAllMessages(): LiveData<List<Message>> = MutableLiveData()

    override suspend fun insertMessage(message: Message) {
    }

}