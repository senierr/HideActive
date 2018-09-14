package com.senierr.repository.service.impl

import com.google.gson.Gson
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobInsert
import com.senierr.repository.bean.BmobServerData
import com.senierr.repository.bean.Channel
import com.senierr.repository.bean.User
import com.senierr.repository.remote.API_CHANNEL
import com.senierr.repository.remote.API_TIME
import com.senierr.repository.remote.BmobObjectConverter
import com.senierr.repository.remote.ObjectFunction
import com.senierr.repository.service.api.IChannelService
import io.reactivex.Observable

/**
 * 频道模块
 *
 * @author zhouchunjie
 * @date 2018/9/3
 */
class ChannelService : IChannelService {

    override fun create(owner: User, invitee: User, line: String): Observable<Channel> {
        val param = mapOf(
                Pair("line", line),
                Pair("owner", owner),
                Pair("invitee", invitee)
        )
        return Repository.dataHttp.post(API_CHANNEL)
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobInsert::class.java))
                .map(ObjectFunction())
                .flatMap {
                    val channel = Channel(it.objectId, line, owner, invitee, it.createdAt, it.createdAt)
                    return@flatMap Observable.just(channel)
                }
    }

    override fun get(channelId: String): Observable<Channel> {
        return Repository.dataHttp.get("$API_CHANNEL/$channelId")
                .execute(BmobObjectConverter(Channel::class.java))
                .map(ObjectFunction())
    }

    override fun getServerData(): Observable<BmobServerData> {
        return Repository.dataHttp.get(API_TIME)
                .execute(BmobObjectConverter(BmobServerData::class.java))
                .map(ObjectFunction())
    }
}