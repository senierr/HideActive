package com.hideactive.ext

import com.hideactive.base.BaseActivity
import com.hideactive.base.BaseFragment
import io.reactivex.disposables.Disposable

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