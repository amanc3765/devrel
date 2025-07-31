package com.example.exoplayerreview

import android.content.Context
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
import androidx.media3.exoplayer.offline.DownloadManager
import java.io.File
import java.util.concurrent.Executor

@UnstableApi
object DefaultDownloadManagerImpl : DefaultDownloadManager {
    private var dataSourceFactory: DataSource.Factory? = null
    private var httpDataSourceFactory: DefaultHttpDataSource.Factory? = null
    private var downloadCache: Cache? = null
    private var downloadDirectory: File? = null
    private var databaseProvider: DatabaseProvider? = null
    private var downloadManager: DownloadManager? = null

    override fun getDownloadManager(context: Context): DownloadManager {
        if (downloadManager == null) {
            downloadManager = DownloadManager(
                context,
                getDatabaseProvider(context),
                getDownloadCache(context),
                getHttpDataSourceFactory(),
                Executor(Runnable::run)
            )
        }
        return downloadManager!!
    }

    override fun getCacheDataSourceFactory(context: Context): DataSource.Factory {
        if (dataSourceFactory == null) {
            val upstreamFactory = DefaultDataSource.Factory(
                context, getUpstreamDataSourceFactory()
            )
            val downloadCache = getDownloadCache(context)
            dataSourceFactory = CacheDataSource.Factory().setCache(downloadCache)
                .setUpstreamDataSourceFactory(upstreamFactory).setCacheWriteDataSinkFactory(null)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }
        return dataSourceFactory!!
    }

    override fun getUpstreamDataSourceFactory(): DataSource.Factory {
        return getHttpDataSourceFactory()
    }

    private fun getHttpDataSourceFactory(): DefaultHttpDataSource.Factory {
        if (httpDataSourceFactory == null) {
            httpDataSourceFactory = DefaultHttpDataSource.Factory()
        }
        return httpDataSourceFactory!!
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

    private fun getDatabaseProvider(context: Context): DatabaseProvider {
        if (databaseProvider == null) {
            databaseProvider = StandaloneDatabaseProvider(context)
        }
        return databaseProvider!!
    }
}