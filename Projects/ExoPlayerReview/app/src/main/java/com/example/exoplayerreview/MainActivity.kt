package com.example.exoplayerreview

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import androidx.media3.ui.PlayerView

@UnstableApi
class MainActivity : ComponentActivity() {

    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null

    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    private lateinit var downloadTracker: DownloadTracker
    private lateinit var dataSourceFactory: DataSource.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.playerView)

        dataSourceFactory = DownloadUtil.getDataSourceFactory(this)
        downloadMedia()
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
        val mediaSourceFactory =
            DefaultMediaSourceFactory(this).setDataSourceFactory(dataSourceFactory)
        val exoPlayerBuilder: ExoPlayer.Builder = ExoPlayer.Builder(this).setMediaSourceFactory(
            mediaSourceFactory
        )
        player = exoPlayerBuilder.build()

        val firstMediaItem = MediaItem.fromUri(getString(R.string.default_video_uri))
        val secondMediaItem = MediaItem.fromUri(getString(R.string.default_audio_uri))
        player?.let { exoPlayer ->
            playerView.player = exoPlayer

            exoPlayer.setMediaItems(
                listOf(firstMediaItem, secondMediaItem), mediaItemIndex, playbackPosition
            )
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.addAnalyticsListener(object : AnalyticsListener {
                override fun onLoadStarted(
                    eventTime: AnalyticsListener.EventTime,
                    loadEventInfo: LoadEventInfo,
                    mediaLoadData: MediaLoadData
                ) {
                    Log.d("Aman", "➡️ Load started: ${loadEventInfo.dataSpec.uri}")
                }

                override fun onLoadCompleted(
                    eventTime: AnalyticsListener.EventTime,
                    loadEventInfo: LoadEventInfo,
                    mediaLoadData: MediaLoadData
                ) {
                    Log.d(
                        "Aman",
                        "✅ Load completed: ${loadEventInfo.dataSpec.uri}, bytes loaded: ${loadEventInfo.bytesLoaded}"
                    )
                }
            })

            exoPlayer.prepare()
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            mediaItemIndex = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    private fun downloadMedia() {
        startDownloadService()

        val downloadManager = DownloadUtil.getDownloadManager(this)
        downloadTracker = DownloadTracker(this, downloadManager)

        val downloadList: List<Uri> = listOf(
            getString(R.string.default_video_uri).toUri(),
            getString(R.string.default_audio_uri).toUri()
        )

        for ((index, uri) in downloadList.withIndex()) {
            val mediaId = uri.toString()

            val isDownloaded = downloadTracker.isDownloaded(uri)
            if (!isDownloaded) {
                downloadTracker.startDownload(mediaId, uri)
                Log.i("Aman", "False : Started download for: $uri with ID: $mediaId")
            } else {
                Log.i("Aman", "True: $uri is already downloaded.")
            }
        }
    }

    @UnstableApi
    private fun startDownloadService() {
        try {
            DownloadService.start(this, DemoDownloadService::class.java)
            Log.d("Aman", "Download service started")
        } catch (e: IllegalStateException) {
            Log.e(
                "Aman", "Failed to start download service in background, attempting foreground.", e
            )
            DownloadService.startForeground(this, DemoDownloadService::class.java)
            Log.d("Aman", "Download service started in foreground")
        }
    }
}