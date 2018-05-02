package com.senierr.repository.util

import android.util.Log

/**
 * 日志工具类
 *
 * @author zhouchunjie
 * @date 2018/4/2
 */
object LogUtil {

    var isDebug = true
    var tag = "LogUtil"

    fun logV(msg: String) {
        if (isDebug) {
            Log.v(tag, msg)
        }
    }

    fun logD(msg: String) {
        if (isDebug) {
            Log.d(tag, msg)
        }
    }

    fun logI(msg: String) {
        if (isDebug) {
            Log.i(tag, msg)
        }
    }

    fun logW(msg: String) {
        if (isDebug) {
            Log.w(tag, msg)
        }
    }

    fun logE(msg: String) {
        if (isDebug) {
            Log.e(tag, msg)
        }
    }
}