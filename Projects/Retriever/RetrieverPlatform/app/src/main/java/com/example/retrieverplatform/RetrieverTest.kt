package com.example.retrieverplatform

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.Future

@OptIn(UnstableApi::class)
class RetrieverTest(
    private val mediaPath: String, private val numIter: Int
) {

    fun startMetadataRetrievalTestSerial(): Long {
        var totalTimeUs: Long = 0L
        for (i in 0..<numIter) {
            val currTimeUs = retrieveMetadataPlatform()
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

    fun startMetadataRetrievalTestBulk(): Long {
        val totalTimeUs = retrieveMetadataPlatformBulk()
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

    private fun retrieveMetadataPlatformBulk(): Long {
        val executors = Executors.newFixedThreadPool(5)
        val startTimeNs = System.nanoTime()

        val retrieversAndFutures = mutableListOf<Pair<MediaMetadataRetriever, Future<*>>>()
        for (i in 0..<numIter) {
            val metadataRetrieverPlatform: MediaMetadataRetriever = getRetrieverPlatform()
            val future = executors.submit {
                try {
                    metadataRetrieverPlatform.extractMetadata(METADATA_KEY_HAS_VIDEO)
                } catch (e: Exception) {
                    Log.e(TAG, "$i: Error retrieving metadata: $e")
                }
            }
            retrieversAndFutures.add(Pair(metadataRetrieverPlatform, future))
//            Log.d(TAG, "$i: Retriever submitted.")
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

        val totalTime = (System.nanoTime() - startTimeNs) / 1000
        executors.shutdown()
        return totalTime
    }

    private fun getRetrieverPlatform(): MediaMetadataRetriever {
        val metadataRetrieverPlatform = MediaMetadataRetriever()
        metadataRetrieverPlatform.setDataSource(mediaPath);
        return metadataRetrieverPlatform
    }

}