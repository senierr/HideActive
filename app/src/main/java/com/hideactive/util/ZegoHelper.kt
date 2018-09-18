package com.hideactive.util

import android.app.Application
import com.hideactive.app.SessionApplication
import com.zego.zegoliveroom.ZegoLiveRoom
import com.zego.zegoliveroom.callback.IZegoInitSDKCompletionCallback

/**
 * Zego辅助类
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
object ZegoHelper {

    private const val APP_ID = 1L
    private const val APP_SIGN = ""

    var zegoLiveRoom = ZegoLiveRoom()

    /**
     * 初始化
     */
    fun init() {
        ZegoLiveRoom.setSDKContext(object : ZegoLiveRoom.SDKContext {
            override fun getAppContext(): Application = SessionApplication.instance

            override fun getSoFullPath(): String? = null

            override fun getLogPath(): String? = null
        })

        zegoLiveRoom.initSDK(APP_ID, APP_SIGN.toByteArray(), IZegoInitSDKCompletionCallback {

        })
    }

    /**
     * 设置用户
     */
    fun setUser(userId: String, userName: String) {
        ZegoLiveRoom.setUser(userId, userName)
    }
}