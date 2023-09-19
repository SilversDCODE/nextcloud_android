package com.nextcloud.client.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nextcloud.client.account.User
import com.owncloud.android.R
import com.owncloud.gshare.utils.theme.ViewThemeUtils
import javax.inject.Inject

class AppNotificationManagerImpl @Inject constructor(
    private val context: Context,
    private val resources: Resources,
    private val platformNotificationsManager: NotificationManager,
    private val viewThemeUtils: ViewThemeUtils
) : AppNotificationManager {

    companion object {
        const val PROGRESS_PERCENTAGE_MAX = 100
        const val PROGRESS_PERCENTAGE_MIN = 0
    }

    private fun builder(channelId: String): NotificationCompat.Builder {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, channelId)
        } else {
            NotificationCompat.Builder(context)
        }
        viewThemeUtils.androidx.themeNotificationCompatBuilder(context, builder)
        return builder
    }

    override fun buildDownloadServiceForegroundNotification(): Notification {
        val icon = BitmapFactory.decodeResource(resources, R.drawable.notification_icon)
        return builder(com.owncloud.gshare.ui.notifications.NotificationUtils.NOTIFICATION_CHANNEL_DOWNLOAD)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(resources.getString(R.string.foreground_service_download))
            .setSmallIcon(R.drawable.notification_icon)
            .setLargeIcon(icon)
            .build()
    }

    override fun postDownloadTransferProgress(fileOwner: User, file: com.owncloud.gshare.datamodel.OCFile, progress: Int, allowPreview: Boolean) {
        val builder = builder(com.owncloud.gshare.ui.notifications.NotificationUtils.NOTIFICATION_CHANNEL_DOWNLOAD)
        val content = resources.getString(
            R.string.downloader_download_in_progress_content,
            progress,
            file.fileName
        )
        builder
            .setSmallIcon(R.drawable.ic_cloud_download)
            .setTicker(resources.getString(R.string.downloader_download_in_progress_ticker))
            .setContentTitle(resources.getString(R.string.downloader_download_in_progress_ticker))
            .setOngoing(true)
            .setProgress(PROGRESS_PERCENTAGE_MAX, progress, progress <= PROGRESS_PERCENTAGE_MIN)
            .setContentText(content)

        if (allowPreview) {
            val openFileIntent = if (com.owncloud.gshare.ui.preview.PreviewImageFragment.canBePreviewed(file)) {
                com.owncloud.gshare.ui.preview.PreviewImageActivity.previewFileIntent(context, fileOwner, file)
            } else {
                com.owncloud.gshare.ui.activity.FileDisplayActivity.openFileIntent(context, fileOwner, file)
            }
            openFileIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val pendingOpenFileIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                openFileIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingOpenFileIntent)
        }
        platformNotificationsManager.notify(AppNotificationManager.TRANSFER_NOTIFICATION_ID, builder.build())
    }

    override fun postUploadTransferProgress(fileOwner: User, file: com.owncloud.gshare.datamodel.OCFile, progress: Int) {
        val builder = builder(com.owncloud.gshare.ui.notifications.NotificationUtils.NOTIFICATION_CHANNEL_DOWNLOAD)
        val content = resources.getString(
            R.string.uploader_upload_in_progress_content,
            progress,
            file.fileName
        )
        builder
            .setSmallIcon(R.drawable.ic_cloud_upload)
            .setTicker(resources.getString(R.string.uploader_upload_in_progress_ticker))
            .setContentTitle(resources.getString(R.string.uploader_upload_in_progress_ticker))
            .setOngoing(true)
            .setProgress(PROGRESS_PERCENTAGE_MAX, progress, progress <= PROGRESS_PERCENTAGE_MIN)
            .setContentText(content)

        platformNotificationsManager.notify(AppNotificationManager.TRANSFER_NOTIFICATION_ID, builder.build())
    }

    override fun cancelTransferNotification() {
        platformNotificationsManager.cancel(AppNotificationManager.TRANSFER_NOTIFICATION_ID)
    }
}
