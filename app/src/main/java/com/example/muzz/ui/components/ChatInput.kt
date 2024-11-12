import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun ChatInput(
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }

    fun sendMessage() {
        if (text.text.isNotBlank()) {
            onSend(text.text)
            text = TextFieldValue("")
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            label = { Text("Type a message...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    sendMessage()
                }
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            sendMessage()
        }) {
            Text("Send")
        }
    }
}