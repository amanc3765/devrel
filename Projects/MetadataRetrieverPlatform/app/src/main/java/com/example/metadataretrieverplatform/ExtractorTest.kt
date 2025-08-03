package com.example.metadataretrieverplatform

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import java.nio.ByteBuffer
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class ExtractorTest(private val mediaPath: String, private val numIter: Int) {

    fun startExtractorTest(context: Context): Long {
        var totalTimeUs: Long = 0L
        for (i in 0..<numIter) {
            val currTimeUs = runExtractorMedia3(context)
            totalTimeUs += currTimeUs
//            Log.d(
//                TAG, String.format(
//                    Locale.ROOT, "%30s %3d: %7d", mediaPath, i, currTimeUs
//                )
//            )
        }

        val meanTimeUs = totalTimeUs / numIter
//        Log.d(
//            TAG, String.format(
//                Locale.ROOT, "(single) %7d", meanTimeUs,
//            )
//        )

        return meanTimeUs
    }


    private fun runExtractorMedia3(context: Context): Long {
        val startTimeNs = System.nanoTime()

        val extractor = MediaExtractor()
        extractor.setDataSource(mediaPath)

        for (i in 0 until extractor.trackCount) {
//            Log.i(TAG, "----- Track $i -----")
            val mediaFormat: MediaFormat = extractor.getTrackFormat(i)
//            Log.i(TAG, "$mediaFormat")

            extractor.selectTrack(i)
//            Log.i(TAG, "Selected track $i")
        }

        val metrics = extractor.metrics
//        Log.i(TAG, "Metrics: $metrics")

        extractor.seekTo(0L, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

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

            val cachedUs = extractor.cachedDuration
//            Log.d(TAG, "Cached duration ahead: $cachedUs µs")

            extractor.advance()
        }

        val isComplete = extractor.hasCacheReachedEndOfStream()
//        if (isComplete) {
//            Log.d(TAG, "All data is cached. End of stream reached.")
//        }

        extractor.release()
//        Log.i(TAG, "Extractor released.")

        return (System.nanoTime() - startTimeNs) / 1000
    }
}