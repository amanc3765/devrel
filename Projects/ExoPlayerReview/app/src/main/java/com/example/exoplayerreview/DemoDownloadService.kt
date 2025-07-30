package com.example.exoplayerreview

import android.app.Notification
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Scheduler

@UnstableApi
class DemoDownloadService : DownloadService(FOREGROUND_NOTIFICATION_ID_NONE) {

    companion object {
        private const val JOB_ID: Int = 1
    }

    override fun getDownloadManager(): DownloadManager {
        return DownloadUtil.getDownloadManager(this)
    }

    override fun getScheduler(): Scheduler {
        return PlatformScheduler(this, JOB_ID)
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>, notMetRequirements: Int
    ): Notification {
        return DownloadUtil.getDownloadNotificationHelper( /* context= */this)
            .buildProgressNotification( /* context= */
                this, R.drawable.ic_launcher_foreground,  /* contentIntent= */
                null,  /* message= */
                null, downloads, notMetRequirements
            )
    }
}