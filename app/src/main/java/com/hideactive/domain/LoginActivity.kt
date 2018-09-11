package com.hideactive.domain

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.comm.ErrorHandler
import com.hideactive.comm.REGEX_ACCOUNT
import com.hideactive.comm.REGEX_PASSWORD
import com.hideactive.ext.bindToLifecycle
import com.hideactive.ext.hideSoftInput
import com.hideactive.widget.CircularAnim
import com.module.library.util.OnThrottleClickListener
import com.module.library.util.ToastUtil
import com.senierr.repository.Repository
import com.senierr.repository.service.api.IUserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

/**
 * 登录
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class LoginActivity : BaseActivity() {

    companion object {
        const val EXTRA_KEY_ACCOUNT = "login_account"
        const val EXTRA_KEY_PASSWORD = "login_password"
        const val LOGIN_SUCCESS = 65534
        const val LOGIN_FAILURE = 65535
    }

    private val userService = Repository.getService<IUserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        tb_top.setTitle(R.string.login)
        tb_top.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance)
        setSupportActionBar(tb_top)
        tb_top.setNavigationIcon(R.drawable.ic_close_white_24dp)
        tb_top.setNavigationOnClickListener {
            doFinish(false)
        }

        btn_eye.isSelected = false
        btn_eye.setOnClickListener(onThrottleClickListener)
        btn_login.setOnClickListener(onThrottleClickListener)
        btn_register.setOnClickListener(onThrottleClickListener)
    }

    /**
     * 防抖动点击事件
     */
    private val onThrottleClickListener = object : OnThrottleClickListener() {
        override fun onThrottleClick(view: View?) {
            when(view?.id) {
                // 密码可见
                R.id.btn_eye -> {
                    btn_eye.isSelected = !btn_eye.isSelected
                    et_password.setPasswordVisible(btn_eye.isSelected)
                }
                // 登录
                R.id.btn_login ->
                    login()
                // 注册
                R.id.btn_register ->
                    startActivityForResult(Intent(this@LoginActivity, RegisterActivity::class.java), 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        data?.let {
            // 注册信息自动填入
            val account = it.getStringExtra(EXTRA_KEY_ACCOUNT)
            val password = it.getStringExtra(EXTRA_KEY_PASSWORD)
            et_account.setText(account)
            et_password.setText(password)
        }
    }

    private fun login() {
        val account = et_account.text.toString().trim()
        val password = et_password.text.toString().trim()
        // 检查账号
        if (account.isEmpty()) {
            ToastUtil.showShort(this@LoginActivity, R.string.account_empty)
            return
        }
        if (!Pattern.matches(REGEX_ACCOUNT, account)) {
            ToastUtil.showShort(this@LoginActivity, R.string.account_not_match_regex)
            return
        }
        // 检查密码
        if (password.isEmpty()) {
            ToastUtil.showShort(this@LoginActivity, R.string.password_empty)
            return
        }
        if (!Pattern.matches(REGEX_PASSWORD, password)) {
            ToastUtil.showShort(this@LoginActivity, R.string.password_not_match_regex)
            return
        }

        // 登录
        userService.login(account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    hideSoftInput()
                    btn_login.isEnabled = false
                    btn_login.setText(R.string.logging)
                }
                .doFinally {
                    btn_login.isEnabled = true
                    btn_login.setText(R.string.login)
                }
                .subscribe({
                    CircularAnim().fullActivity(this, btn_login)
                            .colorOrImageRes(R.color.colorPrimary)
                            .go(object : CircularAnim.OnAnimationEndListener {
                                override fun onAnimationEnd() {
                                    doFinish(true)
                                }
                            })
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 页面关闭处理
     */
    private fun doFinish(isSuccess: Boolean) {
        if (isSuccess) {
            setResult(LOGIN_SUCCESS, intent)
        } else {
            setResult(LOGIN_FAILURE, intent)
        }
        finish()
    }
}