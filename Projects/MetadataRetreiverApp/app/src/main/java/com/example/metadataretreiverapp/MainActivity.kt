package com.example.metadataretreiverapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.MetadataRetriever

const val TAG = "Aman"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runMetadataRetriever(this)
    }

    @OptIn(UnstableApi::class)
    private fun runMetadataRetriever(context: Context){
        val mediaItem = MediaItem.fromUri(getString(R.string.video_url))

        val metadataRetriever : MetadataRetriever = MetadataRetriever.Builder(context, mediaItem).build()

        Log.i(TAG, "MetadataRetriever closed.")
        metadataRetriever.close()
    }
}