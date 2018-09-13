package com.senierr.repository

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.senierr.http.RxHttp
import com.senierr.http.internal.LogInterceptor
import com.senierr.repository.db.AppDatabase
import com.senierr.repository.remote.*
import com.senierr.repository.service.api.IChannelService
import com.senierr.repository.service.api.IPushService
import com.senierr.repository.service.api.IUserService
import com.senierr.repository.service.impl.ChannelService
import com.senierr.repository.service.impl.PushService
import com.senierr.repository.service.impl.UserService
import com.tencent.android.tpush.XGPushManager

/**
 * 数据服务入口
 *
 * @author zhouchunjie
 * @date 2018/3/9
 */
object Repository {

    private const val DEBUG_TAG = "Repository"
    private const val TIMEOUT = 15 * 1000L

    private const val DB_NAME = "HideActive.db"
    private const val SP_NAME = "HideActive"

    internal lateinit var dataHttp: RxHttp
    internal lateinit var pushHttp: RxHttp
    internal lateinit var database: AppDatabase
    internal lateinit var sp: SharedPreferences

    /**
     * 数据层初始化
     */
    fun initialize(context: Context) {
        // 网络请求
        dataHttp = RxHttp.Builder()
                .debug(DEBUG_TAG, LogInterceptor.LogLevel.BODY)
                .addCommonHeader(APP_ID_KEY, APP_ID_VALUE)
                .addCommonHeader(REST_API_KEY, REST_API_VALUE)
                .connectTimeout(TIMEOUT)
                .readTimeout(TIMEOUT)
                .writeTimeout(TIMEOUT)
                .build()
        pushHttp = RxHttp.Builder()
                .debug(DEBUG_TAG, LogInterceptor.LogLevel.BODY)
                .addCommonHeader(AUTHORIZATION_KEY, "Basic " + Base64.encodeToString(
                        "$APP_ID:$SECRET_KEY".toByteArray(), Base64.NO_WRAP))
                .connectTimeout(TIMEOUT)
                .readTimeout(TIMEOUT)
                .writeTimeout(TIMEOUT)
                .build()
        // 数据库
        database = Room.databaseBuilder(context,
                AppDatabase::class.java,
                DB_NAME)
                .build()
        // SharedPreferences
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        // 信鸽
        XGPushManager.registerPush(context)
    }

    /**
     * 获取数据服务
     */
    inline fun <reified T> getService(): T = when(T::class.java) {
        IUserService::class.java ->
            UserService() as T
        IPushService::class.java ->
            PushService() as T
        IChannelService::class.java ->
            ChannelService() as T
        else -> throw IllegalArgumentException("Can not find this type of the service!")
    }
}