package com.hideactive.domain.entrance

import com.module.library.extension.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 *
 * @author zhouchunjie
 * @date 2018/5/25
 */
class SplashPresenter(private val splashView: ISplashView): ISplashPresenter() {

    override fun startDelay() {
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    splashView.showMainPage()
                }
                .bindToLifecycle(this)
    }
}