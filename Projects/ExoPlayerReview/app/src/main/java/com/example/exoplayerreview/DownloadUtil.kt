package com.example.exoplayerreview

import android.content.Context
import android.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import java.io.File
import java.util.concurrent.Executor

@UnstableApi
object DownloadUtil {
    private var dataSourceFactory: DataSource.Factory? = null
    private var httpDataSourceFactory: DefaultHttpDataSource.Factory? = null
    private var downloadCache: Cache? = null
    private var downloadDirectory: File? = null
    private var databaseProvider: DatabaseProvider? = null
    private var downloadManager: DownloadManager? = null
    private var downloadNotificationHelper: DownloadNotificationHelper? = null
    private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"

    fun getDownloadManager(context: Context): DownloadManager {
        if (downloadManager == null) {
            downloadManager = DownloadManager(
                context,
                getDatabaseProvider(context),
                getDownloadCache(context),
                getHttpDataSourceFactory(context),
                Executor(Runnable::run)
            )
            downloadManager?.addListener(object : DownloadManager.Listener {
                override fun onDownloadChanged(
                    downloadManager: DownloadManager, download: Download, finalException: Exception?
                ) {
                    Log.d("Aman", "Download state: ${download.state}")
                    when (download.state) {
                        Download.STATE_DOWNLOADING -> Log.d("Aman", "Download in progress")
                        Download.STATE_COMPLETED -> Log.d("Aman", "Download completed")
                        Download.STATE_FAILED -> Log.e("Aman", "Download failed")
                    }
                }
            })
        }
        return downloadManager!!
    }

    fun getDataSourceFactory(context: Context): DataSource.Factory {
        if (dataSourceFactory == null) {
            val upstreamFactory = DefaultDataSource.Factory(
                context, getHttpDataSourceFactory(context)
            )
            dataSourceFactory = buildReadOnlyCacheDataSource(
                upstreamFactory, getDownloadCache(context)
            )
        }
        return dataSourceFactory!!
    }

    private fun getDatabaseProvider(context: Context): DatabaseProvider {
        if (databaseProvider == null) {
            databaseProvider = StandaloneDatabaseProvider(context)
        }
        return databaseProvider!!
    }

    private fun getDownloadCache(context: Context): Cache {
        if (downloadCache == null) {
            downloadCache = SimpleCache(
                getDownloadDirectory(context), NoOpCacheEvictor(), getDatabaseProvider(context)
            )
        }
        return downloadCache!!
    }

    private fun getDownloadDirectory(context: Context): File {
        if (downloadDirectory == null) {
            downloadDirectory = context.getExternalFilesDir(null)
            if (downloadDirectory == null) {
                downloadDirectory = context.filesDir
            }
        }
        return downloadDirectory!!
    }

    private fun getHttpDataSourceFactory(context: Context): DefaultHttpDataSource.Factory {
        if (httpDataSourceFactory == null) {
            httpDataSourceFactory = DefaultHttpDataSource.Factory()
        }
        return httpDataSourceFactory!!
    }

    private fun buildReadOnlyCacheDataSource(
        upstreamFactory: DataSource.Factory, cache: Cache
    ): CacheDataSource.Factory {
        val eventListener = object : CacheDataSource.EventListener {
            override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                Log.d("Aman", "Read $cachedBytesRead bytes from cache")
            }

            override fun onCacheIgnored(reason: Int) {
                Log.w("Aman", "Cache ignored: reason $reason")
            }
        }

        return CacheDataSource.Factory().setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory).setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR).setEventListener(eventListener)
    }

    fun getDownloadNotificationHelper(
        context: Context
    ): DownloadNotificationHelper {
        if (downloadNotificationHelper == null) {
            downloadNotificationHelper = DownloadNotificationHelper(
                context, DOWNLOAD_NOTIFICATION_CHANNEL_ID
            )
        }
        return downloadNotificationHelper!!
    }
}