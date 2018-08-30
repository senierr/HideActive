package com.hideactive.ext

import com.hideactive.domain.base.BaseActivity
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
    activity.bindToLifecycle(this)
}