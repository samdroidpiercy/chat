package com.example.muzz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muzz.data.Message
import com.example.muzz.data.MessageStatus
import com.example.muzz.ui.theme.Colour
import com.example.muzz.ui.theme.PlusJakartaSans

/**
 * Composable function that represents a single chat message as a bubble.
 *
 * The message bubble adjusts its alignment, background color, and text style based on whether
 * the message is sent or received. In LTR layouts sent messages align to the right, and received messages align
 * to the left.
 *
 * @param message The Message object containing the content and metadata (e.g., status).
 */
@Composable
fun MessageBubble(message: Message) {
    // Determine alignment based on message status (sent vs received)
    val alignment = if (message.status == MessageStatus.SENT) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        Text(
            text = message.content,
            fontFamily = PlusJakartaSans, // Use the custom font family
            color = if (message.status == MessageStatus.SENT) Colour.Text.SentMessage else Colour.Text.ReceivedMessage, // Set text color based on status
            fontSize = 16.sp,
            modifier = Modifier
                // Set the background color and shape of the message bubble
                .background(
                    color = if (message.status == MessageStatus.SENT) Colour.Background.SentMessage else Colour.Background.ReceivedMessage,
                    shape = RoundedCornerShape(
                        topStart = 16.dp, // Rounded corners at the top
                        topEnd = 16.dp,
                        bottomStart = if (message.status == MessageStatus.RECEIVED) 0.dp else 16.dp, // Gives speech bubble effect for received messages
                        bottomEnd = if (message.status == MessageStatus.SENT) 0.dp else 16.dp // Gives speech bubble effect for sent messages
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp) // Padding inside the bubble to match example screenshot
        )
    }
}