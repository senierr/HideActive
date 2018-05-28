package com.module.library.extension

import com.module.library.base.BasePresenter
import io.reactivex.disposables.Disposable

/**
 * RxJava扩展函数
 *
 * @author zhouchunjie
 * @date 2018/4/16
 */

/**
 * 绑定至presenter
 */
fun Disposable.bindToLifecycle(presenter: BasePresenter) {
    presenter.compositeDisposable.add(this)
}