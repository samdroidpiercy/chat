package com.example.muzz.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Centralized color definitions for the app's UI theme.
 *
 * The `Colour` object organizes colors into nested objects based on their usage context,
 * such as `Background`, `Divider`, `Text`, and `Border`.
 * This promotes consistency and makes it easier to update colors across the app.
 * When adding new colours use https://chir.ag/projects/name-that-color/ to generate a colour name.
 */
object Colour {
    /**
     * Colors for various background elements in the UI.
     */
    object Background {
        val SentMessage = Rose // Background color for sent message bubbles
        val ReceivedMessage = WhiteLilac // Background color for received message bubbles
        val SendButtonTop = Rose // Gradient top color for the send button
        val SendButtonBottom = BitterSweet // Gradient bottom color for the send button
    }

    /**
     * Colors for dividers used in the app's UI.
     */
    object Divider {
        val Chat = Concrete // Divider color for the chat screen
    }

    /**
     * Colors for text elements.
     */
    object Text {
        val SentMessage = LavenderBlush // Text color for sent messages
        val ReceivedMessage = ShuttleGray // Text color for received messages
        val MessageTimestamp = HitGray // Text color for message timestamps
    }

    /**
     * Colors for borders used in the app's UI, such as input fields.
     */
    object Border {
        val TextEntry = TickleMePink // Border color for active text entry
        val TextEntryEmpty = LobLolly // Border color for inactive/empty text entry
    }
}

/**
 * Standalone color definitions.
 *
 * These are used either directly in the app or referenced by the `Colour` object.
 */
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val Rose = Color(0xFFFF1078)
val WhiteLilac = Color(0xFFF3F5FA)
val Concrete = Color(0xFFF2F2F2)
val ShuttleGray = Color(0xFF596D7D)
val LavenderBlush = Color(0xFFFFEDF3)
val TickleMePink = Color(0xFFFC88A2)
val BitterSweet = Color(0xFFFD6D64)
val LobLolly = Color(0xFFBFC5CC)
val HitGray = Color(0xFFA9B2B9)