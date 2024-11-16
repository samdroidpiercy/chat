package com.example.muzz.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.muzz.data.Message
import com.example.muzz.data.MessageRepository
import com.example.muzz.data.MessageStatus
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val instantExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ChatViewModel
    private lateinit var repository: MessageRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk{
            every { allMessages } returns mockk(relaxed = true)
        }

        viewModel = ChatViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendMessage calls insertMessage on repository`() = runTest {
        // Arrange
        val messageContent = "Hello, World!"
        val messageSlot = slot<Message>()

        coEvery { repository.insertMessage(capture(messageSlot)) } just Runs

        // Act
        viewModel.sendMessage(messageContent)
        advanceUntilIdle()

        // Assert
        coVerify { repository.insertMessage(any()) }
        Assert.assertEquals(messageContent, messageSlot.captured.content)
        Assert.assertEquals(MessageStatus.SENT, messageSlot.captured.status)
    }

    @Test
    fun `messages LiveData observes repository allMessages`() {

        // Arrange
        val messages = listOf(
            Message(content = "Hi!", status = MessageStatus.SENT, timestamp = System.currentTimeMillis()),
            Message(content = "Hello!", status = MessageStatus.RECEIVED, timestamp = System.currentTimeMillis())
        )

        repository = mockk{
            every { allMessages } returns MutableLiveData(messages)
        }

        viewModel = ChatViewModel(repository) //re-instantiate viewmodel to refresh all messages




        val observer = mockk<Observer<List<Message>>>(relaxed = true)

        // Act
        viewModel.messages.observeForever(observer)

        // Assert
        verify { observer.onChanged(messages) }
    }
}