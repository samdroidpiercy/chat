package com.example.muzz.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.muzz.data.Message
import com.example.muzz.data.MessageRepository
import com.example.muzz.data.MessageStatus
import com.example.muzz.util.DefaultTimeProvider
import com.example.muzz.util.TimeProvider
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.rules.TestRule
import java.util.Stack

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val instantExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ChatViewModel
    private lateinit var repository: MessageRepository
    private val testDispatcher = StandardTestDispatcher()
    private val liveDataMessages = MutableLiveData<List<Message>>(emptyList())
    private var currentTimeMillis = System.currentTimeMillis()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Mock the LiveData and repository
        repository = mockk {
            every { allMessages } returns liveDataMessages
        }

        viewModel = ChatViewModel(repository, DefaultTimeProvider())
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
    fun `uiState transforms messages with timestamp and smallGap correctly`() = runTest {
        // Arrange
        val now = System.currentTimeMillis()
        val lessThanTwentySecondsAgo = now - ChatViewModel.TWENTY_SECONDS_IN_MS + 1
        val oneHourAgo = now - ChatViewModel.ONE_HOUR_IN_MS - 1000 // Ensure it's over an hour ago

        val messages = listOf(
            Message(content = "Message 1", status = MessageStatus.SENT, timestamp = oneHourAgo),
            Message(
                content = "Message 2",
                status = MessageStatus.SENT,
                timestamp = lessThanTwentySecondsAgo
            ),
            Message(content = "Message 3", status = MessageStatus.SENT, timestamp = now)
        )

        // Attach an observer to uiState
        val observer = mockk<Observer<List<MessageUIState>>>(relaxed = true)
        val uiStateCaptures = mutableListOf<List<MessageUIState>>()
        every { observer.onChanged(capture(uiStateCaptures)) } answers { } // Capture all emissions
        viewModel.uiState.observeForever(observer)

        // Act: Trigger LiveData emission
        liveDataMessages.postValue(messages)

        // Assert: Verify the transformation logic
        Assert.assertTrue(uiStateCaptures.isNotEmpty())
        val uiStates = uiStateCaptures.last() // Check the last emitted value
        Assert.assertEquals(3, uiStates.size)

        // First message should have a timestamp and no smallGap
        Assert.assertNotNull(uiStates[0].timestamp)
        Assert.assertFalse(uiStates[0].smallGap)

        // Second message should have no timestamp and no smallGap
        Assert.assertNull(uiStates[1].timestamp)
        Assert.assertFalse(uiStates[1].smallGap)

        // Third message should have no timestamp and a smallGap
        Assert.assertNull(uiStates[2].timestamp)
        Assert.assertTrue(uiStates[2].smallGap)
    }

    @Test
    fun `uiState does not add timestamp for consecutive messages within one hour`() = runTest {
        // Arrange
        val now = System.currentTimeMillis()
        val thirtyMinutesAgo = now - (30 * 60 * 1000) // 30 minutes ago

        val messages = listOf(
            Message(
                content = "Message 1",
                status = MessageStatus.SENT,
                timestamp = thirtyMinutesAgo
            ),
            Message(content = "Message 2", status = MessageStatus.SENT, timestamp = now)
        )

        // Attach an observer to uiState
        val observer = mockk<Observer<List<MessageUIState>>>(relaxed = true)
        val uiStateCaptures = mutableListOf<List<MessageUIState>>()
        every { observer.onChanged(capture(uiStateCaptures)) } answers { }
        viewModel.uiState.observeForever(observer)

        // Act: Trigger LiveData emission
        liveDataMessages.postValue(messages)

        // Assert: Verify the transformation logic
        Assert.assertTrue(uiStateCaptures.isNotEmpty())
        val uiStates = uiStateCaptures.last() // Check the last emitted value
        Assert.assertEquals(2, uiStates.size)

        // First message should have a timestamp
        Assert.assertNotNull(uiStates[0].timestamp)

        // Second message should not have a timestamp
        Assert.assertNull(uiStates[1].timestamp)
    }

    @Test
    fun `uiState adds timestamp for first message`() = runTest {
        // Arrange
        val now = System.currentTimeMillis()
        val messages = listOf(
            Message(content = "Message 1", status = MessageStatus.SENT, timestamp = now)
        )

        // Attach an observer to uiState
        val observer = mockk<Observer<List<MessageUIState>>>(relaxed = true)
        val uiStateCaptures = mutableListOf<List<MessageUIState>>()
        every { observer.onChanged(capture(uiStateCaptures)) } answers { }
        viewModel.uiState.observeForever(observer)

        // Act: Trigger LiveData emission
        liveDataMessages.postValue(messages)

        // Assert: Verify the transformation logic
        Assert.assertTrue(uiStateCaptures.isNotEmpty())
        val uiStates = uiStateCaptures.last() // Check the last emitted value
        Assert.assertEquals(1, uiStates.size)

        // First message should have a timestamp
        Assert.assertNotNull(uiStates[0].timestamp)
    }

    @Test
    fun `sendMessage triggers scrambled auto-reply after 5 seconds of inactivity`() = runTest {

        viewModel = ChatViewModel(repository,  object: TimeProvider{
            override fun currentTimeMillis(): Long {
                //Roll the current time on by 5 seconds everytime
                currentTimeMillis += 5001
                return currentTimeMillis
            }

        })

        // Arrange
        val userMessage = "Hello World"
        val messageSlot = slot<Message>()
        coEvery { repository.insertMessage(capture(messageSlot)) } just Runs

        // Act
        viewModel.sendMessage(userMessage) // Trigger the auto-reply indirectly
        advanceTimeBy(5001) // Simulate 5 seconds passing
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 2) { repository.insertMessage(any()) } // One for the user's message, one for the reply

        val sentMessages = messageSlot.captured.content

        // Verify that the auto-reply is scrambled
        Assert.assertNotEquals(userMessage, sentMessages) // Scrambled message should differ
        Assert.assertEquals(userMessage.length, sentMessages.length) // Length should match
        Assert.assertTrue(sentMessages.toList().sorted() == userMessage.toList().sorted()) // Same characters
    }

    @Test
    fun `sendMessage cancels auto-reply if another message is sent within 5 seconds`() = runTest {

        val currentTimeStack = Stack<Long>()

        //set up time stack so gap between messages is less that 5 seconds, tricky logic this, as the
        //messages send before either autoreply block is reached
        currentTimeStack.push(90001L)
        currentTimeStack.push(5000L)
        currentTimeStack.push(4000L)
        currentTimeStack.push(1000L)

        viewModel = ChatViewModel(repository, object : TimeProvider {
            override fun currentTimeMillis(): Long {
                // Roll the current time on by 5 seconds every time
                return currentTimeStack.pop()
            }
        })

        // Arrange
        val firstMessage = "Hello World"
        val secondMessage = "How are you?"
        coEvery { repository.insertMessage(any()) } just Runs

        // Act
        viewModel.sendMessage(firstMessage) // First message triggers auto-reply
        advanceTimeBy(3000) // Simulate 3 seconds passing
        viewModel.sendMessage(secondMessage) // Second message sent within 5 seconds
        advanceTimeBy(5001) // Simulate another 5 seconds passing
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 3) { repository.insertMessage(any()) } // Only 3 messages: the two sent messages and one received
    }

    @Test
    fun `sendMessage triggers auto-reply if no further messages are sent`() = runTest {

        viewModel = ChatViewModel(repository, object : TimeProvider {
            override fun currentTimeMillis(): Long {
                // Roll the current time on by 5 seconds every time
                currentTimeMillis += 5001
                return currentTimeMillis
            }
        })

        // Arrange
        val userMessage = "Hello!"
        val messageSlot = slot<Message>()
        coEvery { repository.insertMessage(capture(messageSlot)) } just Runs

        // Act
        viewModel.sendMessage(userMessage) // Trigger the auto-reply indirectly
        advanceTimeBy(5001) // Simulate 5 seconds passing
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 2) { repository.insertMessage(any()) } // One for the user, one for auto-reply

        val sentMessages = messageSlot.captured.content

        // Verify that the auto-reply is scrambled
        Assert.assertNotEquals(userMessage, sentMessages) // Scrambled message should differ
        Assert.assertEquals(userMessage.length, sentMessages.length) // Length should match
        Assert.assertTrue(sentMessages.toList().sorted() == userMessage.toList().sorted()) // Same characters
    }
}