package com.hideactive.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.hideactive.app.SessionApplication

/**
 * 通知工具
 *
 * @author zhouchunjie
 * @date 2018/5/4
 */
object NotificationUtil {

    const val CHANNEL_ID_SYSTEM = "system"
    private const val CHANNEL_NAME_SYSTEM_ = "system"

    const val ID_SYSTEM = 1

    val manager = SessionApplication.instance.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * 获取建造者
     */
    fun getBuilder(channelId: String) : NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when(channelId) {
                CHANNEL_ID_SYSTEM ->
                    manager.createNotificationChannel(NotificationChannel(
                            CHANNEL_ID_SYSTEM, CHANNEL_NAME_SYSTEM_,
                            NotificationManager.IMPORTANCE_HIGH))
            }
        }
        return NotificationCompat.Builder(SessionApplication.instance, channelId)
    }
}