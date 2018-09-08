package com.hideactive.domain.push

import android.content.Context
import android.util.Log
import com.hideactive.util.LogUtil
import com.senierr.repository.Repository
import com.senierr.repository.service.api.IPushService
import com.senierr.repository.service.api.IUserService
import com.tencent.android.tpush.*
import io.reactivex.schedulers.Schedulers

/**
 * 信鸽推送接收
 *
 * @author zhouchunjie
 * @date 2018/9/8
 */
class MessageReceiver : XGPushBaseReceiver() {

    private val userService = Repository.getService<IUserService>()
    private val pushService = Repository.getService<IPushService>()

    override fun onRegisterResult(p0: Context?, p1: Int, p2: XGPushRegisterResult?) {
        LogUtil.logE("onRegisterResult: " + p2?.token.orEmpty())
        if (p2 == null) return
        userService.getLocalUser()
                .flatMap {
                    return@flatMap pushService.register(it.objectId, p2.token)
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun onUnregisterResult(p0: Context?, p1: Int) {
        LogUtil.logE("onUnregisterResult: $p1")
        userService.getLocalUser()
                .flatMap {
                    return@flatMap pushService.unregister(it.objectId)
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun onTextMessage(p0: Context?, p1: XGPushTextMessage?) {
        LogUtil.logE("onTextMessage: ${p1.toString()}")
    }

    override fun onSetTagResult(p0: Context?, p1: Int, p2: String?) {
    }

    override fun onNotifactionShowedResult(p0: Context?, p1: XGPushShowedResult?) {
    }

    override fun onDeleteTagResult(p0: Context?, p1: Int, p2: String?) {
    }

    override fun onNotifactionClickedResult(p0: Context?, p1: XGPushClickedResult?) {
    }
}