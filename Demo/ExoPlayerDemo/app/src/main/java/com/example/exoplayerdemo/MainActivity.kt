package com.example.exoplayerdemo

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.ima.ImaAdsLoader
import androidx.media3.exoplayer.ima.ImaServerSideAdInsertionMediaSource
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ads.AdsLoader
import androidx.media3.ui.PlayerView

@UnstableApi
class MainActivity : ComponentActivity() {

    companion object {
        private const val VIDEO_URI_DEFAULT =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"

        private const val VIDEO_URI_SUBTITLE = "https://html5demos.com/assets/dizzy.mp4"
        private const val SUBTITLE_URI_ENGLISH =
            "https://storage.googleapis.com/exoplayer-test-media-1/ttml/netflix_ttml_sample.xml"
        private const val SUBTITLE_URI_JAPANESE =
            "https://storage.googleapis.com/exoplayer-test-media-1/ttml/japanese-ttml.xml"

        private const val DRM_VIDEO_URI =
            "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd"
        private const val DRM_LICENSE_URI =
            "https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test"

        private const val ADS_TAG_URI =
            "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator="
        private const val SSAI_ADS_URI =
            "ssai://dai.google.com/?assetKey=sN_IYUG8STe1ZzhIIE_ksA&format=2&adsId=3"
    }

    private lateinit var playerView: PlayerView
    private lateinit var trackSelectionButton: Button

    private var player: ExoPlayer? = null
    private var mediaItems: List<MediaItem> = listOf()

    private lateinit var clientSideAdsLoader: AdsLoader
    private lateinit var clientSIdeAdsLoaderProvider: AdsLoader.Provider
    private lateinit var serverSideAdsLoader: ImaServerSideAdInsertionMediaSource.AdsLoader

    private var currentItemIndex = 0
    private var playbackPosition = C.TIME_UNSET

    private lateinit var downloadTracker: DownloadTracker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.player_activity)
        playerView = findViewById(R.id.player_view)

        trackSelectionButton = findViewById(R.id.track_selection_button)
        trackSelectionButton.setOnClickListener {
            onTrackSelectionButtonClicked()
        }

        downloadTracker = DownloadUtil.getDownloadTracker(this)
        startDownloadService()
        val renderersFactory: RenderersFactory = DownloadUtil.buildRenderersFactory(
            /* context= */
            this,
        )
        downloadTracker.startDownload(createDefaultMediaItem(), renderersFactory)
    }

    override fun onStart() {
        super.onStart()
//        initializePlayer()
//        playerView.onResume()
    }

    private fun startDownloadService() {
        try {
            DownloadService.start(this, DemoDownloadService::class.java)
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(this, DemoDownloadService::class.java)
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        if (player != null) {
            return
        }

        val trackSelectionParameters = TrackSelectionParameters.Builder(this).build()
        val mediaSourceFactory = createMediaSourceFactory()

        player = ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()
            .also { exoPlayer ->
                playerView.player = exoPlayer
                exoPlayer.trackSelectionParameters = trackSelectionParameters
                exoPlayer.playWhenReady = true
                clientSideAdsLoader.setPlayer(exoPlayer)
                serverSideAdsLoader.setPlayer(exoPlayer)

                mediaItems = createMediaItems()
                exoPlayer.setMediaItems(mediaItems, currentItemIndex, playbackPosition)
                exoPlayer.prepare()
            }
    }

    @OptIn(UnstableApi::class)
    private fun createMediaSourceFactory(): MediaSource.Factory {
        clientSideAdsLoader = ImaAdsLoader.Builder(this).build()
        clientSIdeAdsLoaderProvider = AdsLoader.Provider { clientSideAdsLoader }

        serverSideAdsLoader =
            ImaServerSideAdInsertionMediaSource.AdsLoader.Builder(this, playerView).build()
        val serverSideAdsMediaSourceFactory = ImaServerSideAdInsertionMediaSource.Factory(
            serverSideAdsLoader, DefaultMediaSourceFactory(this)
        )

        return DefaultMediaSourceFactory(this).setLocalAdInsertionComponents(
            clientSIdeAdsLoaderProvider, playerView
        ).setServerSideAdInsertionMediaSourceFactory(serverSideAdsMediaSourceFactory)
    }

    private fun createMediaItems(): List<MediaItem> {
        return listOf(
            createMediaItemWithServerSideAdsConfiguration(),
            createMediaItemWithAdsConfiguration(),
            createMediaItemWithDrmConfiguration(),
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

    private fun createMediaItemWithDrmConfiguration(): MediaItem {
        val videoUri = DRM_VIDEO_URI.toUri()

        val drmConfiguration =
            MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID).setLicenseUri(DRM_LICENSE_URI)
                .build()

        return MediaItem.Builder().setUri(videoUri).setDrmConfiguration(drmConfiguration).build()
    }

    @OptIn(UnstableApi::class)
    private fun createMediaItemWithAdsConfiguration(): MediaItem {
        val videoUri = VIDEO_URI_DEFAULT.toUri()

        val adTagUri = ADS_TAG_URI.toUri()
        val adsConfiguration = MediaItem.AdsConfiguration.Builder(adTagUri).build()

        return MediaItem.Builder().setUri(videoUri).setAdsConfiguration(adsConfiguration).build()
    }

    @OptIn(UnstableApi::class)
    private fun createMediaItemWithServerSideAdsConfiguration(): MediaItem {
        return MediaItem.Builder().setUri(SSAI_ADS_URI.toUri()).build()
    }

    private fun onTrackSelectionButtonClicked() {

    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItemIndex = exoPlayer.currentMediaItemIndex
            exoPlayer.release()
        }
        player = null
        // Release the ad loaders when the player is released.
        clientSideAdsLoader.release() // This will no-op if clientSideAdsLoader is not initialized.
        if (::serverSideAdsLoader.isInitialized) { // Check if serverSideAdsLoader has been initialized
            serverSideAdsLoader.release()
        }
        playerView.player = null
    }
}