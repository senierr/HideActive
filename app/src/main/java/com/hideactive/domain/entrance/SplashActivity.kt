package com.hideactive.domain.entrance

import android.content.Intent
import android.os.Bundle
import com.hideactive.R
import com.hideactive.domain.base.BaseActivity
import com.hideactive.domain.comm.ErrorHandler
import com.hideactive.domain.main.MainActivity
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

    companion object {
        const val REQUEST_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Observable.timer(1, TimeUnit.SECONDS)
                .flatMap {
                    return@flatMap Repository.getService<IUserService>().isLoggedIn()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        // 已登录，跳转
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // 未登录，跳转登录页
                        startActivityForResult(
                                Intent(this, LoginActivity::class.java),
                                REQUEST_CODE)
                    }
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == LoginActivity.LOGIN_SUCCESS) {
            // 登录成功，继续跳转
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}