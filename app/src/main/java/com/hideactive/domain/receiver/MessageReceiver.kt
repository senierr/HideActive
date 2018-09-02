package com.hideactive.domain.receiver

import android.content.Context
import com.tencent.android.tpush.*

/**
 * XG推送消息接受
 *
 * @author zhouchunjie
 * @date 2018/9/2
 */
class MessageReceiver : XGPushBaseReceiver() {
    override fun onSetTagResult(p0: Context?, p1: Int, p2: String?) {

    }

    override fun onNotifactionShowedResult(p0: Context?, p1: XGPushShowedResult?) {

    }

    override fun onUnregisterResult(p0: Context?, p1: Int) {

    }

    override fun onDeleteTagResult(p0: Context?, p1: Int, p2: String?) {

    }

    override fun onRegisterResult(p0: Context?, p1: Int, p2: XGPushRegisterResult?) {

    }

    override fun onTextMessage(p0: Context?, p1: XGPushTextMessage?) {

    }

    override fun onNotifactionClickedResult(p0: Context?, p1: XGPushClickedResult?) {

    }
}