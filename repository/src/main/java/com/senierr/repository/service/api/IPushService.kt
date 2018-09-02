package com.senierr.repository.service.api

import com.senierr.repository.bean.BmobUpdate
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

    fun pushMessage(userId: String, message: String)
}