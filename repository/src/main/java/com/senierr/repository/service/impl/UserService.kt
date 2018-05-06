package com.senierr.repository.service.impl

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
import com.senierr.repository.bean.User
import com.senierr.repository.remote.API_USER
import com.senierr.repository.remote.API_USER_LOGIN
import com.senierr.repository.service.api.IUserService
import com.senierr.sehttp.SeHttp
import io.reactivex.Single

/**
 * @author zhouchunjie
 * @date 2018/4/8
 */
class UserService : IUserService {

    override fun checkAccountIfRepeat(account: String): Single<Boolean> {
        return Single.fromCallable {
            // 构造参数
            val param = "{\"username\":\"$account\"}"
            // 发送请求
            val requestBuilder = SeHttp.get(API_USER)
                    .addUrlParam("where", param)
                    .addUrlParam("count", "0")
            val response = requestBuilder.execute()
            // 解析请求
            response.use {
                val responseStr = it.body()?.string()
                if (it.code() >= 400) {
                    throw Gson().fromJson(responseStr, BmobError::class.java)
                }
                return@fromCallable JsonParser().parse(responseStr)
                        .asJsonObject
                        .get("count")
                        .asInt > 0
            }
        }
    }

    override fun checkNicknameIfRepeat(nickname: String): Single<Boolean> {
        return Single.fromCallable {
            // 构造参数
            val param = "{\"nickname\":\"$nickname\"}"
            // 发送请求
            val requestBuilder = SeHttp.get(API_USER)
                    .addUrlParam("where", param)
                    .addUrlParam("count", "0")
            val response = requestBuilder.execute()
            // 解析请求
            response.use {
                val responseStr = it.body()?.string()
                if (it.code() >= 400) {
                    throw Gson().fromJson(responseStr, BmobError::class.java)
                }
                return@fromCallable JsonParser().parse(responseStr)
                        .asJsonObject
                        .get("count")
                        .asInt > 0
            }
        }
    }

    override fun register(username: String, password: String, nickname: String): Single<String> {
        return Single.fromCallable {
            // 构造参数
            val param = "{\"username\":\"$username\", " +
                    "\"password\":\"$password\", " +
                    "\"nickname\":\"$nickname\", " +
                    "\"gender\":0}"
            // 发送请求
            val requestBuilder = SeHttp.post(API_USER)
                    .requestBody4JSon(param)
            val response = requestBuilder.execute()
            // 解析请求
            response.use {
                val responseStr = it.body()?.string()
                if (it.code() >= 400) {
                    throw Gson().fromJson(responseStr, BmobError::class.java)
                }
                return@fromCallable JsonParser().parse(responseStr)
                        .asJsonObject
                        .get("objectId")
                        .asString
            }
        }
    }

    override fun login(username: String, password: String): Single<User> {
        return Single.fromCallable {
            // 发送请求
            val requestBuilder = SeHttp.get(API_USER_LOGIN)
                    .addUrlParam("username", username)
                    .addUrlParam("password", password)
            val response = requestBuilder.execute()
            // 解析请求
            response.use {
                val responseStr = it.body()?.string()
                if (it.code() >= 400) {
                    throw Gson().fromJson(responseStr, BmobError::class.java)
                }
                return@fromCallable Gson().fromJson(responseStr, User::class.java)
            }
        }
                .map {
                    // 缓存至本地
                    Repository.database.getUserDao().insertOrReplace(it)
                    return@map it
                }
    }
}