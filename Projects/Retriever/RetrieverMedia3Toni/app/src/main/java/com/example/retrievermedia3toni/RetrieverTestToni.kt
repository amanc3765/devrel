package com.example.retrievermedia3toni

import android.app.Activity
import android.content.Context
import android.os.Trace
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.source.TrackGroupArray
import java.util.Locale
import java.util.concurrent.Future

@OptIn(UnstableApi::class)
class RetrieverTestToni(
    private val mediaPath: String, private val numIter: Int
) {

    fun startMetadataRetrievalTestSerial(context: Context): Long {
        var totalTimeUs: Long = 0L
        for (i in 0..<numIter) {
            val currTimeUs = retrieveMetadataMedia3(context, i)
            totalTimeUs += currTimeUs
//            Log.d(
//                TAG, String.format(
//                    Locale.ROOT, "%30s %3d: %7d", mediaPath, i, currTimeUs
//                )
//            )
        }
        val meanTimeUs = totalTimeUs / numIter
        printMeanRetrieverTime(meanTimeUs, "Serial")
        return meanTimeUs
    }

    fun startMetadataRetrievalTestBulk(context: Context): Long {
        val totalTimeUs = retrieveMetadataMedia3Bulk(context)
        val meanTimeUs = totalTimeUs / numIter
        printMeanRetrieverTime(meanTimeUs, "Bulk")
        return meanTimeUs
    }

    private fun printMeanRetrieverTime(meanTimeUs: Long, mode: String) {
        Log.d(
            TAG, String.format(
                Locale.ROOT,
                "[$TAG][$mode] Mean Retriever Time: %30s %7d",
                mediaPath,
                meanTimeUs,
            )
        )
    }

    private fun retrieveMetadataMedia3(context: Context, i: Int): Long {
        Trace.beginSection("retrieveMetadataMedia3Aman $i")
        val startTimeNs = System.nanoTime()

        try {
            val mediaSourceFactory =
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
            val mediaItem = MediaItem.fromUri(mediaPath)
            MetadataRetriever.retrieveMetadata(mediaSourceFactory, mediaItem).get()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        Trace.endSection()
        return (System.nanoTime() - startTimeNs) / 1000
    }

    private fun retrieveMetadataMedia3Bulk(context: Context): Long {
        val startTimeNs = System.nanoTime()

        val futures = mutableListOf<Future<TrackGroupArray>>()
        for (i in 0..<numIter) {
            try {
                val mediaSourceFactory =
                    ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                val mediaItem = MediaItem.fromUri(mediaPath)
                val future = MetadataRetriever.retrieveMetadata(mediaSourceFactory, mediaItem)
                futures.add(future)
//              Log.d(TAG, "$i: Retriever submitted.")
            } catch (e: Exception) {
                Log.e(TAG, "$i: Error retrieving metadata: $e")
            }
        }
        val numRetrievers = futures.size
        for (i in 0..<numRetrievers) {
            try {
                futures[i].get()
//                Log.d(TAG, "$i: Future completed for retriever.")
            } catch (e: Exception) {
                Log.e(TAG, "$i: Error waiting for future of retriever to complete", e)
            }
        }

        return (System.nanoTime() - startTimeNs) / 1000
    }

}