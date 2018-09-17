package com.hideactive.comm

import android.content.Context
import com.hideactive.domain.AgoraActivity
import com.senierr.repository.bean.Channel

/**
 * 通话管理
 *
 * @author zhouchunjie
 * @date 2018/8/31
 */
object ChatManager {

    fun startChat(context: Context, channel: Channel) {
        when (channel.line) {
            "1" -> {
                AgoraActivity.startChat(context, channel.objectId)
            }
        }
    }
}