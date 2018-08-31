package com.hideactive.domain.base

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.hideactive.R
import com.hideactive.util.LogUtil
import com.module.library.util.ToastUtil
import com.senierr.repository.bean.BmobError
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Activity基类
 *
 * @author zhouchunjie
 * @date 2018/5/28
 */
open class BaseActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    /**
     * 绑定至生命周期
     */
    fun bindToLifecycle(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    /**
     * 显示网络错误
     */
    fun showNetworkError(throwable: Throwable) {
        if (throwable is BmobError) {
            when(throwable.code) {
                BmobError.ACCOUNT_REPEAT ->
                    ToastUtil.showShort(this, R.string.account_repeat)
                BmobError.NICKNAME_REPEAT ->
                    ToastUtil.showShort(this, R.string.nickname_repeat)
                BmobError.ACCOUNT_OR_PASSWORD_ERROR ->
                    ToastUtil.showShort(this, R.string.account_or_password_error)
                else ->
                    throwable.error?.let { error ->
                        ToastUtil.showShort(this, error)
                    }
            }
        } else {
            ToastUtil.showShort(this, R.string.network_error)
            LogUtil.logE(Log.getStackTraceString(throwable))
        }
    }
}