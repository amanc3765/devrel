package com.example.socialmedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.socialmedia.ui.SocialMediaTheme
import com.example.socialmedia.ui.home.timeline.Timeline

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialMediaTheme {
                Timeline()
            }
        }
    }
}