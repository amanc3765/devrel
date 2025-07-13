package com.example.socialmedia.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChatScreen() {
    val messages = listOf(
        ChatMessage(
            text = "Hi there!",
            mediaUri = null,
            mediaMimeType = null,
            timestamp = System.currentTimeMillis() - 10000,
            isIncoming = false,
            senderIconUri = null
        ), ChatMessage(
            text = "Hello! How's it going?",
            mediaUri = null,
            mediaMimeType = null,
            timestamp = System.currentTimeMillis() - 5000,
            isIncoming = true,
            senderIconUri = null
        )
    )
    ChatContent(messages = messages)
}

@Composable
fun ChatContent(messages: List<ChatMessage>) {
    Column {
        MessageList(
            messages = messages,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        InputBar()
    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    messages: List<ChatMessage>,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
    ) {
        items(items = messages) { message ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    16.dp,
                    if (message.isIncoming) Alignment.Start else Alignment.End,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MessageBubble(message = message)
            }
        }

    }
}

@Composable
fun MessageBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = if (message.isIncoming) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.primary
        },
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = message.text)
        }
    }
}

@Composable
fun InputBar(modifier: Modifier = Modifier, contentPadding: PaddingValues = PaddingValues(0.dp)) {
    Surface(
        modifier = modifier,
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(onClick = { /* Handle send message */ }) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = { /* Handle send message */ }) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Select Photo or video",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            TextField(
                value = "Type your message here",
                onValueChange = { /* Handle text input */ },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            )
            FilledIconButton(
                onClick = { /* Handle send message */ },
                modifier = Modifier.size(50.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChatScreenPreview() {
    ChatScreen()
}