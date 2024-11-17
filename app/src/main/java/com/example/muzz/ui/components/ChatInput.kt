import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muzz.R
import com.example.muzz.ui.theme.Colour

@Composable
fun ChatInput(
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val isTextEmpty = text.text.isBlank()

    // Helper function to handle sending a message
    fun sendMessage() {
        if (!isTextEmpty) {
            onSend(text.text)
            text = TextFieldValue("")
        }
    }

    // Divider as seen in the exercise screenshot, just slightly more solid
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 2.dp,
        color = Colour.Divider.Chat
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // BasicTextField for entering message content, OutlinedTextField was not malleable enough for this
        val messageInputDescription = stringResource(id = R.string.content_description_message_input)
        // BasicTextField for entering message content, OutlinedTextField was not malleable enough for this
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f) // Ensures the text field takes up remaining space in the row
                .height(48.dp) // Sets a fixed height for the text field to match button height
                .border(
                    width = 1.dp,
                    color = if (isTextEmpty) Colour.Border.TextEntryEmpty else Colour.Border.TextEntry,
                    shape = RoundedCornerShape(50) // Gives the text field rounded edges
                )
                .padding(horizontal = 16.dp)
                .semantics { contentDescription = messageInputDescription },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send, // "Send" action for the keyboard
                keyboardType = KeyboardType.Text
            ),
            textStyle = LocalTextStyle.current.copy(
                lineHeight = 16.sp // Adjusts line height for better text alignment
            ),
            keyboardActions = KeyboardActions(
                onSend = { sendMessage() } // Triggers send on hitting send on the keyboard
            ),
            // Custom decoration for aligning text within the field
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart // Aligns text to the start of the field
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-3).dp) // Moves the text up slightly to match screenshot
                    ) {
                        innerTextField()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.width(16.dp)) // Spacing between the text field and send button

        // Send button with gradient background and click handling, used box instead of button to allow more styling
        Box(
            modifier = Modifier
                .size(48.dp) // Same as text entry box
                .clip(CircleShape) // Clips the button into a circle
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Colour.Background.SendButtonTop.copy(alpha = if (isTextEmpty) 0.5f else 1f),
                            Colour.Background.SendButtonBottom.copy(alpha = if (isTextEmpty) 0.5f else 1f)
                        )
                    ) // Faded button when text is empty
                )
                .clickable { sendMessage() }, // Sends the message when clicked
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(id = R.string.content_description_send), //added for screen readers
                modifier = Modifier.size(30.dp), // Icon size
                tint = Color.White // White icon color
            )
        }
    }
}