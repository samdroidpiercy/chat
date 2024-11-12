package com.example.muzz.data
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessageRepository(private val messageDao: MessageDao) {
    val allMessages: LiveData<List<Message>> = messageDao.getAllMessages()

    suspend fun insertMessage(message: Message) {
        withContext(Dispatchers.IO) {
            messageDao.insertMessage(message)
        }
    }
}