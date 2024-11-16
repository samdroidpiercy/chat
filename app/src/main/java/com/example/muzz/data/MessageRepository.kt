package com.example.muzz.data

import androidx.lifecycle.LiveData

interface MessageRepository {

    val allMessages: LiveData<List<Message>>

    suspend fun insertMessage(message: Message)

}