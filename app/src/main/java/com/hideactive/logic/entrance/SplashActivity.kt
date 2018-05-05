package com.hideactive.logic.entrance

import android.content.Intent
import android.os.Bundle
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.ext.bindToLifecycle
import com.hideactive.ext.observeOnUI
import com.hideactive.ext.subscribeOnIO
import com.hideactive.logic.user.LoginActivity
import com.hideactive.logic.user.RegisterActivity
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOnIO()
                .observeOnUI()
                .subscribe {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
                .bindToLifecycle(this)
    }
}
