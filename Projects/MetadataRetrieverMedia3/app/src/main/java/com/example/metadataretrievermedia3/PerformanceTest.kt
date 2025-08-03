package com.example.metadataretrievermedia3

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.MetadataRetriever
import java.util.Locale

@OptIn(UnstableApi::class)
class PerformanceTest(
    private val activity: Activity, private val mediaPath: String, private val numIter: Int
) {

    fun startMetadataRetrievalTest(context: Context): Long {
        if (Util.maybeRequestReadStoragePermission(activity, MediaItem.fromUri(mediaPath))) {
            return -1L;
        }

        var totalTimeUs: Long = 0L
        for (i in 0..<numIter) {
            val currTimeUs = retrieveMetadataMedia3(context)
            totalTimeUs += currTimeUs
            Log.d(
                TAG, String.format(
                    Locale.ROOT, "%30s %3d: %7d", mediaPath, i, currTimeUs
                )
            )
        }

        val meanTimeUs = totalTimeUs / numIter
        Log.d(
            TAG, String.format(
                Locale.ROOT, "(single) %7d", meanTimeUs,
            )
        )

        return meanTimeUs
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

    private fun getRetrieverMedia3(context: Context): MetadataRetriever {
        val mediaItem = MediaItem.fromUri(mediaPath)
        val metadataRetrieverMedia3 = MetadataRetriever.Builder(context, mediaItem).build()
        return metadataRetrieverMedia3
    }

}