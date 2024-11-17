package com.example.muzz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
fun MessageBubble(message: Message, timestamp: String? = null, smallGap: Boolean) {
    val isSent = message.status == MessageStatus.SENT

    //sent messages align to the right, received to the left for LTR layouts
    val alignment = if (isSent) Arrangement.End else Arrangement.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = if (smallGap) 4.dp else 12.dp,
                end = if (isSent) 0.dp else 48.dp,
                start = if (isSent) 48.dp else 0.dp
            )
    ) {
        timestamp?.let {

            val styledTimestamp = styleTimestamp(it)

            Text(
                text = styledTimestamp,
                fontSize = 12.sp,
                color = Colour.Text.MessageTimestamp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = alignment
        ) {
            Text(
                text = message.content,
                fontFamily = PlusJakartaSans, // Use the custom font family
                color = if (isSent) Colour.Text.SentMessage else Colour.Text.ReceivedMessage,
                fontSize = 16.sp,
                modifier = Modifier // Set the background color and shape of the message bubble
                    .background(
                        color = if (isSent) Colour.Background.SentMessage else Colour.Background.ReceivedMessage,
                        shape = RoundedCornerShape(
                            topStart = 16.dp, // Rounded corners at the top
                            topEnd = 16.dp,
                            bottomStart = if (!isSent) 0.dp else 16.dp, // Gives speech bubble effect for received messages
                            bottomEnd = if (isSent) 0.dp else 16.dp // Gives speech bubble effect for sent messages
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp) // Padding inside the bubble to match example screenshot
            )
        }
    }
}

/**
 * @param
 */
@Composable
private fun styleTimestamp(timestamp: String) = buildAnnotatedString {
    val parts = timestamp.split(" ")
    if (parts.size == 2) {
        append(
            AnnotatedString(
                text = parts[0], // Day
                spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
            )
        )
        append(" ") // Space
        append(
            AnnotatedString(
                text = parts[1],
                spanStyle = SpanStyle(fontWeight = FontWeight.Normal)
            )
        )
    } else {
        append(timestamp) // Fallback
    }
}