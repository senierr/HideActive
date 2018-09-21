package com.senierr.repository.service.impl

import com.google.gson.Gson
import com.senierr.repository.Repository
import com.senierr.repository.bean.*
import com.senierr.repository.remote.*
import com.senierr.repository.service.api.IChannelService
import com.senierr.repository.util.DateUtil
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

    override fun getAllAvailableChannel(currentData: BmobServerData): Observable<MutableList<Channel>> {
        val param = "{\"\$and\":[" +
                        "{\"createdAt\":" +
                            "{\"\$gte\":" +
                                "{" +
                                    "\"__type\":\"Date\", " +
                                    "\"iso\":\"${DateUtil.format(currentData.timestamp * 1000 - 1000 * 10)}\"" +
                                "}" +
                            "}" +
                        "}," +
                        "{\"createdAt\":" +
                            "{\"\$lte\":" +
                                "{" +
                                    "\"__type\":\"Date\", " +
                                    "\"iso\":\"${currentData.datetime}\"" +
                                "}" +
                            "}" +
                        "}" +
                    "]}"
        return Repository.dataHttp.get(API_CHANNEL)
                .addUrlParam("where", param)
                .execute(BmobArrayConverter(Channel::class.java))
                .map(BmobArrayFunction())
    }

    override fun deleteChannel(channelId: String): Observable<BmobDelete> {
        return Repository.dataHttp.delete("$API_CHANNEL/$channelId")
                .execute(BmobObjectConverter(BmobDelete::class.java))
                .map(ObjectFunction())
    }
}