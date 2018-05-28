package com.module.library.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.util.Log
import io.reactivex.disposables.CompositeDisposable

/**
 * 逻辑处理基类
 *
 * @author zhouchunjie
 * @date 2018/5/28
 */
abstract class BasePresenter : LifecycleObserver {

    val compositeDisposable = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.e("BasePresenter", "onCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Log.e("BasePresenter", "onDestroy")
        compositeDisposable.clear()
    }
}