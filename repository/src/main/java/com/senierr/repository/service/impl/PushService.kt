package com.senierr.repository.service.impl

import com.google.gson.Gson
import com.senierr.repository.Repository
import com.senierr.repository.bean.*
import com.senierr.repository.remote.*
import com.senierr.repository.service.api.IPushService
import io.reactivex.Observable

/**
 * 推送模块
 *
 * @author zhouchunjie
 * @date 2018/9/3
 */
class PushService : IPushService {

    override fun register(userId: String, pushToken: String): Observable<BmobUpdate> {
        val param = mapOf(Pair("pushToken", pushToken))
        return Repository.dataHttp.put("$API_USER/$userId")
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobUpdate::class.java))
                .map(ObjectFunction())
    }

    override fun unregister(userId: String): Observable<BmobUpdate> {
        val param = mapOf(Pair("pushToken", ""))
        return Repository.dataHttp.put("$API_USER/$userId")
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobUpdate::class.java))
                .map(ObjectFunction())
    }

    override fun pushMessage(pushToken: String, message: String): Observable<PushResponse> {
        val pushMessage = PushMessage("_", "_", ThroughMessage(mapOf(Pair("extra", message))))
        val pushBean = PushBean(mutableListOf(pushToken), pushMessage)
        return Repository.pushHttp.post(API_PUSH)
                .setRequestBody4JSon(Gson().toJson(pushBean))
                .execute(ObjectConverter(PushResponse::class.java))
                .map(ObjectFunction())
    }
}