package com.hideactive.domain.receiver

import android.content.Context
import com.hideactive.util.LogUtil
import com.tencent.android.tpush.*

/**
 * XG推送消息接受
 *
 * @author zhouchunjie
 * @date 2018/9/2
 */
class MessageReceiver : XGPushBaseReceiver() {

    override fun onRegisterResult(p0: Context?, p1: Int, p2: XGPushRegisterResult?) {
        LogUtil.logE("onRegisterResult: " + p2?.token.orEmpty())
    }

    override fun onUnregisterResult(p0: Context?, p1: Int) {
        LogUtil.logE("onUnregisterResult: $p1")
    }

    override fun onTextMessage(p0: Context?, p1: XGPushTextMessage?) {
        LogUtil.logE("onTextMessage: " + p1?.customContent.orEmpty())
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