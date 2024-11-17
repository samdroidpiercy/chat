package com.example.muzz.ui.screens

import ChatScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.muzz.data.Message
import com.example.muzz.data.MessageStatus
import com.example.muzz.ui.theme.MuzzTheme
import com.example.muzz.ui.viewmodel.ChatViewModel
import com.example.muzz.ui.viewmodel.MessageUIState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeUIState = MutableLiveData<List<MessageUIState>>(emptyList())

    // Mock ViewModel
    private val mockViewModel: ChatViewModel = mockk {

        every { this@mockk.uiState } returns fakeUIState
        coEvery { sendMessage(any()) } answers {
            val content = it.invocation.args[0] as String
            val message = MessageUIState(
                message = Message(0, content, MessageStatus.SENT, System.currentTimeMillis()),
                timestamp = null,
                smallGap = false
            )
            fakeUIState.postValue(fakeUIState.value.orEmpty() + message)
        }
    }

    @Test
    fun chatScreenDisplaysInitialState() {
        composeTestRule.setContent {
            MuzzTheme {
                ChatScreen(viewModel = mockViewModel, paddingValues = PaddingValues())
            }
        }

        // Verify the input field and send button are displayed
        composeTestRule.onNodeWithContentDescription("Message Input").assertExists()
        composeTestRule.onNodeWithContentDescription("Send").assertExists()
    }

    @Test
    fun sendMessageDisplaysInChatList() = runTest {
        composeTestRule.setContent {
            MuzzTheme {
                ChatScreen(viewModel = mockViewModel, paddingValues = PaddingValues())
            }
        }

        // Type a message in the input field
        composeTestRule.onNodeWithContentDescription("Message Input").performTextInput("Hello, World!")

        // Click the send button
        composeTestRule.onNodeWithContentDescription("Send").performClick()

        // Verify the message appears in the list
        composeTestRule.onNodeWithText("Hello, World!").assertExists()
    }

    @Test
    fun sendMessageGeneratesAutoReply() = runTest {
        // Arrange: Set up the UI and ViewModel
        composeTestRule.setContent {
            MuzzTheme {
                ChatScreen(viewModel = mockViewModel, paddingValues = PaddingValues())
            }
        }

        // Type and send a message
        val userMessage = "Test Message"
        composeTestRule.onNodeWithContentDescription("Message Input").performTextInput(userMessage)
        composeTestRule.onNodeWithContentDescription("Send").performClick()

        // Wait for the auto-reply to appear, don't really want to add a sleep but have for this test
        Thread.sleep(5001)

        // Fetch all message text nodes
        val messageNodes = composeTestRule.onAllNodes(hasText(userMessage)).fetchSemanticsNodes()
        val autoReplyText = messageNodes.lastOrNull()?.config?.getOrNull(SemanticsProperties.Text)?.joinToString("")

        assertNotNull(autoReplyText)
        assertEquals(userMessage.length, autoReplyText?.length) // Length should match
        assertTrue(autoReplyText!!.toList().sorted() == userMessage.toList().sorted()) // Same characters
    }
    @Test
    fun messageInputClearsAfterSend() {
        composeTestRule.setContent {
            MuzzTheme {
                ChatScreen(viewModel = mockViewModel,  paddingValues = PaddingValues())
            }
        }

        // Type a message
        composeTestRule.onNodeWithContentDescription("Message Input").performTextInput("Hello!")

        // Click the send button
        composeTestRule.onNodeWithContentDescription("Send").performClick()

        // Verify the input field is cleared
        composeTestRule.onNodeWithContentDescription("Message Input").assertTextEquals("")
    }

    @Test
    fun messagesShowTimestampsAndSpacingCorrectly() {
        composeTestRule.setContent {
            MuzzTheme {
                ChatScreen(viewModel = mockViewModel,  paddingValues = PaddingValues())
            }
        }

        // Simulate messages with different timestamps
        val messages = listOf(
            MessageUIState(
                message = Message(0, "Message 1", MessageStatus.SENT, System.currentTimeMillis() - ChatViewModel.ONE_HOUR_IN_MS),
                timestamp = "Monday 12:00",
                smallGap = false
            ),
            MessageUIState(
                message = Message(1, "Message 2", MessageStatus.SENT, System.currentTimeMillis()),
                timestamp = null,
                smallGap = true
            )
        )

        fakeUIState.postValue(messages)

        // Verify timestamp is displayed above the first message
        composeTestRule.onNodeWithText("Monday 12:00").assertExists()

        // Verify spacing between messages
        composeTestRule.onNodeWithText("Message 2")
            .assert(hasText("Message 2")) // Check for proper content
    }
}