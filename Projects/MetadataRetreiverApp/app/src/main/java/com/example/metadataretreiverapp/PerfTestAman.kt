package com.example.metadataretreiverapp

import android.app.Activity
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.TrackGroupArray
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.Future

@OptIn(UnstableApi::class)
object PerfTestAman {

    private const val MEDIA_PATH = "/sdcard/Download/sample2.mp4"
    private const val NUM_ITER = 50

    fun startMetadataRetrievalTest(context: Activity) {

        retrieveMetadataPlatform(context)
        retrieveMetadataMedia3(context)

        var platformTotal: Long = 0L
        var media3Total: Long = 0L
        for (i in 0..<NUM_ITER) {
            val platform = retrieveMetadataPlatform(context)
            val media3 = retrieveMetadataMedia3(context)
            platformTotal += platform
            media3Total += media3
            Log.d(
                TAG, String.format(
                    Locale.ROOT, "%3d: %7d %7d", i, platform, media3
                )
            )
        }
        Log.d(
            TAG, String.format(
                Locale.ROOT, "(single) %7d %7d", platformTotal / NUM_ITER, media3Total / NUM_ITER
            )
        )

        val media3BulkAverage = retrieveMetadataMedia3Bulk(context, NUM_ITER) / NUM_ITER
        val platformBulkAverage = retrieveMetadataPlatformBulk(context, NUM_ITER) / NUM_ITER
        Log.i(
            TAG, "(bulk) platform: $platformBulkAverage, media3: $media3BulkAverage"
        )
    }

    private fun retrieveMetadataPlatform(context: Context): Long {
        val startTimeNs = System.nanoTime()

        val metadataRetrieverPlatform: MediaMetadataRetriever = getRetrieverPlatform(context)
        try {
            metadataRetrieverPlatform.extractMetadata(METADATA_KEY_HAS_VIDEO)
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            metadataRetrieverPlatform.release()
        }

        return (System.nanoTime() - startTimeNs) / 1000
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

    private fun getRetrieverPlatform(context: Context): MediaMetadataRetriever {
        val metadataRetrieverPlatform = MediaMetadataRetriever()
        metadataRetrieverPlatform.setDataSource(MEDIA_PATH);
        return metadataRetrieverPlatform
    }

    private fun getRetrieverMedia3(context: Context): MetadataRetriever {
        val mediaItem = MediaItem.fromUri(MEDIA_PATH)
        val metadataRetrieverMedia3 = MetadataRetriever.Builder(context, mediaItem).build()
        return metadataRetrieverMedia3
    }

    private fun retrieveMetadataPlatformBulk(context: Context, count: Int): Long {
        val executors = Executors.newFixedThreadPool(5)
        val startTimeNs = System.nanoTime()

        val futures = mutableListOf<Future<*>>()
        for (i in 0..<count) {
            futures.add(executors.submit {
                val metadataRetrieverPlatform: MediaMetadataRetriever =
                    getRetrieverPlatform(context)
                try {
                    metadataRetrieverPlatform.extractMetadata(METADATA_KEY_HAS_VIDEO)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                } finally {
                    metadataRetrieverPlatform.release()
                }
            })
        }
        for (i in 0..<count) {
            try {
                futures[i].get()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        val totalTime = (System.nanoTime() - startTimeNs) / 1000
        executors.shutdown()
        return totalTime
    }

    private fun retrieveMetadataMedia3Bulk(context: Context, count: Int): Long {
        val startTimeNs = System.nanoTime()

        val retrieversAndFutures = mutableListOf<Pair<MetadataRetriever, Future<TrackGroupArray>>>()
        for (i in 0..<count) {
            val metadataRetrieverMedia3: MetadataRetriever = getRetrieverMedia3(context)
            try {
                val future = metadataRetrieverMedia3.retrieveTrackGroups()
                retrieversAndFutures.add(Pair(metadataRetrieverMedia3, future))
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving track groups", e)
            }
        }
        for ((_, future) in retrieversAndFutures) {
            try {
                future.get()
            } catch (e: Exception) {
                Log.e(TAG, "Error waiting for a Media3 future to complete", e)
            }
        }
        for ((retriever, _) in retrieversAndFutures) {
            try {
                retriever.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing a Media3 retriever instance", e)
            }
        }

        val totalTime = (System.nanoTime() - startTimeNs) / 1000
        return totalTime
    }

}