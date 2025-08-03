package com.example.metadataretrieverplatform

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

const val TAG = "PlatformTest"

class MainActivity : ComponentActivity() {

    private val mediaPathList = mutableListOf(
        "/sdcard/Download/mediadataset/file_example_MP4_480_1_5MG.mp4",
        "/sdcard/Download/mediadataset/file_example_MP4_640_3MG.mp4",
        "/sdcard/Download/mediadataset/file_example_MP4_1280_10MG.mp4",
        "/sdcard/Download/mediadataset/file_example_MP4_1920_18MG.mp4",
    )

    private var timeMap = mutableMapOf<String, MutableList<Long>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (i in 0..<25) {
            mediaPathList.shuffle()
            mediaPathList.forEach { mediaPath ->
                val performanceTest = PerformanceTest(this, mediaPath, 5)
                val meanTimeUs = performanceTest.startMetadataRetrievalTest()
                timeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        timeMap.forEach { (mediaPath, timeList) ->
            val meanTime = timeList.average()
            Log.d(TAG, "Mean time for $mediaPath: $meanTime µs")
        }
    }
}