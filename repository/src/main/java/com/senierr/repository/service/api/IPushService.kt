package com.senierr.repository.service.api

import com.senierr.repository.bean.BmobUpdate
import com.senierr.repository.bean.PushResponse
import io.reactivex.Observable

/**
 * 推送模块
 *
 * @author zhouchunjie
 * @date 2018/9/2
 */
interface IPushService {

    /**
     * 用户推送注册
     */
    fun register(userId: String, pushToken: String): Observable<BmobUpdate>

    /**
     * 用户取消注册
     */
    fun unregister(userId: String): Observable<BmobUpdate>

    /**
     * 推送消息
     */
    fun pushMessage(pushToken: String, message: String): Observable<PushResponse>
}