package com.example.metadataretreiverapp

import android.app.Activity
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DATE
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.get
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.TrackGroupArray
import com.google.common.util.concurrent.MoreExecutors
import java.io.IOException
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.Future


const val TAG = "Aman"

@OptIn(UnstableApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        runMetadataRetriever(this)

//        CoroutineScope(Dispatchers.IO).launch {
//            runMediaMetadataRetriever(this@MainActivity)
//        }

        doMetadataRetrievalTest(this)
//        PerfUtil.startMetadataRetrievalTest(this);
    }

    private fun doMetadataRetrievalTest(context: Activity) {

        retrieveMetadataPlatform(context)
        retrieveMetadataMedia3(context)

        val numIterations = 100
        var platformTotal: Long = 0L
        var media3Total: Long = 0L
        for (i in 0..<numIterations) {
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
                Locale.ROOT,
                "(single) %7d %7d",
                platformTotal / numIterations,
                media3Total / numIterations
            )
        )

//        val platformBulkAverage =
//            retrieveMetadataPlatformBulk(context, numIterations) / numIterations
//        val media3BulkAverage = retrieveMetadataMedia3Bulk(context, numIterations) / numIterations
//        Log.i(
//            TAG, "(bulk) platform: $platformBulkAverage, media3: $media3BulkAverage"
//        )
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

//    private fun retrieveMetadataPlatformBulk(context: Context, count: Int): Long {
//        val executors = Executors.newFixedThreadPool(1)
//        val startTimeNs = System.nanoTime()
//
//        val futures = mutableListOf<Future<*>>()
//        for (i in 0..<count) {
//            futures.add(executors.submit {
//                val metadataRetrieverPlatform: MediaMetadataRetriever =
//                    getRetrieverPlatform(context)
//                try {
//                    metadataRetrieverPlatform.extractMetadata(METADATA_KEY_HAS_VIDEO)
//                } catch (e: Exception) {
//                    throw RuntimeException(e)
//                } finally {
//                    metadataRetrieverPlatform.release()
//                }
//            })
//        }
//        for (i in 0..<count) {
//            try {
//                futures[i].get()
//            } catch (e: Exception) {
//                throw RuntimeException(e)
//            }
//        }
//
//        val totalTime = (System.nanoTime() - startTimeNs) / 1000
//        executors.shutdown()
//        return totalTime
//    }
//
//    private fun retrieveMetadataMedia3Bulk(context: Context, count: Int): Long {
//        val startTimeNs = System.nanoTime()
//
//        val retrieversAndFutures = mutableListOf<Pair<MetadataRetriever, Future<TrackGroupArray>>>()
//        for (i in 0..<count) {
//            val metadataRetrieverMedia3: MetadataRetriever = getRetrieverMedia3(context)
//            try {
//                val future = metadataRetrieverMedia3.retrieveTrackGroups()
//                retrieversAndFutures.add(Pair(metadataRetrieverMedia3, future))
//            } catch (e: Exception) {
//                Log.e(TAG, "Error retrieving track groups", e)
//            }
//        }
//        for ((_, future) in retrieversAndFutures) {
//            try {
//                future.get()
//            } catch (e: Exception) {
//                Log.e(TAG, "Error waiting for a Media3 future to complete", e)
//            }
//        }
//        for ((retriever, _) in retrieversAndFutures) {
//            try {
//                retriever.close()
//            } catch (e: Exception) {
//                Log.e(TAG, "Error closing a Media3 retriever instance", e)
//            }
//        }
//
//        val totalTime = (System.nanoTime() - startTimeNs) / 1000
//        return totalTime
//    }

    private fun getRetrieverPlatform(context: Context): MediaMetadataRetriever {
        val metadataRetrieverPlatform = MediaMetadataRetriever()
//        val afd: AssetFileDescriptor = getMediaPlatform(context)
//        metadataRetrieverPlatform.setDataSource(
//            afd.fileDescriptor, afd.startOffset, afd.length
//        )
        val path = "/sdcard/Download/sample.mp4"
        metadataRetrieverPlatform.setDataSource(path);
        return metadataRetrieverPlatform
    }

    private fun getMediaPlatform(context: Context): AssetFileDescriptor {
        return context.resources.openRawResourceFd(R.raw.sample)
    }

    private fun getRetrieverMedia3(context: Context): MetadataRetriever {
        val mediaItem = getMediaMedia3(context)
        val metadataRetrieverMedia3 = MetadataRetriever.Builder(context, mediaItem).build()
        return metadataRetrieverMedia3
    }

    private fun getMediaMedia3(context: Context): MediaItem {
//        val rawUri = ("android.resource://" + context.packageName + "/" + R.raw.sample).toUri()
        val rawUri = "/sdcard/Download/sample.mp4"
        val mediaItem = MediaItem.fromUri(rawUri)
        return mediaItem
    }

//    private fun runMetadataRetriever(context: Context) {
//        val mediaItem = MediaItem.fromUri(getString(R.string.video_url))
////        val mediaItem = MediaItem.fromUri(getString(R.string.audio_url))
//
//        val metadataRetriever: MetadataRetriever =
//            MetadataRetriever.Builder(context, mediaItem).build()
//
//        val durationFuture = metadataRetriever.retrieveDurationUs()
//        durationFuture.addListener(
//            {
//                try {
//                    val durationUs = durationFuture.get()  // Microseconds
//                    val totalSeconds = durationUs / 1_000_000
//                    val minutes = totalSeconds / 60
//                    val seconds = totalSeconds % 60
//                    Log.d(TAG, "Duration: $durationUs µs ($minutes m $seconds s)")
//                } catch (e: Exception) {
//                    Log.e(TAG, "Error retrieving duration", e)
//                }
//            }, MoreExecutors.directExecutor() // Executes on same thread
//        )
//
//        val timelineFuture = metadataRetriever.retrieveTimeline()
//        timelineFuture.addListener(
//            {
//                try {
//                    val timeline = timelineFuture.get()
//                    Log.d(TAG, "Timeline: $timeline")
//
//                    RetrieverUtil.printTimeline(timeline)
//                } catch (e: Exception) {
//                    Log.e(TAG, "Error retrieving timeline", e)
//                }
//            }, MoreExecutors.directExecutor() // Executes on same thread
//        )
//
//        val trackGroupsFuture = metadataRetriever.retrieveTrackGroups()
//        trackGroupsFuture.addListener(
//            {
//                try {
//                    val trackGroups = trackGroupsFuture.get()
//                    Log.d(TAG, "Track groups: $trackGroups")
//
//                    RetrieverUtil.printTrackGroups(trackGroups)
//                } catch (e: Exception) {
//                    Log.e(TAG, "Error retrieving track groups", e)
//                }
//            }, MoreExecutors.directExecutor() // Executes on same thread
//        )
//
////        Log.i(TAG, "MetadataRetriever closed.")
////        metadataRetriever.close()
//    }
//
//    private fun runMediaMetadataRetriever(context: Context) {
//        val mediaMetadataRetriever = MediaMetadataRetriever()
//
////        val mediaItemUri = getString(R.string.audio_url).toUri()
////        val mediaItemUri = getString(R.string.video_url).toUri()
////        mediaMetadataRetriever.setDataSource(context, mediaItemUri)
//        mediaMetadataRetriever.setDataSource(
//            getString(R.string.video_url), HashMap<String, String>()
//        )
//        Log.i(TAG, "MetadataRetriever setup.")
//
//        val duration =
//            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        Log.i(TAG, "Duration: $duration")
//    }

}