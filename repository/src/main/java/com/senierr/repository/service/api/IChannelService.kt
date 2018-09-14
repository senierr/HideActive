package com.senierr.repository.service.api

import com.senierr.repository.bean.BmobServerData
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

    /**
     * 获取服务器时间
     */
    fun getServerData(): Observable<BmobServerData>

    /**
     * 检查对方是否可接通
     */
    fun checkInviteeIsAvailable(inviteeId: String, currentData: BmobServerData): Observable<Boolean>
}