package com.hideactive.domain.entrance

import com.module.library.base.BasePresenter

/**
 * 引导页
 *
 * @author zhouchunjie
 * @date 2018/5/25
 */

interface ISplashView {

    fun showMainPage()
}

abstract class ISplashPresenter : BasePresenter() {

    abstract fun startDelay()
}