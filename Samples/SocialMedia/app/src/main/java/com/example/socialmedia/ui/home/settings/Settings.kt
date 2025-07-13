package com.example.socialmedia.ui.home.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.socialmedia.R

@Composable
fun Settings() {
    LazyColumn() {
        item {
            // Clear Message History
            Box(modifier = Modifier.padding(32.dp)) {
                Button(
                    onClick = { /* TODO: Implement */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(text = stringResource(R.string.clear_message_history))
                }
            }

            // AI Chatbot
            val chatbotStatusResource = "disabled"

            Box(modifier = Modifier.padding(32.dp)) {
                Button(
                    onClick = { /* TODO: Implement */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(text = "${stringResource(R.string.ai_chatbot_setting)}: $chatbotStatusResource")
                }
            }
        }

        // Media Performance Class
        val mediaPerformanceClass: Int = 0
        item {
            Box(modifier = Modifier.padding(32.dp)) {
                Text(
                    text = stringResource(
                        R.string.performance_class_level, mediaPerformanceClass
                    ),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SettingsPreview() {
    Settings()
}