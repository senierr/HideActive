package com.module.library.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat

/**
 * 通知工具
 *
 * @author zhouchunjie
 * @date 2018/5/4
 */
object NotificationUtil {

    private const val CHANNEL_ID_SYSTEM = "system"
    private const val CHANNEL_NAME_SYSTEM = "system"

    /**
     * 获取建造者
     */
    fun getBuilder(context: Context,
                   channelId: String = CHANNEL_ID_SYSTEM,
                   channelName: String = CHANNEL_NAME_SYSTEM
    ) : NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(NotificationChannel(
                    channelId, channelName,
                    NotificationManager.IMPORTANCE_HIGH))
        }
        return NotificationCompat.Builder(context, channelId)
    }
}