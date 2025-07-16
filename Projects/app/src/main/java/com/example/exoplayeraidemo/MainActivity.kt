package com.example.exoplayeraidemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : ComponentActivity() {
    // An instance of ExoPlayer
    private var player: ExoPlayer? = null

    // A reference to the PlayerView in the layout
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)

        initializePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure player is released if not already when the activity is destroyed
        releasePlayer()
    }

    private fun initializePlayer() {
        if (player == null) {
            // Build a new ExoPlayer instance
            player = ExoPlayer.Builder(this).build()
            // Attach the player to the PlayerView
            playerView.player = player
        }

        // Define the URL of your video
        val videoUrl =
            "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
        // Create a MediaItem from the video URL
        val mediaItem = MediaItem.fromUri(videoUrl.toUri())

        // Set the MediaItem to the player
        player?.setMediaItem(mediaItem)
        // Prepare the player to start loading the media
        player?.prepare()
        // Start playing automatically when ready
        player?.playWhenReady = true
    }

    private fun releasePlayer() {
        // Release the player and set it to null
        player?.release()
        player = null
    }
}
