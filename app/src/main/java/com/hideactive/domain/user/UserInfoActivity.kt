package com.hideactive.domain.user

import android.os.Bundle
import android.view.View
import com.hideactive.R
import com.hideactive.domain.base.BaseActivity
import com.module.library.util.OnThrottleClickListener
import com.senierr.repository.Repository
import com.senierr.repository.service.api.IUserService
import kotlinx.android.synthetic.main.activity_user_info.*

/**
 * 登录
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class UserInfoActivity : BaseActivity() {

    private val userService: IUserService = Repository.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

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

    }

    /**
     * 防抖动点击事件
     */
    private val onThrottleClickListener = object : OnThrottleClickListener() {
        override fun onThrottleClick(view: View?) {
            when(view?.id) {
                // 密码可见
                R.id.btn_eye -> {
                }
            }
        }
    }
}