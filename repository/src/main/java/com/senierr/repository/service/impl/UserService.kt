package com.senierr.repository.service.impl

import com.google.gson.Gson
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobInsert
import com.senierr.repository.bean.BmobUpdate
import com.senierr.repository.bean.User
import com.senierr.repository.remote.*
import com.senierr.repository.service.api.IUserService
import io.reactivex.Observable

/**
 * @author zhouchunjie
 * @date 2018/4/8
 */
class UserService : IUserService {

    override fun checkAccountIfRepeat(account: String): Observable<Boolean> {
        val param = mapOf(Pair("account", account))
        return Repository.dataHttp.get(API_USER)
                .addUrlParam("where", Gson().toJson(param))
                .addUrlParam("count", "0")
                .execute(BmobArrayConverter(User::class.java))
                .map(BmobExistFunction())
    }

    override fun checkNicknameIfRepeat(nickname: String): Observable<Boolean> {
        val param = mapOf(Pair("nickname", nickname))
        return Repository.dataHttp.get(API_USER)
                .addUrlParam("where", Gson().toJson(param))
                .addUrlParam("count", "0")
                .execute(BmobArrayConverter(User::class.java))
                .map(BmobExistFunction())
    }

    override fun register(account: String, password: String): Observable<BmobInsert> {
        val param = mapOf(
                Pair("account", account),
                Pair("password", password)
        )
        return Repository.dataHttp.post(API_USER)
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobInsert::class.java))
                .map(ObjectFunction())
    }

    override fun login(account: String, password: String): Observable<User> {
        var userId = ""
        val param = mapOf(Pair("account", account))
        return Repository.dataHttp.get(API_USER)
                .addUrlParam("where", Gson().toJson(param))
                .execute(BmobArrayConverter(User::class.java))
                .map(BmobArrayFirstFunction())
                .flatMap {
                    userId = it.objectId
                    // 更新用户在线状态
                    return@flatMap Repository.dataHttp.put("$API_USER/${it.objectId}")
                            .setRequestBody4JSon(Gson().toJson(mapOf(Pair("isOnline", true))))
                            .execute(BmobObjectConverter(BmobUpdate::class.java))
                            .map(ObjectFunction())
                }
                .flatMap {
                    // 获取最新数据
                    return@flatMap Repository.dataHttp.get("$API_USER/$userId")
                            .execute(BmobObjectConverter(User::class.java))
                            .map(ObjectFunction())
                }
                .map {
                    // 清除缓存
                    Repository.database.getUserDao().deleteAll()
                    // 缓存本地
                    Repository.database.getUserDao().insertOrReplace(it)
                    return@map it
                }
    }

    override fun logout(userId: String): Observable<BmobUpdate> {
        return Repository.dataHttp.put("$API_USER/$userId")
                .setRequestBody4JSon(Gson().toJson(mapOf(Pair("isOnline", false))))
                .execute(BmobObjectConverter(BmobUpdate::class.java))
                .map(ObjectFunction())
    }

    override fun updateUserPortrait(objectId: String, portrait: String): Observable<BmobUpdate> {
        val param = mapOf(Pair("portrait", portrait))
        return Repository.dataHttp.put("$API_USER/$objectId")
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobUpdate::class.java))
                .map(ObjectFunction())
    }

    override fun updateUserNickname(objectId: String, nickname: String): Observable<BmobUpdate> {
        val param = mapOf(Pair("nickname", nickname))
        return Repository.dataHttp.put("$API_USER/$objectId")
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobUpdate::class.java))
                .map(ObjectFunction())
    }

    override fun getLocalUser(): Observable<User> {
        return Observable.create {
            val user = Repository.database.getUserDao().getList()?.firstOrNull()
            if (user != null) {
                it.onNext(user)
            }
            it.onComplete()
        }
    }

    override fun getRemoteUser(objectId: String): Observable<User> {
        return Repository.dataHttp.get("$API_USER/$objectId")
                .execute(BmobObjectConverter(User::class.java))
                .map(ObjectFunction())
                .map {
                    // 更新本地
                    Repository.database.getUserDao().insertOrReplace(it)
                    return@map it
                }
    }

    override fun isLoggedIn(): Observable<Boolean> {
        return Observable.fromCallable {
            val userList = Repository.database.getUserDao().getList()
            return@fromCallable userList != null && userList.isNotEmpty()
        }
    }

    override fun getFriends(userId: String): Observable<MutableList<User>> {
        val param = "{\"\$and\":[{\"objectId\":{\"\$ne\":\"$userId\"}},{\"isOnline\":true}]}"
        return Repository.dataHttp.get(API_USER)
                .addUrlParam("where", param)
                .execute(BmobArrayConverter(User::class.java))
                .map(BmobArrayFunction())
    }
}