package com.hideactive.app

import android.app.Application
import com.hideactive.R
import com.hideactive.util.LogUtil
import com.hideactive.util.ZegoHelper
import com.senierr.repository.Repository

/**
 * 应用入口
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class SessionApplication : Application() {

    companion object {
        @JvmStatic lateinit var instance: SessionApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Repository.initialize(this)

        LogUtil.isDebug = true
        LogUtil.tag = getString(R.string.app_name)

        ZegoHelper.init()
    }
}