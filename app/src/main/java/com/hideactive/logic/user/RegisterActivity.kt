package com.hideactive.logic.user

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.comm.KEY_ACCOUNT
import com.hideactive.comm.KEY_PASSWORD
import com.hideactive.comm.REGEX_MOBILE_EXACT
import com.hideactive.comm.REGEX_PASSWORD
import com.hideactive.ext.bindToLifecycle
import com.hideactive.ext.hideSoftInput
import com.hideactive.util.NotificationUtil
import com.hideactive.util.OnThrottleClickListener
import com.hideactive.util.ToastUtil
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
import com.senierr.repository.service.api.IUserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import java.util.regex.Pattern


/**
 * 注册
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class RegisterActivity : BaseActivity() {

    private val userService: IUserService = Repository.getService()
    private var verificationCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initView()
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        tb_top.setTitle(R.string.register)
        tb_top.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance)
        setSupportActionBar(tb_top)
        tb_top.setNavigationIcon(R.drawable.ic_back_white)
        tb_top.setNavigationOnClickListener {
            finish()
        }

        btn_eye.isSelected = false
        btn_eye.setOnClickListener(onThrottleClickListener)
        btn_request_code.setOnClickListener(onThrottleClickListener)
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
                    if (btn_eye.isSelected) {
                        et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    } else {
                        et_password.transformationMethod = PasswordTransformationMethod.getInstance()
                    }
                    et_password.setSelection(et_password.text.length)
                }
                // 发送验证码
                R.id.btn_request_code -> {
                    if (checkAccount()) {
                        requestVerificationCode()
                    }
                }
                // 注册
                R.id.btn_register -> {
                    if (checkAccount() && checkNickname()
                            && checkPassword() && checkVerificationCode()) {
                        register()
                    }
                }
            }
        }
    }

    /**
     * 检查手机号
     */
    private fun checkAccount(): Boolean {
        val account = et_account.text.toString().trim()
        if (account.isEmpty()) {
            ToastUtil.showShort(this@RegisterActivity, R.string.account_empty)
            return false
        }
        if (!Pattern.matches(REGEX_MOBILE_EXACT, account)) {
            ToastUtil.showShort(this@RegisterActivity, R.string.account_error)
            return false
        }
        return true
    }

    /**
     * 检查昵称
     */
    private fun checkNickname(): Boolean {
        val nickname = et_nickname.text.toString().trim()
        if (nickname.isEmpty()) {
            ToastUtil.showShort(this@RegisterActivity, R.string.nickname_empty)
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
            ToastUtil.showShort(this@RegisterActivity, R.string.password_empty)
            return false
        }
        if (!Pattern.matches(REGEX_PASSWORD, password)) {
            ToastUtil.showShort(this@RegisterActivity, R.string.password_error)
            return false
        }
        return true
    }

    /**
     * 检查验证码
     */
    private fun checkVerificationCode(): Boolean {
        val code = et_verification.text.toString().trim()
        if (code.isEmpty()) {
            ToastUtil.showShort(this@RegisterActivity, R.string.verification_code_empty)
            return false
        }
        if (verificationCode.toString() != code) {
            ToastUtil.showShort(this@RegisterActivity, R.string.verification_code_error)
            return false
        }
        return true
    }

    /**
     * 请求验证码
     */
    private fun requestVerificationCode() {
        verificationCode = Random().nextInt(9000) + 1000
        NotificationUtil.manager.notify(NotificationUtil.ID_SYSTEM,
                NotificationUtil.getBuilder(NotificationUtil.CHANNEL_ID_SYSTEM)
                        .setContentTitle(getString(R.string.register))
                        .setContentText(String.format(getString(R.string.verification_code_msg), verificationCode))
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                        .build())
    }

    /**
     * 注册
     */
    private fun register() {
        val account = et_account.text.toString().trim()
        val nickname = et_nickname.text.toString().trim()
        val password = et_password.text.toString().trim()
        // 检测账号是否重复
        userService.checkAccountIfRepeat(account)
                .flatMap {
                    // 账号重复
                    if (it) throw BmobError(BmobError.ACCOUNT_REPEAT)
                    // 检测昵称是否重复
                    return@flatMap userService.checkNicknameIfRepeat(nickname)
                }
                .flatMap {
                    // 昵称重复
                    if (it) throw BmobError(BmobError.NICKNAME_REPEAT)
                    // 注册
                    return@flatMap userService.register(account, password, nickname)
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
                    data.putExtra(KEY_ACCOUNT, account)
                    data.putExtra(KEY_PASSWORD, password)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }, {
                    if (it is BmobError) {
                        when(it.code) {
                            BmobError.ACCOUNT_REPEAT ->
                                ToastUtil.showShort(this, R.string.account_repeat)
                            BmobError.NICKNAME_REPEAT ->
                                ToastUtil.showShort(this, R.string.nickname_repeat)
                            else ->
                                ToastUtil.showShort(this, it.error)
                        }
                    } else {
                        ToastUtil.showShort(this, R.string.network_error)
                    }
                })
                .bindToLifecycle(this)
    }
}