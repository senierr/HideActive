package com.hideactive.domain.user.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.hideactive.R
import com.hideactive.comm.KEY_ACCOUNT
import com.hideactive.comm.KEY_PASSWORD
import com.hideactive.comm.REGEX_MOBILE_EXACT
import com.hideactive.comm.REGEX_PASSWORD
import com.hideactive.domain.user.RegisterActivity
import com.hideactive.widget.CircularAnim
import com.module.library.extension.hideSoftInput
import com.module.library.util.OnThrottleClickListener
import com.module.library.util.ToastUtil
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

/**
 * 登录
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class LoginActivity : AppCompatActivity(), ILoginView {

    private val loginPresenter = LoginPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        lifecycle.addObserver(loginPresenter)
        initView()
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        tb_top.setTitle(R.string.login)
        tb_top.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance)
        setSupportActionBar(tb_top)
        tb_top.setNavigationIcon(R.drawable.ic_back_white)
        tb_top.setNavigationOnClickListener {
            finish()
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
                R.id.btn_eye ->
                    toggleEye()
                // 登录
                R.id.btn_login ->
                    if (checkAccount() && checkPassword()) loginPresenter.login()
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
            val account = it.getStringExtra(KEY_ACCOUNT)
            val password = it.getStringExtra(KEY_PASSWORD)
            et_account.setText(account)
            et_password.setText(password)
        }
    }

    /**
     * 检查手机号
     */
    private fun checkAccount(): Boolean {
        val account = et_account.text.toString().trim()
        if (account.isEmpty()) {
            ToastUtil.showShort(this@LoginActivity, R.string.account_empty)
            return false
        }
        if (!Pattern.matches(REGEX_MOBILE_EXACT, account)) {
            ToastUtil.showShort(this@LoginActivity, R.string.account_error)
            return false
        }
        return true
    }

    /**
     * 检查密码
     */
    private fun checkPassword(): Boolean {
        val password = et_password.text.toString().trim()
        if (password.isEmpty()) {
            ToastUtil.showShort(this@LoginActivity, R.string.password_empty)
            return false
        }
        if (!Pattern.matches(REGEX_PASSWORD, password)) {
            ToastUtil.showShort(this@LoginActivity, R.string.password_error)
            return false
        }
        return true
    }

    /**
     * 密码可见切换
     */
    private fun toggleEye() {
        btn_eye.isSelected = !btn_eye.isSelected
        if (btn_eye.isSelected) {
            et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            et_password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        et_password.setSelection(et_password.text.length)
    }

    override fun getAccount(): String {
        return et_account.text.toString().trim()
    }

    override fun getPassword(): String {
        return et_password.text.toString().trim()
    }

    override fun showLoginStart() {
        hideSoftInput()
        btn_login.isEnabled = false
        btn_login.setText(R.string.logging)
    }

    override fun showLoginEnd() {
        btn_login.isEnabled = true
        btn_login.setText(R.string.login)
    }

    override fun showLoginSuccess() {
        CircularAnim().fullActivity(this, btn_login)
                .colorOrImageRes(R.color.colorPrimary)
                .go(object : CircularAnim.OnAnimationEndListener {
                    override fun onAnimationEnd() {
                        finish()
                    }
                })
    }

    override fun showAccountOrPasswordError() {
        ToastUtil.showShort(this, R.string.account_or_password_error)
    }

    override fun showOtherError(msg: String) {
        ToastUtil.showShort(this, msg)
    }

    override fun showNetworkError() {
        ToastUtil.showShort(this, R.string.network_error)
    }
}