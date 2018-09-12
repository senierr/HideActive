package com.hideactive.domain

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.comm.ErrorHandler
import com.hideactive.comm.REGEX_ACCOUNT
import com.hideactive.comm.REGEX_PASSWORD
import com.hideactive.domain.LoginActivity.Companion.EXTRA_KEY_ACCOUNT
import com.hideactive.domain.LoginActivity.Companion.EXTRA_KEY_PASSWORD
import com.hideactive.ext.bindToLifecycle
import com.hideactive.ext.hideSoftInput
import com.module.library.util.OnThrottleClickListener
import com.module.library.util.ToastUtil
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
import com.senierr.repository.service.api.IUserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.layout_top_bar_normal.*
import java.util.regex.Pattern

/**
 * 注册
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class RegisterActivity : BaseActivity() {

    private val userService = Repository.getService<IUserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initView()
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        tv_title.setText(R.string.register)
        btn_left.setOnClickListener(onThrottleClickListener)

        btn_eye.isSelected = false
        btn_eye.setOnClickListener(onThrottleClickListener)
        btn_register.setOnClickListener(onThrottleClickListener)
    }

    /**
     * 防抖动点击事件
     */
    private val onThrottleClickListener = object : OnThrottleClickListener() {
        override fun onThrottleClick(view: View?) {
            when(view?.id) {
                // 退出
                R.id.btn_left -> {
                    finish()
                }
                // 密码可见
                R.id.btn_eye -> {
                    btn_eye.isSelected = !btn_eye.isSelected
                    et_password.setPasswordVisible(btn_eye.isSelected)
                }
                // 注册
                R.id.btn_register -> {
                    register()
                }
            }
        }
    }

    /**
     * 注册
     */
    private fun register() {
        val account = et_account.text.toString().trim()
        val password = et_password.text.toString().trim()
        // 检查账号
        if (account.isEmpty()) {
            ToastUtil.showShort(this@RegisterActivity, R.string.account_empty)
            return
        }
        if (!Pattern.matches(REGEX_ACCOUNT, account)) {
            ToastUtil.showShort(this@RegisterActivity, R.string.account_not_match_regex)
            return
        }
        // 检查密码
        if (password.isEmpty()) {
            ToastUtil.showShort(this@RegisterActivity, R.string.password_empty)
            return
        }
        if (!Pattern.matches(REGEX_PASSWORD, password)) {
            ToastUtil.showShort(this@RegisterActivity, R.string.password_not_match_regex)
            return
        }
        // 检测账号是否重复
        userService.checkAccountIfRepeat(account)
                .flatMap {
                    // 账号重复
                    if (it) throw BmobError(BmobError.ACCOUNT_REPEAT)
                    // 注册
                    return@flatMap userService.register(account, password)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    hideSoftInput()
                    btn_register.isEnabled = false
                    btn_register.setText(R.string.registering)
                }
                .doFinally {
                    btn_register.isEnabled = true
                    btn_register.setText(R.string.register)
                }
                .subscribe({
                    // 注册成功，返回登录页面
                    val data = intent
                    data.putExtra(EXTRA_KEY_ACCOUNT, account)
                    data.putExtra(EXTRA_KEY_PASSWORD, password)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }, {
                    ErrorHandler.showNetworkError(this@RegisterActivity, it)
                })
                .bindToLifecycle(this)
    }
}