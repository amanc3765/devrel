package com.example.exoplayerdemo

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.DefaultRenderersFactory.ExtensionRendererMode
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import java.io.File
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.Executors

@UnstableApi
object DownloadUtil {
    private var downloadManager: DownloadManager? = null
    private var databaseProvider: DatabaseProvider? = null
    private var httpDataSourceFactory: DefaultHttpDataSource.Factory? = null
    private var downloadCache: Cache? = null
    private var downloadDirectory: File? = null
    private var downloadNotificationHelper: DownloadNotificationHelper? = null
    private var downloadTracker: DownloadTracker? = null // Potential memory leak
    private const val DOWNLOAD_CONTENT_DIRECTORY: String = "downloads"
    private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID: String = "download_channel"

    @Synchronized
    fun getDownloadManager(context: Context): DownloadManager {
        ensureDownloadManagerInitialized(context)
        return downloadManager!!
    }

    @Synchronized
    fun getDownloadTracker(context: Context): DownloadTracker {
        ensureDownloadManagerInitialized(context)
        return downloadTracker!!
    }


    @Synchronized
    private fun ensureDownloadManagerInitialized(context: Context) {
        if (downloadManager == null) {
            downloadManager = DownloadManager(
                context,
                getDatabaseProvider(context),
                getDownloadCache(context),
                getHttpDataSourceFactory(context),
                Executors.newFixedThreadPool(6)
            )
            downloadTracker =
                DownloadTracker(context, getHttpDataSourceFactory(context), downloadManager!!)
        }
    }

    @Synchronized
    private fun getDatabaseProvider(context: Context): DatabaseProvider {
        if (databaseProvider == null) {
            databaseProvider = StandaloneDatabaseProvider(context)
        }
        return databaseProvider!!
    }

    @Synchronized
    private fun getDownloadCache(context: Context): Cache {
        if (downloadCache == null) {
            val downloadContentDirectory = File(
                getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY
            )
            downloadCache = SimpleCache(
                downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider(context)
            )
        }
        return downloadCache!!
    }

    @Synchronized
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
        return httpDataSourceFactory ?: synchronized(this) {
            if (httpDataSourceFactory != null) {
                return httpDataSourceFactory!!
            }

            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
            CookieHandler.setDefault(cookieManager)

            httpDataSourceFactory = DefaultHttpDataSource.Factory()
            return httpDataSourceFactory!!
        }
    }

    @Synchronized
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

    fun buildRenderersFactory(
        context: Context
    ): RenderersFactory {
        return DefaultRenderersFactory(context.applicationContext).setExtensionRendererMode(
            DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        )
    }
}