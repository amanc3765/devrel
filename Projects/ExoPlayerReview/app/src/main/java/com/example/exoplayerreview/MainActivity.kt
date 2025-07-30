package com.example.exoplayerreview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : ComponentActivity() {

    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.playerView)
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    public override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer()
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()

        val mediaItem =
            MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")
        player?.setMediaItem(mediaItem)

        playerView.player = player
        player?.playWhenReady = true
        player?.prepare()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }
}