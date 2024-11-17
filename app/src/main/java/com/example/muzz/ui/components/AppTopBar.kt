package com.example.muzz.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.muzz.R
import com.example.muzz.ui.theme.Colour

/**
 * Composable function representing the top app bar for the chat screen.
 *
 * This component includes a centered title and a horizontal divider
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() = Column {
    // The main top app bar with a title aligned at the center
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.chat_title), // Decided to localise strings
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background // Set background color to match theme and allow gray messages to be seen (default is FFFAFAFA)
        )
    )

    // Divider below the app bar for visual separation, as seen in the example screenshot
    HorizontalDivider(
        thickness = 2.dp,
        color = Colour.Divider.Chat
    )
}