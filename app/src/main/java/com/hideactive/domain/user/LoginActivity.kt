package com.hideactive.domain.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.hideactive.R
import com.hideactive.comm.EXTRA_KEY_ACCOUNT
import com.hideactive.comm.EXTRA_KEY_PASSWORD
import com.hideactive.comm.REGEX_ACCOUNT
import com.hideactive.comm.REGEX_PASSWORD
import com.hideactive.domain.base.BaseActivity
import com.hideactive.ext.bindToLifecycle
import com.hideactive.ext.hideSoftInput
import com.hideactive.widget.CircularAnim
import com.module.library.util.OnThrottleClickListener
import com.module.library.util.ToastUtil
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
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

    private val userService: IUserService = Repository.getService()

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
                                    finish()
                                }
                            })
                }, {
                    if (it is BmobError) {
                        when(it.code) {
                            BmobError.ACCOUNT_OR_PASSWORD_ERROR ->
                                ToastUtil.showShort(this, R.string.account_or_password_error)
                            else ->
                                it.error?.let { error ->
                                    ToastUtil.showShort(this, error)
                                }
                        }
                    } else {
                        ToastUtil.showShort(this, R.string.network_error)
                    }
                })
                .bindToLifecycle(this)
    }
}