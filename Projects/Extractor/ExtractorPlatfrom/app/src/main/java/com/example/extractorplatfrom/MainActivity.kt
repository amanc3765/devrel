package com.example.extractorplatfrom

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import java.io.File
import kotlin.math.roundToLong

const val TAG = "PlatformTest"

// /sdcard/Download/mediadataset/1_duration
// /sdcard/Download/mediadataset/2_fps
// /sdcard/Download/mediadataset/3_bitrate
// /sdcard/Download/mediadataset/4_resolution
// /sdcard/Download/mediadataset/6_container

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    private val mediaFolderPath = "/sdcard/Download/dataset/4_resolution/"
    private lateinit var mediaFilesList: MutableList<String>
    private var extractorTimeMap = mutableMapOf<String, MutableList<Long>>()

    private val numWarmupRuns = 3
    private val numTestRuns = 3
    private val numIterations = 3

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

        extractorTest()
    }

    private fun extractorTest(
    ) {
        extractorTimeMap.clear()

        for (i in 1..numWarmupRuns) {
            Log.i(TAG, " ------------- Warmup Run $i ------------- ")
            mediaFilesList.shuffle()
            mediaFilesList.forEach { mediaPath ->
                ExtractorTest(mediaPath, numIterations).startExtractorTest(this)
            }
        }

        for (i in 1..numTestRuns) {
            Log.i(TAG, " ------------- Test Run $i ------------- ")
            mediaFilesList.shuffle()
            mediaFilesList.forEach { mediaPath ->
                val meanTimeUs = ExtractorTest(mediaPath, numIterations).startExtractorTest(this)
                extractorTimeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        Log.i(TAG, " ------------- Mean Extractor Times ------------- ")
        extractorTimeMap.forEach { (mediaPath, timeList) ->
//            Log.d(TAG, "$timeList")
            val meanTimeMs = (timeList.average() / 1000).roundToLong()
            Log.d(TAG, "[$TAG] Extractor time for $mediaPath: $meanTimeMs ms")
        }
    }

}