package com.hideactive.domain.entrance

import android.content.Intent
import android.os.Bundle
import com.hideactive.R
import com.hideactive.domain.base.BaseActivity
import com.hideactive.domain.user.UserInfoActivity
import com.hideactive.ext.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Observable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    startActivity(Intent(this@SplashActivity, UserInfoActivity::class.java))
                    finish()
                }
                .bindToLifecycle(this)
    }
}
