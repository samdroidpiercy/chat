import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.muzz.ui.components.MessageBubble
import com.example.muzz.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

/**
 * Composable function representing the chat screen UI.
 *
 * This screen displays a list of chat messages and provides an input area for sending new messages.
 * Messages are displayed in a reversed order (latest at the bottom), and the list scrolls to the most recent message.
 *
 * @param viewModel The ViewModel providing data and handling business logic for the chat screen.
 * @param paddingValues Padding values passed from parent composable (Scaffold).
 */
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel(), paddingValues: PaddingValues) {
    // Observes the messages from the ViewModel as a state
    val messages by viewModel.messages.observeAsState(emptyList())

    // Maintains the state of the LazyColumn's scroll position
    val listState = rememberLazyListState()

    // Creates a coroutine scope for triggering scroll animations
    val coroutineScope = rememberCoroutineScope()

    // Scroll to the latest message when the list of messages changes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    // Main container for the chat screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Apply external padding from Scaffold
    ) {
        // Displays the list of messages in a vertically scrollable LazyColumn
        LazyColumn(
            modifier = Modifier.weight(1f), // Allows the column to fill available space
            state = listState, // Maintains scroll state
            reverseLayout = true, // Reverses the order so latest messages appear at the bottom
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp), // Padding around the list
            verticalArrangement = Arrangement.Top // Aligns items to the top
        ) {
            // Displays each message using the MessageBubble composable
            items(messages.asReversed()) { message ->
                MessageBubble(message = message)
            }
        }

        // Input field for sending new messages
        ChatInput(
            onSend = { content ->
                // Sends the new message via the ViewModel
                viewModel.sendMessage(content)
                // Scrolls to the latest message after sending
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        )
    }
}