package com.example.muzz.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of the MessageRepository interface.
 * This class provides a concrete implementation for accessing and modifying messages
 * using a MessageDao (Data Access Object) to interact with the Room database.
 *
 * @property messageDao The DAO used to interact with the database for message operations.
 */
class MessageRepositoryImpl(private val messageDao: MessageDao) : MessageRepository {

    /**
     * A LiveData object representing all messages in the database.
     * Observers can use this property to listen for changes to the messages in real-time.
     */
    override val allMessages: LiveData<List<Message>> = messageDao.getAllMessages()

    /**
     * Inserts a message into the database.
     *
     * @param message The message object to be inserted.
     *
     * This method runs on the IO thread to avoid blocking the main thread,
     * as database operations can be time-consuming.
     */
    override suspend fun insertMessage(message: Message) {
        withContext(Dispatchers.IO) { // Switches to IO dispatcher for background operation
            messageDao.insertMessage(message)
        }
    }
}