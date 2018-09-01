package com.hideactive.domain.entrance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hideactive.R
import com.hideactive.domain.base.BaseActivity
import com.hideactive.domain.user.LoginActivity
import com.hideactive.domain.user.UserInfoActivity
import com.hideactive.ext.bindToLifecycle
import com.hideactive.util.LogUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

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
        startActivityWithVerify(this, Intent(this, UserInfoActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            LogUtil.logE("onActivityResult: " + data.getStringExtra("text"))
        }
    }
}
