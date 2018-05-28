package com.hideactive.domain.user.login

import com.module.library.base.BasePresenter

/**
 * 登录
 *
 * @author zhouchunjie
 * @date 2018/5/25
 */

interface ILoginView {

    fun getAccount(): String

    fun getPassword(): String

    fun showLoginStart()

    fun showLoginEnd()

    fun showLoginSuccess()

    fun showAccountOrPasswordError()

    fun showOtherError(msg: String)

    fun showNetworkError()
}

abstract class ILoginPresenter : BasePresenter() {

    abstract fun login()
}