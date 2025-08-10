package com.example.extractorplatfrom

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class ExtractorTest(private val mediaPath: String, private val numIter: Int) {


    fun startExtractorTest(context: Context): Long {
        var totalTimeUs: Long = 0L
        for (i in 0..<numIter) {
            val currTimeUs = runExtractorPlatform(context)
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
                Locale.ROOT, "%s %7d", mediaPath, meanTimeUs,
            )
        )

        return meanTimeUs
    }


    private fun runExtractorPlatform(context: Context): Long {
        val startTimeNs = System.nanoTime()

        var extractor: MediaExtractor? = null
        try {
            extractor = MediaExtractor()
            extractor.setDataSource(mediaPath)

            for (i in 0 until extractor.trackCount) {
                val mediaFormat: MediaFormat = extractor.getTrackFormat(i)
                extractor.selectTrack(i)
            }

            val metrics = extractor.metrics
            extractor.seekTo(0L, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

            val buffer = ByteBuffer.allocate(10 * 1024 * 1024)
            while (true) {
                val trackIndex = extractor.sampleTrackIndex
                if (trackIndex < 0) break

                val readBytes = extractor.readSampleData(buffer, 0)
                if (readBytes < 0) break

                val sampleTime: Long = extractor.sampleTime
                val sampleFlags: Int = extractor.sampleFlags
                val cachedUs = extractor.cachedDuration

                extractor.advance()
            }
            val isComplete = extractor.hasCacheReachedEndOfStream()
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            extractor?.release()
        }

        return (System.nanoTime() - startTimeNs) / 1000
    }
}