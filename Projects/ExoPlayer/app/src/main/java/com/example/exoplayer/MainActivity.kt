package com.example.exoplayer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Tracks
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.PlayerView

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private var player: ExoPlayer? = null

    private var playerView: PlayerView? = null

    private val playbackStateListener: Player.Listener = playbackStateListener()

    private val analyticsListener: AnalyticsListener = analyticsListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
    }

    override fun onStart() {
        super.onStart()
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView?.player = player

        // Add media item to the player
        val firstMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3))
        val secondMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
        player?.setMediaItem(firstMediaItem)
        player?.addMediaItem(secondMediaItem)

        // Register the listener
        player?.addListener(playbackStateListener)

        // Register the analytics listener
        player?.addAnalyticsListener(analyticsListener)

        // Track selection parameters
        val trackSelectionParameters =
            TrackSelectionParameters.Builder(this).setMaxVideoBitrate(5_000_000) // 5 Mbps
                .build()
        player?.trackSelectionParameters = trackSelectionParameters

        // Prepare the player
        player?.prepare()

        // Start playback
        player?.play()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
        playerView?.player = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val playingString: String = if (isPlaying) {
                "PLAYING"
            } else {
                "NOT PLAYING"
            }
            Log.d(TAG, "isPlaying: $playingString")
        }

        override fun onTracksChanged(tracks: Tracks) {
            Log.d(TAG, "onTracksChanged: ${tracks.groups}")
        }

    }

    @OptIn(UnstableApi::class)
    private fun analyticsListener() = object : AnalyticsListener {
        override fun onDroppedVideoFrames(
            eventTime: AnalyticsListener.EventTime,
            droppedFrames: Int,
            elapsedMs: Long,
        ) {
            Log.w(TAG, "Dropped frames: $droppedFrames")
        }
    }
}
