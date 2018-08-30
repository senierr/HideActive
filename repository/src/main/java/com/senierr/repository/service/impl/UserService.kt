package com.senierr.repository.service.impl

import com.google.gson.Gson
import com.senierr.repository.Repository
import com.senierr.repository.bean.User
import com.senierr.repository.remote.API_USER
import com.senierr.repository.remote.BmobConverter
import com.senierr.repository.remote.CountFunc
import com.senierr.repository.service.api.IUserService
import io.reactivex.Single

/**
 * @author zhouchunjie
 * @date 2018/4/8
 */
class UserService : IUserService {

    override fun checkAccountIfRepeat(account: String): Single<Boolean> {
        // 构造参数
        val param = mapOf(Pair("username", account))
        // 发送请求
        return Repository.rxHttp.get(API_USER)
                .addUrlParam("where", Gson().toJson(param))
                .addUrlParam("count", "0")
                .execute(BmobConverter())
                .singleOrError()
                .map(CountFunc())
                .flatMap {
                    if (it > 0) {
                        return@flatMap Single.just(true)
                    } else {
                        return@flatMap Single.just(false)
                    }
                }
    }

    override fun checkNicknameIfRepeat(nickname: String): Single<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun register(account: String, password: String): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun login(account: String, password: String): Single<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateUserInfo(user: User): Single<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //    override fun checkNicknameIfRepeat(nickname: String): Single<Boolean> {
//        return Single.fromCallable {
//            // 构造参数
//            val param = "{\"nickname\":\"$nickname\"}"
//            // 发送请求
//            val requestBuilder = SeHttp.get(API_USER)
//                    .addUrlParam("where", param)
//                    .addUrlParam("count", "0")
//            val response = requestBuilder.execute()
//            // 解析请求
//            response.use {
//                val responseStr = it.body()?.string()
//                if (it.code() >= 400) {
//                    throw Gson().fromJson(responseStr, Bmob::class.java)
//                }
//                return@fromCallable JsonParser().parse(responseStr)
//                        .asJsonObject
//                        .get("count")
//                        .asInt > 0
//            }
//        }
//    }
//
//    override fun register(account: String, password: String): Single<String> {
//        return Single.fromCallable {
//            // 构造参数
//            val param = "{\"account\":\"$account\", " +
//                    "\"password\":\"$password\", " +
//                    "\"gender\":0}"
//            // 发送请求
//            val requestBuilder = SeHttp.post(API_USER)
//                    .requestBody4JSon(param)
//            val response = requestBuilder.execute()
//            // 解析请求
//            response.use {
//                val responseStr = it.body()?.string()
//                if (it.code() >= 400) {
//                    throw Gson().fromJson(responseStr, Bmob::class.java)
//                }
//                return@fromCallable JsonParser().parse(responseStr)
//                        .asJsonObject
//                        .get("objectId")
//                        .asString
//            }
//        }
//    }
//
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