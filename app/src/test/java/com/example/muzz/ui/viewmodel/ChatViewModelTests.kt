package com.example.muzz.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.muzz.data.MessageRepository
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class ChatViewModelTests {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val messageDao = FakeMessageDao() // Mock or fake DAO
    private val repository = MessageRepository(messageDao)
    private val viewModel = ChatViewModel(repository)

    @Test
    fun sendMessage_addsMessageToDatabase() {
        viewModel.sendMessage("Hello")
        val messages = viewModel.messages.getOrAwaitValue()
        assertTrue(messages.any { it.content == "Hello" && it.sender == "user" })
    }
}