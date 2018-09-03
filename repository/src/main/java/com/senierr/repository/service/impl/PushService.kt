package com.senierr.repository.service.impl

import android.util.Base64
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobUpdate
import com.senierr.repository.bean.PushBean
import com.senierr.repository.bean.PushMessage
import com.senierr.repository.bean.PushResponse
import com.senierr.repository.remote.*
import com.senierr.repository.service.api.IPushService
import com.senierr.repository.util.EncryptUtil
import io.reactivex.Observable

/**
 * 推送模块
 *
 * @author zhouchunjie
 * @date 2018/9/3
 */
class PushService : IPushService {

    override fun register(userId: String, pushToken: String): Observable<BmobUpdate> {

        return Repository.rxHttp.post(API_USER)
                .addHeader("Authorization", Base64.encodeToString("$APP_ID:$SECRET_KEY".toByteArray(), Base64.NO_WRAP))
                .execute(BmobObjectConverter(BmobUpdate::class.java))
                .map(ObjectFunction())
    }

    override fun unregister(userId: String, pushToken: String): Observable<BmobUpdate> {

    }

    override fun pushMessage(userId: String, message: String): Observable<PushResponse> {
        val pushMessage = PushMessage()
        val pushBean = PushBean(mutableListOf(userId), pushMessage)
        return Repository.rxHttp.post(API_PUSH)
                .addHeader("Authorization", Base64.encodeToString("$APP_ID:$SECRET_KEY".toByteArray(), Base64.NO_WRAP))
                .execute(ObjectConverter(PushResponse::class.java))
                .map(ObjectFunction())
    }
}