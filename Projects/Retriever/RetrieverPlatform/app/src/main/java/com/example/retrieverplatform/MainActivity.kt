package com.example.retrieverplatform

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import java.io.File
import kotlin.math.roundToLong

const val TAG = "PlatformTest"

class MainActivity : ComponentActivity() {

    private val mediaFolderPath = "/sdcard/Download/mediadataset/bitrate/"
    private lateinit var mediaFilesList: MutableList<String>
    private var retrieverTimeMap = mutableMapOf<String, MutableList<Long>>()

    private val numWarmupRuns = 3
    private val numTestRuns = 5
    private val numIterations = 50

    private fun initializeMediaFiles() {
        Util.maybeRequestReadStoragePermission(
            this, MediaItem.fromUri(mediaFolderPath)
        )

        val folder = File(mediaFolderPath)
        if (folder.exists() && folder.isDirectory) {
            mediaFilesList =
                folder.listFiles()?.filter { it.isFile }?.map { it.absolutePath }?.toMutableList()
                    ?: mutableListOf()
        } else {
            Log.e(TAG, "Folder does not exist or is not a directory: $mediaFolderPath")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeMediaFiles()
        if (mediaFilesList.isEmpty()) {
            Log.e(TAG, "No media files found in $mediaFolderPath. Aborting tests.")
            return
        }
        mediaFilesList.forEach {
            Log.d(TAG, it)
        }

        retrieverTest("Serial") { mediaPath, iterations ->
            RetrieverTest(
                mediaPath, iterations
            ).startMetadataRetrievalTestSerial()
        }
        retrieverTest("Bulk") { mediaPath, iterations ->
            RetrieverTest(mediaPath, iterations).startMetadataRetrievalTestBulk()
        }
    }

    private fun retrieverTest(
        mode: String, retrievalFunction: (mediaPath: String, iterations: Int) -> Long
    ) {
        retrieverTimeMap.clear()

        for (i in 1..numWarmupRuns) {
            Log.i(TAG, " ------------- Warmup Run $i ------------- ")
            mediaFilesList.shuffle()
            mediaFilesList.forEach { mediaPath ->
                retrievalFunction(mediaPath, numIterations)
            }
        }

        for (i in 1..numTestRuns) {
            Log.i(TAG, " ------------- Test Run $i ------------- ")
            mediaFilesList.shuffle()
            mediaFilesList.forEach { mediaPath ->
                val meanTimeUs = retrievalFunction(mediaPath, numIterations)
                retrieverTimeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        Log.i(TAG, " ------------- Mean Retriever Times ------------- ")
        retrieverTimeMap.forEach { (mediaPath, timeList) ->
            Log.d(TAG, "$timeList")
            val meanTimeMs = (timeList.average() / 1000).roundToLong()
            Log.d(TAG, "[$TAG][${mode}] Retriever time for $mediaPath: $meanTimeMs ms")
        }
    }

}