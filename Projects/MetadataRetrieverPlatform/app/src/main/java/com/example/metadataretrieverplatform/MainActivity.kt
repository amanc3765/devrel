package com.example.metadataretrieverplatform

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import kotlin.math.roundToLong

const val TAG = "PlatformTest"

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    private val mediaPathList = mutableListOf(
//        "/sdcard/Download/mediadataset/file_example_MP4_480_1_5MG.mp4",
//        "/sdcard/Download/mediadataset/file_example_MP4_640_3MG.mp4",
//        "/sdcard/Download/mediadataset/file_example_MP4_1280_10MG.mp4",
//        "/sdcard/Download/mediadataset/file_example_MP4_1920_18MG.mp4",
//        "/sdcard/Download/mediadataset/video_1920x1080_5s_2000k_libx264.mp4",
//        "/sdcard/Download/mediadataset/video_1920x1080_5s_5000k_libvpx-vp9.webm",
//        "/sdcard/Download/mediadataset/video_1920x1080_5s_5000k_libaom-av1.mkv",
        "/sdcard/Download/mediadataset/video_1920x1080_30s_5000k_libaom-av1.mkv",
        "/sdcard/Download/mediadataset/video_1920x1080_30s_5000k_libx264.mp4",
        "/sdcard/Download/mediadataset/video_1920x1080_30s_5000k_libvpx-vp9.webm"
    )

    private var extractorTimeMap = mutableMapOf<String, MutableList<Long>>()
    private var retrieverTimeMap = mutableMapOf<String, MutableList<Long>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractorTest()
//        retrieverTest()
    }

    private fun extractorTest() {
        for (i in 0..<50) {
            mediaPathList.shuffle()
            mediaPathList.forEach { mediaPath ->
                val meanTimeUs = ExtractorTest(mediaPath, 5).startExtractorTest(this)
                extractorTimeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        extractorTimeMap.forEach { (mediaPath, timeList) ->
            val meanTimeMs = (timeList.average() / 1000).roundToLong()
            Log.d(TAG, "Extraction time for $mediaPath: $meanTimeMs ms")
        }
    }

    private fun retrieverTest() {
        for (i in 0..<25) {
            mediaPathList.shuffle()
            mediaPathList.forEach { mediaPath ->
                val meanTimeUs = PerformanceTest(this, mediaPath, 5).startMetadataRetrievalTest()
                retrieverTimeMap.getOrPut(mediaPath) { mutableListOf() }.add(meanTimeUs)
            }
        }

        retrieverTimeMap.forEach { (mediaPath, timeList) ->
            val meanTime = timeList.average()
            Log.d(TAG, "Retriever time for $mediaPath: $meanTime µs")
        }
    }
}