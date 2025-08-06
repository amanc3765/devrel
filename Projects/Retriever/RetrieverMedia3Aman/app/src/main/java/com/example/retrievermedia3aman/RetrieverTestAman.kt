package com.example.retrievermedia3aman

import android.app.Activity
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.TrackGroupArray
import java.util.Locale
import java.util.concurrent.Future

@OptIn(UnstableApi::class)
class RetrieverTestAman(
    private val activity: Activity, private val mediaPath: String, private val numIter: Int
) {

    fun startMetadataRetrievalTestSerial(context: Context): Long {
        var totalTimeUs: Long = 0L
        for (i in 0..<numIter) {
            val currTimeUs = retrieveMetadataMedia3(context)
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

    private fun retrieveMetadataMedia3(context: Context): Long {
        val startTimeNs = System.nanoTime()

        val metadataRetrieverMedia3: MetadataRetriever = getRetrieverMedia3(context)
        try {
            metadataRetrieverMedia3.retrieveTrackGroups().get()
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            metadataRetrieverMedia3.close()
        }

        return (System.nanoTime() - startTimeNs) / 1000
    }

    private fun retrieveMetadataMedia3Bulk(context: Context): Long {
        val startTimeNs = System.nanoTime()

        val retrieversAndFutures = mutableListOf<Pair<MetadataRetriever, Future<TrackGroupArray>>>()
        for (i in 0..<numIter) {
            val metadataRetrieverMedia3: MetadataRetriever = getRetrieverMedia3(context)
            try {
                val future = metadataRetrieverMedia3.retrieveTrackGroups()
                retrieversAndFutures.add(Pair(metadataRetrieverMedia3, future))
//              Log.d(TAG, "$i: Retriever submitted.")
            } catch (e: Exception) {
                Log.e(TAG, "$i: Error retrieving metadata: $e")
            }
        }

        val numRetrievers = retrieversAndFutures.size
        for (i in 0..<numRetrievers) {
            val (retriever, future) = retrieversAndFutures[i]
            try {
                future.get()
//                Log.d(TAG, "$i: Future completed for retriever.")
            } catch (e: Exception) {
                Log.e(TAG, "$i: Error waiting for future of retriever to complete", e)
            } finally {
                try {
                    retriever.close()
//                    Log.d(TAG, "$i: Retriever destroyed.")
                } catch (e: Exception) {
                    Log.e(TAG, "$i Error closing retriever.", e)
                }
            }
        }

        return (System.nanoTime() - startTimeNs) / 1000
    }

    private fun getRetrieverMedia3(context: Context): MetadataRetriever {
        val mediaItem = MediaItem.fromUri(mediaPath)
        val metadataRetrieverMedia3 = MetadataRetriever.Builder(context, mediaItem)
//                .setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
            .build()
        return metadataRetrieverMedia3
    }

}