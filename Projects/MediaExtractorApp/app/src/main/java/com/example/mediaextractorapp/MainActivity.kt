package com.example.mediaextractorapp

import android.content.Context
import android.media.MediaFormat
import android.media.metrics.MediaMetricsManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.media3.common.DrmInitData
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.MediaExtractorCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

const val TAG = "Aman"

@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            runExtractor(this@MainActivity)
        }
    }

    @OptIn(UnstableApi::class)
    fun runExtractor(context: Context) {
        val extractor = MediaExtractorCompat(context)

        val mediaMetricsManager = context.getSystemService(MediaMetricsManager::class.java)
        val logSessionId = mediaMetricsManager?.createPlaybackSession()?.sessionId!!
        extractor.logSessionId = logSessionId
        val currentSession = extractor.logSessionId
        Log.d(TAG, "Current LogSessionId: $currentSession")

//        val mediaUri: Uri = getString(R.string.audio_url).toUri()
//        val mediaUri: Uri = getString(R.string.video_url).toUri()
        val mediaUri: Uri = getString(R.string.drm_url).toUri()
        extractor.setDataSource(mediaUri, 0L)


        Log.i(TAG, "Number of tracks: ${extractor.trackCount}")
        for (i in 0 until extractor.trackCount) {
            val mediaFormat: MediaFormat = extractor.getTrackFormat(i)

            Log.i(TAG, "----- Track $i -----")
            Log.i(TAG, "$mediaFormat")
//            ExtractorUtil.printMediaFormat(mediaFormat)

            extractor.selectTrack(i)
            Log.i(TAG, "Selected track $i")
//            extractor.unselectTrack(i)

            val drmInitData: DrmInitData? = extractor.drmInitData
            Log.d(TAG, "DrmInitData: $drmInitData")
        }

        val metrics = extractor.metrics  // or extractor.getMetrics()
        Log.i(TAG, "Metrics: $metrics")

        val targetTimeUs = 595_000_000L
//        extractor.seekTo(targetTimeUs, MediaExtractorCompat.SEEK_TO_PREVIOUS_SYNC) // 595_904_000
//        extractor.seekTo(targetTimeUs, MediaExtractorCompat.SEEK_TO_NEXT_SYNC)     // 596_224_000
        extractor.seekTo(targetTimeUs, MediaExtractorCompat.SEEK_TO_CLOSEST_SYNC)    // 596_224_000

        val buffer = ByteBuffer.allocate(10 * 1024 * 1024)
        while (true) {
            val trackIndex = extractor.sampleTrackIndex
            if (trackIndex < 0) break

            val readBytes = extractor.readSampleData(buffer, 0)
            if (readBytes < 0) break

            val sampleTime: Long = extractor.sampleTime
            val sampleFlags: Int = extractor.sampleFlags
//            Log.i(
//                TAG, "Track %2d: Read %8d bytes at %12d us, flags %d".format(
//                    trackIndex, readBytes, sampleTime, sampleFlags
//                )
//            )
//
//            val cachedUs = extractor.cachedDuration
//            Log.d(TAG, "Cached duration ahead: $cachedUs µs")

            extractor.advance()
        }

        val isComplete = extractor.hasCacheReachedEndOfStream()
        if (isComplete) {
            Log.d(TAG, "All data is cached. End of stream reached.")
        }

        extractor.release()
        Log.i(TAG, "Extractor released.")
    }

}