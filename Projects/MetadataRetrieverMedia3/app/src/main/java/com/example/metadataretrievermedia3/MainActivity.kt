package com.example.metadataretrievermedia3

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi

const val TAG = "Media3Test"

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    private val mediaPathList = mutableListOf(
        "/sdcard/Download/mediadataset/file_example_MP4_480_1_5MG.mp4",
        "/sdcard/Download/mediadataset/file_example_MP4_640_3MG.mp4",
        "/sdcard/Download/mediadataset/file_example_MP4_1280_10MG.mp4",
        "/sdcard/Download/mediadataset/file_example_MP4_1920_18MG.mp4",
    )


    private var extractorTimeMap = mutableMapOf<String, MutableList<Long>>()
    private var retrieverTimeMap = mutableMapOf<String, MutableList<Long>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        extractorTest()
//        retrieverTest()
    }

    private fun extractorTest() {
        for (i in 0..<25) {
            mediaPathList.shuffle()
            mediaPathList.forEach { mediaPath ->
                val meanTimeUs = ExtractorTest(mediaPath, 5).startExtractorTest(this)
                extractorTimeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        extractorTimeMap.forEach { (mediaPath, timeList) ->
            val meanTime = timeList.average()
            Log.d(TAG, "Extraction time for $mediaPath: $meanTime µs")
        }
    }

    private fun retrieverTest() {
        for (i in 0..<25) {
            mediaPathList.shuffle()
            mediaPathList.forEach { mediaPath ->
                val performanceTest = PerformanceTest(this, mediaPath, 5)
                val meanTimeUs = performanceTest.startMetadataRetrievalTest(this)
                retrieverTimeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        retrieverTimeMap.forEach { (mediaPath, timeList) ->
            val meanTime = timeList.average()
            Log.d(TAG, "Retriever time for $mediaPath: $meanTime µs")
        }
    }
}