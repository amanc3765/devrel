package com.example.exoplayerreview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : ComponentActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.playerView)

        initializePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()

        val mediaItem =
            MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")
        player.setMediaItem(mediaItem)

        playerView.player = player
        player.playWhenReady = true
        player.prepare()
    }
}