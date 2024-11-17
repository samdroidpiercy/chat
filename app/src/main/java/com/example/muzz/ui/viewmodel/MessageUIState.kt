package com.example.muzz.ui.viewmodel

import com.example.muzz.data.Message

data class MessageUIState(
    val message: Message,
    val timestamp: String?, // Formatted timestamp to display above the message
    val smallGap: Boolean // Whether to show a smaller or regular gap
)
