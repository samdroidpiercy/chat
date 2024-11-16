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

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(viewModel = FakeChatViewModel(), paddingValues = PaddingValues())
}

class FakeChatViewModel : ChatViewModel(repository = MessageRepositoryImpl(FakeMessagesDao())) {
    override val messages: LiveData<List<Message>> = MutableLiveData(
        listOf(
            Message(1, "Hello!", MessageStatus.SENT, System.currentTimeMillis() - 3),
            Message(2, "Hi there!", MessageStatus.RECEIVED, System.currentTimeMillis() - 2),
            Message(3, "How's it going?", MessageStatus.SENT, System.currentTimeMillis() - 1),
            Message(4, "Good! And you?",  MessageStatus.RECEIVED, System.currentTimeMillis()),
        )
    )

    override fun sendMessage(content: String, status: MessageStatus) {
        //no op for preview
    }

}

private fun FakeMessagesDao() = object : MessageDao {
    override fun getAllMessages(): LiveData<List<Message>> = MutableLiveData()

    override suspend fun insertMessage(message: Message) {
    }

}