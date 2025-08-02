package com.example.metadataretreiverapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi


const val TAG = "Aman"

@OptIn(UnstableApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        PerfTestToni.startMetadataRetrievalTest(this);
        PerfTestAman.startMetadataRetrievalTest(this)
    }
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
