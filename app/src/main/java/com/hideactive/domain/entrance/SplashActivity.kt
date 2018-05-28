package com.hideactive.domain.entrance

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hideactive.R
import com.hideactive.domain.user.login.LoginActivity

class SplashActivity : AppCompatActivity(), ISplashView {

    private val splashPresenter = SplashPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycle.addObserver(splashPresenter)
        splashPresenter.startDelay()
    }

    override fun showMainPage() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
    }
}
