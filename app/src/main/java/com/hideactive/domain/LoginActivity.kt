package com.hideactive.domain

import android.content.Intent
import android.os.Bundle
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.comm.ErrorHandler
import com.hideactive.ext.bindToLifecycle
import com.hideactive.ext.hideSoftInput
import com.hideactive.widget.CircularAnim
import com.module.library.util.ToastUtil
import com.senierr.repository.Repository
import com.senierr.repository.service.api.IUserService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_top_bar_normal.*

/**
 * 登录
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class LoginActivity : BaseActivity() {

    private val userService = Repository.getService<IUserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()

        userService.isLoggedIn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        // 已登录，跳转
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        tv_title.setText(R.string.login)
        btn_left.setImageResource(R.drawable.ic_close_black_24dp)
        btn_left.setOnClickListener {
            finish()
        }

        btn_login.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val account = et_account.text.toString().trim()
        // 检查账号
        if (account.isEmpty()) {
            ToastUtil.showShort(this@LoginActivity, R.string.account_empty)
            return
        }

        // 检测账号是否存在
        userService.checkAccountExist(account)
                .flatMap {
                    if (it) {
                        return@flatMap Observable.just(it)
                    } else {
                        // 注册
                        return@flatMap userService.register(account, "123456")
                    }
                }
                .flatMap {
                    // 登录
                    return@flatMap userService.login(account, "123456")
                }
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
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                }
                            })
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }
}