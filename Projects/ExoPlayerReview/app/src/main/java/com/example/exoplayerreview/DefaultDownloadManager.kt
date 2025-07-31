package com.example.exoplayerreview

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.offline.DownloadManager

@UnstableApi
interface DefaultDownloadManager {

    fun getDownloadManager(context: Context): DownloadManager

    fun getCacheDataSourceFactory(context: Context): DataSource.Factory

    fun getUpstreamDataSourceFactory(): DataSource.Factory

}