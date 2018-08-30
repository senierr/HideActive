package com.senierr.repository.service.impl

import com.google.gson.Gson
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobInsert
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
                .execute(BmobArrayConverter<User>())
                .map(ExistFunction())
    }

    override fun checkNicknameIfRepeat(nickname: String): Observable<Boolean> {
        val param = mapOf(Pair("nickname", nickname))
        return Repository.rxHttp.get(API_USER)
                .addUrlParam("where", Gson().toJson(param))
                .addUrlParam("count", "0")
                .execute(BmobArrayConverter<User>())
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateUserInfo(user: User): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    override fun login(account: String, password: String): Single<User> {
//        return Single.fromCallable {
//            // 发送请求
//            val requestBuilder = SeHttp.get(API_USER_LOGIN)
//                    .addUrlParam("account", account)
//                    .addUrlParam("password", password)
//            val response = requestBuilder.execute()
//            // 解析请求
//            response.use {
//                val responseStr = it.body()?.string()
//                if (it.code() >= 400) {
//                    throw Gson().fromJson(responseStr, Bmob::class.java)
//                }
//                return@fromCallable Gson().fromJson(responseStr, User::class.java)
//            }
//        }
//                .map {
//                    // 缓存至本地
//                    Repository.database.getUserDao().insertOrReplace(it)
//                    return@map it
//                }
//    }
//
//    override fun updateUserInfo(user: User): Single<Boolean> {
//
//    }
}