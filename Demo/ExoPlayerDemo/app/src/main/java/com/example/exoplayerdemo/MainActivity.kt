package com.example.exoplayerdemo

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MimeTypes

class MainActivity : ComponentActivity() {

    companion object {
        private const val VIDEO_URI_DEFAULT =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        private const val VIDEO_URI_SUBTITLE = "https://html5demos.com/assets/dizzy.mp4"
        private const val SUBTITLE_URI_ENGLISH =
            "https://storage.googleapis.com/exoplayer-test-media-1/ttml/netflix_ttml_sample.xml"
        private const val SUBTITLE_URI_JAPANESE =
            "https://storage.googleapis.com/exoplayer-test-media-1/ttml/japanese-ttml.xml"
    }

    private lateinit var playerView: PlayerView
    private lateinit var trackSelectionButton: Button

    private var player: ExoPlayer? = null
    private var mediaItems: List<MediaItem> = listOf()
    private lateinit var trackSelectionParameters: TrackSelectionParameters

    private var playbackPosition = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.player_activity)
        playerView = findViewById(R.id.player_view)

        trackSelectionButton = findViewById(R.id.track_selection_button)
        trackSelectionButton.setOnClickListener {
            onTrackSelectionButtonClicked()
        }

        trackSelectionParameters =
            TrackSelectionParameters.Builder().clearVideoSizeConstraints().build()
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

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()

            mediaItems = createMediaItems()
            if (mediaItems.isEmpty()) {
                return
            }
            player?.setMediaItems(mediaItems)

            player?.seekTo(playbackPosition)
            player?.trackSelectionParameters = trackSelectionParameters

            playerView.player = player
        }
        player?.prepare()
        player?.playWhenReady = true

        val tracks: Tracks = player?.currentTracks ?: Tracks.EMPTY
        Log.d("Demo", "Tracks: $tracks")
        for (trackGroup in tracks.groups) {
            Log.d("Demo", "Track Group: $trackGroup")
        }
    }

    private fun createMediaItems(): List<MediaItem> {
        return listOf(
            createMediaItemWithSubtitleConfiguration(),
            createMediaItemWithClippingConfiguration(VIDEO_URI_DEFAULT.toUri()),
            createDefaultMediaItem()
        )
    }

    private fun createDefaultMediaItem(): MediaItem {
        return MediaItem.Builder().setUri(VIDEO_URI_DEFAULT.toUri()).build()
    }

    private fun createMediaItemWithClippingConfiguration(mediaUri: Uri): MediaItem {
        val clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
            .setStartPositionMs(10_000) // Start at 10 seconds
            .setEndPositionMs(15_000)   // End at 15 seconds
            .build()

        return MediaItem.Builder().setUri(mediaUri).setClippingConfiguration(clippingConfiguration)
            .build()
    }

    private fun createMediaItemWithSubtitleConfiguration(): MediaItem {
        val videoUri = VIDEO_URI_SUBTITLE.toUri()

        val subtitleConfigEnglish =
            MediaItem.SubtitleConfiguration.Builder(SUBTITLE_URI_ENGLISH.toUri())
                .setMimeType(MimeTypes.APPLICATION_TTML).setLanguage("en").setLabel("English")
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT).build()

        val subtitleConfigJapanese =
            MediaItem.SubtitleConfiguration.Builder(SUBTITLE_URI_JAPANESE.toUri())
                .setMimeType(MimeTypes.APPLICATION_TTML).setLanguage("ja").setLabel("Japanese")
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT).build()

        return MediaItem.Builder().setUri(videoUri).setSubtitleConfigurations(
            listOf(
                subtitleConfigEnglish, subtitleConfigJapanese
            )
        ).build()
    }


    private fun onTrackSelectionButtonClicked() {

    }

    private fun releasePlayer() {
        playbackPosition = player?.currentPosition ?: 0L
        player?.release()
        player = null
        playerView.player = null
    }
}