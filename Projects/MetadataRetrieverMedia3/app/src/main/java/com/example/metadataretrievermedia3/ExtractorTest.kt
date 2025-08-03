package com.example.metadataretrievermedia3

import android.content.Context
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.MediaExtractorCompat
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Locale

@OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.O)
class ExtractorTest(private val mediaPath: String, private val numIter: Int) {

    fun startExtractorTest(context: Context): Long {
        var totalTimeUs: Long = 0L
        for (i in 0..<numIter) {
            val currTimeUs = runExtractorMedia3(context)
            if (currTimeUs < 0) {
                continue
            }
            totalTimeUs += currTimeUs
//            Log.d(
//                TAG, String.format(
//                    Locale.ROOT, "%30s %3d: %7d", mediaPath, i, currTimeUs
//                )
//            )
        }

        val meanTimeUs = totalTimeUs / numIter
        Log.d(
            TAG, String.format(
                Locale.ROOT, "(single) %s %7d", mediaPath, meanTimeUs,
            )
        )

        return meanTimeUs
    }

    private fun runExtractorMedia3(context: Context): Long {
        val startTimeNs = System.nanoTime()

        var extractor: MediaExtractorCompat? = null
        try {
            extractor = MediaExtractorCompat(context)
            extractor.setDataSource(mediaPath.toUri(), 0L)

            for (i in 0 until extractor.trackCount) {
                val mediaFormat: MediaFormat = extractor.getTrackFormat(i)
                extractor.selectTrack(i)
            }

            val metrics = extractor.metrics
            extractor.seekTo(0L, MediaExtractorCompat.SEEK_TO_CLOSEST_SYNC)

            val buffer = ByteBuffer.allocate(10 * 1024 * 1024)
            var totalNumberSamples = 0L
            var sumSamplesSize = 0L
            while (true) {
                val trackIndex = extractor.sampleTrackIndex
                if (trackIndex < 0) break

                val readBytes = extractor.readSampleData(buffer, 0)
                if (readBytes < 0) break
                totalNumberSamples += 1
                sumSamplesSize += readBytes

                val sampleTime: Long = extractor.sampleTime
                val sampleFlags: Int = extractor.sampleFlags
                val cachedUs = extractor.cachedDuration

                extractor.advance()
            }
            val meanSampleSize = sumSamplesSize / totalNumberSamples

//            Log.d(TAG, "totalNumberSamples: $totalNumberSamples")
//            Log.d(TAG, "Mean size of samples: $meanSampleSize")
            val isComplete = extractor.hasCacheReachedEndOfStream()
        } catch (e: IOException) {
            return -1L
        } finally {
            extractor?.release()
        }

        return (System.nanoTime() - startTimeNs) / 1000
    }
}