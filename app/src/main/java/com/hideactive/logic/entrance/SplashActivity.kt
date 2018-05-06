package com.hideactive.logic.entrance

import android.content.Intent
import android.os.Bundle
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.ext.bindToLifecycle
import com.hideactive.logic.user.LoginActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
                .bindToLifecycle(this)
    }
}
