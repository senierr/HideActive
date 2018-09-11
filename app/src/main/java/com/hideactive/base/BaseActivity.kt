package com.hideactive.base

import android.support.v7.app.AppCompatActivity
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
}