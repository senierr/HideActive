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
        return Repository.rxHttp.get(API_USER)
                .addUrlParam("where", Gson().toJson(param))
                .addUrlParam("count", "0")
                .execute(BmobArrayConverter(User::class.java))
                .map(ExistFunction())
    }

    override fun checkNicknameIfRepeat(nickname: String): Observable<Boolean> {
        val param = mapOf(Pair("nickname", nickname))
        return Repository.rxHttp.get(API_USER)
                .addUrlParam("where", Gson().toJson(param))
                .addUrlParam("count", "0")
                .execute(BmobArrayConverter(User::class.java))
                .map(ExistFunction())
    }

    override fun register(account: String, password: String): Observable<BmobInsert> {
        val param = mapOf(
                Pair("account", account),
                Pair("password", password)
        )
        return Repository.rxHttp.post(API_USER)
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobInsert::class.java))
                .map(ObjectFunction())
    }

    override fun login(account: String, password: String): Observable<User> {
        val param = mapOf(Pair("account", account))
        return Repository.rxHttp.get(API_USER)
                .addUrlParam("where", Gson().toJson(param))
                .execute(BmobArrayConverter(User::class.java))
                .map(ArrayFirstFunction())
                .map {
                    // 清除缓存
                    Repository.database.getUserDao().deleteAll()
                    // 缓存本地
                    Repository.database.getUserDao().insertOrReplace(it)
                    return@map it
                }
    }

    override fun updateUserPortrait(objectId: String, portrait: String): Observable<BmobUpdate> {
        val param = mapOf(Pair("portrait", portrait))
        return Repository.rxHttp.put("$API_USER/$objectId")
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobUpdate::class.java))
                .map(ObjectFunction())
    }

    override fun updateUserNickname(objectId: String, nickname: String): Observable<BmobUpdate> {
        val param = mapOf(Pair("nickname", nickname))
        return Repository.rxHttp.put("$API_USER/$objectId")
                .setRequestBody4JSon(Gson().toJson(param))
                .execute(BmobObjectConverter(BmobUpdate::class.java))
                .map(ObjectFunction())
    }

    override fun getLocalUser(): Observable<User> {
        return Observable.fromCallable {
            return@fromCallable Repository.database.getUserDao().getList()?.first()
        }
    }

    override fun getRemoteUser(objectId: String): Observable<User> {
        return Repository.rxHttp.get("$API_USER/$objectId")
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
}