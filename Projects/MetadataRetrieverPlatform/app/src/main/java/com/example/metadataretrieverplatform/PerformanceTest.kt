package com.example.metadataretrieverplatform

import android.app.Activity
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import java.util.Locale

class PerformanceTest(
    private val activity: Activity, private val mediaPath: String, private val numIter: Int
) {

    fun startMetadataRetrievalTest(): Long {
        if (Util.maybeRequestReadStoragePermission(activity, MediaItem.fromUri(mediaPath))) {
            return -1L;
        }

        var totalTimeUs: Long = 0L
        for (i in 0..<numIter) {
            val currTimeUs = retrieveMetadataPlatform()
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

    private fun retrieveMetadataPlatform(): Long {
        val startTimeNs = System.nanoTime()

        val metadataRetrieverPlatform: MediaMetadataRetriever = getRetrieverPlatform()
        try {
            metadataRetrieverPlatform.extractMetadata(METADATA_KEY_HAS_VIDEO)
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            metadataRetrieverPlatform.release()
        }

        return (System.nanoTime() - startTimeNs) / 1000
    }

    private fun getRetrieverPlatform(): MediaMetadataRetriever {
        val metadataRetrieverPlatform = MediaMetadataRetriever()
        metadataRetrieverPlatform.setDataSource(mediaPath);
        return metadataRetrieverPlatform
    }

}