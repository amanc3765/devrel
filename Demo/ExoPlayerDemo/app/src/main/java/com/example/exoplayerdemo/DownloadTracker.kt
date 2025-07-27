package com.example.exoplayerdemo

import android.content.Context
import android.net.Uri
import androidx.fragment.app.FragmentManager
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadHelper
import androidx.media3.exoplayer.offline.DownloadIndex
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import com.google.common.base.Preconditions
import java.io.IOException

@UnstableApi
class DownloadTracker(
    context: Context,
    private val dataSourceFactory: DataSource.Factory,
    downloadManager: DownloadManager
) {

    private val context: Context = context.applicationContext
    private val downloads: HashMap<Uri, Download> = HashMap()
    private val downloadIndex: DownloadIndex = downloadManager.downloadIndex
    private lateinit var downloadHelper: DownloadHelper
    private lateinit var mediaItem: MediaItem

    companion object {
        private const val TAG: String = "DownloadTracker"
    }

    init {
        loadDownloads()
    }

    private fun loadDownloads() {
        try {
            downloadIndex.getDownloads().use { loadedDownloads ->
                while (loadedDownloads.moveToNext()) {
                    Log.i(TAG, "Aman Found download: " + loadedDownloads.download.request.uri)
                    val download = loadedDownloads.download
                    downloads[download.request.uri] = download
                }
            }
        } catch (e: IOException) {
            Log.w(DownloadTracker.TAG, "Failed to query downloads", e)
        }
    }


    fun startDownload(
        myMediaItem: MediaItem, renderersFactory: RenderersFactory?
    ) {
        mediaItem = myMediaItem
        downloadHelper = DownloadHelper.forMediaItem(
            context, mediaItem, renderersFactory, dataSourceFactory
        )
        startDownloadInternal(buildDownloadRequest())
    }

    private fun startDownloadInternal(downloadRequest: DownloadRequest) {
        DownloadService.sendAddDownload(
            context, DemoDownloadService::class.java, downloadRequest,  /* foreground= */false
        )
        Log.i(TAG, "Aman Starting download for $mediaItem")
    }

    private fun buildDownloadRequest(): DownloadRequest {
        return downloadHelper.getDownloadRequest(
            Util.getUtf8Bytes(Preconditions.checkNotNull<String>(mediaItem.mediaMetadata.title.toString()))
        )
    }
}