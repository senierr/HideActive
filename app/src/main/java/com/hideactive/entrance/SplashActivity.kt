package com.hideactive.entrance

import android.os.Bundle
import android.util.Log
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.extension.bindToLifecycle
import com.hideactive.extension.observeOnUI
import com.hideactive.extension.subscribeOnIO
import com.senierr.repository.Repository
import com.senierr.repository.service.api.IUserService
import com.senierr.repository.util.LogUtil
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
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))

                    val userService = Repository.getService<IUserService>()
                    userService.login("zhou", "123456")
                            .subscribeOnIO()
                            .observeOnUI()
                            .subscribe({
                                LogUtil.logE(it.toString())
                            }, {
                                LogUtil.logE(Log.getStackTraceString(it))
                            })

                }
                .bindToLifecycle(this)
    }
}
