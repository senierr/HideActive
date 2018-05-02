package com.hideactive.entrance

import android.content.Intent
import android.os.Bundle
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.extension.bindToLifecycle
import com.hideactive.extension.observeOnUI
import com.hideactive.extension.subscribeOnIO
import com.hideactive.user.RegisterActivity
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
                    startActivity(Intent(this@SplashActivity, RegisterActivity::class.java))
                }
                .bindToLifecycle(this)
    }
}
