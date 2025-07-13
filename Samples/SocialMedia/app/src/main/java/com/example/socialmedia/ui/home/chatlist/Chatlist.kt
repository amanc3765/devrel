package com.example.socialmedia.ui.home.chatlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import com.example.socialmedia.ui.ChatRow

@Composable
fun ChatList() {
    LazyColumn {
        val contacts = listOf("Alice", "Bob", "Charlie", "David", "Eve")
        items(items = contacts) {
            ChatRow()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChatListPreview() {
    ChatList()
}