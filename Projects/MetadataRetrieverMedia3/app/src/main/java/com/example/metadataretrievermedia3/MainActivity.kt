package com.example.metadataretrievermedia3

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import java.io.File
import kotlin.math.roundToLong

const val TAG = "Media3TestToni"

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    private val mediaFolderPath = "/sdcard/Download/mediadataset/bitrate/"
    private val mediaFilesList = getFilesFromFolder()

    private val numWarmupRuns = 3
    private val numTestRuns = 5
    private val numIterations = 50

    private var extractorTimeMap = mutableMapOf<String, MutableList<Long>>()
    private var retrieverTimeMap = mutableMapOf<String, MutableList<Long>>()

    private fun getFilesFromFolder(): MutableList<String> {
        val folder = File(mediaFolderPath)
        if (folder.exists() && folder.isDirectory) {
            return folder.listFiles()?.filter { it.isFile }?.map { it.absolutePath }
                ?.toMutableList() ?: mutableListOf()
        }
        Log.e(TAG, "Folder does not exist or is not a directory: $mediaFolderPath")
        return mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (mediaFilesList.isEmpty()) {
            Log.e(TAG, "No media files found in $mediaFolderPath. Aborting tests.")
            return
        }

//        extractorTest()

//        retrieverTest("Serial") { activity, mediaPath, iterations ->
//            RetrieverTest(
//                activity, mediaPath, iterations
//            ).startMetadataRetrievalTestSerial(activity)
//        }
        retrieverTest("Bulk") { activity, mediaPath, iterations ->
            RetrieverTest(activity, mediaPath, iterations).startMetadataRetrievalTestBulk(activity)
        }

//        retrieverTest("Serial") { activity, mediaPath, iterations ->
//            RetrieverTestToni(
//                activity, mediaPath, iterations
//            ).startMetadataRetrievalTestSerial(activity)
//        }
//        retrieverTest("Bulk") { activity, mediaPath, iterations ->
//            RetrieverTestToni(activity, mediaPath, iterations).startMetadataRetrievalTestBulk(
//                activity
//            )
//        }

    }

    private fun extractorTest() {
        for (i in 0..<3) {
            Log.d(TAG, "Warmup Iteration $i -----------")
            mediaFilesList.shuffle()
            mediaFilesList.forEach { mediaPath ->
                ExtractorTest(mediaPath, 5).startExtractorTest(this)
            }
        }

        for (i in 0..<10) {
            Log.d(TAG, "Iteration $i -----------")
            mediaFilesList.shuffle()
            mediaFilesList.forEach { mediaPath ->
                val meanTimeUs = ExtractorTest(mediaPath, 5).startExtractorTest(this)
                extractorTimeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        extractorTimeMap.forEach { (mediaPath, timeList) ->
            val meanTimeMs = (timeList.average() / 1000).roundToLong()
            Log.d(TAG, "Extraction time for $mediaPath: $meanTimeMs ms")
        }
    }

    private fun retrieverTest(
        mode: String,
        retrievalFunction: (activity: Activity, mediaPath: String, iterations: Int) -> Long
    ) {
        retrieverTimeMap.clear()

        for (i in 1..numWarmupRuns) {
            Log.i(TAG, " ------------- Warmup Run $i ------------- ")
            mediaFilesList.shuffle()
            mediaFilesList.forEach { mediaPath ->
                retrievalFunction(this, mediaPath, numIterations)
            }
        }

        for (i in 1..numTestRuns) {
            Log.i(TAG, " ------------- Test Run $i ------------- ")
            mediaFilesList.shuffle()
            mediaFilesList.forEach { mediaPath ->
                val meanTimeUs = retrievalFunction(this, mediaPath, numIterations)
                retrieverTimeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        Log.i(TAG, " ------------- Mean Retriever Times ------------- ")
        retrieverTimeMap.forEach { (mediaPath, timeList) ->
            Log.d(TAG, "Timelist for $mediaPath: $timeList")
            val meanTimeMs = (timeList.average() / 1000).roundToLong()
            Log.d(TAG, "[$TAG][${mode}] Retriever time for $mediaPath: $meanTimeMs ms")
        }
    }

}