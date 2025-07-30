package com.example.exoplayerreview


import android.content.Context
import android.net.Uri
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadIndex
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import java.io.IOException

@UnstableApi
class DownloadTracker(
    context: Context, downloadManager: DownloadManager
) {
    private val context: Context = context.applicationContext
    private val downloadsMap: HashMap<Uri, Download> = HashMap()
    private val downloadIndex: DownloadIndex = downloadManager.downloadIndex

    companion object {
        private const val TAG: String = "Aman"
    }

    init {
        loadDownloads()
    }

    private fun loadDownloads() {
        try {
            downloadIndex.getDownloads().use { loadedDownloads ->
                while (loadedDownloads.moveToNext()) {
                    Log.i(TAG, "Found download: " + loadedDownloads.download.request.uri)
                    val download = loadedDownloads.download
                    downloadsMap[download.request.uri] = download
                }
            }
        } catch (e: IOException) {
            Log.w(TAG, "Failed to query downloads", e)
        }
    }

    fun startDownload(
        contentId: String, contentUri: Uri
    ) {
        val downloadRequest = DownloadRequest.Builder(contentId, contentUri).build()
        DownloadService.sendAddDownload(
            context, DemoDownloadService::class.java, downloadRequest,  /* foreground= */false
        )
        Log.i(TAG, "Starting download for $contentUri")
    }

    fun isDownloaded(contentUri: Uri): Boolean {
        val download = downloadsMap[contentUri]
        return download != null && download.state != Download.STATE_FAILED
    }
}