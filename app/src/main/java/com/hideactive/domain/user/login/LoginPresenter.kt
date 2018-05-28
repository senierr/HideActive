package com.hideactive.domain.user.login

import com.module.library.extension.bindToLifecycle
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
import com.senierr.repository.service.api.IUserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *
 * @author zhouchunjie
 * @date 2018/5/25
 */
class LoginPresenter(private val loginView: ILoginView): ILoginPresenter() {

    private val userService: IUserService = Repository.getService()

    override fun login() {
        val account = loginView.getAccount()
        val password = loginView.getPassword()
        userService.login(account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loginView.showLoginStart()
                }
                .doFinally {
                    loginView.showLoginEnd()
                }
                .subscribe({
                    loginView.showLoginSuccess()
                }, {
                    if (it is BmobError) {
                        when(it.code) {
                            BmobError.ACCOUNT_OR_PASSWORD_ERROR ->
                                loginView.showAccountOrPasswordError()
                            else ->
                                it.error?.let {
                                    loginView.showOtherError(it)
                                }
                        }
                    } else {
                        loginView.showNetworkError()
                    }
                })
                .bindToLifecycle(this)
    }
}