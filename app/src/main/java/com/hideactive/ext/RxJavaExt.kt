package com.hideactive.ext

import com.hideactive.base.BaseActivity
import com.hideactive.base.BaseFragment
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * RxJava扩展函数
 *
 * @author zhouchunjie
 * @date 2018/4/16
 */

/**
 * 绑定至BaseActivity
 */
fun Disposable.bindToLifecycle(activity: BaseActivity) {
    activity.compositeDisposable.add(this)
}

/**
 * 绑定至BaseFragment
 */
fun Disposable.bindToLifecycle(fragment: BaseFragment) {
    fragment.compositeDisposable.add(this)
}

/**
 * Observable: IO线程订阅
 */
fun Observable<*>.subscribeOnIO(): Observable<*> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Observable: UI线程观察
 */
fun Observable<*>.observeOnUI(): Observable<*> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

/**
 * Single: IO线程订阅
 */
fun Single<*>.subscribeOnIO(): Single<*> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Single: UI线程观察
 */
fun Single<*>.observeOnUI(): Single<*> {
    return this.observeOn(AndroidSchedulers.mainThread())
}