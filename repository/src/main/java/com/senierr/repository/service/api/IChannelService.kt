package com.senierr.repository.service.api

import com.senierr.repository.bean.Channel
import com.senierr.repository.bean.User
import io.reactivex.Observable

/**
 * 频道模块
 *
 * @author zhouchunjie
 * @date 2018/9/2
 */
interface IChannelService {

    /**
     * 创建频道
     */
    fun create(owner: User, invitee: User, line: String): Observable<Channel>

    /**
     * 获取频道
     */
    fun get(channelId: String): Observable<Channel>
}