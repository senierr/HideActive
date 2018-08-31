package com.hideactive.domain.entrance

import android.content.Intent
import android.os.Bundle
import com.hideactive.R
import com.hideactive.domain.base.BaseActivity
import com.hideactive.domain.user.LoginActivity
import com.hideactive.domain.user.UserInfoActivity
import com.hideactive.ext.bindToLifecycle
import com.senierr.repository.Repository
import com.senierr.repository.service.api.IUserService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    private val userService = Repository.getService<IUserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Observable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    openUserInfo()
                }
                .bindToLifecycle(this)
    }

    private fun openUserInfo() {
        userService.isLoggedIn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        startActivity(Intent(this@SplashActivity, UserInfoActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    }
                    finish()
                }, {
                    showNetworkError(it)
                })
    }
}
