package com.senierr.repository

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.senierr.repository.db.AppDatabase
import com.senierr.repository.service.api.IUserService
import com.senierr.repository.service.impl.UserService
import com.senierr.repository.util.LogUtil
import com.senierr.sehttp.SeHttp
import com.senierr.sehttp.interceptor.LogLevel
import io.reactivex.plugins.RxJavaPlugins

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

    internal lateinit var database: AppDatabase
    internal lateinit var sp: SharedPreferences

    /**
     * 数据层初始化
     */
    fun initialize(context: Context) {
        // 日志
        LogUtil.isDebug = true
        LogUtil.tag = DEBUG_TAG
        // 网络
        SeHttp.getInstance()
                .debug(DEBUG_TAG, LogLevel.BODY)
                .connectTimeout(TIMEOUT)
                .readTimeout(TIMEOUT)
                .writeTimeout(TIMEOUT)
        // 数据库
        database = Room.databaseBuilder(context,
                AppDatabase::class.java,
                DB_NAME)
                .build()
        // SharedPreferences
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        // RxJava
        RxJavaPlugins.setErrorHandler({
            LogUtil.logW("Error: ${Log.getStackTraceString(it)}")
        })
    }

    /**
     * 获取数据服务
     */
    inline fun <reified T> getService(): T = when(T::class.java) {
        IUserService::class.java ->
            UserService() as T
        else -> throw IllegalArgumentException("Can not find this type of the service!")
    }
}