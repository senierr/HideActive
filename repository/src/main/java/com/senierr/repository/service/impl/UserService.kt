package com.senierr.repository.service.impl

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.senierr.repository.bean.BmobError
import com.senierr.repository.bean.User
import com.senierr.repository.remote.USER_LOGIN
import com.senierr.repository.remote.USER_REGISTER
import com.senierr.repository.service.api.IUserService
import com.senierr.sehttp.SeHttp
import io.reactivex.Single
import org.json.JSONObject

/**
 * @author zhouchunjie
 * @date 2018/4/8
 */
class UserService : IUserService {

    override fun register(username: String, password: String): Single<String> {
        return Single.fromCallable {
            // 构造参数
            val user = User("")
            user.username = username
            user.password = password
            // 发送请求
            val requestBuilder = SeHttp.post(USER_REGISTER)
                    .requestBody4JSon(Gson().toJson(user))
            val response = requestBuilder.execute()
            // 解析请求
            response.use {
                val responseCode = it.code()
                val responseStr = it.body()?.string()
                if (responseCode in 200 until 400) {
                    // 请求成功
                    return@fromCallable JsonParser().parse(responseStr)
                            .asJsonObject
                            .get("objectId")
                            .asString
                } else {
                    // 请求失败
                    throw Gson().fromJson(responseStr, BmobError::class.java)
                }
            }
        }
    }

    override fun login(username: String, password: String): Single<User> {
        return Single.fromCallable {
            // 发送请求
            val requestBuilder = SeHttp.get(USER_LOGIN)
                    .addUrlParam("username", username)
                    .addUrlParam("password", password)
            val response = requestBuilder.execute()
            // 解析请求
            response.use {
                val responseCode = it.code()
                val responseStr = it.body()?.string()
                if (responseCode in 200 until 400) {
                    // 请求成功
                    return@fromCallable Gson().fromJson(responseStr, User::class.java)
                } else {
                    // 请求失败
                    throw Gson().fromJson(responseStr, BmobError::class.java)
                }
            }
        }
    }
}