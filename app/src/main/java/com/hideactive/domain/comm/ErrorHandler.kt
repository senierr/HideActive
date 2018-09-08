package com.hideactive.domain.comm

import android.content.Context
import android.content.Intent
import android.util.Log
import com.hideactive.R
import com.hideactive.domain.user.LoginActivity
import com.hideactive.util.LogUtil
import com.module.library.util.ToastUtil
import com.senierr.repository.bean.BmobError

/**
 * 错误处理
 *
 * @author zhouchunjie
 * @date 2018/9/1
 */
object ErrorHandler {

    /**
     * 显示网络错误
     */
    fun showNetworkError(context: Context, throwable: Throwable) {
        if (throwable is BmobError) {
            when(throwable.code) {
                BmobError.ACCOUNT_REPEAT ->
                    ToastUtil.showShort(context, R.string.account_repeat)
                BmobError.NICKNAME_REPEAT ->
                    ToastUtil.showShort(context, R.string.nickname_repeat)
                BmobError.ACCOUNT_OR_PASSWORD_ERROR ->
                    ToastUtil.showShort(context, R.string.account_or_password_error)
                else ->
                    throwable.error?.let { error ->
                        ToastUtil.showShort(context, error)
                    }
            }
        } else {
            ToastUtil.showShort(context, R.string.network_error)
            LogUtil.logE(Log.getStackTraceString(throwable))
        }
    }
}