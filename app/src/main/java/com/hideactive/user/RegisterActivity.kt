package com.hideactive.user

import android.os.Bundle
import android.text.Editable
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.util.EditTextWatcher
import com.hideactive.util.ToastUtil
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern

/**
 * 注册
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class RegisterActivity : BaseActivity() {

    private val regEx = "^[a-zA-Z0-9_]{4,15}$"

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
        tb_top.setNavigationIcon(R.mipmap.ic_back_white)
        tb_top.setNavigationOnClickListener {
            finish()
        }

        val textWatcher = object : EditTextWatcher() {
            override fun afterTextChanged(p0: Editable?) {
                if (!Pattern.matches(regEx, getAccount())) {
                    ToastUtil.showShort(this@RegisterActivity, "账号格式不对")
                }

                if (!Pattern.matches(regEx, getPassword())) {
                    ToastUtil.showShort(this@RegisterActivity, "密码格式不对")
                }
            }
        }

        et_account.addTextChangedListener(textWatcher)
        et_password.addTextChangedListener(textWatcher)

    }

    /**
     * 获取输入账号
     */
    private fun getAccount(): String {
        return et_account.text.toString().trim()
    }

    /**
     * 获取输入密码
     */
    private fun getPassword(): String {
        return et_password.text.toString().trim()
    }


    /**
     * 注册
     */
    private fun register() {
        val account = getAccount()
    }
}